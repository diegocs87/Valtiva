package com.example.valtiva.domain.repository

import com.example.valtiva.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

/**
 * Storage of the user's appearance preference.
 *
 * Reads are exposed as a [Flow] so the app can re-theme itself the moment the preference changes,
 * while the write is a one-shot suspending operation.
 */
interface ThemeRepository {

  /** Emits the current theme mode and re-emits whenever it changes. */
  fun getThemeMode(): Flow<ThemeMode>

  /** Persists [mode] as the user's appearance preference. */
  suspend fun setThemeMode(mode: ThemeMode)
}
