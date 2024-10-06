package com.saefulrdevs.dicodingevent.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.saefulrdevs.dicodingevent.data.local.SettingPreferences
import com.saefulrdevs.dicodingevent.data.repository.EventRepository
import com.saefulrdevs.dicodingevent.di.Injection

class ViewModelFactory private constructor(
    private val pref: SettingPreferences,
    private val eventRepository: EventRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(pref, eventRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                val preferences = Injection.providePreferences(context)
                val repository = Injection.provideRepository(context)
                instance ?: ViewModelFactory(preferences, repository)
            }.also { instance = it }
    }
}