package com.example.valtiva.data.local.source

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.valtiva.data.local.ValtivaDatabase
import com.example.valtiva.domain.COP
import com.example.valtiva.domain.EUR
import com.example.valtiva.domain.USD
import com.example.valtiva.domain.model.ExchangeRate
import java.math.BigDecimal
import java.time.LocalDate
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RoomExchangeRateDataSourceTest {

  private lateinit var database: ValtivaDatabase
  private lateinit var dataSource: RoomExchangeRateDataSource

  private val august14 = LocalDate.of(2026, 8, 14)
  private val august17 = LocalDate.of(2026, 8, 17)

  @Before
  fun setUp() {
    database = Room.inMemoryDatabaseBuilder(
      ApplicationProvider.getApplicationContext(),
      ValtivaDatabase::class.java,
    ).build()
    dataSource = RoomExchangeRateDataSource(database.exchangeRateDao())
  }

  @After
  fun tearDown() {
    database.close()
  }

  @Test
  fun `round trips domain rates through the database`() = runTest {
    val rates = listOf(
      ExchangeRate(USD, COP, BigDecimal("3980.2512345678901234567890"), august14),
      ExchangeRate(USD, EUR, BigDecimal("0.85"), august14),
    )

    dataSource.saveRates(rates)
    val restored = dataSource.getRatesForDate(USD, august14)

    // Currency equality is code-based, so full domain equality also checks rate (exact, with
    // scale) and date.
    assertEquals(rates.toSet(), restored.toSet())
  }

  @Test
  fun `filters by base currency and date`() = runTest {
    dataSource.saveRates(
      listOf(
        ExchangeRate(USD, COP, BigDecimal("3980.25"), august14),
        ExchangeRate(USD, COP, BigDecimal("3123.53"), august17),
        ExchangeRate(EUR, COP, BigDecimal("4600.00"), august14),
      ),
    )

    val usdOn14 = dataSource.getRatesForDate(USD, august14)

    assertEquals(1, usdOn14.size)
    assertEquals(BigDecimal("3980.25"), usdOn14.single().rate)
  }

  @Test
  fun `returns empty for a day that was never stored`() = runTest {
    assertTrue(dataSource.getRatesForDate(USD, august14).isEmpty())
  }

  @Test
  fun `re-saving the same day replaces the stored rates`() = runTest {
    dataSource.saveRates(listOf(ExchangeRate(USD, COP, BigDecimal("3980.25"), august14)))
    dataSource.saveRates(listOf(ExchangeRate(USD, COP, BigDecimal("4000.00"), august14)))

    val stored = dataSource.getRatesForDate(USD, august14)

    assertEquals(1, stored.size)
    assertEquals(BigDecimal("4000.00"), stored.single().rate)
  }

  @Test
  fun `saving an empty batch is a no-op`() = runTest {
    dataSource.saveRates(emptyList())

    assertTrue(dataSource.getRatesForDate(USD, august14).isEmpty())
  }
}
