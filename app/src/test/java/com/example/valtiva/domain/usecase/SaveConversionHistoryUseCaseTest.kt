package com.example.valtiva.domain.usecase

import com.example.valtiva.domain.COP
import com.example.valtiva.domain.RATE_DATE
import com.example.valtiva.domain.USD
import com.example.valtiva.domain.model.ConversionResult
import com.example.valtiva.domain.model.Money
import com.example.valtiva.domain.repository.ConversionHistoryRepository
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import java.math.BigDecimal
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SaveConversionHistoryUseCaseTest {

  private val repository = mockk<ConversionHistoryRepository>()
  private val fixedInstant = Instant.parse("2026-08-30T15:32:00Z")
  private val clock = Clock.fixed(fixedInstant, ZoneOffset.UTC)
  private val saveConversionHistory = SaveConversionHistoryUseCase(repository, clock)

  private val conversion = ConversionResult(
    source = Money(BigDecimal("100.00"), USD),
    target = Money(BigDecimal("398025.00"), COP),
    exchangeRate = BigDecimal("3980.25"),
    rateDate = RATE_DATE,
  )

  @Test
  fun `saves the conversion stamped with the current moment of the injected clock`() = runTest {
    coJustRun { repository.saveConversion(any(), any()) }

    saveConversionHistory(conversion)

    coVerify(exactly = 1) { repository.saveConversion(conversion, fixedInstant) }
  }
}
