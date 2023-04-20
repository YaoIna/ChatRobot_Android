package com.yao.chatrobot.repo

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.map

class ApiKeyRepository(private val dataStore: DataStore<Preferences>) {
    private val apiStoreKey = stringPreferencesKey("api_store_key")

    fun getApiKey() = dataStore.data.map {
        it[apiStoreKey] ?: ""
    }

    suspend fun saveApiKey(apiKey: String) {
        dataStore.edit {
            it[apiStoreKey] = apiKey
        }
    }
}