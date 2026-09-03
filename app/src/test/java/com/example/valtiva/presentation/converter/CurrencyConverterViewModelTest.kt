package com.example.valtiva.presentation.converter

import androidx.lifecycle.SavedStateHandle
import com.example.valtiva.domain.COP
import com.example.valtiva.domain.EUR
import com.example.valtiva.domain.RATE_DATE
import com.example.valtiva.domain.USD
import com.example.valtiva.domain.exception.ExchangeRateSyncException
import com.example.valtiva.domain.exception.ExchangeRateUnavailableException
import com.example.valtiva.domain.model.ConversionResult
import com.example.valtiva.domain.model.CurrencyPair
import com.example.valtiva.domain.model.ExchangeRateSnapshot
import com.example.valtiva.domain.model.FavoriteExchange
import com.example.valtiva.domain.model.Money
import com.example.valtiva.domain.usecase.AddFavoriteExchangeUseCase
import com.example.valtiva.domain.usecase.ConvertCurrencyUseCase
import com.example.valtiva.domain.usecase.GetFavoriteExchangesUseCase
import com.example.valtiva.domain.usecase.GetLatestRatesUseCase
import com.example.valtiva.domain.usecase.RefreshRatesUseCase
import com.example.valtiva.domain.usecase.RemoveFavoriteExchangeUseCase
import com.example.valtiva.domain.usecase.SaveConversionHistoryUseCase
import com.example.valtiva.presentation.MainDispatcherRule
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import java.math.BigDecimal
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Behavioural tests for the converter ViewModel: an event goes in, the use cases answer, and the
 * resulting [CurrencyConverterUiState] is asserted. The use cases are mocked because what they do
 * is already covered by their own tests — here they only need to succeed or fail on cue.
 *
 * No Room, no Retrofit, no Hilt: this class is a plain JVM test, which is the point of a ViewModel
 * that depends on nothing but the domain.
 */
class CurrencyConverterViewModelTest {

  @get:Rule
  val mainDispatcherRule = MainDispatcherRule()

  private val convertCurrency = mockk<ConvertCurrencyUseCase>()
  private val getLatestRates = mockk<GetLatestRatesUseCase>()
  private val refreshRates = mockk<RefreshRatesUseCase>()
  private val getFavoriteExchanges = mockk<GetFavoriteExchangesUseCase>()
  private val addFavoriteExchange = mockk<AddFavoriteExchangeUseCase>()
  private val removeFavoriteExchange = mockk<RemoveFavoriteExchangeUseCase>()
  private val saveConversionHistory = mockk<SaveConversionHistoryUseCase>()
  private val converterHandoffReceiver = mockk<ConverterHandoffReceiver>()

  private val favorites = MutableStateFlow<List<FavoriteExchange>>(emptyList())
  private val pendingConversions = MutableSharedFlow<PendingConversion>()

  private val usdSnapshot = ExchangeRateSnapshot(
    baseCurrency = USD,
    date = RATE_DATE,
    rates = mapOf(COP to BigDecimal("3980.25"), EUR to BigDecimal("0.85")),
  )

  private val usdToCop = ConversionResult(
    source = Money(BigDecimal("100.00"), USD),
    target = Money(BigDecimal("398025.00"), COP),
    exchangeRate = BigDecimal("3980.25"),
    rateDate = RATE_DATE,
  )

  @Before
  fun setUp() {
    every { getFavoriteExchanges() } returns favorites
    coEvery { getLatestRates(any()) } returns usdSnapshot
    coEvery { refreshRates(any()) } returns usdSnapshot
    coEvery { addFavoriteExchange(any()) } just Runs
    coEvery { removeFavoriteExchange(any()) } just Runs
    coEvery { saveConversionHistory(any()) } just Runs
    every { converterHandoffReceiver.pendingConversion } returns pendingConversions
  }

  private fun viewModel(savedStateHandle: SavedStateHandle = SavedStateHandle()) =
    CurrencyConverterViewModel(
      convertCurrency = convertCurrency,
      getLatestRates = getLatestRates,
      refreshRates = refreshRates,
      getFavoriteExchanges = getFavoriteExchanges,
      addFavoriteExchange = addFavoriteExchange,
      removeFavoriteExchange = removeFavoriteExchange,
      saveConversionHistory = saveConversionHistory,
      converterHandoffReceiver = converterHandoffReceiver,
      savedStateHandle = savedStateHandle,
    )

  /** Puts a complete, converted form on screen, which most transition tests start from. */
  private fun convertedViewModel(): CurrencyConverterViewModel {
    coEvery { convertCurrency(BigDecimal("100"), USD, COP) } returns usdToCop
    return viewModel().apply {
      onEvent(CurrencyConverterUiEvent.AmountChanged("100"))
      onEvent(CurrencyConverterUiEvent.TargetCurrencySelected(COP))
      onEvent(CurrencyConverterUiEvent.ConvertClicked)
    }
  }

  // -------------------------------------------------------------------------------------------
  // Initialization
  // -------------------------------------------------------------------------------------------

  @Test
  fun `starts on an empty form loading the rates of the default base currency`() = runTest {
    // The load is held open so the state can be observed exactly as the screen first sees it.
    val gate = CompletableDeferred<ExchangeRateSnapshot?>()
    coEvery { getLatestRates(USD) } coAnswers { gate.await() }

    val state = viewModel().uiState.value

    assertEquals("", state.amount)
    assertEquals(CurrencyConverterUiState.DEFAULT_BASE_CURRENCY, state.baseCurrency)
    assertNull(state.targetCurrency)
    assertTrue(state.availableCurrencies.isEmpty())
    assertEquals(CurrencyConverterUiStatus.Loading, state.status)
  }

  @Test
  fun `offers the currencies of the loaded snapshot, including its base`() = runTest {
    val state = viewModel().uiState.value

    assertEquals(listOf(COP, EUR, USD), state.availableCurrencies)
    assertEquals(CurrencyConverterUiStatus.Idle, state.status)
  }

  @Test
  fun `reads the rates instead of forcing a sync when the screen opens`() = runTest {
    viewModel()

    coVerify(exactly = 1) { getLatestRates(USD) }
    coVerify(exactly = 0) { refreshRates(any()) }
  }

  @Test
  fun `reports that nothing is published yet when no snapshot exists for the base currency`() = runTest {
    coEvery { getLatestRates(USD) } returns null

    val state = viewModel().uiState.value

    assertEquals(
      CurrencyConverterUiStatus.Error(
        CurrencyConverterUiError.RatesNotPublished(USD),
        RetryOperation.LOAD_RATES,
      ),
      state.status,
    )
  }

  @Test
  fun `reports an unreachable provider when the initial load cannot sync`() = runTest {
    coEvery { getLatestRates(USD) } throws ExchangeRateSyncException(USD)

    val state = viewModel().uiState.value

    assertEquals(CurrencyConverterUiError.RatesUnreachable, state.error)
    assertTrue(state.isRetryable)
  }

  // -------------------------------------------------------------------------------------------
  // Form
  // -------------------------------------------------------------------------------------------

  @Test
  fun `keeps the amount the user typed`() = runTest {
    val viewModel = viewModel()

    viewModel.onEvent(CurrencyConverterUiEvent.AmountChanged("42.50"))

    assertEquals("42.50", viewModel.uiState.value.amount)
  }

  @Test
  fun `keeps the currencies the user picked`() = runTest {
    val viewModel = viewModel()

    viewModel.onEvent(CurrencyConverterUiEvent.BaseCurrencySelected(EUR))
    viewModel.onEvent(CurrencyConverterUiEvent.TargetCurrencySelected(COP))

    assertEquals(EUR, viewModel.uiState.value.baseCurrency)
    assertEquals(COP, viewModel.uiState.value.targetCurrency)
  }

  @Test
  fun `drops the conversion on screen when the amount changes`() = runTest {
    val viewModel = convertedViewModel()
    assertEquals(usdToCop, viewModel.uiState.value.conversion)

    viewModel.onEvent(CurrencyConverterUiEvent.AmountChanged("200"))

    assertNull(viewModel.uiState.value.conversion)
    assertEquals(CurrencyConverterUiStatus.Idle, viewModel.uiState.value.status)
  }

  @Test
  fun `drops the conversion on screen when a currency changes`() = runTest {
    val viewModel = convertedViewModel()

    viewModel.onEvent(CurrencyConverterUiEvent.TargetCurrencySelected(EUR))

    assertNull(viewModel.uiState.value.conversion)
  }

  @Test
  fun `swaps the two currencies`() = runTest {
    val viewModel = convertedViewModel()

    viewModel.onEvent(CurrencyConverterUiEvent.SwapCurrenciesClicked)

    assertEquals(COP, viewModel.uiState.value.baseCurrency)
    assertEquals(USD, viewModel.uiState.value.targetCurrency)
    assertNull(viewModel.uiState.value.conversion)
  }

  @Test
  fun `ignores a swap while no target currency is chosen`() = runTest {
    val viewModel = viewModel()

    viewModel.onEvent(CurrencyConverterUiEvent.SwapCurrenciesClicked)

    assertEquals(USD, viewModel.uiState.value.baseCurrency)
    assertNull(viewModel.uiState.value.targetCurrency)
  }

  @Test
  fun `only allows converting once the form is complete`() = runTest {
    val viewModel = viewModel()
    assertFalse(viewModel.uiState.value.canConvert)

    viewModel.onEvent(CurrencyConverterUiEvent.AmountChanged("100"))
    assertFalse(viewModel.uiState.value.canConvert)

    viewModel.onEvent(CurrencyConverterUiEvent.TargetCurrencySelected(COP))
    assertTrue(viewModel.uiState.value.canConvert)
  }

  // -------------------------------------------------------------------------------------------
  // Conversion
  // -------------------------------------------------------------------------------------------

  @Test
  fun `publishes the conversion returned by the use case`() = runTest {
    val viewModel = convertedViewModel()

    assertEquals(CurrencyConverterUiStatus.Success(usdToCop), viewModel.uiState.value.status)
    assertNull(viewModel.uiState.value.error)
  }

  @Test
  fun `converts the amount typed between the selected currencies`() = runTest {
    convertedViewModel()

    coVerify(exactly = 1) { convertCurrency(BigDecimal("100"), USD, COP) }
  }

  @Test
  fun `is loading while the conversion runs`() = runTest {
    val gate = CompletableDeferred<ConversionResult>()
    coEvery { convertCurrency(any(), any(), any()) } coAnswers { gate.await() }
    val viewModel = viewModel()
    viewModel.onEvent(CurrencyConverterUiEvent.AmountChanged("100"))
    viewModel.onEvent(CurrencyConverterUiEvent.TargetCurrencySelected(COP))

    viewModel.onEvent(CurrencyConverterUiEvent.ConvertClicked)
    assertEquals(CurrencyConverterUiStatus.Loading, viewModel.uiState.value.status)
    assertTrue(viewModel.uiState.value.isLoading)

    gate.complete(usdToCop)
    assertEquals(CurrencyConverterUiStatus.Success(usdToCop), viewModel.uiState.value.status)
  }

  @Test
  fun `refuses to convert before a target currency is chosen`() = runTest {
    val viewModel = viewModel()
    viewModel.onEvent(CurrencyConverterUiEvent.AmountChanged("100"))

    viewModel.onEvent(CurrencyConverterUiEvent.ConvertClicked)

    assertEquals(CurrencyConverterUiError.MissingTargetCurrency, viewModel.uiState.value.error)
    assertFalse(viewModel.uiState.value.isRetryable)
    coVerify(exactly = 0) { convertCurrency(any(), any(), any()) }
  }

  @Test
  fun `rejects an amount that is not a number without calling the use case`() = runTest {
    val viewModel = viewModel()
    viewModel.onEvent(CurrencyConverterUiEvent.TargetCurrencySelected(COP))

    viewModel.onEvent(CurrencyConverterUiEvent.AmountChanged("not a number"))
    viewModel.onEvent(CurrencyConverterUiEvent.ConvertClicked)

    assertEquals(CurrencyConverterUiError.InvalidAmount, viewModel.uiState.value.error)
    assertFalse(viewModel.uiState.value.isRetryable)
    coVerify(exactly = 0) { convertCurrency(any(), any(), any()) }
  }

  @Test
  fun `rejects a negative amount`() = runTest {
    val viewModel = viewModel()
    viewModel.onEvent(CurrencyConverterUiEvent.TargetCurrencySelected(COP))

    viewModel.onEvent(CurrencyConverterUiEvent.AmountChanged("-5"))
    viewModel.onEvent(CurrencyConverterUiEvent.ConvertClicked)

    assertEquals(CurrencyConverterUiError.InvalidAmount, viewModel.uiState.value.error)
    coVerify(exactly = 0) { convertCurrency(any(), any(), any()) }
  }

  @Test
  fun `reports a pair the provider does not cover`() = runTest {
    coEvery { convertCurrency(any(), any(), any()) } throws ExchangeRateUnavailableException(USD, COP)
    val viewModel = viewModel()
    viewModel.onEvent(CurrencyConverterUiEvent.AmountChanged("100"))
    viewModel.onEvent(CurrencyConverterUiEvent.TargetCurrencySelected(COP))

    viewModel.onEvent(CurrencyConverterUiEvent.ConvertClicked)

    assertEquals(
      CurrencyConverterUiError.RateUnavailable(CurrencyPair(USD, COP)),
      viewModel.uiState.value.error,
    )
  }

  @Test
  fun `reports an unreachable provider when the conversion cannot sync`() = runTest {
    coEvery { convertCurrency(any(), any(), any()) } throws ExchangeRateSyncException(USD)
    val viewModel = viewModel()
    viewModel.onEvent(CurrencyConverterUiEvent.AmountChanged("100"))
    viewModel.onEvent(CurrencyConverterUiEvent.TargetCurrencySelected(COP))

    viewModel.onEvent(CurrencyConverterUiEvent.ConvertClicked)

    assertEquals(CurrencyConverterUiError.RatesUnreachable, viewModel.uiState.value.error)
    assertTrue(viewModel.uiState.value.isRetryable)
  }

  @Test
  fun `never lets a technical failure through`() = runTest {
    val failure = IllegalStateException("Room is on fire")
    coEvery { convertCurrency(any(), any(), any()) } throws failure
    val viewModel = viewModel()
    viewModel.onEvent(CurrencyConverterUiEvent.AmountChanged("100"))
    viewModel.onEvent(CurrencyConverterUiEvent.TargetCurrencySelected(COP))

    viewModel.onEvent(CurrencyConverterUiEvent.ConvertClicked)

    assertEquals(CurrencyConverterUiError.Unexpected(failure), viewModel.uiState.value.error)
  }

  @Test
  fun `discards a conversion whose form changed while it was running`() = runTest {
    val gate = CompletableDeferred<ConversionResult>()
    coEvery { convertCurrency(any(), any(), any()) } coAnswers { gate.await() }
    val viewModel = viewModel()
    viewModel.onEvent(CurrencyConverterUiEvent.AmountChanged("100"))
    viewModel.onEvent(CurrencyConverterUiEvent.TargetCurrencySelected(COP))
    viewModel.onEvent(CurrencyConverterUiEvent.ConvertClicked)

    viewModel.onEvent(CurrencyConverterUiEvent.AmountChanged("250"))
    gate.complete(usdToCop)

    assertNull(viewModel.uiState.value.conversion)
    assertEquals(CurrencyConverterUiStatus.Idle, viewModel.uiState.value.status)
  }

  // -------------------------------------------------------------------------------------------
  // Rates refresh and retry
  // -------------------------------------------------------------------------------------------

  @Test
  fun `forces a sync when the user refreshes the rates`() = runTest {
    val refreshed = ExchangeRateSnapshot(USD, RATE_DATE, mapOf(EUR to BigDecimal("0.90")))
    coEvery { refreshRates(USD) } returns refreshed
    val viewModel = viewModel()

    viewModel.onEvent(CurrencyConverterUiEvent.RefreshRatesClicked)

    coVerify(exactly = 1) { refreshRates(USD) }
    assertEquals(listOf(EUR, USD), viewModel.uiState.value.availableCurrencies)
  }

  @Test
  fun `retrying a failed load asks for a sync`() = runTest {
    coEvery { getLatestRates(USD) } throws ExchangeRateSyncException(USD)
    val viewModel = viewModel()

    viewModel.onEvent(CurrencyConverterUiEvent.RetryClicked)

    coVerify(exactly = 1) { refreshRates(USD) }
    assertEquals(CurrencyConverterUiStatus.Idle, viewModel.uiState.value.status)
    assertEquals(listOf(COP, EUR, USD), viewModel.uiState.value.availableCurrencies)
  }

  @Test
  fun `retrying a failed conversion runs it again`() = runTest {
    coEvery { convertCurrency(any(), any(), any()) } throws ExchangeRateSyncException(USD)
    val viewModel = viewModel()
    viewModel.onEvent(CurrencyConverterUiEvent.AmountChanged("100"))
    viewModel.onEvent(CurrencyConverterUiEvent.TargetCurrencySelected(COP))
    viewModel.onEvent(CurrencyConverterUiEvent.ConvertClicked)

    coEvery { convertCurrency(BigDecimal("100"), USD, COP) } returns usdToCop
    viewModel.onEvent(CurrencyConverterUiEvent.RetryClicked)

    assertEquals(CurrencyConverterUiStatus.Success(usdToCop), viewModel.uiState.value.status)
  }

  @Test
  fun `ignores a retry when the failure is the user's input`() = runTest {
    val viewModel = viewModel()
    viewModel.onEvent(CurrencyConverterUiEvent.ConvertClicked)

    viewModel.onEvent(CurrencyConverterUiEvent.RetryClicked)

    assertEquals(CurrencyConverterUiError.MissingTargetCurrency, viewModel.uiState.value.error)
    coVerify(exactly = 0) { convertCurrency(any(), any(), any()) }
    coVerify(exactly = 0) { refreshRates(any()) }
  }

  // -------------------------------------------------------------------------------------------
  // Favorites
  // -------------------------------------------------------------------------------------------

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
  fun `saves the selected pair when it is not a favorite yet`() = runTest {
    val viewModel = viewModel()
    viewModel.onEvent(CurrencyConverterUiEvent.TargetCurrencySelected(COP))

    viewModel.onEvent(CurrencyConverterUiEvent.ToggleFavoriteClicked)

    coVerify(exactly = 1) { addFavoriteExchange(CurrencyPair(USD, COP)) }
    coVerify(exactly = 0) { removeFavoriteExchange(any()) }
  }

  @Test
  fun `unsaves the selected pair when it already is a favorite`() = runTest {
    favorites.value = listOf(FavoriteExchange(CurrencyPair(USD, COP)))
    val viewModel = viewModel()
    viewModel.onEvent(CurrencyConverterUiEvent.TargetCurrencySelected(COP))
    assertTrue(viewModel.uiState.value.isSelectedPairFavorite)

    viewModel.onEvent(CurrencyConverterUiEvent.ToggleFavoriteClicked)

    coVerify(exactly = 1) { removeFavoriteExchange(CurrencyPair(USD, COP)) }
    coVerify(exactly = 0) { addFavoriteExchange(any()) }
  }

  @Test
  fun `refuses to save a favorite before a target currency is chosen`() = runTest {
    val viewModel = viewModel()

    viewModel.onEvent(CurrencyConverterUiEvent.ToggleFavoriteClicked)

    assertEquals(CurrencyConverterUiError.MissingTargetCurrency, viewModel.uiState.value.error)
    coVerify(exactly = 0) { addFavoriteExchange(any()) }
  }

  @Test
  fun `keeps the conversion on screen while a favorite is saved`() = runTest {
    val viewModel = convertedViewModel()

    viewModel.onEvent(CurrencyConverterUiEvent.ToggleFavoriteClicked)

    assertEquals(CurrencyConverterUiStatus.Success(usdToCop), viewModel.uiState.value.status)
  }

  @Test
  fun `selecting a favorite fills the form with its pair`() = runTest {
    val favorite = FavoriteExchange(CurrencyPair(EUR, COP))
    favorites.value = listOf(favorite)
    val viewModel = convertedViewModel()

    viewModel.onEvent(CurrencyConverterUiEvent.FavoriteSelected(favorite))

    assertEquals(EUR, viewModel.uiState.value.baseCurrency)
    assertEquals(COP, viewModel.uiState.value.targetCurrency)
    assertNull(viewModel.uiState.value.conversion)
  }

  @Test
  fun `removing a favorite deletes its pair`() = runTest {
    val favorite = FavoriteExchange(CurrencyPair(EUR, COP))
    favorites.value = listOf(favorite)
    val viewModel = viewModel()

    viewModel.onEvent(CurrencyConverterUiEvent.FavoriteRemoved(favorite))

    coVerify(exactly = 1) { removeFavoriteExchange(CurrencyPair(EUR, COP)) }
  }

  @Test
  fun `marks the favorites unavailable when their stream fails, leaving the converter usable`() = runTest {
    every { getFavoriteExchanges() } returns flow<List<FavoriteExchange>> {
      throw IllegalStateException("the favorites table is gone")
    }

    val state = viewModel().uiState.value

    assertTrue(state.favoritesUnavailable)
    assertEquals(emptyList<FavoriteExchange>(), state.favorites)
    // The failure belongs to the favorites section only; the rest of the screen is untouched.
    assertEquals(CurrencyConverterUiStatus.Idle, state.status)
    assertEquals(listOf(COP, EUR, USD), state.availableCurrencies)
  }


  // -------------------------------------------------------------------------------------------
  // History
  // -------------------------------------------------------------------------------------------

  @Test
  fun `records a successful conversion to history`() = runTest {
    convertedViewModel()

    coVerify(exactly = 1) { saveConversionHistory(usdToCop) }
  }

  @Test
  fun `never records a conversion that failed`() = runTest {
    coEvery { convertCurrency(any(), any(), any()) } throws ExchangeRateUnavailableException(USD, COP)
    val viewModel = viewModel()
    viewModel.onEvent(CurrencyConverterUiEvent.AmountChanged("100"))
    viewModel.onEvent(CurrencyConverterUiEvent.TargetCurrencySelected(COP))

    viewModel.onEvent(CurrencyConverterUiEvent.ConvertClicked)

    coVerify(exactly = 0) { saveConversionHistory(any()) }
  }

  @Test
  fun `a failure to record history does not turn a successful conversion into an error`() = runTest {
    coEvery { saveConversionHistory(any()) } throws IllegalStateException("disk is full")

    val viewModel = convertedViewModel()

    assertEquals(CurrencyConverterUiStatus.Success(usdToCop), viewModel.uiState.value.status)
  }

  // -------------------------------------------------------------------------------------------
  // Set as current (hand-off from History)
  // -------------------------------------------------------------------------------------------

  @Test
  fun `applies a conversion handed off from another screen to the form`() = runTest {
    val viewModel = viewModel()

    pendingConversions.emit(PendingConversion("250.50", EUR, COP))

    assertEquals("250.50", viewModel.uiState.value.amount)
    assertEquals(EUR, viewModel.uiState.value.baseCurrency)
    assertEquals(COP, viewModel.uiState.value.targetCurrency)
  }

  @Test
  fun `a hand-off clears whatever the form was previously showing`() = runTest {
    val viewModel = convertedViewModel()
    assertEquals(usdToCop, viewModel.uiState.value.conversion)

    pendingConversions.emit(PendingConversion("250.50", EUR, COP))

    assertNull(viewModel.uiState.value.conversion)
    assertEquals(CurrencyConverterUiStatus.Idle, viewModel.uiState.value.status)
  }

  @Test
  fun `a hand-off does not run a conversion on its own`() = runTest {
    viewModel()

    pendingConversions.emit(PendingConversion("250.50", EUR, COP))

    coVerify(exactly = 0) { convertCurrency(any(), any(), any()) }
  }

  @Test
  fun `converting after a hand-off asks for the current rate, never the historical one`() = runTest {
    coEvery { convertCurrency(BigDecimal("250.50"), EUR, COP) } returns usdToCop
    val viewModel = viewModel()
    pendingConversions.emit(PendingConversion("250.50", EUR, COP))

    viewModel.onEvent(CurrencyConverterUiEvent.ConvertClicked)

    coVerify(exactly = 1) { convertCurrency(BigDecimal("250.50"), EUR, COP) }
  }

  // -------------------------------------------------------------------------------------------
  // State restoration
  // -------------------------------------------------------------------------------------------

  @Test
  fun `restores the form the user had filled in after the process is recreated`() = runTest {
    val savedStateHandle = SavedStateHandle()
    viewModel(savedStateHandle).apply {
      onEvent(CurrencyConverterUiEvent.AmountChanged("125.40"))
      onEvent(CurrencyConverterUiEvent.BaseCurrencySelected(EUR))
      onEvent(CurrencyConverterUiEvent.TargetCurrencySelected(COP))
    }

    val recreated = viewModel(savedStateHandle).uiState.value

    assertEquals("125.40", recreated.amount)
    assertEquals(EUR, recreated.baseCurrency)
    assertEquals(COP, recreated.targetCurrency)
  }

  @Test
  fun `does not restore a conversion, which would be re-derived from possibly newer rates`() = runTest {
    val savedStateHandle = SavedStateHandle()
    coEvery { convertCurrency(BigDecimal("100"), USD, COP) } returns usdToCop
    viewModel(savedStateHandle).apply {
      onEvent(CurrencyConverterUiEvent.AmountChanged("100"))
      onEvent(CurrencyConverterUiEvent.TargetCurrencySelected(COP))
      onEvent(CurrencyConverterUiEvent.ConvertClicked)
    }

    val recreated = viewModel(savedStateHandle).uiState.value

    assertNull(recreated.conversion)
  }

  @Test
  fun `loads the rates of the restored base currency, not of the default one`() = runTest {
    val savedStateHandle = SavedStateHandle()
    viewModel(savedStateHandle).onEvent(CurrencyConverterUiEvent.BaseCurrencySelected(EUR))

    viewModel(savedStateHandle)

    coVerify(exactly = 1) { getLatestRates(EUR) }
  }
}
