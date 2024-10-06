package com.saefulrdevs.dicodingevent.di

import android.content.Context
import com.saefulrdevs.dicodingevent.data.local.SettingPreferences
import com.saefulrdevs.dicodingevent.data.local.dataStore
import com.saefulrdevs.dicodingevent.data.local.database.FavoriteEventRoomDatabase
import com.saefulrdevs.dicodingevent.data.repository.EventRepository
import com.saefulrdevs.dicodingevent.data.remote.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): EventRepository {
        val apiService = ApiConfig.getApiService()
        val database = FavoriteEventRoomDatabase.getDatabase(context)
        val dao = database.favoriteEventDao()
        return EventRepository.getInstance(apiService, dao)
    }


    fun providePreferences(context: Context): SettingPreferences {
        return SettingPreferences.getInstance(context.dataStore)
    }
}