package com.example.valtiva.data.local.source

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.example.valtiva.data.local.ValtivaDatabase
import com.example.valtiva.domain.COP
import com.example.valtiva.domain.RATE_DATE
import com.example.valtiva.domain.USD
import com.example.valtiva.domain.model.ConversionResult
import com.example.valtiva.domain.model.Money
import java.math.BigDecimal
import java.time.Instant
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RoomConversionHistoryDataSourceTest {

  private lateinit var database: ValtivaDatabase
  private lateinit var dataSource: RoomConversionHistoryDataSource

  private val conversion = ConversionResult(
    source = Money(BigDecimal("100.00"), USD),
    target = Money(BigDecimal("398025.00"), COP),
    exchangeRate = BigDecimal("3980.25"),
    rateDate = RATE_DATE,
  )
  private val performedAt: Instant = Instant.parse("2026-08-14T10:00:00Z")

  @Before
  fun setUp() {
    database = Room.inMemoryDatabaseBuilder(
      ApplicationProvider.getApplicationContext(),
      ValtivaDatabase::class.java,
    ).build()
    dataSource = RoomConversionHistoryDataSource(database.conversionHistoryDao())
  }

  @After
  fun tearDown() {
    database.close()
  }

  @Test
  fun `stores a conversion and emits it as a domain value`() = runTest {
    dataSource.save(conversion, performedAt)

    dataSource.getHistory().test {
      val stored = awaitItem()
      assertEquals(1, stored.size)
      assertEquals(conversion, stored.single().conversion)
      assertEquals(performedAt, stored.single().performedAt)
      cancelAndIgnoreRemainingEvents()
    }
  }

  @Test
  fun `most recent conversion comes first`() = runTest {
    dataSource.save(conversion, performedAt)
    dataSource.save(conversion, performedAt.plusSeconds(60))

    dataSource.getHistory().test {
      val stored = awaitItem()
      assertEquals(performedAt.plusSeconds(60), stored[0].performedAt)
      assertEquals(performedAt, stored[1].performedAt)
      cancelAndIgnoreRemainingEvents()
    }
  }
}
