package com.example.valtiva.presentation.settings

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.valtiva.R
import com.example.valtiva.domain.model.ThemeMode
import com.example.valtiva.domain.usecase.GetThemeModeUseCase
import com.example.valtiva.domain.usecase.SetThemeModeUseCase
import com.example.valtiva.presentation.settings.SettingsUiEvent.AboutRowClicked
import com.example.valtiva.presentation.settings.SettingsUiEvent.DailyAutoRefreshToggled
import com.example.valtiva.presentation.settings.SettingsUiEvent.InfoDialogDismissed
import com.example.valtiva.presentation.settings.SettingsUiEvent.PrivacyPolicyRowClicked
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
 * Placeholder copy shown for a row with no real destination behind it yet.
 *
 * Stands in for the About screen and the Privacy Policy document until those are built;
 * [SettingsViewModel] picks one at random on every tap so the placeholder does not read as a single
 * hard-coded message.
 */
private val PlaceholderMessages = listOf(
  R.string.settings_info_placeholder_1,
  R.string.settings_info_placeholder_2,
  R.string.settings_info_placeholder_3,
  R.string.settings_info_placeholder_4,
)

/**
 * Drives the settings screen.
 *
 * The theme use cases are the only real dependency: [dailyAutoRefreshEnabled] and the info dialogs
 * have no domain or data layer behind them yet, so those stay plain in-memory state, the same way
 * [com.example.valtiva.presentation.favorites.FavoritesViewModel] only depends on what the screen
 * genuinely observes or changes.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
  private val getThemeMode: GetThemeModeUseCase,
  private val setThemeMode: SetThemeModeUseCase,
) : ViewModel() {

  private val _uiState = MutableStateFlow(SettingsUiState())

  /** The single source of truth for the screen. Read-only: the UI changes it through events only. */
  val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

  init {
    observeThemeMode()
  }

  /** The screen's only way in. */
  fun onEvent(event: SettingsUiEvent) {
    when (event) {
      is ThemeRowClicked -> _uiState.update { it.copy(themeDialogVisible = true) }
      is ThemeModeSelected -> selectThemeMode(event.mode)
      is ThemeDialogDismissed -> _uiState.update { it.copy(themeDialogVisible = false) }
      is AboutRowClicked -> showInfoDialog(R.string.settings_about_title)
      is PrivacyPolicyRowClicked -> showInfoDialog(R.string.settings_privacy_title)
      is DailyAutoRefreshToggled -> _uiState.update { it.copy(dailyAutoRefreshEnabled = event.enabled) }
      is InfoDialogDismissed -> _uiState.update { it.copy(infoDialog = null) }
    }
  }

  private fun observeThemeMode() {
    viewModelScope.launch {
      getThemeMode().collect { mode -> _uiState.update { it.copy(themeMode = mode) } }
    }
  }

  private fun selectThemeMode(mode: ThemeMode) {
    _uiState.update { it.copy(themeDialogVisible = false) }
    viewModelScope.launch { setThemeMode(mode) }
  }

  private fun showInfoDialog(@StringRes titleRes: Int) {
    val dialog = SettingsInfoDialog(titleRes = titleRes, messageRes = PlaceholderMessages.random())
    _uiState.update { it.copy(infoDialog = dialog) }
  }
}
