package com.example.valtiva.presentation.converter

import com.example.valtiva.domain.model.ConversionResult
import com.example.valtiva.domain.model.Currency
import com.example.valtiva.domain.model.CurrencyPair
import com.example.valtiva.domain.model.FavoriteExchange

/**
 * Everything the converter screen renders, and nothing else.
 *
 * The state splits in two on purpose. The *form* — [amount], [baseCurrency], [targetCurrency] — is
 * what the user typed and picked, and it stays put no matter what any operation is doing. The
 * *outcome* of the current operation lives in [status], where loading, a result and a failure are
 * mutually exclusive by construction, so the screen can never be told to show a spinner over a
 * stale result and an error at the same time.
 *
 * The domain models travel as they are ([ConversionResult], [Currency], [FavoriteExchange]):
 * presentation is allowed to depend on the domain, and re-wrapping them would buy nothing. What is
 * deliberately absent is formatting — amounts are not turned into display strings here, because
 * doing that needs a `Locale` and a currency format, which belong to the composable, not to a
 * ViewModel that has to stay testable on a plain JVM.
 *
 * The favorites are the one thing kept outside [status]. They are a stream the screen observes
 * rather than an operation the user started, and the converter works perfectly well without them,
 * so a failure to read them degrades that one section ([favoritesUnavailable]) instead of taking
 * over a screen that is busy converting.
 *
 * Nothing about infrastructure appears here: no connectivity flag, no "reading from cache", no
 * "calling the API". Whether an answer came from Room or from the network changes nothing this
 * screen draws, and the app deliberately runs no connectivity monitor.
 */
data class CurrencyConverterUiState(
  val amount: String = "",
  val baseCurrency: Currency = DEFAULT_BASE_CURRENCY,
  val targetCurrency: Currency? = null,
  val availableCurrencies: List<Currency> = emptyList(),
  val favorites: List<FavoriteExchange> = emptyList(),
  val favoritesUnavailable: Boolean = false,
  val status: CurrencyConverterUiStatus = CurrencyConverterUiStatus.Idle,
) {

  /** The pair the form currently describes, or `null` while no target currency is chosen. */
  val selectedPair: CurrencyPair?
    get() = targetCurrency?.let { CurrencyPair(baseCurrency, it) }

  /** Whether [selectedPair] is already saved, which is what a favorite toggle renders. */
  val isSelectedPairFavorite: Boolean
    get() = selectedPair?.let { pair -> favorites.any { it.pair == pair } } == true

  /** Whether an operation is running, for spinners and to disable the form. */
  val isLoading: Boolean
    get() = status is CurrencyConverterUiStatus.Loading

  /** The conversion to display, or `null` when the form has not produced one yet. */
  val conversion: ConversionResult?
    get() = (status as? CurrencyConverterUiStatus.Success)?.conversion

  /** The failure to display, or `null` when nothing failed. */
  val error: CurrencyConverterUiError?
    get() = (status as? CurrencyConverterUiStatus.Error)?.error

  /** Whether the failure on screen, if any, is worth offering a retry for. */
  val isRetryable: Boolean
    get() = (status as? CurrencyConverterUiStatus.Error)?.retry != null

  /** Whether the form holds enough to ask for a conversion. */
  val canConvert: Boolean
    get() = !isLoading && targetCurrency != null && amount.isNotBlank()

  companion object {
    /**
     * The currency the form starts on.
     *
     * A presentation default, not a business rule: the screen needs *some* base to ask for a first
     * set of rates, and the app has neither a stored preference nor a domain contract that names
     * one. Once either exists, this constant is what it replaces.
     */
    val DEFAULT_BASE_CURRENCY: Currency = Currency.of("USD")
  }
}

/**
 * What the converter screen is doing right now.
 *
 * A sealed status rather than parallel `isLoading` / `error` / `result` fields, because those three
 * allow eight combinations of which only four mean anything, and the meaningless ones are exactly
 * the bugs that reach users. Here the compiler enumerates the states for the UI's `when`.
 *
 * The screen runs one operation at a time, so a single status covers all of them: loading the
 * currencies, refreshing the rates and converting. A consequence worth stating plainly — starting a
 * rate refresh drops the conversion currently on screen, which is intended, since a result computed
 * with rates that are being replaced is exactly the thing not to keep showing.
 */
sealed interface CurrencyConverterUiStatus {

  /** Nothing is running and there is nothing to report; the form is simply waiting for input. */
  data object Idle : CurrencyConverterUiStatus

  /** An operation is in flight. */
  data object Loading : CurrencyConverterUiStatus

  /** The last conversion succeeded and produced [conversion]. */
  data class Success(val conversion: ConversionResult) : CurrencyConverterUiStatus

  /**
   * The last operation failed with [error]. [retry] names the operation to run again when the user
   * asks, or is `null` when retrying makes no sense — a malformed amount fails identically however
   * many times it is sent.
   */
  data class Error(
    val error: CurrencyConverterUiError,
    val retry: RetryOperation? = null,
  ) : CurrencyConverterUiStatus
}

/** The operations a failed attempt can be resumed with. */
enum class RetryOperation {
  /** Bring the rates for the current base currency in again. */
  LOAD_RATES,

  /** Run the conversion the form describes again. */
  CONVERT,
}
