package com.example.valtiva.presentation.converter

import com.example.valtiva.domain.COP
import com.example.valtiva.domain.EUR
import com.example.valtiva.domain.USD
import com.example.valtiva.domain.model.Currency
import org.junit.Assert.assertEquals
import org.junit.Test

/** Unit tests for [filterByQuery], the pure matching logic behind [CurrencySelectorChip]'s search. */
class CurrencySearchTest {

  private val currencies = listOf(USD, COP, EUR)

  @Test
  fun `blank query keeps the full list`() {
    assertEquals(currencies, currencies.filterByQuery(""))
    assertEquals(currencies, currencies.filterByQuery("   "))
  }

  @Test
  fun `matches by code, case-insensitively`() {
    assertEquals(listOf(COP), currencies.filterByQuery("cop"))
    assertEquals(listOf(COP), currencies.filterByQuery("COP"))
  }

  @Test
  fun `matches by full name, case-insensitively`() {
    assertEquals(listOf(COP), currencies.filterByQuery("colombian"))
    assertEquals(listOf(COP), currencies.filterByQuery("peso"))
  }

  @Test
  fun `matches a query with surrounding whitespace`() {
    assertEquals(listOf(COP), currencies.filterByQuery("  cop  "))
  }

  @Test
  fun `returns every currency whose code or name contains the query`() {
    assertEquals(listOf(USD, EUR), currencies.filterByQuery("u"))
  }

  @Test
  fun `returns nothing when no currency matches`() {
    assertEquals(emptyList<Currency>(), currencies.filterByQuery("xyz"))
  }
}
