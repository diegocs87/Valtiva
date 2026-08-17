package com.example.valtiva.domain.usecase

import com.example.valtiva.domain.model.FavoriteExchange
import com.example.valtiva.domain.repository.FavoriteExchangeRepository
import kotlinx.coroutines.flow.Flow

/** Observes the currency pairs the user marked as favorites. */
class GetFavoriteExchangesUseCase(
  private val favoriteExchangeRepository: FavoriteExchangeRepository,
) {
  operator fun invoke(): Flow<List<FavoriteExchange>> = favoriteExchangeRepository.getFavorites()
}
