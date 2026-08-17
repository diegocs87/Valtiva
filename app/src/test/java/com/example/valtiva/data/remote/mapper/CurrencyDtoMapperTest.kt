package com.example.valtiva.data.remote.mapper

import com.example.valtiva.data.remote.dto.FrankfurterCurrencyDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

class CurrencyDtoMapperTest {

  @Test
  fun `maps a currency into the domain`() {
    val mapped = FrankfurterCurrencyDto("USD", "United States Dollar", "\$").toDomain()

    assertEquals("USD", mapped.code)
    assertEquals("United States Dollar", mapped.name)
    assertEquals("\$", mapped.symbol)
  }

  @Test
  fun `normalizes the ISO code`() {
    assertEquals("EUR", FrankfurterCurrencyDto("eur", "Euro", "€").toDomain().code)
  }

  @Test
  fun `treats a missing symbol as absent`() {
    assertNull(FrankfurterCurrencyDto("XDR", "Special Drawing Rights", null).toDomain().symbol)
  }

  @Test
  fun `treats a blank symbol as absent`() {
    assertNull(FrankfurterCurrencyDto("XDR", "Special Drawing Rights", "   ").toDomain().symbol)
  }

  @Test
  fun `fails on an unusable ISO code`() {
    assertThrows(IllegalArgumentException::class.java) {
      FrankfurterCurrencyDto("DOLLAR", "Dollar", null).toDomain()
    }
  }
}
