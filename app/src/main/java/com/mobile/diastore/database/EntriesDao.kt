package com.mobile.diastore.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.mobile.diastore.model.Entry

@Dao
interface EntriesDao {

    @Insert
    suspend fun insert(entry: Entry)

    @Update
    suspend fun update(entry: Entry)

    @Query("select * from entries where id = :entryId")
    suspend fun getEntryById(entryId: String): Entry?

    @Query("delete from entries where id = :entryId")
    suspend fun deleteEntryById(entryId: String)

    @Query("SELECT * FROM entries ORDER BY entryTime DESC")
    suspend fun getAllEntries(): List<Entry>
}