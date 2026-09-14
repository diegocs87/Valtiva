package com.example.valtiva.data.local.source

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.example.valtiva.data.source.LocalRatesRefreshSettingsDataSource
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * [LocalRatesRefreshSettingsDataSource] backed by Preferences DataStore, sharing the same
 * `valtiva_preferences` file as [DataStoreThemeDataSource]. A missing value resolves to `true`,
 * matching [com.example.valtiva.presentation.settings.SettingsUiState]'s default so a first launch
 * needs no separate default anywhere else.
 */
class DataStoreRatesRefreshSettingsDataSource @Inject constructor(
  private val dataStore: DataStore<Preferences>,
) : LocalRatesRefreshSettingsDataSource {

  override fun getDailyAutoRefreshEnabled(): Flow<Boolean> =
    dataStore.data.map { preferences -> preferences[DAILY_AUTO_REFRESH_ENABLED_KEY] ?: true }

  override suspend fun setDailyAutoRefreshEnabled(enabled: Boolean) {
    dataStore.edit { preferences -> preferences[DAILY_AUTO_REFRESH_ENABLED_KEY] = enabled }
  }

  private companion object {
    val DAILY_AUTO_REFRESH_ENABLED_KEY: Preferences.Key<Boolean> =
      booleanPreferencesKey("daily_auto_refresh_enabled")
  }
}
