package com.example.valtiva.data.local.source

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.example.valtiva.data.source.LocalRatesRefreshSettingsDataSource
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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
