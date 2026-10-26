package com.example.valtiva.data.remote.mapper

import com.example.valtiva.data.remote.dto.APIRateDto
import com.example.valtiva.domain.COP
import com.example.valtiva.domain.EUR
import com.example.valtiva.domain.USD
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeParseException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class RateDtoMapperTest {

  private fun dto(
    date: String = "2026-08-14",
    base: String = "USD",
    quote: String = "COP",
    rate: String = "3980.25",
  ) = APIRateDto(date, base, quote, BigDecimal(rate))

  @Test
  fun `maps a USD to COP rate into the domain`() {
    val mapped = dto().toDomain()

    assertEquals(USD, mapped.base)
    assertEquals(COP, mapped.quote)
    assertEquals(BigDecimal("3980.25"), mapped.rate)
    assertEquals(LocalDate.of(2026, 8, 14), mapped.date)
  }

  @Test
  fun `parses the wire date into a LocalDate`() {
    assertEquals(LocalDate.of(2026, 8, 14), dto(date = "2026-08-14").toDomain().date)
  }

  @Test
  fun `keeps the rate exact including its scale`() {
    val mapped = dto(rate = "3980.2512345678901234567890").toDomain()

    assertEquals(BigDecimal("3980.2512345678901234567890"), mapped.rate)
    assertEquals(22, mapped.rate.scale())
  }

  @Test
  fun `normalizes lowercase currency codes`() {
    val mapped = dto(base = "usd", quote = "cop").toDomain()

    assertEquals(USD, mapped.base)
    assertEquals(COP, mapped.quote)
  }

  @Test
  fun `fails on a malformed date instead of guessing one`() {
    assertThrows(DateTimeParseException::class.java) { dto(date = "14-08-2026").toDomain() }
  }

  @Test
  fun `fails on an unusable currency code`() {
    assertThrows(IllegalArgumentException::class.java) { dto(quote = "PESOS").toDomain() }
  }

  @Test
  fun `fails on a non positive rate`() {
    assertThrows(IllegalArgumentException::class.java) { dto(rate = "0").toDomain() }
    assertThrows(IllegalArgumentException::class.java) { dto(rate = "-3980.25").toDomain() }
  }

  @Test
  fun `builds a single snapshot when every entry shares a date`() {
    val response = listOf(
      dto(quote = "COP", rate = "3980.25"),
      dto(quote = "EUR", rate = "0.85"),
      dto(quote = "GBP", rate = "0.74"),
    )

    val snapshots = response.toExchangeRateSnapshots()

    assertEquals(1, snapshots.size)
    assertEquals(USD, snapshots.single().baseCurrency)
    assertEquals(LocalDate.of(2026, 8, 14), snapshots.single().date)
    assertEquals(3, snapshots.single().rates.size)
    assertEquals(BigDecimal("3980.25"), snapshots.single().rates[COP])
  }

  @Test
  fun `splits a response that spans several dates into one snapshot per day`() {
    // The live API does this: each currency carries the last day its own provider published.
    val response = listOf(
      dto(date = "2026-08-15", quote = "COP", rate = "3123.53"),
      dto(date = "2026-08-17", quote = "EUR", rate = "0.86539"),
      dto(date = "2026-08-17", quote = "GBP", rate = "0.74053"),
    )

    val snapshots = response.toExchangeRateSnapshots()

    assertEquals(2, snapshots.size)
    // Newest first.
    assertEquals(LocalDate.of(2026, 8, 17), snapshots.first().date)
    assertEquals(2, snapshots.first().rates.size)
    assertEquals(LocalDate.of(2026, 8, 15), snapshots.last().date)
    assertEquals(mapOf(COP to BigDecimal("3123.53")), snapshots.last().rates)
  }

  @Test
  fun `drops the self rate the API includes when no quotes filter is given`() {
    val response = listOf(
      dto(quote = "USD", rate = "1.0"),
      dto(quote = "COP", rate = "3980.25"),
    )

    val snapshots = response.toExchangeRateSnapshots()

    assertEquals(1, snapshots.size)
    assertEquals(setOf(COP), snapshots.single().rates.keys)
  }

  @Test
  fun `keeps snapshots of different bases apart`() {
    val response = listOf(
      dto(base = "USD", quote = "COP", rate = "3980.25"),
      dto(base = "EUR", quote = "COP", rate = "4600.00"),
    )

    val snapshots = response.toExchangeRateSnapshots()

    assertEquals(2, snapshots.size)
    assertEquals(setOf(USD, EUR), snapshots.map { it.baseCurrency }.toSet())
  }

  @Test
  fun `maps an empty response to no snapshots`() {
    assertTrue(emptyList<APIRateDto>().toExchangeRateSnapshots().isEmpty())
  }
}
