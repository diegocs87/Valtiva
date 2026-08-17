package com.example.valtiva.domain.usecase

import com.example.valtiva.domain.model.Currency
import com.example.valtiva.domain.model.ExchangeRateSnapshot
import com.example.valtiva.domain.repository.ExchangeRateRepository
import java.time.LocalDate

/** Returns the rates published for a base currency on a given date. */
class GetHistoricalRatesUseCase(
  private val exchangeRateRepository: ExchangeRateRepository,
) {
  suspend operator fun invoke(
    baseCurrency: Currency,
    date: LocalDate,
  ): ExchangeRateSnapshot? = exchangeRateRepository.getRatesForDate(baseCurrency, date)
}
