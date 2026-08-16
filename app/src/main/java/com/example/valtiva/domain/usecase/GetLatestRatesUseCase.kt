package com.example.valtiva.domain.usecase

import com.example.valtiva.domain.model.Currency
import com.example.valtiva.domain.model.ExchangeRateSnapshot
import com.example.valtiva.domain.repository.ExchangeRateRepository

/** Returns the latest rates known for a base currency, or `null` when none are available yet. */
class GetLatestRatesUseCase(
  private val exchangeRateRepository: ExchangeRateRepository,
) {
  suspend operator fun invoke(baseCurrency: Currency): ExchangeRateSnapshot? =
    exchangeRateRepository.getLatestRates(baseCurrency)
}
