package com.example.valtiva.data.repository

import com.example.valtiva.data.source.LocalConversionHistoryDataSource
import com.example.valtiva.domain.model.ConversionHistoryEntry
import com.example.valtiva.domain.model.ConversionResult
import com.example.valtiva.domain.repository.ConversionHistoryRepository
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * The history is purely local: no remote source, no freshness rules, so — like
 * [FavoriteExchangeRepositoryImpl] — this is a thin pass-through over the local data source. The
 * value is the boundary, not the logic.
 */
class ConversionHistoryRepositoryImpl @Inject constructor(
  private val localDataSource: LocalConversionHistoryDataSource,
) : ConversionHistoryRepository {

  override fun getConversionHistory(): Flow<List<ConversionHistoryEntry>> = localDataSource.getHistory()

  override suspend fun saveConversion(conversion: ConversionResult, performedAt: Instant) {
    localDataSource.save(conversion, performedAt)
  }
}
