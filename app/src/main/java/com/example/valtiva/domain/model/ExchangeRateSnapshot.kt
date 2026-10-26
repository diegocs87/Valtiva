package com.example.valtiva.domain.model

import java.math.BigDecimal
import java.time.LocalDate

private const val RATES_SHOULD_BE_POSITIVE_MSG = "All rates in a snapshot must be strictly positive, but found "

private const val IMPLICIT_BASE_RATE_ERROR_MSG = "the base is implicit and always worth one."

/**
 * Every rate published for [baseCurrency] on [date].
 *
 * Rates are keyed by [Currency] rather than by raw `String` so an invalid or unnormalized code
 * cannot reach the snapshot, and so a lookup cannot silently miss because of letter case.
 *
 * The base currency must not appear inside [rates]: it is implicit and always worth exactly one, so
 * an entry for it means the mapping that produced this snapshot is wrong. Providers that do include
 * the base in their payload have to drop it while mapping. [rateFor] still answers for the base.
 */
data class ExchangeRateSnapshot(
  val baseCurrency: Currency,
  val date: LocalDate,
  val rates: Map<Currency, BigDecimal>,
) {
  init {
    validatePositiveRatesAndBaseNotIncluded()
  }

  /**
   * The [ExchangeRate] converting [baseCurrency] into [quote], or `null` when this snapshot does
   * not publish that currency. Returns a rate of one when [quote] is the base currency itself.
   */
  fun rateFor(quote: Currency): ExchangeRate? {
    val rate = if (quote == baseCurrency) BigDecimal.ONE else rates[quote]
    return rate?.let { ExchangeRate(baseCurrency, quote, it, date) }
  }

  private fun validatePositiveRatesAndBaseNotIncluded() {
    require(rates.values.all { it.signum() > 0 }) {
      RATES_SHOULD_BE_POSITIVE_MSG +
              "${rates.filterValues { it.signum() <= 0 }}."
    }
    require(baseCurrency !in rates) {
      "A snapshot must not publish a rate for its own base currency (${baseCurrency.code}); " +
              IMPLICIT_BASE_RATE_ERROR_MSG
    }
  }
}
