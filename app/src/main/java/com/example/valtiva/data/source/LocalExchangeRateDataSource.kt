package com.example.valtiva.data.source

import com.example.valtiva.domain.model.Currency
import com.example.valtiva.domain.model.ExchangeRate
import java.time.LocalDate

/**
 * Locally stored rates, spoken entirely in domain types.
 *
 * This is the seam that keeps Room out of the repository: the implementation owns the entity
 * mapping and the DAO, and nothing above it ever sees an entity. The surface is intentionally
 * limited to what the repository actually uses — new operations are added when a flow needs them,
 * not in advance.
 */
interface LocalExchangeRateDataSource {

  /** Every stored rate for [baseCurrency] published on [date]; empty when that day is not stored. */
  suspend fun getRatesForDate(baseCurrency: Currency, date: LocalDate): List<ExchangeRate>

  /**
   * For [baseCurrency], the most recently stored rate for each quote currency independently — one
   * entry per quote, each carrying its own date. Unlike [getRatesForDate], quotes are not required
   * to share a date: a currency published less often than others still comes back at its own last
   * known rate instead of being left out.
   */
  suspend fun getLatestRatesPerQuote(baseCurrency: Currency): List<ExchangeRate>

  /**
   * Persists [rates] as one atomic batch: either every rate lands or none does, so a failed sync
   * can never leave a day half-written. Re-saving an already stored (base, quote, date) replaces it.
   */
  suspend fun saveRates(rates: List<ExchangeRate>)
}
