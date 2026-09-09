package com.example.valtiva.presentation.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * Wires [SettingsViewModel] to [SettingsScreen].
 *
 * The only place in this feature that knows a ViewModel exists, the same role
 * [com.example.valtiva.presentation.favorites.FavoritesRoute] plays for the favorites screen.
 */
@Composable
fun SettingsRoute(
  modifier: Modifier = Modifier,
  viewModel: SettingsViewModel = hiltViewModel(),
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  SettingsScreen(
    state = uiState,
    onEvent = viewModel::onEvent,
    modifier = modifier,
  )
}
