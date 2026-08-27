package com.example.valtiva.presentation.converter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * Wires [CurrencyConverterViewModel] to [CurrencyConverterScreen].
 *
 * The only place in this feature that knows a ViewModel exists: it obtains one through Hilt,
 * observes [CurrencyConverterViewModel.uiState] and forwards events back through
 * [CurrencyConverterViewModel.onEvent]. Everything below it is plain state in, events out.
 */
@Composable
fun CurrencyConverterRoute(
  modifier: Modifier = Modifier,
  viewModel: CurrencyConverterViewModel = hiltViewModel(),
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  CurrencyConverterScreen(
    state = uiState,
    onEvent = viewModel::onEvent,
    modifier = modifier,
  )
}
