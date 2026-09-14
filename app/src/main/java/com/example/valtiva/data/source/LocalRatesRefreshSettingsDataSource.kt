package com.example.valtiva.data.source

import kotlinx.coroutines.flow.Flow

/**
 * Locally stored daily automatic refresh preference. The implementation owns whatever storage
 * backs it; nothing above it sees a preference key.
 */
interface LocalRatesRefreshSettingsDataSource {

  /** Emits the stored preference and re-emits on every change. */
  fun getDailyAutoRefreshEnabled(): Flow<Boolean>

  /** Persists [enabled]. */
  suspend fun setDailyAutoRefreshEnabled(enabled: Boolean)
}
