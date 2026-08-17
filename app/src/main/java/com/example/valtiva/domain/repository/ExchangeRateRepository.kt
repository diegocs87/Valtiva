package com.example.valtiva.domain.repository

import com.example.valtiva.domain.model.Currency
import com.example.valtiva.domain.model.ExchangeRateSnapshot
import java.time.LocalDate

/**
 * Access to exchange rates.
 *
 * The domain states what it needs and stays unaware of where the rates come from: a remote
 * provider, a local cache, or any combination of both is an implementation concern of the data
 * layer.
 */
interface ExchangeRateRepository {

  /**
   * The most recent rates known for [baseCurrency], or `null` when none are available yet.
   * Implementations may answer from a cache without hitting the network.
   */
  suspend fun getLatestRates(baseCurrency: Currency): ExchangeRateSnapshot?

  /**
   * The rates published for [baseCurrency] on [date], or `null` when that day is not available.
   */
  suspend fun getRatesForDate(baseCurrency: Currency, date: LocalDate): ExchangeRateSnapshot?

  /**
   * Forces the latest rates for [baseCurrency] to be fetched from the source of truth and returns
   * them. Unlike [getLatestRates] this is expected to bypass any cache.
   */
  suspend fun refreshLatestRates(baseCurrency: Currency): ExchangeRateSnapshot
}
