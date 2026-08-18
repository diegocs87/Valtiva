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
   * Every way the provider interaction can fail — transport, HTTP status, malformed payload, a
   * value the domain rejects — means the same thing to callers: the sync did not happen. They are
   * all funnelled into [ExchangeRateSyncException] with the original failure as the cause, so no
   * caller ever handles Retrofit or serialization types. Coroutine cancellation is not a failure
   * and is rethrown untouched.
   */
  @Suppress("TooGenericExceptionCaught")
  private suspend fun fetch(
    base: Currency,
    request: suspend () -> List<FrankfurterRateDto>,
  ): List<ExchangeRate> = try {
    request().map { it.toDomain() }.filterNot { it.base == it.quote }
  } catch (cancellation: CancellationException) {
    throw cancellation
  } catch (failure: Exception) {
    throw ExchangeRateSyncException(base, failure)
  }
}
