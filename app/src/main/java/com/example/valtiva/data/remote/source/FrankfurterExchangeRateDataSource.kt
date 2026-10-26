package com.example.valtiva.data.remote.source

import com.example.valtiva.data.remote.api.FrankfurterApi
import com.example.valtiva.data.remote.dto.APIRateDto
import com.example.valtiva.data.remote.mapper.toDomain
import com.example.valtiva.data.source.RemoteExchangeRateDataSource
import com.example.valtiva.domain.exception.ExchangeRateSyncException
import com.example.valtiva.domain.model.Currency
import com.example.valtiva.domain.model.ExchangeRate
import java.time.LocalDate
import kotlin.coroutines.cancellation.CancellationException
import javax.inject.Inject

class FrankfurterExchangeRateDataSource @Inject constructor(
  private val api: FrankfurterApi,
) : RemoteExchangeRateDataSource {

  override suspend fun getLatestRates(baseCurrency: Currency): List<ExchangeRate> =
    fetch(baseCurrency) { api.getLatestRates(base = baseCurrency.code) }

  override suspend fun getHistoricalRates(baseCurrency: Currency, date: LocalDate): List<ExchangeRate> =
    fetch(baseCurrency) { api.getHistoricalRates(base = baseCurrency.code, date = date.toString()) }

  @Suppress("TooGenericExceptionCaught")
  private suspend fun fetch(
      base: Currency,
      request: suspend () -> List<APIRateDto>,
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
