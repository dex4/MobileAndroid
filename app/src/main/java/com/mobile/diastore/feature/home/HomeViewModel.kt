package com.mobile.diastore.feature.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.diastore.database.EntriesDao
import com.mobile.diastore.model.Entry
import com.mobile.diastore.service.EntriesListDTO
import com.mobile.diastore.service.EntriesService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
//TODO: Move service/dao calls to Repository class(es)
class HomeViewModel(
    private val entriesDao: EntriesDao,
    private val entriesService: EntriesService
) : ViewModel() {

    private val _entries = MutableLiveData<List<Entry>>()
    val entries: LiveData<List<Entry>>
        get() = _entries

    private val _isDataLoading = MutableLiveData<Boolean>(false)
    val isDataLoading: LiveData<Boolean>
        get() = _isDataLoading

    private val _deletedEntry = MutableLiveData<Entry>()
    val deletedEntry: LiveData<Entry>
        get() = _deletedEntry

    private val _deleteError = MutableLiveData<String>()
    val deleteError: LiveData<String>
        get() = _deleteError

    private val _getError = MutableLiveData<String>()
    val getError: LiveData<String>
        get() = _getError

    private val _addError = MutableLiveData<Entry>()
    val addError: LiveData<Entry>
        get() = _addError

    private val _updateError = MutableLiveData<Entry>()
    val updateError: LiveData<Entry>
        get() = _updateError


    init {
        getEntriesFromServer()
    }

    fun handleSelectedEntryChange(selectedEntry: Entry) {
        val tempEntries = _entries.value?.toMutableList() ?: mutableListOf()
        val containsEntry = tempEntries.find { it.id == selectedEntry.id }
        if (containsEntry != null) {
            tempEntries.forEachIndexed { index, entry ->
                if (entry.id == selectedEntry.id) {
                    tempEntries[index] = selectedEntry
                    updateEntry(selectedEntry)
                    return@forEachIndexed
                }
            }
        } else {
            tempEntries.add(0, selectedEntry)
            addEntry(selectedEntry)
        }
    }

    private fun insertInDB(entry: Entry) {
        viewModelScope.launch {
            entriesDao.insert(entry)
        }
    }

    private fun updateInDB(entry: Entry) {
        viewModelScope.launch {
            entriesDao.update(entry)
        }
    }

    private fun deleteFromDB(entryId: String) {
        viewModelScope.launch {
            entriesDao.deleteEntryById(entryId)
        }
    }

    fun getEntriesFromServer() {
        CoroutineScope(Dispatchers.IO).launch {
            val request = entriesService.getAllEntries()
            withContext(Dispatchers.Main) {
                _isDataLoading.value = true
                try {
                    val serverEntries = request.await().toMutableList()
                    val dbEntries: MutableList<Entry> = entriesDao.getAllEntries().toMutableList()
                    serverEntries.forEach {
                        if (it !in dbEntries) {
                            dbEntries.add(it)
                            insertInDB(it)
                        }
                    }
                    val entriesToSync = mutableListOf<Entry>()
                    dbEntries.forEach {
                        if (it.id.contains("*")) {
                            entriesToSync.add(it)
                        }
                    }
                    addMultipleEntries(entriesToSync)
                    _entries.value = serverEntries
                    _isDataLoading.value = false
                } catch (e: Throwable) {
                    Log.e("WRKR", e.toString())
                    _entries.value = entriesDao.getAllEntries()
                    _isDataLoading.value = false
                    _getError.value = e.message
                }
            }
        }
    }

    fun deleteEntry(id: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val request = entriesService.deleteEntryById(id)
            withContext(Dispatchers.Main) {
                _isDataLoading.value = true
                try {
                    _deletedEntry.value = request.await()
                    deleteFromDB(id)
                    val tempEntries = _entries.value?.toMutableList() ?: mutableListOf()
                    var indexToDelete = -1
                    tempEntries.forEachIndexed { index, entry ->
                        if (entry.id == id) {
                            indexToDelete = index
                            return@forEachIndexed
                        }
                    }
                    if (indexToDelete != -1) {
                        tempEntries.removeAt(indexToDelete)
                    }
                    _entries.value = tempEntries
                    _isDataLoading.value = false
                } catch (e: Throwable) {
                    Log.e("WRKR", e.toString())
                    _isDataLoading.value = false
                    _deleteError.value = id
                }
            }
        }
    }

    private fun addEntry(entry: Entry) {
        CoroutineScope(Dispatchers.IO).launch {
            val request = entriesService.addEntry(entry)
            withContext(Dispatchers.Main) {
                _isDataLoading.value = true
                val tempEntries = _entries.value?.toMutableList() ?: mutableListOf()
                try {
                    val addedEntry = request.await()
                    tempEntries.add(0, addedEntry)
                    insertInDB(addedEntry)
                    _entries.value = tempEntries
                    _isDataLoading.value = false
                } catch (e: Throwable) {
                    Log.e("WRKR", e.toString())
                    entry.apply {
                        id = "*" + UUID.randomUUID().toString()
                        tempEntries.add(0, this)
                        insertInDB(entry)
                    }
                    _entries.value = tempEntries
                    _isDataLoading.value = false
                    _addError.value = entry
                }
            }
        }
    }

    private fun addMultipleEntries(entries: List<Entry>) {
        CoroutineScope(Dispatchers.IO).launch {
            val request = entriesService.addMultipleEntries(EntriesListDTO(entries))
            withContext(Dispatchers.Main) {
                _isDataLoading.value = true
                val tempEntries = _entries.value?.toMutableList() ?: mutableListOf()
                try {
                    val responseList = request.await()
                    entries.forEach {
                        deleteFromDB(it.id)
                    }
                    responseList.entriesList.forEach { addedEntry ->
                        tempEntries.add(0, addedEntry)
                        insertInDB(addedEntry)
                    }
                    _entries.value = tempEntries
                    _isDataLoading.value = false
                } catch (e: Throwable) {
                    Log.e("WRKR", e.toString())
                    _isDataLoading.value = false
                }
            }
        }
    }

    private fun updateEntry(entry: Entry) {
        CoroutineScope(Dispatchers.IO).launch {
            val request = entriesService.updateEntry(entry.id, entry)
            withContext(Dispatchers.Main) {
                _isDataLoading.value = true
                try {
                    val updatedEntry = request.await()
                    updateInDB(updatedEntry)
                    val tempEntries = _entries.value?.toMutableList() ?: mutableListOf()
                    var indexToUpdate = -1
                    tempEntries.forEachIndexed { index, e ->
                        if (e.id == updatedEntry.id) {
                            indexToUpdate = index
                            return@forEachIndexed
                        }
                    }
                    if (indexToUpdate != -1) {
                        tempEntries[indexToUpdate] = updatedEntry
                    }
                    _entries.value = tempEntries
                    _isDataLoading.value = false
                } catch (e: Throwable) {
                    Log.e("WRKR", e.toString())
                    _isDataLoading.value = false
                    _updateError.value = entry
                }
            }
        }
    }
}
