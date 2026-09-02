package com.example.valtiva.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.valtiva.domain.model.FavoriteExchange
import com.example.valtiva.domain.usecase.GetFavoriteExchangesUseCase
import com.example.valtiva.domain.usecase.RemoveFavoriteExchangeUseCase
import com.example.valtiva.presentation.favorites.FavoritesUiEvent.FavoriteRemoved
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Drives the favorites screen.
 *
 * Only use cases are injected, the same two [com.example.valtiva.presentation.converter.CurrencyConverterViewModel]
 * already depends on: this screen adds no favorite of its own, it only observes and removes them.
 * No new business rule, model or repository is introduced for it.
 */
@HiltViewModel
class FavoritesViewModel @Inject constructor(
  private val getFavoriteExchanges: GetFavoriteExchangesUseCase,
  private val removeFavoriteExchange: RemoveFavoriteExchangeUseCase,
) : ViewModel() {

  private val _uiState = MutableStateFlow(FavoritesUiState())

  /** The single source of truth for the screen. Read-only: the UI changes it through events only. */
  val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

  init {
    observeFavorites()
  }

  /** The screen's only way in. */
  fun onEvent(event: FavoritesUiEvent) {
    when (event) {
      is FavoriteRemoved -> removeFavorite(event.favorite)
    }
  }

  /**
   * Keeps the favorites in the state for as long as the screen lives.
   *
   * A failure of the stream marks the favorites as unavailable rather than being surfaced as a
   * crash or swallowed silently, mirroring how the converter screen treats the same stream.
   */
  private fun observeFavorites() {
    viewModelScope.launch {
      getFavoriteExchanges()
        .catch { _uiState.update { it.copy(unavailable = true) } }
        .collect { favorites -> _uiState.update { it.copy(favorites = favorites) } }
    }
  }

  private fun removeFavorite(favorite: FavoriteExchange) {
    viewModelScope.launch { removeFavoriteExchange(favorite.pair) }
  }
}
