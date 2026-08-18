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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Offline-first orchestration of the rate sources.
 *
 * The local store is what the app reads; the remote provider only exists to fill it. Every
 * operation checks local first and touches the network only when local cannot satisfy the request,
 * so converting 100, 1000 and 2000 USD in a row costs one rate lookup, not three HTTP calls.
 *
 * Freshness is date-based, never clock-delta-based: rates are daily, so "current" means published
 * on today's date. When today's rates are missing and the provider has not published them yet, the
 * caller is told explicitly (a `null`, or an honestly dated snapshot from [refreshLatestRates]) —
 * an older rate is never dressed up as today's.
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
class ExchangeRateRepositoryImpl(
  private val localDataSource: LocalExchangeRateDataSource,
  private val remoteDataSource: RemoteExchangeRateDataSource,
  private val clock: Clock = Clock.systemDefaultZone(),
) : ExchangeRateRepository {

  private val syncMutex = Mutex()

  override suspend fun getLatestRates(baseCurrency: Currency): ExchangeRateSnapshot? {
    val today = LocalDate.now(clock)
    val local = localDataSource.getRatesForDate(baseCurrency, today)
    if (local.isNotEmpty()) return snapshotOrNull(baseCurrency, today, local)

    val fetched = syncLatest(baseCurrency, today)
    return snapshotOrNull(baseCurrency, today, fetched.filter { it.date == today })
  }

  override suspend fun getRatesForDate(baseCurrency: Currency, date: LocalDate): ExchangeRateSnapshot? {
    val local = localDataSource.getRatesForDate(baseCurrency, date)
    if (local.isNotEmpty()) return snapshotOrNull(baseCurrency, date, local)

    val fetched = remoteDataSource.getHistoricalRates(baseCurrency, date)
    localDataSource.saveRates(fetched)
    return snapshotOrNull(baseCurrency, date, fetched.filter { it.date == date })
  }

  override suspend fun refreshLatestRates(baseCurrency: Currency): ExchangeRateSnapshot {
    val today = LocalDate.now(clock)
    val rates = syncLatest(baseCurrency, today)
    val newestDate = rates.maxOfOrNull { it.date }
      ?: throw ExchangeRateSyncException(
        base = baseCurrency,
        message = "The provider returned no rates at all for ${baseCurrency.code}.",
      )
    return ExchangeRateSnapshot(
      baseCurrency = baseCurrency,
      date = newestDate,
      rates = rates.filter { it.date == newestDate }.associate { it.quote to it.rate },
    )
  }

  /**
   * Returns today's rates from the local store, fetching and persisting the provider's latest
   * batch first when they are missing. The local re-check inside the lock is what makes repeated
   * and concurrent refreshes idempotent: once today's rates are stored, no further network call
   * happens that day.
   */
  private suspend fun syncLatest(baseCurrency: Currency, today: LocalDate): List<ExchangeRate> =
    syncMutex.withLock {
      val local = localDataSource.getRatesForDate(baseCurrency, today)
      if (local.isNotEmpty()) return@withLock local

      val fetched = remoteDataSource.getLatestRates(baseCurrency)
      localDataSource.saveRates(fetched)
      fetched
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
