package com.example.valtiva.data.repository

import com.example.valtiva.data.source.LocalExchangeRateDataSource
import com.example.valtiva.data.source.RemoteExchangeRateDataSource
import com.example.valtiva.domain.exception.ExchangeRateSyncException
import com.example.valtiva.domain.model.Currency
import com.example.valtiva.domain.model.ExchangeRate
import com.example.valtiva.domain.model.ExchangeRateSnapshot
import com.example.valtiva.domain.repository.ExchangeRateRepository
import java.time.Clock
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Offline-first orchestration of the rate sources.
 *
 * The local store is what the app reads; the remote provider only exists to fill it. Every
 * operation checks local first and touches the network only when local cannot satisfy the request,
 * so converting 100, 1000 and 2000 USD in a row costs one rate lookup, not three HTTP calls.
 *
 * Freshness is resolved per currency, not per snapshot: the provider does not publish every quote
 * on the same cadence, so "latest rates for USD" legitimately mixes a quote refreshed minutes ago
 * with one whose last publication was days earlier. [getLatestRates] and [refreshLatestRates]
 * therefore report each quote at its own most recent known rate rather than requiring every quote
 * in the snapshot to share one date; the snapshot's own [ExchangeRateSnapshot.date] is only a label
 * for "as of the newest information we have," not a claim that every quote inside it is that fresh.
 * [getRatesForDate] is the one exception, and deliberately so: asking for an exact historical day
 * must not silently substitute a different day for a quote that was not published on it.
 *
 * A single [Mutex] serializes fetch-and-persist, and the local store is re-checked inside the lock,
 * so concurrent callers that miss the cache at the same time produce one network call, not several.
 * The [clock] is injectable to keep "today" out of the system's hands in tests; note that "today"
 * is the device's calendar day, which can briefly disagree with the provider's publication timezone
 * around midnight — acceptable for daily rates and not worth a timezone protocol.
 *
 * No connectivity monitor is used, deliberately: knowing the device is online would not change any
 * decision here (local data is used when valid either way, and a request can fail even when a
 * monitor says online). The failure itself is the signal — the remote source translates it into
 * [ExchangeRateSyncException] carrying the original cause, which is enough to distinguish offline
 * from server trouble if the UI ever wants to.
 */
class ExchangeRateRepositoryImpl @Inject constructor(
  private val localDataSource: LocalExchangeRateDataSource,
  private val remoteDataSource: RemoteExchangeRateDataSource,
  private val clock: Clock,
) : ExchangeRateRepository {

  private val syncMutex = Mutex()

  override suspend fun getLatestRates(baseCurrency: Currency): ExchangeRateSnapshot? {
    ensureSyncedToday(baseCurrency)
    return latestSnapshotOrNull(baseCurrency)
  }

  override suspend fun getRatesForDate(baseCurrency: Currency, date: LocalDate): ExchangeRateSnapshot? {
    val local = localDataSource.getRatesForDate(baseCurrency, date)
    if (local.isNotEmpty()) return snapshotOrNull(baseCurrency, date, local)

    val fetched = remoteDataSource.getHistoricalRates(baseCurrency, date)
    localDataSource.saveRates(fetched)
    return snapshotOrNull(baseCurrency, date, fetched.filter { it.date == date })
  }

  override suspend fun refreshLatestRates(baseCurrency: Currency): ExchangeRateSnapshot {
    ensureSyncedToday(baseCurrency)
    return latestSnapshotOrNull(baseCurrency)
      ?: throw ExchangeRateSyncException(
        base = baseCurrency,
        message = "The provider returned no rates at all for ${baseCurrency.code}.",
      )
  }

  /**
   * Fetches and persists the provider's latest batch for [baseCurrency] when today's rates are not
   * stored yet. The local re-check inside the lock is what makes repeated and concurrent calls
   * idempotent: once today's rates are stored, no further network call happens that day — by either
   * [getLatestRates] or [refreshLatestRates], which share this same sync.
   */
  private suspend fun ensureSyncedToday(baseCurrency: Currency) {
    val today = LocalDate.now(clock)
    if (localDataSource.getRatesForDate(baseCurrency, today).isNotEmpty()) return

    syncMutex.withLock {
      if (localDataSource.getRatesForDate(baseCurrency, today).isNotEmpty()) return@withLock
      localDataSource.saveRates(remoteDataSource.getLatestRates(baseCurrency))
    }
  }

  /** Builds a snapshot from each quote's own latest stored rate, or `null` when none is stored. */
  private suspend fun latestSnapshotOrNull(baseCurrency: Currency): ExchangeRateSnapshot? {
    val rates = localDataSource.getLatestRatesPerQuote(baseCurrency)
    val newestDate = rates.maxOfOrNull { it.date } ?: return null
    return ExchangeRateSnapshot(baseCurrency, newestDate, rates.associate { it.quote to it.rate })
  }

  private fun snapshotOrNull(
    baseCurrency: Currency,
    date: LocalDate,
    rates: List<ExchangeRate>,
  ): ExchangeRateSnapshot? = when {
    rates.isEmpty() -> null
    else -> ExchangeRateSnapshot(
      baseCurrency = baseCurrency,
      date = date,
      rates = rates.associate { it.quote to it.rate },
    )
  }
}
