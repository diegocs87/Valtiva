package com.example.valtiva.domain.usecase

import app.cash.turbine.test
import com.example.valtiva.domain.COP
import com.example.valtiva.domain.RATE_DATE
import com.example.valtiva.domain.USD
import com.example.valtiva.domain.model.ConversionHistoryEntry
import com.example.valtiva.domain.model.ConversionResult
import com.example.valtiva.domain.model.Money
import com.example.valtiva.domain.repository.ConversionHistoryRepository
import io.mockk.every
import io.mockk.mockk
import java.math.BigDecimal
import java.time.Instant
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetConversionHistoryUseCaseTest {

  private val repository = mockk<ConversionHistoryRepository>()
  private val getConversionHistory = GetConversionHistoryUseCase(repository)

  private fun entry(id: Long) = ConversionHistoryEntry(
    id = id,
    conversion = ConversionResult(
      source = Money(BigDecimal("100.00"), USD),
      target = Money(BigDecimal("398025.00"), COP),
      exchangeRate = BigDecimal("3980.25"),
      rateDate = RATE_DATE,
    ),
    performedAt = Instant.parse("2026-08-14T10:00:00Z"),
  )

  @Test
  fun `emits the history published by the repository`() = runTest {
    val history = listOf(entry(2), entry(1))
    every { repository.getConversionHistory() } returns flowOf(history)

    getConversionHistory().test {
      assertEquals(history, awaitItem())
      awaitComplete()
    }
  }

  @Test
  fun `re-emits every update of the history stream`() = runTest {
    val first = listOf(entry(1))
    val second = listOf(entry(2), entry(1))
    every { repository.getConversionHistory() } returns flowOf(first, second)

    getConversionHistory().test {
      assertEquals(first, awaitItem())
      assertEquals(second, awaitItem())
      awaitComplete()
    }
  }
}
