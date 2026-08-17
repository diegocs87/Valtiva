package com.example.valtiva.domain.repository

import com.example.valtiva.domain.model.CurrencyPair
import com.example.valtiva.domain.model.FavoriteExchange
import kotlinx.coroutines.flow.Flow

/**
 * Storage of the currency pairs the user marked as favorites.
 *
 * Reads are exposed as a [Flow] so the UI keeps observing changes, while modifications are
 * one-shot suspending operations.
 */
interface FavoriteExchangeRepository {

  /** Emits the current favorites and re-emits whenever they change. */
  fun getFavorites(): Flow<List<FavoriteExchange>>

  /** Adds [favorite]. Adding one that is already stored must not duplicate it. */
  suspend fun addFavorite(favorite: FavoriteExchange)

  /** Removes the favorite for [pair]. Removing an absent pair must be a no-op. */
  suspend fun removeFavorite(pair: CurrencyPair)
}
