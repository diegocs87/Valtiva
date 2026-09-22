package com.example.valtiva.presentation.favorites

import com.example.valtiva.analytics.AnalyticsEvent
import com.example.valtiva.analytics.AnalyticsTracker
import com.example.valtiva.diagnostics.CrashReporter
import com.example.valtiva.domain.COP
import com.example.valtiva.domain.EUR
import com.example.valtiva.domain.USD
import com.example.valtiva.domain.model.CurrencyPair
import com.example.valtiva.domain.model.FavoriteExchange
import com.example.valtiva.domain.usecase.GetFavoriteExchangesUseCase
import com.example.valtiva.domain.usecase.RemoveFavoriteExchangeUseCase
import com.example.valtiva.presentation.MainDispatcherRule
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Behavioural tests for the favorites ViewModel: the stream emits, an event goes in, and the
 * resulting [FavoritesUiState] — or the calls this ViewModel makes on the way — is asserted.
 */
class FavoritesViewModelTest {

  @get:Rule
  val mainDispatcherRule = MainDispatcherRule()

  private val getFavoriteExchanges = mockk<GetFavoriteExchangesUseCase>()
  private val removeFavoriteExchange = mockk<RemoveFavoriteExchangeUseCase>()
  private val analyticsTracker = mockk<AnalyticsTracker>(relaxed = true)
  private val crashReporter = mockk<CrashReporter>(relaxed = true)
  private val favorites = MutableStateFlow<List<FavoriteExchange>>(emptyList())

  @Before
  fun setUp() {
    every { getFavoriteExchanges() } returns favorites
    coEvery { removeFavoriteExchange(any()) } just Runs
  }

  private fun viewModel() =
    FavoritesViewModel(getFavoriteExchanges, removeFavoriteExchange, analyticsTracker, crashReporter)

  @Test
  fun `publishes the favorites and every later change to them`() = runTest {
    val first = listOf(FavoriteExchange(CurrencyPair(USD, COP)))
    favorites.value = first
    val viewModel = viewModel()
    assertEquals(first, viewModel.uiState.value.favorites)

    val second = first + FavoriteExchange(CurrencyPair(EUR, COP))
    favorites.value = second

    assertEquals(second, viewModel.uiState.value.favorites)
  }

  @Test
  fun `marks the favorites unavailable when their stream fails`() = runTest {
    val failure = IllegalStateException("the favorites table is gone")
    every { getFavoriteExchanges() } returns flow<List<FavoriteExchange>> { throw failure }

    val state = viewModel().uiState.value

    assertTrue(state.unavailable)
  }

  @Test
  fun `records the failure behind an unavailable favorites stream`() = runTest {
    val failure = IllegalStateException("the favorites table is gone")
    every { getFavoriteExchanges() } returns flow<List<FavoriteExchange>> { throw failure }

    viewModel()

    verify(exactly = 1) { crashReporter.recordException(failure) }
  }

  @Test
  fun `removing a favorite deletes its pair`() = runTest {
    val favorite = FavoriteExchange(CurrencyPair(EUR, COP))
    favorites.value = listOf(favorite)
    val viewModel = viewModel()

    viewModel.onEvent(FavoritesUiEvent.FavoriteRemoved(favorite))

    coVerify(exactly = 1) { removeFavoriteExchange(CurrencyPair(EUR, COP)) }
  }

  @Test
  fun `reports a favorite being removed`() = runTest {
    val favorite = FavoriteExchange(CurrencyPair(EUR, COP))
    favorites.value = listOf(favorite)
    val viewModel = viewModel()

    viewModel.onEvent(FavoritesUiEvent.FavoriteRemoved(favorite))

    verify(exactly = 1) { analyticsTracker.trackEvent(AnalyticsEvent.FavoriteRemoved("EUR", "COP")) }
  }
}
