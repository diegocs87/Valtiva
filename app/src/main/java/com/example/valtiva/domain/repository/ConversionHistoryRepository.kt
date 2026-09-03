package com.example.valtiva.domain.repository

import com.example.valtiva.domain.model.ConversionHistoryEntry
import com.example.valtiva.domain.model.ConversionResult
import java.time.Instant
import kotlinx.coroutines.flow.Flow

/**
 * Storage of the conversions the user actually performed.
 *
 * Reads are exposed as a [Flow] so the history screen keeps observing changes, while saving is a
 * one-shot suspending operation — the same split [FavoriteExchangeRepository] uses.
 *
 * Keeping at most the most recent entries is a storage guarantee this contract makes, not something
 * a caller has to ask for: [getConversionHistory] never grows without bound.
 */
interface ConversionHistoryRepository {

  /** Emits the stored conversions, most recent first, and re-emits whenever they change. */
  fun getConversionHistory(): Flow<List<ConversionHistoryEntry>>

  /** Records that [conversion] was performed at [performedAt]. */
  suspend fun saveConversion(conversion: ConversionResult, performedAt: Instant)
}
