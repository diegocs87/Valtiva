package com.example.valtiva.presentation.converter

import com.example.valtiva.domain.model.Currency
import com.example.valtiva.domain.model.FavoriteExchange

/**
 * Everything the converter screen can ask the ViewModel to do.
 *
 * A single sealed hierarchy behind one `onEvent` entry point instead of a public method per action:
 * the set of interactions is then closed and exhaustively handled by the `when` inside the
 * ViewModel, so adding an interaction cannot be silently forgotten. It also keeps the ViewModel's
 * surface to exactly two members — `uiState` to observe and `onEvent` to act — which is all a
 * composable ever needs to know about it.
 *
 * Events describe *what the user did*, never what the ViewModel should do about it: the screen says
 * "convert was clicked", not "run the conversion use case".
 */
sealed interface CurrencyConverterUiEvent {

  /** The user edited the amount field. Carries the raw text, still unparsed. */
  data class AmountChanged(val amount: String) : CurrencyConverterUiEvent

  /** The user picked the currency to convert from. */
  data class BaseCurrencySelected(val currency: Currency) : CurrencyConverterUiEvent

  /** The user picked the currency to convert into. */
  data class TargetCurrencySelected(val currency: Currency) : CurrencyConverterUiEvent

  /** The user asked for the conversion to run. */
  data object ConvertClicked : CurrencyConverterUiEvent

  /** The user asked to exchange the base and target currencies. */
  data object SwapCurrenciesClicked : CurrencyConverterUiEvent

  /** The user asked to save or unsave the currently selected pair. */
  data object ToggleFavoriteClicked : CurrencyConverterUiEvent

  /** The user picked one of their favorites, which selects that pair on the form. */
  data class FavoriteSelected(val favorite: FavoriteExchange) : CurrencyConverterUiEvent

  /** The user deleted one of their favorites from the list. */
  data class FavoriteRemoved(val favorite: FavoriteExchange) : CurrencyConverterUiEvent

  /** The user asked for the rates of the current base currency to be brought up to date. */
  data object RefreshRatesClicked : CurrencyConverterUiEvent

  /** The user asked to run the operation that just failed again. */
  data object RetryClicked : CurrencyConverterUiEvent
}
