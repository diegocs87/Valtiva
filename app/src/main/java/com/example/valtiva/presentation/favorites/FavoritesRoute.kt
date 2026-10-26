package com.example.valtiva.presentation.favorites

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle


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
