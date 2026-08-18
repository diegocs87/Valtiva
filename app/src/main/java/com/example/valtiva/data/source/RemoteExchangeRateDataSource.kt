package com.example.valtiva.data.source

import com.example.valtiva.domain.exception.ExchangeRateSyncException
import com.example.valtiva.domain.model.Currency
import com.example.valtiva.domain.model.ExchangeRate
import java.time.LocalDate

/**
 * The rates provider, spoken entirely in domain types.
 *
 * This is the seam that keeps Retrofit and the provider's wire format out of the repository: the
 * implementation owns the API client, the DTOs and their mapping, and translates every transport
 * failure into [ExchangeRateSyncException] so callers never handle HTTP.
 */
interface RemoteExchangeRateDataSource {

  /**
   * The latest published rate for each currency quoted against [baseCurrency]. Entries carry their
   * own dates and a response can legitimately span several days: each currency comes with the last
   * day it was published.
   *
   * @throws ExchangeRateSyncException when the provider cannot be reached or answers unusably.
   */
  suspend fun getLatestRates(baseCurrency: Currency): List<ExchangeRate>

  /**
   * The rates for [baseCurrency] published on [date]; empty when the provider has none for that
   * day. Unlike [getLatestRates], every entry shares [date].
   *
   * @throws ExchangeRateSyncException when the provider cannot be reached or answers unusably.
   */
  suspend fun getHistoricalRates(baseCurrency: Currency, date: LocalDate): List<ExchangeRate>
}
