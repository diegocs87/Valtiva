package com.example.valtiva.data.repository

import com.example.valtiva.data.source.LocalConversionHistoryDataSource
import com.example.valtiva.domain.COP
import com.example.valtiva.domain.RATE_DATE
import com.example.valtiva.domain.USD
import com.example.valtiva.domain.model.ConversionHistoryEntry
import com.example.valtiva.domain.model.ConversionResult
import com.example.valtiva.domain.model.Money
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import java.math.BigDecimal
import java.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertSame
import org.junit.Test

class ConversionHistoryRepositoryImplTest {

  private val local = mockk<LocalConversionHistoryDataSource>()
  private val repository = ConversionHistoryRepositoryImpl(local)

  private val conversion = ConversionResult(
    source = Money(BigDecimal("100.00"), USD),
    target = Money(BigDecimal("398025.00"), COP),
    exchangeRate = BigDecimal("3980.25"),
    rateDate = RATE_DATE,
  )
  private val performedAt: Instant = Instant.parse("2026-08-14T10:00:00Z")

  @Test
  fun `exposes the local history stream as-is`() {
    val stream: Flow<List<ConversionHistoryEntry>> = flowOf(emptyList())
    every { local.getHistory() } returns stream

    assertSame(stream, repository.getConversionHistory())
  }

  @Test
  fun `saves exactly the conversion and moment it was given`() = runTest {
    coJustRun { local.save(any(), any()) }

    repository.saveConversion(conversion, performedAt)

    coVerify(exactly = 1) { local.save(conversion, performedAt) }
  }
}
