package com.example.valtiva.presentation.settings

import com.example.valtiva.domain.model.ThemeMode

/**
 * Everything the settings screen renders, and nothing else.
 *
 * [themeMode] is the one preference backed by the domain/data layers (see [ThemeRepository]); it is
 * observed, not owned, by [SettingsViewModel]. [dailyAutoRefreshEnabled] has no domain or data layer
 * behind it yet, so it does not persist or trigger a real refresh — it is the switch's single source
 * of truth for the lifetime of the screen. [themeDialogVisible] and [visibleDocument] are each some
 * dialog's own visibility, kept apart because the screen can show at most one dialog at a time and a
 * single flag could not say which.
 */
data class SettingsUiState(
  val themeMode: ThemeMode = ThemeMode.SYSTEM,
  val themeDialogVisible: Boolean = false,
  val dailyAutoRefreshEnabled: Boolean = true,
  val visibleDocument: SettingsDocumentKind? = null,
)

/** The static, in-app documents a row on this screen can open. */
enum class SettingsDocumentKind {
  ABOUT,
  PRIVACY_POLICY,
}
