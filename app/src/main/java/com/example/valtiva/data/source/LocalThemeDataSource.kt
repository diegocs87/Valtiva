package com.example.valtiva.data.source

import com.example.valtiva.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

/**
 * Locally stored appearance preference, spoken entirely in domain types. The implementation owns
 * whatever storage backs it; nothing above it sees a preference key.
 */
interface LocalThemeDataSource {

  /** Emits the stored theme mode and re-emits on every change. */
  fun getThemeMode(): Flow<ThemeMode>

  /** Persists [mode]. */
  suspend fun setThemeMode(mode: ThemeMode)
}
