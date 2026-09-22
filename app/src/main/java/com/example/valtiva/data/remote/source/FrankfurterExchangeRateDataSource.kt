package com.example.valtiva.data.remote.source

import com.example.valtiva.data.remote.api.FrankfurterApi
import com.example.valtiva.data.remote.dto.FrankfurterRateDto
import com.example.valtiva.data.remote.mapper.toDomain
import com.example.valtiva.data.source.RemoteExchangeRateDataSource
import com.example.valtiva.domain.exception.ExchangeRateSyncException
import com.example.valtiva.domain.model.Currency
import com.example.valtiva.domain.model.ExchangeRate
import java.time.LocalDate
import kotlin.coroutines.cancellation.CancellationException
import javax.inject.Inject

/**
 * [RemoteExchangeRateDataSource] backed by Frankfurter. DTOs never leave this class.
 *
 * Self-rates are dropped here: the provider includes `USD -> USD = 1` when no quote filter is
 * given, and the domain treats a currency's rate against itself as implicit, never as data.
 */
class FrankfurterExchangeRateDataSource @Inject constructor(
  private val api: FrankfurterApi,
) : RemoteExchangeRateDataSource {

  override suspend fun getLatestRates(baseCurrency: Currency): List<ExchangeRate> =
    fetch(baseCurrency) { api.getLatestRates(base = baseCurrency.code) }

  override suspend fun getHistoricalRates(baseCurrency: Currency, date: LocalDate): List<ExchangeRate> =
    fetch(baseCurrency) { api.getHistoricalRates(base = baseCurrency.code, date = date.toString()) }

  /**
   * Transport failures and HTTP status errors mean the sync did not happen and are funnelled into
   * [ExchangeRateSyncException] with the original failure as the cause, so no caller ever handles
   * Retrofit types. Coroutine cancellation is not a failure and is rethrown untouched.
   *
   * A single quote the domain rejects (e.g. a precious metal whose rate rounds to zero against a
   * low-value base) does not fail the whole sync: the response covers every published currency, so
   * one bad entry would otherwise mask a fully usable set of rates as a connectivity error. That
   * entry is dropped instead.
   */
  @Suppress("TooGenericExceptionCaught")
  private suspend fun fetch(
    base: Currency,
    request: suspend () -> List<FrankfurterRateDto>,
  ): List<ExchangeRate> = try {
    request()
      .mapNotNull { dto -> runCatching { dto.toDomain() }.getOrNull() }
      .filterNot { it.base == it.quote }
  } catch (cancellation: CancellationException) {
    throw cancellation
  } catch (failure: Exception) {
    throw ExchangeRateSyncException(base, failure)
  }
}
