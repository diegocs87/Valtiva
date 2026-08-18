package com.example.valtiva.data.repository

import com.example.valtiva.data.source.LocalFavoriteExchangeDataSource
import com.example.valtiva.domain.model.CurrencyPair
import com.example.valtiva.domain.model.FavoriteExchange
import com.example.valtiva.domain.repository.FavoriteExchangeRepository
import kotlinx.coroutines.flow.Flow

/**
 * Favorites are the user's own preferences: purely local, no remote source and no freshness rules,
 * so this repository is a thin pass-through over the local data source. That is intentional — the
 * value is the boundary (consumers keep depending on the domain contract, storage stays swappable),
 * not the logic.
 */
class FavoriteExchangeRepositoryImpl(
  private val localDataSource: LocalFavoriteExchangeDataSource,
) : FavoriteExchangeRepository {

  override fun getFavorites(): Flow<List<FavoriteExchange>> = localDataSource.getFavorites()

  override suspend fun addFavorite(favorite: FavoriteExchange) {
    localDataSource.add(favorite)
  }

  override suspend fun removeFavorite(pair: CurrencyPair) {
    localDataSource.remove(pair)
  }
}
