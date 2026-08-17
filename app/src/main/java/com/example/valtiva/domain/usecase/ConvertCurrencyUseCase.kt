package com.example.valtiva.domain.usecase

import com.example.valtiva.domain.exception.ExchangeRateUnavailableException
import com.example.valtiva.domain.model.ConversionResult
import com.example.valtiva.domain.model.Currency
import com.example.valtiva.domain.model.Money
import com.example.valtiva.domain.repository.ExchangeRateRepository
import com.example.valtiva.domain.service.CurrencyConversionService
import java.math.BigDecimal

/**
 * Converts an amount between two currencies.
 *
 * Coordinates the two collaborators so callers never have to: the repository resolves which rate
 * applies, and [CurrencyConversionService] performs the arithmetic.
 *
 * Only direct rates are supported: the rate must be published with [source] as its base. Deriving
 * a cross rate (for example EUR to COP via USD) is intentionally out of scope until the way
 * snapshots are stored is settled, since doing it wrong compounds rounding error.
 */
class ConvertCurrencyUseCase(
  private val exchangeRateRepository: ExchangeRateRepository,
  private val conversionService: CurrencyConversionService,
) {

  /**
   * @throws IllegalArgumentException when [amount] is negative.
   * @throws ExchangeRateUnavailableException when no rate is published for the requested pair.
   */
  suspend operator fun invoke(
    amount: BigDecimal,
    source: Currency,
    target: Currency,
  ): ConversionResult {
    val money = Money(amount, source)
    val snapshot = exchangeRateRepository.getLatestRates(source)
      ?: throw ExchangeRateUnavailableException(source, target)
    val rate = snapshot.rateFor(target)
      ?: throw ExchangeRateUnavailableException(source, target)
    return conversionService.convert(money, target, rate)
  }
}
