package com.example.valtiva.data.local.source

import com.example.valtiva.data.local.dao.ConversionHistoryDao
import com.example.valtiva.data.local.mapper.toDomain
import com.example.valtiva.data.local.mapper.toEntity
import com.example.valtiva.data.source.LocalConversionHistoryDataSource
import com.example.valtiva.domain.model.ConversionHistoryEntry
import com.example.valtiva.domain.model.ConversionResult
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * [LocalConversionHistoryDataSource] backed by Room. Entities never leave this class; the DAO
 * enforces the size cap, so this class has no bookkeeping of its own to do.
 */
class RoomConversionHistoryDataSource @Inject constructor(
  private val dao: ConversionHistoryDao,
) : LocalConversionHistoryDataSource {

  override fun getHistory(): Flow<List<ConversionHistoryEntry>> =
    dao.observeHistory().map { entities -> entities.map { it.toDomain() } }

  override suspend fun save(conversion: ConversionResult, performedAt: Instant) {
    dao.insertAndTrim(conversion.toEntity(performedAt))
  }
}
