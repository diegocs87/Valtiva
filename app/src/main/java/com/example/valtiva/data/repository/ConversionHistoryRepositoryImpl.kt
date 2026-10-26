package com.example.valtiva.data.repository

import com.example.valtiva.data.source.LocalConversionHistoryDataSource
import com.example.valtiva.domain.model.ConversionHistoryEntry
import com.example.valtiva.domain.model.ConversionResult
import com.example.valtiva.domain.repository.ConversionHistoryRepository
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ConversionHistoryRepositoryImpl @Inject constructor(
  private val localDataSource: LocalConversionHistoryDataSource,
) : ConversionHistoryRepository {

  override fun getConversionHistory(): Flow<List<ConversionHistoryEntry>> = localDataSource.getHistory()

  override suspend fun saveConversion(conversion: ConversionResult, performedAt: Instant) {
    localDataSource.save(conversion, performedAt)
  }
}
