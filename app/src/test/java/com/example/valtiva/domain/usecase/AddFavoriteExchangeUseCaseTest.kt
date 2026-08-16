package com.example.valtiva.domain.usecase

import com.example.valtiva.domain.COP
import com.example.valtiva.domain.USD
import com.example.valtiva.domain.model.CurrencyPair
import com.example.valtiva.domain.model.FavoriteExchange
import com.example.valtiva.domain.repository.FavoriteExchangeRepository
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class AddFavoriteExchangeUseCaseTest {

  private val repository = mockk<FavoriteExchangeRepository>()
  private val addFavoriteExchange = AddFavoriteExchangeUseCase(repository)

  @Test
  fun `wraps the pair into a favorite and stores it`() = runTest {
    coJustRun { repository.addFavorite(any()) }

    addFavoriteExchange(CurrencyPair(USD, COP))

    coVerify(exactly = 1) { repository.addFavorite(FavoriteExchange(CurrencyPair(USD, COP))) }
  }

  @Test
  fun `keeps the direction of the pair`() = runTest {
    coJustRun { repository.addFavorite(any()) }

    addFavoriteExchange(CurrencyPair(COP, USD))

    coVerify(exactly = 1) { repository.addFavorite(FavoriteExchange(CurrencyPair(COP, USD))) }
    coVerify(exactly = 0) { repository.addFavorite(FavoriteExchange(CurrencyPair(USD, COP))) }
  }
}
