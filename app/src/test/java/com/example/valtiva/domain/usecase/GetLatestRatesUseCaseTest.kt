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
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GetLatestRatesUseCaseTest {

  private val repository = mockk<ExchangeRateRepository>()
  private val getLatestRates = GetLatestRatesUseCase(repository)

  @Test
  fun `returns the snapshot published by the repository`() = runTest {
    val snapshot = ExchangeRateSnapshot(USD, RATE_DATE, mapOf(COP to BigDecimal("3980.25")))
    coEvery { repository.getLatestRates(USD) } returns snapshot

    assertEquals(snapshot, getLatestRates(USD))
    coVerify(exactly = 1) { repository.getLatestRates(USD) }
  }

  @Test
  fun `returns null when the repository has no rates yet`() = runTest {
    coEvery { repository.getLatestRates(USD) } returns null

    assertNull(getLatestRates(USD))
  }
}
