package com.example.valtiva.domain.usecase

import com.example.valtiva.domain.model.ConversionHistoryEntry
import com.example.valtiva.domain.repository.ConversionHistoryRepository
import kotlinx.coroutines.flow.Flow

/** Observes the conversions the user has performed, most recent first. */
class GetConversionHistoryUseCase(
  private val conversionHistoryRepository: ConversionHistoryRepository,
) {
  operator fun invoke(): Flow<List<ConversionHistoryEntry>> =
    conversionHistoryRepository.getConversionHistory()
}
