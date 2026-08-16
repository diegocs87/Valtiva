package com.example.valtiva.domain.model

import com.example.valtiva.domain.COP
import com.example.valtiva.domain.USD
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class MoneyTest {

  @Test
  fun `accepts a positive amount`() {
    val money = Money(BigDecimal("100.50"), USD)

    assertEquals(BigDecimal("100.50"), money.amount)
    assertEquals(USD, money.currency)
  }

  @Test
  fun `accepts zero`() {
    assertEquals(BigDecimal.ZERO, Money(BigDecimal.ZERO, USD).amount)
  }

  @Test
  fun `rejects a negative amount`() {
    val error = assertThrows(IllegalArgumentException::class.java) {
      Money(BigDecimal("-0.01"), USD)
    }

    assertEquals(true, error.message?.contains("must not be negative"))
  }

  @Test
  fun `preserves the scale it was built with`() {
    assertEquals("100.500", Money(BigDecimal("100.500"), USD).amount.toPlainString())
  }

  @Test
  fun `the same amount in a different currency is a different value`() {
    assertNotEquals(Money(BigDecimal("10"), USD), Money(BigDecimal("10"), COP))
  }
}
