package com.example.valtiva.data.repository

import com.example.valtiva.data.source.LocalFavoriteExchangeDataSource
import com.example.valtiva.domain.COP
import com.example.valtiva.domain.USD
import com.example.valtiva.domain.model.CurrencyPair
import com.example.valtiva.domain.model.FavoriteExchange
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertSame
import org.junit.Test

class FavoriteExchangeRepositoryImplTest {

  private val local = mockk<LocalFavoriteExchangeDataSource>()
  private val repository = FavoriteExchangeRepositoryImpl(local)
  private val usdCop = FavoriteExchange(CurrencyPair(USD, COP))

  @Test
  fun `exposes the local favorites stream as-is`() {
    val stream: Flow<List<FavoriteExchange>> = flowOf(listOf(usdCop))
    every { local.getFavorites() } returns stream

    assertSame(stream, repository.getFavorites())
  }

  @Test
  fun `stores the exact favorite it was given`() = runTest {
    coJustRun { local.add(any()) }

    repository.addFavorite(usdCop)

    coVerify(exactly = 1) { local.add(usdCop) }
  }

  @Test
  fun `removes the exact pair it was given, keeping its direction`() = runTest {
    coJustRun { local.remove(any()) }

    repository.removeFavorite(CurrencyPair(COP, USD))

    coVerify(exactly = 1) { local.remove(CurrencyPair(COP, USD)) }
    coVerify(exactly = 0) { local.remove(CurrencyPair(USD, COP)) }
  }
}
