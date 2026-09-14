package com.example.valtiva.presentation.settings

import com.example.valtiva.domain.model.ThemeMode

/**
 * Everything the settings screen can ask the ViewModel to do.
 *
 * A single sealed hierarchy behind one `onEvent` entry point, mirroring
 * [com.example.valtiva.presentation.favorites.FavoritesUiEvent]: the screen says what the user did,
 * never what the ViewModel should do about it.
 */
sealed interface SettingsUiEvent {

  /** The user tapped the "Theme" row, opening its submenu. */
  data object ThemeRowClicked : SettingsUiEvent

  /** The user picked a theme option from the submenu. */
  data class ThemeModeSelected(val mode: ThemeMode) : SettingsUiEvent

  /** The user dismissed the theme submenu without picking anything new. */
  data object ThemeDialogDismissed : SettingsUiEvent

  /** The user tapped the "About Valtiva" row, opening that document. */
  data object AboutRowClicked : SettingsUiEvent

  /** The user tapped the "Privacy Policy" row, opening that document. */
  data object PrivacyPolicyRowClicked : SettingsUiEvent

  /** The user toggled the daily automatic refresh switch. */
  data class DailyAutoRefreshToggled(val enabled: Boolean) : SettingsUiEvent

  /** The user dismissed the About/Privacy Policy document that was open. */
  data object SettingsDocumentDismissed : SettingsUiEvent
}
