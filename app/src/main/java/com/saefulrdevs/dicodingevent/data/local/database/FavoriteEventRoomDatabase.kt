package com.saefulrdevs.dicodingevent.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.saefulrdevs.dicodingevent.data.local.model.FavoriteEvent

@Database(entities = [FavoriteEvent::class], version = 1, exportSchema = false)
abstract class FavoriteEventRoomDatabase : RoomDatabase() {
    abstract fun favoriteEventDao(): FavoriteEventDao

    companion object {
        @Volatile
        private var INSTANCE: FavoriteEventRoomDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): FavoriteEventRoomDatabase {
            if (INSTANCE == null) {
                synchronized(FavoriteEventRoomDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        FavoriteEventRoomDatabase::class.java, "favorite_event_database"
                    ).build()
                }
            }
            return INSTANCE as FavoriteEventRoomDatabase
        }
    }
}