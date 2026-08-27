package com.example.valtiva.data.local.source

import com.example.valtiva.data.local.dao.ExchangeRateDao
import com.example.valtiva.data.local.mapper.toDomain
import com.example.valtiva.data.local.mapper.toEntity
import com.example.valtiva.data.source.LocalExchangeRateDataSource
import com.example.valtiva.domain.model.Currency
import com.example.valtiva.domain.model.ExchangeRate
import java.time.LocalDate
import javax.inject.Inject

/**
 * [LocalExchangeRateDataSource] backed by Room. Entities never leave this class.
 *
 * Atomicity of [saveRates] comes from Room itself: a single `@Insert` taking a list runs inside one
 * transaction, so a batch either lands completely or not at all — no explicit transaction plumbing
 * is needed here.
 */
class RoomExchangeRateDataSource @Inject constructor(
  private val dao: ExchangeRateDao,
) : LocalExchangeRateDataSource {

  override suspend fun getRatesForDate(baseCurrency: Currency, date: LocalDate): List<ExchangeRate> =
    dao.getRatesForDate(baseCurrency.code, date).map { it.toDomain() }

  override suspend fun getLatestRatesPerQuote(baseCurrency: Currency): List<ExchangeRate> =
    dao.getLatestRatesPerQuote(baseCurrency.code).map { it.toDomain() }

  override suspend fun saveRates(rates: List<ExchangeRate>) {
    dao.insertAll(rates.map { it.toEntity() })
  }
}
