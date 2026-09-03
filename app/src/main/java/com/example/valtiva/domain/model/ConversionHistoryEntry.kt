package com.example.valtiva.domain.model

import java.time.Instant

/**
 * A conversion the user actually performed, kept for the history screen.
 *
 * Wraps [conversion] rather than re-declaring its amounts, currencies and rate: a history entry is
 * exactly that outcome, plus [performedAt] — the real moment the user ran it. That is distinct from
 * [ConversionResult.rateDate], which is when the *rate* was published, not when the conversion was
 * made; a rate published today can still back a conversion performed at any time today.
 *
 * [id] identifies an already persisted entry (assigned by storage). Saving a new conversion never
 * needs one, which is why [com.example.valtiva.domain.repository.ConversionHistoryRepository.saveConversion]
 * takes the pieces directly instead of a whole entry.
 */
data class ConversionHistoryEntry(
  val id: Long,
  val conversion: ConversionResult,
  val performedAt: Instant,
)
