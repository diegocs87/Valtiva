package com.example.valtiva.presentation.history

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * Wires [HistoryViewModel] to [HistoryScreen].
 *
 * The only place in this feature that knows a ViewModel exists, the same role
 * [com.example.valtiva.presentation.converter.CurrencyConverterRoute] plays for the converter
 * screen. [onConversionSelected] is a plain navigation callback, not screen state: it is how the
 * home shell — which owns the tab pager — learns to bring the converter tab into view once a
 * conversion has been handed to it, without this feature needing to know a pager exists.
 */
@Composable
fun HistoryRoute(
  modifier: Modifier = Modifier,
  onConversionSelected: () -> Unit = {},
  viewModel: HistoryViewModel = hiltViewModel(),
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  HistoryScreen(
    state = uiState,
    onEvent = { event ->
      viewModel.onEvent(event)
      if (event is HistoryUiEvent.ConversionSelected) onConversionSelected()
    },
    modifier = modifier,
  )
}
