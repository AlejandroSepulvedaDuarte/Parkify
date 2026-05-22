package com.example.parkify.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "parkify_prefs")

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val KEY_LAST_USER = stringPreferencesKey("last_user_email")
    }

    val lastUserEmail = context.dataStore.data.map { prefs -> prefs[KEY_LAST_USER] ?: "" }

    suspend fun saveLastUserEmail(email: String) {
        context.dataStore.edit { it[KEY_LAST_USER] = email }
    }
}
