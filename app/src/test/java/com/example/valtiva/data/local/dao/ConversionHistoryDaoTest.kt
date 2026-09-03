package com.example.valtiva.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.example.valtiva.data.local.ValtivaDatabase
import com.example.valtiva.data.local.entity.ConversionHistoryEntity
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ConversionHistoryDaoTest {

  private lateinit var database: ValtivaDatabase
  private lateinit var dao: ConversionHistoryDao

  private val rateDate: LocalDate = LocalDate.of(2026, 8, 14)
  private val baseInstant: Instant = Instant.parse("2026-08-14T10:00:00Z")

  private fun timestampOf(minute: Long): Instant = baseInstant.plusSeconds(minute * 60)

  private fun entity(minute: Long) = ConversionHistoryEntity(
    sourceAmount = BigDecimal("100.00"),
    baseCurrency = "USD",
    targetAmount = BigDecimal("398025.00"),
    quoteCurrency = "COP",
    rate = BigDecimal("3980.25"),
    rateDate = rateDate,
    timestamp = timestampOf(minute),
  )

  @Before
  fun setUp() {
    database = Room.inMemoryDatabaseBuilder(
      ApplicationProvider.getApplicationContext(),
      ValtivaDatabase::class.java,
    ).build()
    dao = database.conversionHistoryDao()
  }

  @After
  fun tearDown() {
    database.close()
  }

  @Test
  fun `starts empty`() = runTest {
    dao.observeHistory().test {
      assertEquals(emptyList<ConversionHistoryEntity>(), awaitItem())
      cancelAndIgnoreRemainingEvents()
    }
  }

  @Test
  fun `inserts a conversion and observes it`() = runTest {
    dao.insertAndTrim(entity(minute = 0))

    dao.observeHistory().test {
      val stored = awaitItem()
      assertEquals(1, stored.size)
      assertEquals(BigDecimal("100.00"), stored.single().sourceAmount)
      cancelAndIgnoreRemainingEvents()
    }
  }

  @Test
  fun `orders conversions from most recent to oldest`() = runTest {
    dao.insertAndTrim(entity(minute = 0))
    dao.insertAndTrim(entity(minute = 5))
    dao.insertAndTrim(entity(minute = 2))

    dao.observeHistory().test {
      val timestamps = awaitItem().map { it.timestamp }
      assertEquals(timestamps.sortedDescending(), timestamps)
      cancelAndIgnoreRemainingEvents()
    }
  }

  @Test
  fun `keeps at most the ten most recent conversions`() = runTest {
    // Minutes 0..12, thirteen conversions in total; only the ten most recent (3..12) must remain.
    repeat(13) { minute -> dao.insertAndTrim(entity(minute = minute.toLong())) }

    dao.observeHistory().test {
      val kept = awaitItem().map { it.timestamp }.toSet()
      assertEquals(10, kept.size)
      assertEquals((3L..12L).map(::timestampOf).toSet(), kept)
      cancelAndIgnoreRemainingEvents()
    }
  }

  @Test
  fun `trimming drops only what is past the ten most recent`() = runTest {
    // Minutes 0..10, eleven conversions; only the very oldest (minute 0) should be gone.
    repeat(11) { minute -> dao.insertAndTrim(entity(minute = minute.toLong())) }

    dao.observeHistory().test {
      val kept = awaitItem().map { it.timestamp }.toSet()
      assertEquals((1L..10L).map(::timestampOf).toSet(), kept)
      cancelAndIgnoreRemainingEvents()
    }
  }
}
