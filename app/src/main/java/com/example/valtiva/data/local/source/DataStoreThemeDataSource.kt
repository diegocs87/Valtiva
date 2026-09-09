package com.example.valtiva.data.local.source

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.valtiva.data.source.LocalThemeDataSource
import com.example.valtiva.domain.model.ThemeMode
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * [LocalThemeDataSource] backed by Preferences DataStore. Stored as the enum's name rather than its
 * ordinal, so reordering [ThemeMode]'s entries later cannot silently reinterpret a saved preference.
 * A missing or corrupt value resolves to [ThemeMode.SYSTEM], the same default a first launch has.
 */
class DataStoreThemeDataSource @Inject constructor(
  private val dataStore: DataStore<Preferences>,
) : LocalThemeDataSource {

  override fun getThemeMode(): Flow<ThemeMode> =
    dataStore.data.map { preferences ->
      preferences[THEME_MODE_KEY]?.let { name -> runCatching { ThemeMode.valueOf(name) }.getOrNull() }
        ?: ThemeMode.SYSTEM
    }

  override suspend fun setThemeMode(mode: ThemeMode) {
    dataStore.edit { preferences -> preferences[THEME_MODE_KEY] = mode.name }
  }

  private companion object {
    val THEME_MODE_KEY: Preferences.Key<String> = stringPreferencesKey("theme_mode")
  }
}
