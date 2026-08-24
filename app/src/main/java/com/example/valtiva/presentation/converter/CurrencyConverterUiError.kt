package com.example.valtiva.presentation.converter

import com.example.valtiva.domain.model.Currency
import com.example.valtiva.domain.model.CurrencyPair

/**
 * Everything that can go wrong on the converter screen, expressed in the terms the screen cares
 * about.
 *
 * This type exists so the domain and data exceptions stop at the ViewModel. `IOException`,
 * `HttpException` or a Room failure say nothing to a user, and reusing a domain exception as UI
 * state would tie the wording shown on screen to the layer that raised it: changing a message would
 * mean touching the domain. The mapping is one-way — the domain never learns about this type.
 *
 * No case carries a message. The text is resolved by the UI through `stringResource`, so the same
 * error can read differently per locale without any change here. Cases carry *data* instead
 * ([RateUnavailable] carries the pair, [RatesNotPublished] the base currency) precisely so those
 * strings can be formatted with the concrete currencies involved.
 */
sealed interface CurrencyConverterUiError {

  /** The typed amount is not a usable number: blank, malformed, or negative. */
  data object InvalidAmount : CurrencyConverterUiError

  /** A conversion or a favorite was requested before a target currency had been chosen. */
  data object MissingTargetCurrency : CurrencyConverterUiError

  /**
   * No rate is published for [pair]: the data we have simply does not cover it. This is a fact
   * about coverage, not a transient failure, so retrying the same pair would fail again.
   */
  data class RateUnavailable(val pair: CurrencyPair) : CurrencyConverterUiError

  /**
   * The provider has published nothing for [baseCurrency] on the current day yet. Distinct from
   * [RatesUnreachable]: the request worked, there is just no data for today.
   */
  data class RatesNotPublished(val baseCurrency: Currency) : CurrencyConverterUiError

  /**
   * The rates could not be brought in from the source of truth — offline, provider down, or an
   * unusable payload. The only error that is genuinely worth retrying as-is.
   */
  data object RatesUnreachable : CurrencyConverterUiError

  /**
   * A failure the presentation layer did not anticipate. [cause] is kept for logging and crash
   * reporting only; it must never reach the screen.
   */
  data class Unexpected(val cause: Throwable? = null) : CurrencyConverterUiError
}
