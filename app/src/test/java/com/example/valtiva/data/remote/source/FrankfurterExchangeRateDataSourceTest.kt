package com.example.valtiva.data.remote.source

import com.example.valtiva.data.remote.api.FrankfurterApi
import com.example.valtiva.data.remote.dto.APIRateDto
import com.example.valtiva.domain.COP
import com.example.valtiva.domain.EUR
import com.example.valtiva.domain.USD
import com.example.valtiva.domain.exception.ExchangeRateSyncException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.io.IOException
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Test

class FrankfurterExchangeRateDataSourceTest {

  private val api = mockk<FrankfurterApi>()
  private val dataSource = FrankfurterExchangeRateDataSource(api)

  @Test
  fun `maps the provider payload into domain rates`() = runTest {
    coEvery { api.getLatestRates(base = "USD") } returns listOf(
      APIRateDto("2026-08-17", "USD", "COP", BigDecimal("3123.53")),
      APIRateDto("2026-08-15", "USD", "EUR", BigDecimal("0.86539")),
    )

    val rates = dataSource.getLatestRates(USD)

    assertEquals(2, rates.size)
    assertEquals(COP, rates.first().quote)
    assertEquals(BigDecimal("3123.53"), rates.first().rate)
    assertEquals(LocalDate.of(2026, 8, 17), rates.first().date)
    assertEquals(EUR, rates.last().quote)
  }

  @Test
  fun `drops the self rate the provider includes`() = runTest {
    coEvery { api.getLatestRates(base = "USD") } returns listOf(
      APIRateDto("2026-08-17", "USD", "USD", BigDecimal("1.0")),
      APIRateDto("2026-08-17", "USD", "COP", BigDecimal("3123.53")),
    )

    val rates = dataSource.getLatestRates(USD)

    assertEquals(listOf(COP), rates.map { it.quote })
  }

  @Test
  fun `asks for a historical day as an ISO date`() = runTest {
    coEvery { api.getHistoricalRates(base = "USD", date = "2026-08-14") } returns emptyList()

    dataSource.getHistoricalRates(USD, LocalDate.of(2026, 8, 14))

    coVerify(exactly = 1) { api.getHistoricalRates(base = "USD", date = "2026-08-14") }
  }

  @Test
  fun `wraps a transport failure into the domain sync exception keeping the cause`() {
    val transport = IOException("offline")
    coEvery { api.getLatestRates(base = "USD") } throws transport

    val thrown = assertThrows(ExchangeRateSyncException::class.java) {
      runBlocking { dataSource.getLatestRates(USD) }
    }

    assertEquals(USD, thrown.base)
    assertSame(transport, thrown.cause)
  }

  @Test
  fun `wraps an unusable payload into the domain sync exception`() {
    // A rate the domain rejects (zero) surfaces as a failed sync, never as a silent wrong value.
    coEvery { api.getLatestRates(base = "USD") } returns listOf(
      APIRateDto("2026-08-17", "USD", "COP", BigDecimal.ZERO),
    )

    val thrown = assertThrows(ExchangeRateSyncException::class.java) {
      runBlocking { dataSource.getLatestRates(USD) }
    }

    assertEquals(true, thrown.cause is IllegalArgumentException)
  }

  @Test
  fun `lets coroutine cancellation through untouched`() {
    val cancellation = CancellationException("cancelled")
    coEvery { api.getLatestRates(base = "USD") } throws cancellation

    val thrown = assertThrows(CancellationException::class.java) {
      runBlocking { dataSource.getLatestRates(USD) }
    }

    assertSame(cancellation, thrown)
  }
}
