package com.example.valtiva.domain.model

import com.example.valtiva.domain.USD
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

class CurrencyTest {

  @Test
  fun `keeps code name and symbol of a valid currency`() {
    assertEquals("USD", USD.code)
    assertEquals("US Dollar", USD.name)
    assertEquals("\$", USD.symbol)
  }

  @Test
  fun `rejects a blank code`() {
    assertThrows(IllegalArgumentException::class.java) { Currency("   ") }
  }

  @Test
  fun `rejects a code that is not three letters`() {
    assertThrows(IllegalArgumentException::class.java) { Currency("US") }
    assertThrows(IllegalArgumentException::class.java) { Currency("DOLLAR") }
    assertThrows(IllegalArgumentException::class.java) { Currency("US1") }
  }

  @Test
  fun `rejects a lowercase code through the strict constructor`() {
    assertThrows(IllegalArgumentException::class.java) { Currency("usd") }
  }

  @Test
  fun `normalizes case and surrounding whitespace`() {
    assertEquals("USD", Currency.of("  usd  ").code)
    assertEquals("COP", Currency.of("cOp").code)
  }

  @Test
  fun `defaults the name to the code and leaves the symbol unset`() {
    val currency = Currency.of("gbp")

    assertEquals("GBP", currency.code)
    assertEquals("GBP", currency.name)
    assertNull(currency.symbol)
  }

  @Test
  fun `compares currencies by value`() {
    assertEquals(Currency.of("USD"), Currency.of("usd"))
    assertNotEquals(Currency.of("USD"), Currency.of("EUR"))
  }
}
