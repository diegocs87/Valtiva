package com.example.valtiva.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.valtiva.domain.model.ThemeMode
import com.example.valtiva.domain.usecase.GetDailyAutoRefreshEnabledUseCase
import com.example.valtiva.domain.usecase.GetThemeModeUseCase
import com.example.valtiva.domain.usecase.SetDailyAutoRefreshEnabledUseCase
import com.example.valtiva.domain.usecase.SetThemeModeUseCase
import com.example.valtiva.presentation.settings.SettingsUiEvent.AboutRowClicked
import com.example.valtiva.presentation.settings.SettingsUiEvent.DailyAutoRefreshToggled
import com.example.valtiva.presentation.settings.SettingsUiEvent.PrivacyPolicyRowClicked
import com.example.valtiva.presentation.settings.SettingsUiEvent.SettingsDocumentDismissed
import com.example.valtiva.presentation.settings.SettingsUiEvent.ThemeDialogDismissed
import com.example.valtiva.presentation.settings.SettingsUiEvent.ThemeModeSelected
import com.example.valtiva.presentation.settings.SettingsUiEvent.ThemeRowClicked
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Drives the settings screen.
 *
 * The theme and daily-refresh use cases are its only real dependencies: [SettingsUiState.visibleDocument]
 * has no domain or data layer behind it — it only names which bundled, static document ([About
 * Valtiva][SettingsDocumentKind.ABOUT] or [the privacy policy][SettingsDocumentKind.PRIVACY_POLICY])
 * to show; its actual copy lives in [AboutValtivaBlocks]/[PrivacyPolicyBlocks], not behind a use
 * case, since it ships with the app rather than coming from a repository. That keeps this ViewModel
 * depending on exactly what [com.example.valtiva.presentation.favorites.FavoritesViewModel] depends
 * on for its own screen: only what it genuinely observes or changes.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
  private val getThemeMode: GetThemeModeUseCase,
  private val setThemeMode: SetThemeModeUseCase,
  private val getDailyAutoRefreshEnabled: GetDailyAutoRefreshEnabledUseCase,
  private val setDailyAutoRefreshEnabled: SetDailyAutoRefreshEnabledUseCase,
) : ViewModel() {

  private val _uiState = MutableStateFlow(SettingsUiState())

  /** The single source of truth for the screen. Read-only: the UI changes it through events only. */
  val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

  init {
    observeThemeMode()
    observeDailyAutoRefreshEnabled()
  }

  /** The screen's only way in. */
  fun onEvent(event: SettingsUiEvent) {
    when (event) {
      is ThemeRowClicked -> _uiState.update { it.copy(themeDialogVisible = true) }
      is ThemeModeSelected -> selectThemeMode(event.mode)
      is ThemeDialogDismissed -> _uiState.update { it.copy(themeDialogVisible = false) }
      is AboutRowClicked -> showDocument(SettingsDocumentKind.ABOUT)
      is PrivacyPolicyRowClicked -> showDocument(SettingsDocumentKind.PRIVACY_POLICY)
      is DailyAutoRefreshToggled -> toggleDailyAutoRefresh(event.enabled)
      is SettingsDocumentDismissed -> _uiState.update { it.copy(visibleDocument = null) }
    }
  }

  private fun observeThemeMode() {
    viewModelScope.launch {
      getThemeMode().collect { mode -> _uiState.update { it.copy(themeMode = mode) } }
    }
  }

  private fun observeDailyAutoRefreshEnabled() {
    viewModelScope.launch {
      getDailyAutoRefreshEnabled().collect { enabled ->
        _uiState.update { it.copy(dailyAutoRefreshEnabled = enabled) }
      }
    }
  }

  private fun selectThemeMode(mode: ThemeMode) {
    _uiState.update { it.copy(themeDialogVisible = false) }
    viewModelScope.launch { setThemeMode(mode) }
  }

  private fun toggleDailyAutoRefresh(enabled: Boolean) {
    viewModelScope.launch { setDailyAutoRefreshEnabled(enabled) }
  }

  private fun showDocument(kind: SettingsDocumentKind) {
    _uiState.update { it.copy(visibleDocument = kind) }
  }
}
