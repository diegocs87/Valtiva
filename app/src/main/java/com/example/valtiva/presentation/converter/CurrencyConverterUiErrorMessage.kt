package com.example.valtiva.presentation.converter

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.valtiva.R

/**
 * Resolves a [CurrencyConverterUiError] into the text the screen shows.
 *
 * Kept as its own `@Composable` function, rather than a plain method on the error type, because
 * resolving a string needs [stringResource] and the error type lives in a module with no Android
 * dependency.
 */
@Composable
fun CurrencyConverterUiError.asMessage(): String = when (this) {
  CurrencyConverterUiError.InvalidAmount -> stringResource(R.string.error_invalid_amount)
  CurrencyConverterUiError.MissingTargetCurrency -> stringResource(R.string.error_missing_target_currency)
  is CurrencyConverterUiError.RateUnavailable ->
    stringResource(R.string.error_rate_unavailable, pair.base.code, pair.quote.code)
  is CurrencyConverterUiError.RatesNotPublished ->
    stringResource(R.string.error_rates_not_published, baseCurrency.code)
  CurrencyConverterUiError.RatesUnreachable -> stringResource(R.string.error_rates_unreachable)
  is CurrencyConverterUiError.Unexpected -> stringResource(R.string.error_unexpected)
}
