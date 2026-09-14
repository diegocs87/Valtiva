package com.example.valtiva.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Storage of the user's daily automatic refresh preference.
 *
 * Reads are exposed as a [Flow] so the settings screen stays in sync with the stored value, while
 * the write is a one-shot suspending operation, the same shape as [ThemeRepository].
 */
interface RatesRefreshSettingsRepository {

  /** Emits whether daily automatic refresh is enabled, and re-emits whenever it changes. */
  fun getDailyAutoRefreshEnabled(): Flow<Boolean>

  /** Persists [enabled] as the user's daily automatic refresh preference. */
  suspend fun setDailyAutoRefreshEnabled(enabled: Boolean)
}
