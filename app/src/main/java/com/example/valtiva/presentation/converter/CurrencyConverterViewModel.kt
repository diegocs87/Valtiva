package com.example.valtiva.presentation.converter

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.valtiva.domain.exception.ExchangeRateSyncException
import com.example.valtiva.domain.exception.ExchangeRateUnavailableException
import com.example.valtiva.domain.model.Currency
import com.example.valtiva.domain.model.CurrencyPair
import com.example.valtiva.domain.model.ExchangeRateSnapshot
import com.example.valtiva.domain.model.FavoriteExchange
import com.example.valtiva.domain.usecase.AddFavoriteExchangeUseCase
import com.example.valtiva.domain.usecase.ConvertCurrencyUseCase
import com.example.valtiva.domain.usecase.GetFavoriteExchangesUseCase
import com.example.valtiva.domain.usecase.GetLatestRatesUseCase
import com.example.valtiva.domain.usecase.RefreshRatesUseCase
import com.example.valtiva.domain.usecase.RemoveFavoriteExchangeUseCase
import com.example.valtiva.presentation.converter.CurrencyConverterUiEvent.AmountChanged
import com.example.valtiva.presentation.converter.CurrencyConverterUiEvent.BaseCurrencySelected
import com.example.valtiva.presentation.converter.CurrencyConverterUiEvent.ConvertClicked
import com.example.valtiva.presentation.converter.CurrencyConverterUiEvent.FavoriteRemoved
import com.example.valtiva.presentation.converter.CurrencyConverterUiEvent.FavoriteSelected
import com.example.valtiva.presentation.converter.CurrencyConverterUiEvent.RefreshRatesClicked
import com.example.valtiva.presentation.converter.CurrencyConverterUiEvent.RetryClicked
import com.example.valtiva.presentation.converter.CurrencyConverterUiEvent.SwapCurrenciesClicked
import com.example.valtiva.presentation.converter.CurrencyConverterUiEvent.TargetCurrencySelected
import com.example.valtiva.presentation.converter.CurrencyConverterUiEvent.ToggleFavoriteClicked
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigDecimal
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val KEY_AMOUNT = "converter.amount"
private const val KEY_BASE_CURRENCY = "converter.baseCurrency"
private const val KEY_TARGET_CURRENCY = "converter.targetCurrency"

/**
 * Drives the currency converter screen.
 *
 * Its whole job is translation: a [CurrencyConverterUiEvent] comes in, the matching use case is
 * invoked, and whatever comes back — a [com.example.valtiva.domain.model.ConversionResult], a
 * snapshot, an exception — is turned into the next [CurrencyConverterUiState]. It computes nothing
 * of its own: not a rate, not a converted amount, not what "today" means, not whether an answer
 * should come from the cache. Those decisions live in the domain and the repositories, and this
 * class deliberately stays too thin to duplicate them.
 *
 * Only use cases are injected. There is no repository, no DAO and no Retrofit service in sight, so
 * the dependency arrow only ever points at the domain.
 *
 * ### Two jobs, one status
 * Rate loading and conversion are tracked by separate jobs so that starting one never silently
 * kills the other, and each cancels only its own predecessor: a second `ConvertClicked` replaces
 * the first conversion rather than racing it. Because both report through a single
 * [CurrencyConverterUiStatus], a conversion whose form has changed while it was running is
 * discarded on arrival instead of overwriting the screen with an answer to a question the user no
 * longer asked.
 *
 * ### What is missing from the domain
 * The currency picker is filled from the snapshot's own quotes, which is the only list reachable
 * through a use case today. The API's currency catalogue (names and symbols) exists in the data
 * layer but no domain contract exposes it, and inventing one here would put a data concern in the
 * presentation layer, so the picker shows ISO codes until that contract exists.
 */
@HiltViewModel
class CurrencyConverterViewModel @Inject constructor(
  private val convertCurrency: ConvertCurrencyUseCase,
  private val getLatestRates: GetLatestRatesUseCase,
  private val refreshRates: RefreshRatesUseCase,
  private val getFavoriteExchanges: GetFavoriteExchangesUseCase,
  private val addFavoriteExchange: AddFavoriteExchangeUseCase,
  private val removeFavoriteExchange: RemoveFavoriteExchangeUseCase,
  private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

  private val _uiState = MutableStateFlow(restoredState())

  /** The single source of truth for the screen. Read-only: the UI changes it through events only. */
  val uiState: StateFlow<CurrencyConverterUiState> = _uiState.asStateFlow()

  private var ratesJob: Job? = null
  private var conversionJob: Job? = null

  init {
    observeFavorites()
    loadRates { base -> getLatestRates(base) }
  }

  /** The screen's only way in. */
  fun onEvent(event: CurrencyConverterUiEvent) {
    when (event) {
      is AmountChanged -> onAmountChanged(event.amount)
      is BaseCurrencySelected -> onCurrenciesSelected(event.currency, _uiState.value.targetCurrency)
      is TargetCurrencySelected -> onCurrenciesSelected(_uiState.value.baseCurrency, event.currency)
      is FavoriteSelected -> onCurrenciesSelected(event.favorite.pair.base, event.favorite.pair.quote)
      is FavoriteRemoved -> removeFavorite(event.favorite)
      ConvertClicked -> convert()
      SwapCurrenciesClicked -> swapCurrencies()
      ToggleFavoriteClicked -> toggleFavorite()
      RefreshRatesClicked -> loadRates { base -> refreshRates(base) }
      RetryClicked -> retry()
    }
  }

  // ---------------------------------------------------------------------------------------------
  // Form
  // ---------------------------------------------------------------------------------------------

  private fun onAmountChanged(amount: String) {
    savedStateHandle[KEY_AMOUNT] = amount
    _uiState.update { it.copy(amount = amount).withoutStaleOutcome() }
  }

  private fun onCurrenciesSelected(base: Currency, target: Currency?) {
    savedStateHandle[KEY_BASE_CURRENCY] = base.code
    savedStateHandle[KEY_TARGET_CURRENCY] = target?.code
    _uiState.update {
      it.copy(baseCurrency = base, targetCurrency = target).withoutStaleOutcome()
    }
  }

  private fun swapCurrencies() {
    val state = _uiState.value
    // Nothing to swap with while the target is unset; silently ignoring beats inventing a failure.
    val target = state.targetCurrency ?: return
    onCurrenciesSelected(base = target, target = state.baseCurrency)
  }

  /**
   * Drops a result or a failure that the edit just made obsolete. A running operation is left
   * alone: it is the one that decides, on arrival, whether its answer still matches the form.
   */
  private fun CurrencyConverterUiState.withoutStaleOutcome(): CurrencyConverterUiState =
    if (status is CurrencyConverterUiStatus.Loading) this else copy(status = CurrencyConverterUiStatus.Idle)

  // ---------------------------------------------------------------------------------------------
  // Rates
  // ---------------------------------------------------------------------------------------------

  /**
   * Brings in the rates for the current base currency with [load] and republishes the currencies
   * the user can pick from.
   *
   * The two callers differ only in that lambda: the initial load reads what is already known, while
   * the refresh forces a sync. Both go through a use case; neither knows what "sync" involves.
   */
  private fun loadRates(load: suspend (Currency) -> ExchangeRateSnapshot?) {
    ratesJob?.cancel()
    ratesJob = viewModelScope.launch {
      val base = _uiState.value.baseCurrency
      _uiState.update { it.copy(status = CurrencyConverterUiStatus.Loading) }

      val status = statusFor(RetryOperation.LOAD_RATES) {
        when (val snapshot = load(base)) {
          null -> CurrencyConverterUiStatus.Error(
            error = CurrencyConverterUiError.RatesNotPublished(base),
            retry = RetryOperation.LOAD_RATES,
          )
          else -> {
            _uiState.update { it.copy(availableCurrencies = snapshot.selectableCurrencies()) }
            CurrencyConverterUiStatus.Idle
          }
        }
      }
      _uiState.update { it.copy(status = status) }
    }
  }

  /**
   * The currencies the form can offer, taken from the snapshot itself: its quotes are exactly the
   * currencies a conversion from this base can produce, and the base belongs in the list too since
   * the snapshot never publishes a rate for itself. Sorted by code because a picker needs an order
   * and the domain has no opinion about one.
   */
  private fun ExchangeRateSnapshot.selectableCurrencies(): List<Currency> =
    (rates.keys + baseCurrency).sortedBy { it.code }

  // ---------------------------------------------------------------------------------------------
  // Conversion
  // ---------------------------------------------------------------------------------------------

  private fun convert() {
    val requested = _uiState.value
    val target = requested.targetCurrency ?: return fail(CurrencyConverterUiError.MissingTargetCurrency)
    val amount = parseAmount(requested.amount) ?: return fail(CurrencyConverterUiError.InvalidAmount)

    conversionJob?.cancel()
    conversionJob = viewModelScope.launch {
      _uiState.update { it.copy(status = CurrencyConverterUiStatus.Loading) }

      val status = statusFor(RetryOperation.CONVERT) {
        CurrencyConverterUiStatus.Success(convertCurrency(amount, requested.baseCurrency, target))
      }
      _uiState.update { current ->
        // The form may have moved on while the use case was running; an answer to the previous
        // question must not be presented as an answer to the current one.
        if (current.describesSameConversionAs(requested)) {
          current.copy(status = status)
        } else {
          current.copy(status = CurrencyConverterUiStatus.Idle)
        }
      }
    }
  }

  private fun CurrencyConverterUiState.describesSameConversionAs(other: CurrencyConverterUiState): Boolean =
    amount == other.amount && baseCurrency == other.baseCurrency && targetCurrency == other.targetCurrency

  /**
   * Reads the typed text as an amount, or returns `null` when it is not one.
   *
   * Parsing text is a presentation concern — the domain takes a [BigDecimal] and never sees a
   * keyboard — so it belongs here rather than in a use case. Only the syntax is judged: the domain
   * still owns what a valid amount *means*, and its own rejection is mapped in [toUiError].
   *
   * Known limitation: the parse is locale-independent, so a comma decimal separator is refused.
   * Locale-aware input is a UI concern that arrives with the text field.
   */
  private fun parseAmount(text: String): BigDecimal? =
    text.trim().toBigDecimalOrNull()?.takeIf { it.signum() >= 0 }

  // ---------------------------------------------------------------------------------------------
  // Favorites
  // ---------------------------------------------------------------------------------------------

  /**
   * Keeps the favorites in the state for as long as the screen lives.
   *
   * Favorites are owned by the repository, not by this class: saving one does not update the list
   * here, it writes through the use case and the flow re-emits. That is why adding and removing
   * need no state handling of their own.
   *
   * A failure of the stream marks the favorites as unavailable and nothing else. It deliberately
   * does not become the screen's [CurrencyConverterUiStatus]: this stream lives for as long as the
   * screen does, so letting it claim that slot would mean it either erases a conversion the user is
   * looking at, or gets erased itself by the next operation.
   */
  private fun observeFavorites() {
    viewModelScope.launch {
      getFavoriteExchanges()
        .catch { _uiState.update { it.copy(favoritesUnavailable = true) } }
        // `catch` ends the stream, so there is no recovery to undo the flag afterwards.
        .collect { favorites -> _uiState.update { it.copy(favorites = favorites) } }
    }
  }

  private fun toggleFavorite() {
    val state = _uiState.value
    val pair = state.selectedPair ?: return fail(CurrencyConverterUiError.MissingTargetCurrency)
    val isFavorite = state.isSelectedPairFavorite

    viewModelScope.launch {
      val status = statusFor(retry = null) {
        if (isFavorite) removeFavoriteExchange(pair) else addFavoriteExchange(pair)
        // A favorite write is local and instant, and the list arrives through the flow, so there is
        // nothing to report: whatever the screen was showing stays.
        _uiState.value.status
      }
      _uiState.update { it.copy(status = status) }
    }
  }

  private fun removeFavorite(favorite: FavoriteExchange) {
    viewModelScope.launch {
      val status = statusFor(retry = null) {
        removeFavoriteExchange(favorite.pair)
        _uiState.value.status
      }
      _uiState.update { it.copy(status = status) }
    }
  }

  // ---------------------------------------------------------------------------------------------
  // Failures
  // ---------------------------------------------------------------------------------------------

  private fun retry() {
    when ((_uiState.value.status as? CurrencyConverterUiStatus.Error)?.retry) {
      // A read already failed, so ask for an actual sync rather than the same read again.
      RetryOperation.LOAD_RATES -> loadRates { base -> refreshRates(base) }
      RetryOperation.CONVERT -> convert()
      null -> Unit
    }
  }

  private fun fail(error: CurrencyConverterUiError, retry: RetryOperation? = null) {
    _uiState.update { it.copy(status = CurrencyConverterUiStatus.Error(error, retry)) }
  }

  /**
   * Runs [block] and converts any failure into an [CurrencyConverterUiStatus.Error] carrying a
   * presentation error, so no exception ever escapes into the UI or crashes the app.
   */
  @Suppress("TooGenericExceptionCaught")
  private suspend fun statusFor(
    retry: RetryOperation?,
    block: suspend () -> CurrencyConverterUiStatus,
  ): CurrencyConverterUiStatus =
    try {
      block()
    } catch (cancellation: CancellationException) {
      // Cancellation is how coroutines shut down, not a failure to show anyone.
      throw cancellation
    } catch (failure: Exception) {
      // Deliberately broad: an unforeseen failure has to become a message on screen, never a crash.
      CurrencyConverterUiStatus.Error(failure.toUiError(), retry)
    }

  /**
   * The single place where a lower-layer failure becomes something the screen can talk about.
   *
   * The domain exceptions are told apart because the screen reacts differently to each: a pair that
   * is not covered is a dead end, while an unreachable provider is worth another try. Everything
   * else collapses into [CurrencyConverterUiError.Unexpected], which keeps the cause for logging
   * and shows the user a generic message.
   */
  private fun Throwable.toUiError(): CurrencyConverterUiError = when (this) {
    is ExchangeRateUnavailableException -> CurrencyConverterUiError.RateUnavailable(CurrencyPair(base, quote))
    is ExchangeRateSyncException -> CurrencyConverterUiError.RatesUnreachable
    // The domain rejects an amount it considers invalid; that is the user's input, not a defect.
    is IllegalArgumentException -> CurrencyConverterUiError.InvalidAmount
    else -> CurrencyConverterUiError.Unexpected(this)
  }

  // ---------------------------------------------------------------------------------------------
  // State restoration
  // ---------------------------------------------------------------------------------------------

  /**
   * Rebuilds the form from [SavedStateHandle] so a process death does not wipe what the user typed.
   *
   * Only the form is saved, and only as plain strings. The rest is left out on purpose: the
   * currencies and the favorites are re-read from the repositories, which own them, and a
   * conversion result is derived data that would be re-computed anyway — restoring it risks showing
   * yesterday's rate as today's answer.
   */
  private fun restoredState() = CurrencyConverterUiState(
    amount = savedStateHandle[KEY_AMOUNT] ?: "",
    baseCurrency = restoredCurrency(savedStateHandle[KEY_BASE_CURRENCY])
      ?: CurrencyConverterUiState.DEFAULT_BASE_CURRENCY,
    targetCurrency = restoredCurrency(savedStateHandle[KEY_TARGET_CURRENCY]),
    // The screen is created loading: `init` asks for the rates straight away.
    status = CurrencyConverterUiStatus.Loading,
  )

  private fun restoredCurrency(code: String?): Currency? =
    code?.let { runCatching { Currency.of(it) }.getOrNull() }
}
