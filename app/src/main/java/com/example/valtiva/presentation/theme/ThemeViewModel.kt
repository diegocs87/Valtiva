package com.example.valtiva.presentation.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.valtiva.domain.model.ThemeMode
import com.example.valtiva.domain.usecase.GetThemeModeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn

/**
 * Resolves the theme preference for [com.example.valtiva.MainActivity] to apply to [ValtivaTheme]
 * at the app root.
 *
 * Kept separate from `SettingsViewModel`: this is an app-shell concern (what to render the whole app
 * in), not the settings screen's own state, even though both read the same
 * [com.example.valtiva.domain.repository.ThemeRepository] underneath.
 */
@HiltViewModel
class ThemeViewModel @Inject constructor(
  getThemeMode: GetThemeModeUseCase,
) : ViewModel() {

  /** [ThemeMode.SYSTEM] until the stored preference loads, and if it ever fails to load. */
  val themeMode: StateFlow<ThemeMode> = getThemeMode()
    .catch { emit(ThemeMode.SYSTEM) }
    .stateIn(viewModelScope, SharingStarted.Eagerly, ThemeMode.SYSTEM)
}
