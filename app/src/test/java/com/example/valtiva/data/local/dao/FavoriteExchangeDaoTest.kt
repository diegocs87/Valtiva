package com.example.valtiva.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.example.valtiva.data.local.ValtivaDatabase
import com.example.valtiva.data.local.entity.FavoriteExchangeEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FavoriteExchangeDaoTest {

  private lateinit var database: ValtivaDatabase
  private lateinit var dao: FavoriteExchangeDao

  private val usdCop = FavoriteExchangeEntity("USD", "COP")
  private val eurCop = FavoriteExchangeEntity("EUR", "COP")

  @Before
  fun setUp() {
    database = Room.inMemoryDatabaseBuilder(
      ApplicationProvider.getApplicationContext(),
      ValtivaDatabase::class.java,
    ).build()
    dao = database.favoriteExchangeDao()
  }

  @After
  fun tearDown() {
    database.close()
  }

  @Test
  fun `inserts favorites and observes them`() = runTest {
    dao.insert(usdCop)
    dao.insert(eurCop)

    dao.observeFavorites().test {
      assertEquals(listOf(eurCop, usdCop), awaitItem())
      cancelAndIgnoreRemainingEvents()
    }
  }

  @Test
  fun `starts empty`() = runTest {
    dao.observeFavorites().test {
      assertEquals(emptyList<FavoriteExchangeEntity>(), awaitItem())
      cancelAndIgnoreRemainingEvents()
    }
  }

  @Test
  fun `starring the same pair twice does not duplicate it`() = runTest {
    dao.insert(usdCop)
    dao.insert(usdCop)

    dao.observeFavorites().test {
      assertEquals(listOf(usdCop), awaitItem())
      cancelAndIgnoreRemainingEvents()
    }
  }

  @Test
  fun `the reversed pair is a different favorite`() = runTest {
    dao.insert(usdCop)
    dao.insert(FavoriteExchangeEntity("COP", "USD"))

    dao.observeFavorites().test {
      assertEquals(2, awaitItem().size)
      cancelAndIgnoreRemainingEvents()
    }
  }

  @Test
  fun `deletes a stored favorite`() = runTest {
    dao.insert(usdCop)
    dao.insert(eurCop)

    dao.delete("USD", "COP")

    dao.observeFavorites().test {
      assertEquals(listOf(eurCop), awaitItem())
      cancelAndIgnoreRemainingEvents()
    }
  }

  @Test
  fun `deleting a pair that is not stored is a no-op`() = runTest {
    dao.insert(usdCop)

    dao.delete("GBP", "JPY")

    dao.observeFavorites().test {
      assertEquals(listOf(usdCop), awaitItem())
      cancelAndIgnoreRemainingEvents()
    }
  }

  @Test
  fun `reports whether a pair is starred`() = runTest {
    dao.insert(usdCop)

    assertTrue(dao.exists("USD", "COP"))
    assertFalse(dao.exists("COP", "USD"))
    assertFalse(dao.exists("GBP", "JPY"))
  }
}
