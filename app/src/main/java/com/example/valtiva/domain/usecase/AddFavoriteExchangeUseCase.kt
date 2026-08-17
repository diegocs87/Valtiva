package com.example.valtiva.domain.usecase

import com.example.valtiva.domain.model.CurrencyPair
import com.example.valtiva.domain.model.FavoriteExchange
import com.example.valtiva.domain.repository.FavoriteExchangeRepository

/**
 * Marks a currency pair as a favorite.
 *
 * Takes the [CurrencyPair] rather than a [FavoriteExchange] because the pair is what the caller
 * naturally holds; wrapping it is this use case's job.
 */
class AddFavoriteExchangeUseCase(
  private val favoriteExchangeRepository: FavoriteExchangeRepository,
) {
  suspend operator fun invoke(pair: CurrencyPair) {
    favoriteExchangeRepository.addFavorite(FavoriteExchange(pair))
  }
}
