package com.mobile.diastore.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mobile.diastore.model.Entry

@Database(entities = [Entry::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class DiaStoreDataBase : RoomDatabase() {

    abstract fun entriesDao(): EntriesDao

//    companion object {
//        @Volatile
//        private var INSTANCE: DiaStoreDataBase? = null
//
//        fun getInstance(context: Context): DiaStoreDataBase {
//            synchronized(this) {
//                var instance = INSTANCE
//
//                if (instance == null) {
//                    instance = Room.databaseBuilder(
//                        context.applicationContext,
//                        DiaStoreDataBase::class.java,
//                        "diastore_database"
//                    ).fallbackToDestructiveMigration().build()
//                    INSTANCE = instance
//                }
//
//                return instance
//            }
//        }
//    }
}