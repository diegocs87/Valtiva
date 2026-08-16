package com.example.valtiva.domain.usecase

import com.example.valtiva.domain.model.CurrencyPair
import com.example.valtiva.domain.repository.FavoriteExchangeRepository

/** Removes a currency pair from the user's favorites. */
class RemoveFavoriteExchangeUseCase(
  private val favoriteExchangeRepository: FavoriteExchangeRepository,
) {
  suspend operator fun invoke(pair: CurrencyPair) {
    favoriteExchangeRepository.removeFavorite(pair)
  }
}
