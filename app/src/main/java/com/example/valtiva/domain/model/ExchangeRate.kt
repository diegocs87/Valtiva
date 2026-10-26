package com.example.valtiva.domain.model

import java.math.BigDecimal
import java.time.LocalDate

data class ExchangeRate(
  val base: Currency,
  val quote: Currency,
  val rate: BigDecimal,
  val date: LocalDate,
) {
  init {
    validateAPositiveExchangeRate()
  }

  /** The ordered pair this rate applies to. */
  val pair: CurrencyPair
    get() = CurrencyPair(base, quote)

  private fun validateAPositiveExchangeRate() {
    require(rate.signum() > 0) {
      "Exchange rate for ${base.code}/${quote.code} must be strictly positive, but was $rate."
    }
  }
}
