package com.example.valtiva.presentation.favorites

import com.example.valtiva.domain.model.FavoriteExchange

/**
 * Everything the favorites screen renders, and nothing else.
 *
 * This is deliberately smaller than [com.example.valtiva.presentation.converter.CurrencyConverterUiState]:
 * this screen only ever lists the saved pairs, it starts no operation of its own and produces no
 * result to show, so there is no status to track — [favorites] and [unavailable] are the only two
 * things that can ever be true about it.
 */
data class FavoritesUiState(
  val favorites: List<FavoriteExchange> = emptyList(),
  val unavailable: Boolean = false,
)
