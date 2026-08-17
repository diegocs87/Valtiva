package com.example.valtiva.data.local.mapper

import com.example.valtiva.data.local.entity.FavoriteExchangeEntity
import com.example.valtiva.domain.COP
import com.example.valtiva.domain.USD
import com.example.valtiva.domain.model.CurrencyPair
import com.example.valtiva.domain.model.FavoriteExchange
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class FavoriteExchangeEntityMapperTest {

  @Test
  fun `maps a stored row to the domain favorite`() {
    val mapped = FavoriteExchangeEntity("USD", "COP").toDomain()

    assertEquals("USD", mapped.pair.base.code)
    assertEquals("COP", mapped.pair.quote.code)
  }

  @Test
  fun `maps a domain favorite to a stored row`() {
    val mapped = FavoriteExchange(CurrencyPair(USD, COP)).toEntity()

    assertEquals("USD", mapped.baseCurrency)
    assertEquals("COP", mapped.quoteCurrency)
  }

  @Test
  fun `preserves the direction of the pair`() {
    val reversed = FavoriteExchange(CurrencyPair(COP, USD)).toEntity()

    assertEquals("COP", reversed.baseCurrency)
    assertEquals("USD", reversed.quoteCurrency)
    assertNotEquals(FavoriteExchange(CurrencyPair(USD, COP)).toEntity(), reversed)
  }

  @Test
  fun `round trips a favorite unchanged`() {
    val entity = FavoriteExchangeEntity("USD", "COP")

    assertEquals(entity, entity.toDomain().toEntity())
  }

  @Test
  fun `rebuilt currencies carry only the code`() {
    // Names and symbols live in the currency catalogue, not in the favorites table.
    val mapped = FavoriteExchangeEntity("USD", "COP").toDomain()

    assertEquals("USD", mapped.pair.base.name)
    assertEquals(null, mapped.pair.base.symbol)
  }
}
