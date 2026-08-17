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
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Test

class RefreshRatesUseCaseTest {

  private val repository = mockk<ExchangeRateRepository>()
  private val refreshRates = RefreshRatesUseCase(repository)

  @Test
  fun `delegates the refresh to the repository and returns the fresh snapshot`() = runTest {
    val snapshot = ExchangeRateSnapshot(USD, RATE_DATE, mapOf(COP to BigDecimal("3980.25")))
    coEvery { repository.refreshLatestRates(USD) } returns snapshot

    assertEquals(snapshot, refreshRates(USD))
    coVerify(exactly = 1) { repository.refreshLatestRates(USD) }
  }

  @Test
  fun `propagates a refresh failure`() {
    val failure = IllegalStateException("offline")
    coEvery { repository.refreshLatestRates(USD) } throws failure

    val error = assertThrows(IllegalStateException::class.java) {
      runBlocking { refreshRates(USD) }
    }

    assertSame(failure, error)
  }
}
