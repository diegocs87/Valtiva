package com.example.valtiva.data.repository

import com.example.valtiva.data.source.LocalFavoriteExchangeDataSource
import com.example.valtiva.domain.model.CurrencyPair
import com.example.valtiva.domain.model.FavoriteExchange
import com.example.valtiva.domain.repository.FavoriteExchangeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FavoriteExchangeRepositoryImpl @Inject constructor(
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
