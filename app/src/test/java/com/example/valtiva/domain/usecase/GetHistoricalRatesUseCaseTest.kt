package com.example.valtiva.domain.usecase

import com.example.valtiva.domain.COP
import com.example.valtiva.domain.RATE_DATE
import com.example.valtiva.domain.USD
import com.example.valtiva.domain.model.ExchangeRateSnapshot
import com.example.valtiva.domain.repository.ExchangeRateRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.math.BigDecimal
import java.time.LocalDate
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GetHistoricalRatesUseCaseTest {

  private val repository = mockk<ExchangeRateRepository>()
  private val getHistoricalRates = GetHistoricalRatesUseCase(repository)

  @Test
  fun `asks the repository for the requested date`() = runTest {
    val snapshot = ExchangeRateSnapshot(USD, RATE_DATE, mapOf(COP to BigDecimal("3980.25")))
    coEvery { repository.getRatesForDate(USD, RATE_DATE) } returns snapshot

    assertEquals(snapshot, getHistoricalRates(USD, RATE_DATE))
    coVerify(exactly = 1) { repository.getRatesForDate(USD, RATE_DATE) }
  }

  @Test
  fun `returns null for a date the repository does not cover`() = runTest {
    val uncovered = LocalDate.of(1999, 1, 1)
    coEvery { repository.getRatesForDate(USD, uncovered) } returns null

    assertNull(getHistoricalRates(USD, uncovered))
  }
}
