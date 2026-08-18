package com.example.valtiva.domain.repository

import com.example.valtiva.domain.exception.ExchangeRateSyncException
import com.example.valtiva.domain.model.Currency
import com.example.valtiva.domain.model.ExchangeRateSnapshot
import java.time.LocalDate

/**
 * Access to exchange rates.
 *
 * The domain states what it needs and stays unaware of where the rates come from: a remote
 * provider, a local cache, or any combination of both is an implementation concern of the data
 * layer.
 *
 * Rates are daily, and the app never passes an older rate off as today's: a snapshot always carries
 * the date its rates were actually published, and "latest" means *published for the current day*.
 * Showing an older rate as a deliberate fallback would be a separate, explicit operation.
 */
interface ExchangeRateRepository {

  /**
   * The rates published for [baseCurrency] **on the current day**, or `null` when the provider has
   * not published today's rates yet. Implementations answer from local data when it already covers
   * today and only then reach out; a `null` therefore means "not published anywhere yet", never
   * "not cached".
   *
   * @throws ExchangeRateSyncException when today's rates are not stored locally and the source of
   * truth cannot be reached — an older cached rate is *not* returned in its place.
   */
  suspend fun getLatestRates(baseCurrency: Currency): ExchangeRateSnapshot?

  /**
   * The rates published for [baseCurrency] on [date], or `null` when the provider has no rates for
   * that day. Days already stored locally are answered without touching the network.
   *
   * @throws ExchangeRateSyncException when the day is not stored locally and the source of truth
   * cannot be reached.
   */
  suspend fun getRatesForDate(baseCurrency: Currency, date: LocalDate): ExchangeRateSnapshot?

  /**
   * Brings the latest rates for [baseCurrency] into local storage and returns the freshest snapshot
   * available afterwards — dated today when the provider already published today, or the newest
   * published day otherwise (the snapshot's date says which; nothing is disguised as current).
   *
   * Idempotent per day: once today's rates are stored locally, further calls return them without
   * touching the network, so repeated refreshes are safe and cheap.
   *
   * @throws ExchangeRateSyncException when the source of truth cannot be reached or returns no
   * rates at all.
   */
  suspend fun refreshLatestRates(baseCurrency: Currency): ExchangeRateSnapshot
}
