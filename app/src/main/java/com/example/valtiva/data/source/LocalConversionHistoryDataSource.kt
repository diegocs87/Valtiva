package com.example.valtiva.data.source

import com.example.valtiva.domain.model.ConversionHistoryEntry
import com.example.valtiva.domain.model.ConversionResult
import java.time.Instant
import kotlinx.coroutines.flow.Flow

/**
 * Locally stored conversion history, spoken entirely in domain types. The implementation owns the
 * entity mapping and the DAO; nothing above it sees an entity.
 */
interface LocalConversionHistoryDataSource {

  /** Emits the stored conversions, most recent first, and re-emits on every change. */
  fun getHistory(): Flow<List<ConversionHistoryEntry>>

  /** Stores [conversion], performed at [performedAt]. */
  suspend fun save(conversion: ConversionResult, performedAt: Instant)
}
