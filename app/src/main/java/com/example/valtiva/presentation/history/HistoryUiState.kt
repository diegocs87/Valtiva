package com.example.valtiva.presentation.history

import com.example.valtiva.domain.model.ConversionHistoryEntry

/**
 * Everything the history screen renders, and nothing else.
 *
 * [isLoading] and [isUnavailable] are independent flags rather than a status hierarchy like
 * [com.example.valtiva.presentation.converter.CurrencyConverterUiStatus]: this screen runs no
 * operation of its own, it only observes one stream, so there is no in-flight action whose outcome
 * could conflict with another. Empty is not a flag — it is simply [conversions] being empty once
 * loading has finished and the stream has not failed, which is exactly what [HistoryScreen] checks.
 */
data class HistoryUiState(
  val conversions: List<ConversionHistoryEntry> = emptyList(),
  val isLoading: Boolean = true,
  val isUnavailable: Boolean = false,
)
