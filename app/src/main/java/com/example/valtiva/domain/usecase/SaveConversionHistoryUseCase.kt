package com.example.valtiva.domain.usecase

import com.example.valtiva.domain.model.ConversionResult
import com.example.valtiva.domain.repository.ConversionHistoryRepository
import java.time.Clock
import java.time.Instant

/**
 * Records a conversion that just completed successfully.
 *
 * The timestamp is resolved here from an injected [Clock], the same way [ExchangeRateRepositoryImpl]
 * resolves "today", rather than by the caller passing [Instant.now]: a ViewModel reading the system
 * clock directly would be untestable and would duplicate a concern this app already centralizes.
 */
class SaveConversionHistoryUseCase(
  private val conversionHistoryRepository: ConversionHistoryRepository,
  private val clock: Clock,
) {
  suspend operator fun invoke(conversion: ConversionResult) {
    conversionHistoryRepository.saveConversion(conversion, Instant.now(clock))
  }
}
