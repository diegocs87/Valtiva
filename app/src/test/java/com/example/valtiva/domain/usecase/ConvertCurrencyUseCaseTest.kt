package com.example.valtiva.domain.usecase

import com.example.valtiva.domain.COP
import com.example.valtiva.domain.EUR
import com.example.valtiva.domain.RATE_DATE
import com.example.valtiva.domain.USD
import com.example.valtiva.domain.exception.ExchangeRateUnavailableException
import com.example.valtiva.domain.model.Currency
import com.example.valtiva.domain.model.ExchangeRate
import com.example.valtiva.domain.model.ExchangeRateSnapshot
import com.example.valtiva.domain.model.Money
import com.example.valtiva.domain.repository.ExchangeRateRepository
import com.example.valtiva.domain.service.CurrencyConversionService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import java.math.BigDecimal
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Test

class ConvertCurrencyUseCaseTest {

  private val repository = mockk<ExchangeRateRepository>()

  // The conversion service is pure arithmetic, so the real one is used and only spied on when a
  // test needs to assert exactly which values it received.
  private val conversionService = spyk(CurrencyConversionService())
  private val convertCurrency = ConvertCurrencyUseCase(repository, conversionService)

  private fun usdSnapshot(vararg rates: Pair<Currency, String>) = ExchangeRateSnapshot(
    baseCurrency = USD,
    date = RATE_DATE,
    rates = rates.associate { (currency, rate) -> currency to BigDecimal(rate) },
  )

  @Test
  fun `converts using the rate published by the repository`() = runTest {
    coEvery { repository.getLatestRates(USD) } returns usdSnapshot(COP to "3980.25")

    val result = convertCurrency(BigDecimal("100"), USD, COP)

    assertEquals("398025.00", result.target.amount.toPlainString())
    assertEquals(COP, result.target.currency)
    assertEquals(BigDecimal("3980.25"), result.exchangeRate)
    assertEquals(RATE_DATE, result.rateDate)
  }

  @Test
  fun `asks the repository for rates based on the source currency`() = runTest {
    coEvery { repository.getLatestRates(USD) } returns usdSnapshot(COP to "3980.25")

    convertCurrency(BigDecimal("100"), USD, COP)

    coVerify(exactly = 1) { repository.getLatestRates(USD) }
  }

  @Test
  fun `hands the resolved money and rate to the conversion service`() = runTest {
    coEvery { repository.getLatestRates(USD) } returns usdSnapshot(COP to "3980.25")

    convertCurrency(BigDecimal("100"), USD, COP)

    verify(exactly = 1) {
      conversionService.convert(
        Money(BigDecimal("100"), USD),
        COP,
        ExchangeRate(USD, COP, BigDecimal("3980.25"), RATE_DATE),
      )
    }
  }

  @Test
  fun `fails when the repository has no rates for the source currency`() {
    coEvery { repository.getLatestRates(USD) } returns null

    val error = assertThrows(ExchangeRateUnavailableException::class.java) {
      runBlocking { convertCurrency(BigDecimal("100"), USD, COP) }
    }

    assertEquals(USD, error.base)
    assertEquals(COP, error.quote)
  }

  @Test
  fun `fails when the snapshot does not publish the target currency`() {
    coEvery { repository.getLatestRates(USD) } returns usdSnapshot(EUR to "0.85")

    assertThrows(ExchangeRateUnavailableException::class.java) {
      runBlocking { convertCurrency(BigDecimal("100"), USD, COP) }
    }
  }

  @Test
  fun `propagates a failure coming from the repository`() {
    val failure = IllegalStateException("offline")
    coEvery { repository.getLatestRates(USD) } throws failure

    val error = assertThrows(IllegalStateException::class.java) {
      runBlocking { convertCurrency(BigDecimal("100"), USD, COP) }
    }

    assertSame(failure, error)
  }

  @Test
  fun `rejects a negative amount before touching the repository`() {
    assertThrows(IllegalArgumentException::class.java) {
      runBlocking { convertCurrency(BigDecimal("-1"), USD, COP) }
    }

    coVerify(exactly = 0) { repository.getLatestRates(any()) }
  }

  @Test
  fun `converts a currency into itself without needing a published rate`() = runTest {
    coEvery { repository.getLatestRates(USD) } returns usdSnapshot(COP to "3980.25")

    val result = convertCurrency(BigDecimal("100"), USD, USD)

    assertEquals("100.00", result.target.amount.toPlainString())
    assertEquals(BigDecimal.ONE, result.exchangeRate)
  }
}
