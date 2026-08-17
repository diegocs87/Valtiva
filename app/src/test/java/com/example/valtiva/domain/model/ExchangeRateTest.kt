package com.example.valtiva.domain.model

import com.example.valtiva.domain.COP
import com.example.valtiva.domain.RATE_DATE
import com.example.valtiva.domain.USD
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class ExchangeRateTest {

  @Test
  fun `describes one unit of the base expressed in the quote`() {
    val rate = ExchangeRate(USD, COP, BigDecimal("3980.25"), RATE_DATE)

    assertEquals(USD, rate.base)
    assertEquals(COP, rate.quote)
    assertEquals(BigDecimal("3980.25"), rate.rate)
    assertEquals(RATE_DATE, rate.date)
  }

  @Test
  fun `exposes the ordered pair it applies to`() {
    val rate = ExchangeRate(USD, COP, BigDecimal("3980.25"), RATE_DATE)

    assertEquals(CurrencyPair(USD, COP), rate.pair)
  }

  @Test
  fun `rejects a negative rate`() {
    assertThrows(IllegalArgumentException::class.java) {
      ExchangeRate(USD, COP, BigDecimal("-1"), RATE_DATE)
    }
  }

  @Test
  fun `rejects a rate of zero because it would make the base currency worthless`() {
    val error = assertThrows(IllegalArgumentException::class.java) {
      ExchangeRate(USD, COP, BigDecimal.ZERO, RATE_DATE)
    }

    assertEquals(true, error.message?.contains("strictly positive"))
  }

  @Test
  fun `allows a rate between a currency and itself`() {
    val rate = ExchangeRate(USD, USD, BigDecimal.ONE, RATE_DATE)

    assertEquals(BigDecimal.ONE, rate.rate)
  }
}
