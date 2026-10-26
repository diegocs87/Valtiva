package com.example.valtiva.domain.repository

import com.example.valtiva.domain.exception.ExchangeRateSyncException
import com.example.valtiva.domain.model.Currency
import com.example.valtiva.domain.model.ExchangeRateSnapshot
import java.time.LocalDate


interface ExchangeRateRepository {

  suspend fun getLatestRates(baseCurrency: Currency): ExchangeRateSnapshot?
  suspend fun getRatesForDate(baseCurrency: Currency, date: LocalDate): ExchangeRateSnapshot?
  suspend fun refreshLatestRates(baseCurrency: Currency): ExchangeRateSnapshot
}
