package com.mobile.diastore.feature.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mobile.diastore.model.Entry

class EntriesSharedViewModel : ViewModel() {

    private val _selectedEntry = MutableLiveData<Entry?>()
    val selectedEntry: LiveData<Entry?>
        get() = _selectedEntry

    val bloodSugarLevel = MutableLiveData<String>()
    val carbohydratesIntake = MutableLiveData<String>()
    val insulinIntake = MutableLiveData<String>()
    val physicalActivityDuration = MutableLiveData<String>()

    fun setSelectedEntry(entry: Entry?) {
        _selectedEntry.value = entry
    }
}