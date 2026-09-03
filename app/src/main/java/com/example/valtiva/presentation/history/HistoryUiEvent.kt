package com.example.valtiva.presentation.history

import com.example.valtiva.domain.model.ConversionHistoryEntry

/**
 * Everything the history screen can ask the ViewModel to do.
 *
 * A single sealed hierarchy behind one `onEvent` entry point, mirroring
 * [com.example.valtiva.presentation.converter.CurrencyConverterUiEvent]: the screen says what the
 * user did, never what the ViewModel should do about it.
 */
sealed interface HistoryUiEvent {

  /** The user asked to load [entry] back onto the converter's form. */
  data class ConversionSelected(val entry: ConversionHistoryEntry) : HistoryUiEvent
}
