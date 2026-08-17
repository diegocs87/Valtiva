package com.example.valtiva.data.local.mapper

import com.example.valtiva.data.local.entity.CurrencyEntity
import com.example.valtiva.domain.model.Currency
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

class CurrencyEntityMapperTest {

  @Test
  fun `maps a stored row to the domain currency`() {
    val mapped = CurrencyEntity("USD", "United States Dollar", "\$").toDomain()

    assertEquals("USD", mapped.code)
    assertEquals("United States Dollar", mapped.name)
    assertEquals("\$", mapped.symbol)
  }

  @Test
  fun `maps a domain currency to a stored row`() {
    val mapped = Currency.of("cop", "Colombian Peso", "\$").toEntity()

    assertEquals("COP", mapped.code)
    assertEquals("Colombian Peso", mapped.name)
    assertEquals("\$", mapped.symbol)
  }

  @Test
  fun `keeps a missing symbol absent in both directions`() {
    val entity = CurrencyEntity("XDR", "Special Drawing Rights", null)

    val restored = entity.toDomain().toEntity()

    assertNull(entity.toDomain().symbol)
    assertNull(restored.symbol)
    assertEquals(entity, restored)
  }

  @Test
  fun `round trips a currency keeping its display metadata`() {
    val original = Currency.of("EUR", "Euro", "€")

    val restored = original.toEntity().toDomain()

    assertEquals(original, restored)
    // Currency equality only compares the code, so the metadata is asserted explicitly.
    assertEquals("Euro", restored.name)
    assertEquals("€", restored.symbol)
  }

  @Test
  fun `fails loudly when a stored code is not a valid currency`() {
    assertThrows(IllegalArgumentException::class.java) {
      CurrencyEntity("dollars", "Dollars", null).toDomain()
    }
  }
}
