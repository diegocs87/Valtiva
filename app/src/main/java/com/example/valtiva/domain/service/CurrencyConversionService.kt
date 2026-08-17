package com.example.valtiva.domain.service

import com.example.valtiva.domain.model.ConversionResult
import com.example.valtiva.domain.model.Currency
import com.example.valtiva.domain.model.ExchangeRate
import com.example.valtiva.domain.model.MonetaryPrecision
import com.example.valtiva.domain.model.Money

/**
 * Applies an already resolved [ExchangeRate] to an amount.
 *
 * The service is deliberately pure: it never fetches, caches or persists anything, and it does not
 * decide *which* rate to use. Callers resolve the rate and hand it over, which keeps the monetary
 * arithmetic in one place and trivially testable.
 *
 * Precision and rounding follow [MonetaryPrecision]: the multiplication runs at 34 significant
 * digits and only the final amount is rounded to two decimals using banker's rounding.
 */
class CurrencyConversionService {

  /**
   * Converts [money] into [targetCurrency] using [rate].
   *
   * The rate must describe exactly the requested conversion, so its base has to match the currency
   * of [money] and its quote has to match [targetCurrency]; a mismatch is a programming error and
   * fails fast. Converting a currency into itself works through a rate of one and is not a special
   * case here.
   */
  fun convert(money: Money, targetCurrency: Currency, rate: ExchangeRate): ConversionResult {
    require(money.currency == rate.base) {
      "Rate ${rate.base.code}/${rate.quote.code} cannot convert an amount in ${money.currency.code}."
    }
    require(targetCurrency == rate.quote) {
      "Rate ${rate.base.code}/${rate.quote.code} does not produce ${targetCurrency.code}."
    }

    val convertedAmount = money.amount
      .multiply(rate.rate, MonetaryPrecision.MATH_CONTEXT)
      .setScale(MonetaryPrecision.SCALE, MonetaryPrecision.ROUNDING)

    return ConversionResult(
      source = money,
      target = Money(convertedAmount, targetCurrency),
      exchangeRate = rate.rate,
      rateDate = rate.date,
    )
  }
}
