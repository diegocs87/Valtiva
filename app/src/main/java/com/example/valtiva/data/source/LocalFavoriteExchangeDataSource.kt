package com.example.valtiva.data.source

import com.example.valtiva.domain.model.CurrencyPair
import com.example.valtiva.domain.model.FavoriteExchange
import kotlinx.coroutines.flow.Flow

/**
 * Locally stored favorite pairs, spoken entirely in domain types. The implementation owns the
 * entity mapping and the DAO; nothing above it sees an entity.
 */
interface LocalFavoriteExchangeDataSource {

  /** Emits the stored favorites and re-emits on every change. */
  fun getFavorites(): Flow<List<FavoriteExchange>>

  /** Stores [favorite]; storing an already present pair must not duplicate it. */
  suspend fun add(favorite: FavoriteExchange)

  /** Removes the favorite for [pair]; removing an absent pair is a no-op. */
  suspend fun remove(pair: CurrencyPair)
}
