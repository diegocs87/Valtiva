package com.example.valtiva.domain.model

import java.math.BigDecimal
import java.time.LocalDate

/**
 * The value of one unit of [base] expressed in [quote] on a given [date].
 *
 * `ExchangeRate(USD, COP, 3980.25, 2026-08-14)` reads as `1 USD = 3980.25 COP on 2026-08-14`.
 *
 * The rate must be strictly positive. A negative rate is meaningless, and a rate of zero would
 * claim the base currency is worthless; both signal corrupt data, so they are rejected here
 * instead of silently producing a zero conversion further downstream.
 */
data class ExchangeRate(
  val base: Currency,
  val quote: Currency,
  val rate: BigDecimal,
  val date: LocalDate,
) {
  init {
    require(rate.signum() > 0) {
      "Exchange rate for ${base.code}/${quote.code} must be strictly positive, but was $rate."
    }
  }

  /** The ordered pair this rate applies to. */
  val pair: CurrencyPair
    get() = CurrencyPair(base, quote)
}
