package com.janbina.habits.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Datastore @Inject constructor(
    @ApplicationContext context: Context
) {
    private val store = context.createDataStore(name = "habits_store")

    val isShowArchived: Flow<Boolean> = store.data.map { preferences ->
        preferences[KEY_SHOW_ARCHIVED] ?: false
    }

    suspend fun setShowArchived(value: Boolean) {
        store.edit { settings ->
            settings[KEY_SHOW_ARCHIVED] = value
        }
    }

    companion object {
        private val KEY_SHOW_ARCHIVED = preferencesKey<Boolean>("show_archived")
    }
}