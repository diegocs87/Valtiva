package com.example.valtiva.domain.model

import com.example.valtiva.domain.COP
import com.example.valtiva.domain.EUR
import com.example.valtiva.domain.RATE_DATE
import com.example.valtiva.domain.USD
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

class ExchangeRateSnapshotTest {

  private val snapshot = ExchangeRateSnapshot(
    baseCurrency = USD,
    date = RATE_DATE,
    rates = mapOf(COP to BigDecimal("3980.25"), EUR to BigDecimal("0.85")),
  )

  @Test
  fun `builds the rate for a published currency using the snapshot date`() {
    val rate = snapshot.rateFor(COP)

    assertEquals(ExchangeRate(USD, COP, BigDecimal("3980.25"), RATE_DATE), rate)
  }

  @Test
  fun `returns null for a currency it does not publish`() {
    assertNull(snapshot.rateFor(Currency.of("JPY")))
  }

  @Test
  fun `answers one for the base currency itself even though it is not in the rates`() {
    val rate = snapshot.rateFor(USD)

    assertEquals(BigDecimal.ONE, rate?.rate)
    assertEquals(RATE_DATE, rate?.date)
  }

  @Test
  fun `rejects a non positive rate`() {
    assertThrows(IllegalArgumentException::class.java) {
      ExchangeRateSnapshot(USD, RATE_DATE, mapOf(COP to BigDecimal.ZERO))
    }
    assertThrows(IllegalArgumentException::class.java) {
      ExchangeRateSnapshot(USD, RATE_DATE, mapOf(COP to BigDecimal("-3980.25")))
    }
  }

  @Test
  fun `an empty snapshot publishes no rate but still answers for its base`() {
    val empty = ExchangeRateSnapshot(USD, RATE_DATE, emptyMap())

    assertNull(empty.rateFor(COP))
    assertEquals(BigDecimal.ONE, empty.rateFor(USD)?.rate)
  }
}
