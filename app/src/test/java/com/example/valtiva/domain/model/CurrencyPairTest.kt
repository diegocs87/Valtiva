package com.example.valtiva.domain.model

import com.example.valtiva.domain.COP
import com.example.valtiva.domain.USD
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class CurrencyPairTest {

  @Test
  fun `keeps base and quote in the given order`() {
    val pair = CurrencyPair(base = USD, quote = COP)

    assertEquals(USD, pair.base)
    assertEquals(COP, pair.quote)
  }

  @Test
  fun `USD to COP is not the same pair as COP to USD`() {
    assertNotEquals(CurrencyPair(USD, COP), CurrencyPair(COP, USD))
  }

  @Test
  fun `pairs with the same base and quote are equal`() {
    assertEquals(CurrencyPair(USD, COP), CurrencyPair(USD, COP))
  }
}
