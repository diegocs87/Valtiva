package com.example.valtiva.domain.usecase

import com.example.valtiva.domain.model.Currency
import com.example.valtiva.domain.model.ExchangeRateSnapshot
import com.example.valtiva.domain.repository.ExchangeRateRepository

/**
 * Forces the latest rates for a base currency to be refreshed from the source of truth.
 *
 * Meant to be triggered both by an explicit user action and, later on, by a background worker.
 */
class RefreshRatesUseCase(
  private val exchangeRateRepository: ExchangeRateRepository,
) {
  suspend operator fun invoke(baseCurrency: Currency): ExchangeRateSnapshot =
    exchangeRateRepository.refreshLatestRates(baseCurrency)
}
