package com.example.valtiva.data.local.mapper

import com.example.valtiva.data.local.entity.ExchangeRateEntity
import com.example.valtiva.domain.COP
import com.example.valtiva.domain.EUR
import com.example.valtiva.domain.RATE_DATE
import com.example.valtiva.domain.USD
import com.example.valtiva.domain.model.ExchangeRate
import com.example.valtiva.domain.model.ExchangeRateSnapshot
import java.math.BigDecimal
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class ExchangeRateEntityMapperTest {

  private val entity = ExchangeRateEntity("USD", "COP", BigDecimal("3980.25"), RATE_DATE)
  private val domain = ExchangeRate(USD, COP, BigDecimal("3980.25"), RATE_DATE)

  @Test
  fun `maps a stored row to the domain rate`() {
    val mapped = entity.toDomain()

    assertEquals(USD, mapped.base)
    assertEquals(COP, mapped.quote)
    assertEquals(BigDecimal("3980.25"), mapped.rate)
    assertEquals(RATE_DATE, mapped.date)
  }

  @Test
  fun `maps a domain rate to a stored row keeping only the codes`() {
    val mapped = domain.toEntity()

    assertEquals("USD", mapped.baseCurrency)
    assertEquals("COP", mapped.quoteCurrency)
    assertEquals(BigDecimal("3980.25"), mapped.rate)
    assertEquals(RATE_DATE, mapped.date)
  }

  @Test
  fun `round trips a rate without touching its value or scale`() {
    val precise = ExchangeRate(USD, COP, BigDecimal("3980.2512345678901234567890"), RATE_DATE)

    val restored = precise.toEntity().toDomain()

    assertEquals(precise.rate, restored.rate)
    assertEquals(precise.rate.scale(), restored.rate.scale())
    assertEquals(precise, restored)
  }

  @Test
  fun `fails loudly when a stored code is not a valid currency`() {
    val corrupt = ExchangeRateEntity("US", "COP", BigDecimal("3980.25"), RATE_DATE)

    assertThrows(IllegalArgumentException::class.java) { corrupt.toDomain() }
  }

  @Test
  fun `flattens a snapshot into one row per quoted currency`() {
    val snapshot = ExchangeRateSnapshot(
      baseCurrency = USD,
      date = RATE_DATE,
      rates = mapOf(COP to BigDecimal("3980.25"), EUR to BigDecimal("0.85")),
    )

    val rows = snapshot.toEntities()

    assertEquals(2, rows.size)
    assertEquals(setOf("COP", "EUR"), rows.map { it.quoteCurrency }.toSet())
    assertEquals(setOf("USD"), rows.map { it.baseCurrency }.toSet())
    assertEquals(setOf(RATE_DATE), rows.map { it.date }.toSet())
  }

  @Test
  fun `rebuilds one snapshot per stored day, newest first`() {
    val august15 = LocalDate.of(2026, 8, 15)
    val rows = listOf(
      ExchangeRateEntity("USD", "COP", BigDecimal("3980.25"), RATE_DATE),
      ExchangeRateEntity("USD", "EUR", BigDecimal("0.85"), RATE_DATE),
      ExchangeRateEntity("USD", "COP", BigDecimal("3990.10"), august15),
    )

    val snapshots = rows.toExchangeRateSnapshots()

    assertEquals(2, snapshots.size)
    assertEquals(august15, snapshots.first().date)
    assertEquals(1, snapshots.first().rates.size)
    assertEquals(2, snapshots.last().rates.size)
  }

  @Test
  fun `keeps snapshots of different bases apart`() {
    val rows = listOf(
      ExchangeRateEntity("USD", "COP", BigDecimal("3980.25"), RATE_DATE),
      ExchangeRateEntity("EUR", "COP", BigDecimal("4600.00"), RATE_DATE),
    )

    val snapshots = rows.toExchangeRateSnapshots()

    assertEquals(2, snapshots.size)
    assertEquals(setOf(USD, EUR), snapshots.map { it.baseCurrency }.toSet())
  }
}
