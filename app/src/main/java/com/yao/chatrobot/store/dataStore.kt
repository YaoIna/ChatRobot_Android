package com.yao.chatrobot.store

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

private const val dataStoreFileName = "chat_robot_data_store"
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = dataStoreFileName)

