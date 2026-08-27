package com.example.valtiva.data.repository

import com.example.valtiva.data.source.LocalExchangeRateDataSource
import com.example.valtiva.data.source.RemoteExchangeRateDataSource
import com.example.valtiva.domain.COP
import com.example.valtiva.domain.EUR
import com.example.valtiva.domain.USD
import com.example.valtiva.domain.exception.ExchangeRateSyncException
import com.example.valtiva.domain.model.Currency
import com.example.valtiva.domain.model.ExchangeRate
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.math.BigDecimal
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class ExchangeRateRepositoryImplTest {

  private val today = LocalDate.of(2026, 8, 17)
  private val friday = LocalDate.of(2026, 8, 14)
  private val fixedClock = Clock.fixed(Instant.parse("2026-08-17T12:00:00Z"), ZoneOffset.UTC)

  private val local = FakeLocalExchangeRateDataSource()
  private val remote = mockk<RemoteExchangeRateDataSource>()
  private val repository = ExchangeRateRepositoryImpl(local, remote, fixedClock)

  private fun rate(quote: Currency, value: String, date: LocalDate) =
    ExchangeRate(USD, quote, BigDecimal(value), date)

  // ---------------------------------------------------------------- getLatestRates

  @Test
  fun `answers from local when today's rates are stored and never touches the network`() = runTest {
    local.saved += rate(COP, "3980.25", today)

    val snapshot = repository.getLatestRates(USD)

    assertEquals(today, snapshot?.date)
    assertEquals(BigDecimal("3980.25"), snapshot?.rates?.get(COP))
    coVerify(exactly = 0) { remote.getLatestRates(any()) }
  }

  @Test
  fun `fetches, persists and returns today's rates when local has none`() = runTest {
    coEvery { remote.getLatestRates(USD) } returns listOf(
      rate(COP, "3123.53", today),
      rate(EUR, "0.86539", today),
    )

    val snapshot = repository.getLatestRates(USD)

    assertEquals(today, snapshot?.date)
    assertEquals(2, snapshot?.rates?.size)
    // Persisted, so the next call is answered locally.
    assertEquals(2, local.saved.size)
    assertEquals(today, repository.getLatestRates(USD)?.date)
    coVerify(exactly = 1) { remote.getLatestRates(USD) }
  }

  @Test
  fun `persists everything the provider sent and returns every currency at its own latest rate`() = runTest {
    coEvery { remote.getLatestRates(USD) } returns listOf(
      rate(EUR, "0.86539", today),
      rate(COP, "3123.53", friday), // laggard currency: its provider has not published today yet
    )

    val snapshot = repository.getLatestRates(USD)

    // COP is not dropped for lagging behind EUR; it is included at its own last known rate.
    assertEquals(setOf(EUR, COP), snapshot?.rates?.keys)
    assertEquals(BigDecimal("3123.53"), snapshot?.rates?.get(COP))
    // The snapshot's date labels the newest information available, not every quote's own date.
    assertEquals(today, snapshot?.date)
    assertEquals(2, local.saved.size)
  }

  @Test
  fun `returns the most recent available rate per currency even when nothing was published today`() = runTest {
    coEvery { remote.getLatestRates(USD) } returns listOf(rate(COP, "3123.64", friday))

    val snapshot = repository.getLatestRates(USD)

    assertEquals(friday, snapshot?.date)
    assertEquals(BigDecimal("3123.64"), snapshot?.rates?.get(COP))
  }

  @Test
  fun `getLatestRates resolves each quote independently across rates synced on different days`() = runTest {
    local.saved += rate(EUR, "0.85", friday)
    coEvery { remote.getLatestRates(USD) } returns listOf(rate(COP, "3123.53", today))

    val snapshot = repository.getLatestRates(USD)

    // EUR was never re-synced today, but its Friday rate is still the best one known for it.
    assertEquals(setOf(EUR, COP), snapshot?.rates?.keys)
    assertEquals(today, snapshot?.date)
  }

  @Test
  fun `propagates the sync failure instead of returning stale local data as current`() = runTest {
    local.saved += rate(COP, "3980.25", friday) // stale: not today's
    val failure = ExchangeRateSyncException(USD, cause = null)
    coEvery { remote.getLatestRates(USD) } throws failure

    val thrown = assertThrows(ExchangeRateSyncException::class.java) {
      runBlocking { repository.getLatestRates(USD) }
    }

    assertSame(failure, thrown)
  }

  @Test
  fun `concurrent cache misses produce a single network call`() = runTest {
    coEvery { remote.getLatestRates(USD) } coAnswers {
      delay(50)
      listOf(rate(COP, "3123.53", today))
    }

    val first = async { repository.getLatestRates(USD) }
    val second = async { repository.getLatestRates(USD) }

    assertEquals(first.await(), second.await())
    coVerify(exactly = 1) { remote.getLatestRates(USD) }
  }

  // ---------------------------------------------------------------- getRatesForDate

  @Test
  fun `answers a stored historical day from local without touching the network`() = runTest {
    local.saved += rate(COP, "3980.25", friday)

    val snapshot = repository.getRatesForDate(USD, friday)

    assertEquals(friday, snapshot?.date)
    coVerify(exactly = 0) { remote.getHistoricalRates(any(), any()) }
  }

  @Test
  fun `fetches, persists and returns a historical day that is not stored`() = runTest {
    coEvery { remote.getHistoricalRates(USD, friday) } returns listOf(rate(COP, "3123.64", friday))

    val snapshot = repository.getRatesForDate(USD, friday)

    assertEquals(friday, snapshot?.date)
    assertEquals(1, local.saved.size)
    // Second lookup is local.
    assertEquals(friday, repository.getRatesForDate(USD, friday)?.date)
    coVerify(exactly = 1) { remote.getHistoricalRates(USD, friday) }
  }

  @Test
  fun `returns null for a day the provider has no rates for`() = runTest {
    coEvery { remote.getHistoricalRates(USD, friday) } returns emptyList()

    assertNull(repository.getRatesForDate(USD, friday))
  }

  @Test
  fun `propagates a sync failure for a historical day that is not stored`() = runTest {
    coEvery { remote.getHistoricalRates(USD, friday) } throws ExchangeRateSyncException(USD)

    assertThrows(ExchangeRateSyncException::class.java) {
      runBlocking { repository.getRatesForDate(USD, friday) }
    }
  }

  // ---------------------------------------------------------------- refreshLatestRates

  @Test
  fun `refresh fetches and persists when today's rates are missing`() = runTest {
    coEvery { remote.getLatestRates(USD) } returns listOf(rate(COP, "3123.53", today))

    val snapshot = repository.refreshLatestRates(USD)

    assertEquals(today, snapshot.date)
    assertEquals(1, local.saved.size)
  }

  @Test
  fun `refresh is idempotent per day - repeated calls cost one network call`() = runTest {
    coEvery { remote.getLatestRates(USD) } returns listOf(rate(COP, "3123.53", today))

    repository.refreshLatestRates(USD)
    repository.refreshLatestRates(USD)
    repository.refreshLatestRates(USD)

    coVerify(exactly = 1) { remote.getLatestRates(USD) }
  }

  @Test
  fun `refresh returns the newest published day with its honest date when today is not out yet`() = runTest {
    coEvery { remote.getLatestRates(USD) } returns listOf(
      rate(COP, "3123.64", friday),
      rate(EUR, "0.86539", friday),
    )

    val snapshot = repository.refreshLatestRates(USD)

    // Not disguised as today: the snapshot says these rates are Friday's.
    assertEquals(friday, snapshot.date)
    assertEquals(2, snapshot.rates.size)
  }

  @Test
  fun `refresh includes every currency at its own latest rate, not just the newest date`() = runTest {
    coEvery { remote.getLatestRates(USD) } returns listOf(
      rate(EUR, "0.86539", today),
      rate(COP, "3123.53", friday), // laggard currency
    )

    val snapshot = repository.refreshLatestRates(USD)

    assertEquals(setOf(EUR, COP), snapshot.rates.keys)
    assertEquals(today, snapshot.date)
  }

  @Test
  fun `refresh fails loudly when the provider returns nothing at all`() = runTest {
    coEvery { remote.getLatestRates(USD) } returns emptyList()

    val thrown = assertThrows(ExchangeRateSyncException::class.java) {
      runBlocking { repository.refreshLatestRates(USD) }
    }

    assertEquals(USD, thrown.base)
    assertTrue(thrown.message.orEmpty().contains("no rates"))
  }
}

/**
 * In-memory stand-in for the local store. A fake rather than a mock because the interesting
 * behavior (idempotency, persist-then-read-back) is stateful.
 */
private class FakeLocalExchangeRateDataSource : LocalExchangeRateDataSource {
  val saved = mutableListOf<ExchangeRate>()

  override suspend fun getRatesForDate(baseCurrency: Currency, date: LocalDate): List<ExchangeRate> =
    saved.filter { it.base == baseCurrency && it.date == date }

  override suspend fun getLatestRatesPerQuote(baseCurrency: Currency): List<ExchangeRate> =
    saved.filter { it.base == baseCurrency }
      .groupBy { it.quote }
      .map { (_, rates) -> rates.maxBy { it.date } }

  override suspend fun saveRates(rates: List<ExchangeRate>) {
    saved += rates
  }
}
