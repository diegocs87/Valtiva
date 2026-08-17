package com.example.valtiva.domain.usecase

import com.example.valtiva.domain.COP
import com.example.valtiva.domain.USD
import com.example.valtiva.domain.model.CurrencyPair
import com.example.valtiva.domain.repository.FavoriteExchangeRepository
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class RemoveFavoriteExchangeUseCaseTest {

  private val repository = mockk<FavoriteExchangeRepository>()
  private val removeFavoriteExchange = RemoveFavoriteExchangeUseCase(repository)

  @Test
  fun `removes exactly the requested pair`() = runTest {
    coJustRun { repository.removeFavorite(any()) }

    removeFavoriteExchange(CurrencyPair(USD, COP))

    coVerify(exactly = 1) { repository.removeFavorite(CurrencyPair(USD, COP)) }
    coVerify(exactly = 0) { repository.removeFavorite(CurrencyPair(COP, USD)) }
  }
}
