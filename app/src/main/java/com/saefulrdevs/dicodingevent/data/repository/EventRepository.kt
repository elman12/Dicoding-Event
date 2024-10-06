package com.saefulrdevs.dicodingevent.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.saefulrdevs.dicodingevent.data.local.database.FavoriteEventDao
import com.saefulrdevs.dicodingevent.data.local.model.FavoriteEvent
import com.saefulrdevs.dicodingevent.data.remote.response.Event
import com.saefulrdevs.dicodingevent.data.remote.response.ListEventsItem
import com.saefulrdevs.dicodingevent.data.remote.retrofit.ApiService

class EventRepository(
    private val favoriteEventDao: FavoriteEventDao,
    private val apiService: ApiService
) {
    suspend fun getUpcomingEvent(): Result<List<ListEventsItem>> {
        return try {
            val response = apiService.getAllActiveEvent()
            if (response.isSuccessful) {
                Result.success(response.body()?.listEvents ?: emptyList())
            } else {
                Result.failure(Exception("Failed to load data from API, Status code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFinishedEvent(): Result<List<ListEventsItem>> {
        return try {
            val response = apiService.getAllFinishedEvent()
            if (response.isSuccessful) {
                Result.success(response.body()?.listEvents ?: emptyList())
            } else {
                Result.failure(Exception("Failed to load data from API, Status code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDetailEvent(id: Int): Result<Event> {
        return try {
            val response = apiService.getDetailEvent(id)
            if (response.isSuccessful) {
                val event = response.body()?.event ?: throw Exception("Event not found")
                Result.success(event)
            } else {
                Result.failure(Exception("Failed to load data from API, Status code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchEvent(keyword: String): Result<List<ListEventsItem>> {
        return try {
            val response = apiService.searchEvent(keyword)
            if (response.isSuccessful) {
                Result.success(response.body()?.listEvents ?: emptyList())
            } else {
                Result.failure(Exception("Failed to load data from API, Status code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun insertFavoriteEvent(event: FavoriteEvent): Boolean {
        return try {
            val result = favoriteEventDao.insertFavoriteEvent(event)
            Log.d("Insert EventRepository", "Insert Favorite Event: $result")
            result != -1L
            true
        } catch (e: Exception) {
            Log.e("Insert EventRepository", "Error inserting favorite event: ${e.message}")
            false
        }
    }

    suspend fun deleteFavoriteEvent(event: FavoriteEvent): Boolean {
        return try {
            favoriteEventDao.deleteFavoriteEvent(event)
            Log.d("Delete EventRepository", "Delete Favorite Event: $event")
            true
        } catch (e: Exception) {
            Log.e("Delete EventRepository", "Error deleting favorite event: ${e.message}")
            false
        }
    }

    fun getAllFavoriteEvent(): LiveData<List<FavoriteEvent>> {
        return favoriteEventDao.getAllFavoriteEvent()
    }

    fun getFavoriteEventById(eventId: Int): LiveData<FavoriteEvent> {
        return favoriteEventDao.getFavoriteEventById(eventId)
    }

    companion object {
        @Volatile
        private var instance: EventRepository? = null
        fun getInstance(
            apiService: ApiService,
            favoriteEventDao: FavoriteEventDao
        ): EventRepository =
            instance ?: synchronized(this) {
                instance ?: EventRepository(favoriteEventDao, apiService)
            }
                .also { instance = it }
    }
}