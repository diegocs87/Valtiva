package com.example.valtiva.presentation.settings

import androidx.annotation.StringRes
import com.example.valtiva.domain.model.ThemeMode

/**
 * Everything the settings screen renders, and nothing else.
 *
 * [themeMode] is the one preference backed by the domain/data layers (see [ThemeRepository]); it is
 * observed, not owned, by [SettingsViewModel]. [dailyAutoRefreshEnabled] and [infoDialog] have no
 * domain or data layer behind them yet: [dailyAutoRefreshEnabled] does not persist or trigger a real
 * refresh, it is the switch's single source of truth for the lifetime of the screen, and
 * [infoDialog] stands in for the About/Privacy Policy destinations until those are built.
 * [themeDialogVisible] is the theme submenu's own visibility, kept separate from [infoDialog] since
 * it is a real, functional picker rather than a placeholder.
 */
data class SettingsUiState(
  val themeMode: ThemeMode = ThemeMode.SYSTEM,
  val themeDialogVisible: Boolean = false,
  val dailyAutoRefreshEnabled: Boolean = true,
  val infoDialog: SettingsInfoDialog? = null,
)

/**
 * A placeholder explanation shown for a row that has no real destination yet.
 *
 * [titleRes] names which row was tapped; [messageRes] is chosen at random by [SettingsViewModel]
 * from its placeholder messages each time, standing in for the screen or document that row will
 * eventually open.
 */
data class SettingsInfoDialog(
  @param:StringRes val titleRes: Int,
  @param:StringRes val messageRes: Int,
)
