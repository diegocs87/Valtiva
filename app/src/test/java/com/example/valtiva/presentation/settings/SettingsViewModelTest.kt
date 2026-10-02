package com.example.valtiva.presentation.settings

import com.example.valtiva.analytics.AnalyticsEvent
import com.example.valtiva.analytics.AnalyticsTracker
import com.example.valtiva.domain.model.ThemeMode
import com.example.valtiva.domain.usecase.GetDailyAutoRefreshEnabledUseCase
import com.example.valtiva.domain.usecase.GetThemeModeUseCase
import com.example.valtiva.domain.usecase.SetDailyAutoRefreshEnabledUseCase
import com.example.valtiva.domain.usecase.SetThemeModeUseCase
import com.example.valtiva.presentation.MainDispatcherRule
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Behavioural tests for the settings ViewModel: the theme mode and daily-refresh preference streams
 * emit, and the resulting [SettingsUiState] is asserted. The document dialogs have no domain or data
 * dependency, so those are asserted as plain in-memory state, the way
 * [com.example.valtiva.presentation.favorites.FavoritesViewModel] tests only mock what the ViewModel
 * genuinely depends on.
 */
class SettingsViewModelTest {

  @get:Rule
  val mainDispatcherRule = MainDispatcherRule()

  private val getThemeMode = mockk<GetThemeModeUseCase>()
  private val setThemeMode = mockk<SetThemeModeUseCase>()
  private val getDailyAutoRefreshEnabled = mockk<GetDailyAutoRefreshEnabledUseCase>()
  private val setDailyAutoRefreshEnabled = mockk<SetDailyAutoRefreshEnabledUseCase>()
  private val analyticsTracker = mockk<AnalyticsTracker>(relaxed = true)
  private val themeMode = MutableSharedFlow<ThemeMode>(replay = 1)
  private val dailyAutoRefreshEnabled = MutableSharedFlow<Boolean>(replay = 1)

  @Before
  fun setUp() {
    every { getThemeMode() } returns themeMode
    coJustRun { setThemeMode(any()) }
    every { getDailyAutoRefreshEnabled() } returns dailyAutoRefreshEnabled
    coJustRun { setDailyAutoRefreshEnabled(any()) }
  }

  private fun viewModel() = SettingsViewModel(
    getThemeMode,
    setThemeMode,
    getDailyAutoRefreshEnabled,
    setDailyAutoRefreshEnabled,
    analyticsTracker,
  )

  @Test
  fun `starts with the system default theme, the daily refresh switch on, and no dialog showing`() = runTest {
    themeMode.emit(ThemeMode.SYSTEM)
    dailyAutoRefreshEnabled.emit(true)

    val state = viewModel().uiState.value

    assertEquals(ThemeMode.SYSTEM, state.themeMode)
    assertFalse(state.themeDialogVisible)
    assertTrue(state.dailyAutoRefreshEnabled)
    assertNull(state.visibleDocument)
  }

  @Test
  fun `publishes the theme mode the stream emits`() = runTest {
    themeMode.emit(ThemeMode.DARK)
    dailyAutoRefreshEnabled.emit(true)

    val state = viewModel().uiState.value

    assertEquals(ThemeMode.DARK, state.themeMode)
  }

  @Test
  fun `tapping the theme row opens its submenu`() = runTest {
    themeMode.emit(ThemeMode.SYSTEM)
    dailyAutoRefreshEnabled.emit(true)

    val viewModel = viewModel()
    viewModel.onEvent(SettingsUiEvent.ThemeRowClicked)

    assertTrue(viewModel.uiState.value.themeDialogVisible)
  }

  @Test
  fun `selecting a theme mode persists it and closes the submenu`() = runTest {
    themeMode.emit(ThemeMode.SYSTEM)
    dailyAutoRefreshEnabled.emit(true)
    val viewModel = viewModel()
    viewModel.onEvent(SettingsUiEvent.ThemeRowClicked)

    viewModel.onEvent(SettingsUiEvent.ThemeModeSelected(ThemeMode.LIGHT))

    assertFalse(viewModel.uiState.value.themeDialogVisible)
    coVerify(exactly = 1) { setThemeMode(ThemeMode.LIGHT) }
  }

  @Test
  fun `dismissing the submenu closes it without persisting anything`() = runTest {
    themeMode.emit(ThemeMode.SYSTEM)
    dailyAutoRefreshEnabled.emit(true)
    val viewModel = viewModel()
    viewModel.onEvent(SettingsUiEvent.ThemeRowClicked)

    viewModel.onEvent(SettingsUiEvent.ThemeDialogDismissed)

    assertFalse(viewModel.uiState.value.themeDialogVisible)
    coVerify(exactly = 0) { setThemeMode(any()) }
  }

  @Test
  fun `publishes the daily refresh preference the stream emits`() = runTest {
    themeMode.emit(ThemeMode.SYSTEM)
    dailyAutoRefreshEnabled.emit(false)

    val state = viewModel().uiState.value

    assertFalse(state.dailyAutoRefreshEnabled)
  }

  @Test
  fun `toggling daily refresh off persists the change and does not touch the theme`() = runTest {
    themeMode.emit(ThemeMode.SYSTEM)
    dailyAutoRefreshEnabled.emit(true)
    val viewModel = viewModel()

    viewModel.onEvent(SettingsUiEvent.DailyAutoRefreshToggled(false))

    coVerify(exactly = 1) { setDailyAutoRefreshEnabled(false) }
    assertEquals(ThemeMode.SYSTEM, viewModel.uiState.value.themeMode)
    assertNull(viewModel.uiState.value.visibleDocument)
  }

  @Test
  fun `toggling daily refresh on persists the change`() = runTest {
    themeMode.emit(ThemeMode.SYSTEM)
    dailyAutoRefreshEnabled.emit(false)
    val viewModel = viewModel()

    viewModel.onEvent(SettingsUiEvent.DailyAutoRefreshToggled(true))

    coVerify(exactly = 1) { setDailyAutoRefreshEnabled(true) }
  }

  @Test
  fun `tapping About Valtiva opens the About document`() = runTest {
    themeMode.emit(ThemeMode.SYSTEM)
    dailyAutoRefreshEnabled.emit(true)
    val viewModel = viewModel()

    viewModel.onEvent(SettingsUiEvent.AboutRowClicked)

    assertEquals(SettingsDocumentKind.ABOUT, viewModel.uiState.value.visibleDocument)
  }

  @Test
  fun `tapping Privacy Policy opens the Privacy Policy document`() = runTest {
    themeMode.emit(ThemeMode.SYSTEM)
    dailyAutoRefreshEnabled.emit(true)
    val viewModel = viewModel()

    viewModel.onEvent(SettingsUiEvent.PrivacyPolicyRowClicked)

    assertEquals(SettingsDocumentKind.PRIVACY_POLICY, viewModel.uiState.value.visibleDocument)
  }

  @Test
  fun `dismissing the document dialog clears it`() = runTest {
    themeMode.emit(ThemeMode.SYSTEM)
    dailyAutoRefreshEnabled.emit(true)
    val viewModel = viewModel()
    viewModel.onEvent(SettingsUiEvent.AboutRowClicked)

    viewModel.onEvent(SettingsUiEvent.SettingsDocumentDismissed)

    assertNull(viewModel.uiState.value.visibleDocument)
  }

  @Test
  fun `reports that the settings screen was opened`() = runTest {
    themeMode.emit(ThemeMode.SYSTEM)
    dailyAutoRefreshEnabled.emit(true)

    viewModel()

    verify(exactly = 1) { analyticsTracker.trackEvent(AnalyticsEvent.SettingsOpened) }
  }
}
