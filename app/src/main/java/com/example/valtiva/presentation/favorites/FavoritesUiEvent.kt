package com.example.valtiva.presentation.favorites

import com.example.valtiva.domain.model.FavoriteExchange

/**
 * Everything the favorites screen can ask the ViewModel to do.
 *
 * A single sealed hierarchy behind one `onEvent` entry point, mirroring
 * [com.example.valtiva.presentation.converter.CurrencyConverterUiEvent]: the screen says what the
 * user did, never what the ViewModel should do about it.
 */
sealed interface FavoritesUiEvent {

  /** The user deleted one of their favorites from the list. */
  data class FavoriteRemoved(val favorite: FavoriteExchange) : FavoritesUiEvent
}
