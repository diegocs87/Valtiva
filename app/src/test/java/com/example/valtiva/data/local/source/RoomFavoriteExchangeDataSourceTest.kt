package com.example.valtiva.data.local.source

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.example.valtiva.data.local.ValtivaDatabase
import com.example.valtiva.domain.COP
import com.example.valtiva.domain.EUR
import com.example.valtiva.domain.USD
import com.example.valtiva.domain.model.CurrencyPair
import com.example.valtiva.domain.model.FavoriteExchange
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RoomFavoriteExchangeDataSourceTest {

  private lateinit var database: ValtivaDatabase
  private lateinit var dataSource: RoomFavoriteExchangeDataSource

  private val usdCop = FavoriteExchange(CurrencyPair(USD, COP))
  private val eurCop = FavoriteExchange(CurrencyPair(EUR, COP))

  @Before
  fun setUp() {
    database = Room.inMemoryDatabaseBuilder(
      ApplicationProvider.getApplicationContext(),
      ValtivaDatabase::class.java,
    ).build()
    dataSource = RoomFavoriteExchangeDataSource(database.favoriteExchangeDao())
  }

  @After
  fun tearDown() {
    database.close()
  }

  @Test
  fun `stores favorites and emits them as domain values`() = runTest {
    dataSource.add(usdCop)
    dataSource.add(eurCop)

    dataSource.getFavorites().test {
      assertEquals(listOf(eurCop, usdCop), awaitItem())
      cancelAndIgnoreRemainingEvents()
    }
  }

  @Test
  fun `adding the same favorite twice does not duplicate it`() = runTest {
    dataSource.add(usdCop)
    dataSource.add(usdCop)

    dataSource.getFavorites().test {
      assertEquals(listOf(usdCop), awaitItem())
      cancelAndIgnoreRemainingEvents()
    }
  }

  @Test
  fun `removing a favorite re-emits the remaining ones`() = runTest {
    dataSource.add(usdCop)
    dataSource.add(eurCop)

    dataSource.getFavorites().test {
      assertEquals(2, awaitItem().size)

      dataSource.remove(CurrencyPair(USD, COP))

      assertEquals(listOf(eurCop), awaitItem())
      cancelAndIgnoreRemainingEvents()
    }
  }

  @Test
  fun `removing an absent pair is a no-op`() = runTest {
    dataSource.add(usdCop)

    dataSource.remove(CurrencyPair(EUR, COP))

    dataSource.getFavorites().test {
      assertEquals(listOf(usdCop), awaitItem())
      cancelAndIgnoreRemainingEvents()
    }
  }
}
