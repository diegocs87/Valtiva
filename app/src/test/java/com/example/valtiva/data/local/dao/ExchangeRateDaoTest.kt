package com.example.valtiva.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.example.valtiva.data.local.ValtivaDatabase
import com.example.valtiva.data.local.entity.ExchangeRateEntity
import java.math.BigDecimal
import java.time.LocalDate
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ExchangeRateDaoTest {

  private lateinit var database: ValtivaDatabase
  private lateinit var dao: ExchangeRateDao

  private val august14 = LocalDate.of(2026, 8, 14)
  private val august15 = LocalDate.of(2026, 8, 15)

  private fun rate(
    base: String = "USD",
    quote: String,
    rate: String,
    date: LocalDate = august14,
  ) = ExchangeRateEntity(base, quote, BigDecimal(rate), date)

  @Before
  fun setUp() {
    database = Room.inMemoryDatabaseBuilder(
      ApplicationProvider.getApplicationContext(),
      ValtivaDatabase::class.java,
    ).build()
    dao = database.exchangeRateDao()
  }

  @After
  fun tearDown() {
    database.close()
  }

  @Test
  fun `inserts a rate and reads it back by pair and date`() = runTest {
    dao.insert(rate(quote = "COP", rate = "3980.25"))

    val stored = dao.getRate("USD", "COP", august14)

    assertEquals(BigDecimal("3980.25"), stored?.rate)
    assertEquals(august14, stored?.date)
  }

  @Test
  fun `returns null for a pair that was never stored`() = runTest {
    assertNull(dao.getRate("USD", "COP", august14))
  }

  @Test
  fun `inserts many rates at once and reads them back by date`() = runTest {
    dao.insertAll(
      listOf(
        rate(quote = "COP", rate = "3980.25"),
        rate(quote = "EUR", rate = "0.85"),
        rate(quote = "GBP", rate = "0.74"),
      ),
    )

    val stored = dao.getRatesForDate("USD", august14)

    assertEquals(3, stored.size)
    assertEquals(setOf("COP", "EUR", "GBP"), stored.map { it.quoteCurrency }.toSet())
  }

  @Test
  fun `getLatestRates returns only the most recent stored day`() = runTest {
    dao.insertAll(
      listOf(
        rate(quote = "COP", rate = "3980.25", date = august14),
        rate(quote = "COP", rate = "3990.10", date = august15),
        rate(quote = "EUR", rate = "0.86", date = august15),
      ),
    )

    val latest = dao.getLatestRates("USD")

    assertEquals(2, latest.size)
    assertEquals(setOf(august15), latest.map { it.date }.toSet())
  }

  @Test
  fun `getLatestRates resolves the most recent day per base currency`() = runTest {
    dao.insertAll(
      listOf(
        rate(base = "USD", quote = "COP", rate = "3980.25", date = august14),
        rate(base = "EUR", quote = "COP", rate = "4600.00", date = august15),
      ),
    )

    val latestUsd = dao.getLatestRates("USD")

    // A newer row for another base must not hide USD's own most recent day.
    assertEquals(1, latestUsd.size)
    assertEquals(august14, latestUsd.single().date)
  }

  @Test
  fun `re-inserting the same pair and date replaces the stored rate`() = runTest {
    dao.insert(rate(quote = "COP", rate = "3980.25"))
    dao.insert(rate(quote = "COP", rate = "4000.00"))

    val stored = dao.getRatesForDate("USD", august14)

    assertEquals(1, stored.size)
    assertEquals(BigDecimal("4000.00"), stored.single().rate)
  }

  @Test
  fun `the same pair is kept once per day`() = runTest {
    dao.insertAll(
      listOf(
        rate(quote = "COP", rate = "3980.25", date = august14),
        rate(quote = "COP", rate = "3990.10", date = august15),
      ),
    )

    assertEquals(BigDecimal("3980.25"), dao.getRate("USD", "COP", august14)?.rate)
    assertEquals(BigDecimal("3990.10"), dao.getRate("USD", "COP", august15)?.rate)
  }

  @Test
  fun `a rate survives the round trip through SQLite without losing precision`() = runTest {
    // Stored as TEXT, so both the digits and the scale come back untouched. Through a REAL column
    // this value would be rounded to the nearest binary double.
    val exact = BigDecimal("3980.2512345678901234567890")
    dao.insert(ExchangeRateEntity("USD", "COP", exact, august14))

    val stored = dao.getRate("USD", "COP", august14)?.rate

    assertEquals(exact, stored)
    assertEquals(exact.scale(), stored?.scale())
  }

  @Test
  fun `observeLatestRates re-emits when a newer day is stored`() = runTest {
    dao.insert(rate(quote = "COP", rate = "3980.25", date = august14))

    dao.observeLatestRates("USD").test {
      assertEquals(BigDecimal("3980.25"), awaitItem().single().rate)

      dao.insert(rate(quote = "COP", rate = "3990.10", date = august15))

      assertEquals(BigDecimal("3990.10"), awaitItem().single().rate)
      cancelAndIgnoreRemainingEvents()
    }
  }
}
