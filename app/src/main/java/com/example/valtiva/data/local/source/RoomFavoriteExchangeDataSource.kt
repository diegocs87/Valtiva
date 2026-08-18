package com.example.valtiva.data.local.source

import com.example.valtiva.data.local.dao.FavoriteExchangeDao
import com.example.valtiva.data.local.mapper.toDomain
import com.example.valtiva.data.local.mapper.toEntity
import com.example.valtiva.data.source.LocalFavoriteExchangeDataSource
import com.example.valtiva.domain.model.CurrencyPair
import com.example.valtiva.domain.model.FavoriteExchange
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * [LocalFavoriteExchangeDataSource] backed by Room. Entities never leave this class; duplicate
 * inserts are absorbed by the DAO's IGNORE strategy and the table's composite key.
 */
class RoomFavoriteExchangeDataSource @Inject constructor(
  private val dao: FavoriteExchangeDao,
) : LocalFavoriteExchangeDataSource {

  override fun getFavorites(): Flow<List<FavoriteExchange>> =
    dao.observeFavorites().map { entities -> entities.map { it.toDomain() } }

  override suspend fun add(favorite: FavoriteExchange) {
    dao.insert(favorite.toEntity())
  }

  override suspend fun remove(pair: CurrencyPair) {
    dao.delete(pair.base.code, pair.quote.code)
  }
}
