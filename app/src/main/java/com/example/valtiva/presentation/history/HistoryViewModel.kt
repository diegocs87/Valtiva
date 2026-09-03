package com.example.valtiva.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.valtiva.domain.model.ConversionHistoryEntry
import com.example.valtiva.domain.usecase.GetConversionHistoryUseCase
import com.example.valtiva.presentation.converter.ConverterHandoffPublisher
import com.example.valtiva.presentation.converter.PendingConversion
import com.example.valtiva.presentation.history.HistoryUiEvent.ConversionSelected
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Drives the history screen.
 *
 * Depends on [ConverterHandoffPublisher] — an interface, not [com.example.valtiva.presentation.converter.CurrencyConverterViewModel]
 * — to hand a selected conversion to the converter. This ViewModel never learns whether anything is
 * listening, let alone how the converter's own state is shaped; that boundary is what lets the two
 * screens change independently of each other.
 */
@HiltViewModel
class HistoryViewModel @Inject constructor(
  private val getConversionHistory: GetConversionHistoryUseCase,
  private val converterHandoff: ConverterHandoffPublisher,
) : ViewModel() {

  private val _uiState = MutableStateFlow(HistoryUiState())

  /** The single source of truth for the screen. Read-only: the UI changes it through events only. */
  val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

  init {
    observeHistory()
  }

  /** The screen's only way in. */
  fun onEvent(event: HistoryUiEvent) {
    when (event) {
      is ConversionSelected -> setAsCurrent(event.entry)
    }
  }

  private fun observeHistory() {
    viewModelScope.launch {
      getConversionHistory()
        .catch { _uiState.update { it.copy(isLoading = false, isUnavailable = true) } }
        .collect { entries -> _uiState.update { it.copy(conversions = entries, isLoading = false) } }
    }
  }

  private fun setAsCurrent(entry: ConversionHistoryEntry) {
    val conversion = entry.conversion
    converterHandoff.requestConversion(
      PendingConversion(
        amount = conversion.source.amount.toPlainString(),
        baseCurrency = conversion.source.currency,
        targetCurrency = conversion.target.currency,
      ),
    )
  }
}
