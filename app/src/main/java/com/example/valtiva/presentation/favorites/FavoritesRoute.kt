package com.example.valtiva.presentation.favorites

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * Wires [FavoritesViewModel] to [FavoritesScreen].
 *
 * The only place in this feature that knows a ViewModel exists, the same role
 * [com.example.valtiva.presentation.converter.CurrencyConverterRoute] plays for the converter
 * screen: obtain it through Hilt, observe [FavoritesViewModel.uiState] and forward events back
 * through [FavoritesViewModel.onEvent]. Everything below it is plain state in, events out.
 */
@Composable
fun FavoritesRoute(
  modifier: Modifier = Modifier,
  viewModel: FavoritesViewModel = hiltViewModel(),
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  FavoritesScreen(
    state = uiState,
    onEvent = viewModel::onEvent,
    modifier = modifier,
  )
}
