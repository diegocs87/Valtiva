package com.example.valtiva.domain.usecase

import app.cash.turbine.test
import com.example.valtiva.domain.COP
import com.example.valtiva.domain.EUR
import com.example.valtiva.domain.USD
import com.example.valtiva.domain.model.CurrencyPair
import com.example.valtiva.domain.model.FavoriteExchange
import com.example.valtiva.domain.repository.FavoriteExchangeRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetFavoriteExchangesUseCaseTest {

  private val repository = mockk<FavoriteExchangeRepository>()
  private val getFavoriteExchanges = GetFavoriteExchangesUseCase(repository)

  @Test
  fun `emits the favorites published by the repository`() = runTest {
    val favorites = listOf(
      FavoriteExchange(CurrencyPair(USD, COP)),
      FavoriteExchange(CurrencyPair(EUR, COP)),
    )
    every { repository.getFavorites() } returns flowOf(favorites)

    getFavoriteExchanges().test {
      assertEquals(favorites, awaitItem())
      awaitComplete()
    }
  }

  @Test
  fun `re-emits every update of the favorites stream`() = runTest {
    val first = listOf(FavoriteExchange(CurrencyPair(USD, COP)))
    val second = first + FavoriteExchange(CurrencyPair(EUR, COP))
    every { repository.getFavorites() } returns flowOf(first, second)

    getFavoriteExchanges().test {
      assertEquals(first, awaitItem())
      assertEquals(second, awaitItem())
      awaitComplete()
    }
  }
}
