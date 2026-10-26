package com.example.valtiva.domain.model

import java.time.Instant

data class ConversionHistoryEntry(
  val id: Long,
  val conversion: ConversionResult,
  val performedAt: Instant,
)
