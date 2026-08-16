package com.example.valtiva.domain.model

import java.math.BigDecimal
import java.time.LocalDate

/**
 * Every rate published for [baseCurrency] on [date].
 *
 * Rates are keyed by [Currency] rather than by raw `String` so an invalid or unnormalized code
 * cannot reach the snapshot, and so a lookup cannot silently miss because of letter case.
 *
 * Rate providers do not repeat the base currency inside their payload; [rateFor] still answers for
 * it, because one unit of a currency is always worth exactly one unit of itself.
 */
data class ExchangeRateSnapshot(
  val baseCurrency: Currency,
  val date: LocalDate,
  val rates: Map<Currency, BigDecimal>,
) {
  init {
    require(rates.values.all { it.signum() > 0 }) {
      "All rates in a snapshot must be strictly positive, but found " +
        "${rates.filterValues { it.signum() <= 0 }}."
    }
  }

  /**
   * The [ExchangeRate] converting [baseCurrency] into [quote], or `null` when this snapshot does
   * not publish that currency. Returns a rate of one when [quote] is the base currency itself.
   */
  fun rateFor(quote: Currency): ExchangeRate? {
    val rate = if (quote == baseCurrency) BigDecimal.ONE else rates[quote]
    return rate?.let { ExchangeRate(baseCurrency, quote, it, date) }
  }
}
