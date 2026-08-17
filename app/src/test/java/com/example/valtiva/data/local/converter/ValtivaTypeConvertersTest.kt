package com.example.valtiva.data.local.converter

import java.math.BigDecimal
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ValtivaTypeConvertersTest {

  private val converters = ValtivaTypeConverters()

  @Test
  fun `stores a BigDecimal as plain text`() {
    assertEquals("3980.25", converters.fromBigDecimal(BigDecimal("3980.25")))
  }

  @Test
  fun `never stores a BigDecimal in scientific notation`() {
    // toString() would render this as 1E+3, which reads back as a different scale.
    val value = BigDecimal("1000").setScale(0).stripTrailingZeros()

    assertEquals("1000", converters.fromBigDecimal(value))
  }

  @Test
  fun `restores a BigDecimal with its exact digits and scale`() {
    val original = BigDecimal("3980.2512345678901234567890")

    val restored = converters.toBigDecimal(converters.fromBigDecimal(original))

    assertEquals(original, restored)
    assertEquals(original.scale(), restored?.scale())
  }

  @Test
  fun `keeps trailing zeros so the stored scale is not lost`() {
    val original = BigDecimal("10.00")

    val restored = converters.toBigDecimal(converters.fromBigDecimal(original))

    assertEquals(original, restored)
    assertEquals(2, restored?.scale())
  }

  @Test
  fun `stores a LocalDate as an ISO-8601 date`() {
    assertEquals("2026-08-14", converters.fromLocalDate(LocalDate.of(2026, 8, 14)))
  }

  @Test
  fun `restores a LocalDate from its ISO-8601 form`() {
    assertEquals(LocalDate.of(2026, 8, 14), converters.toLocalDate("2026-08-14"))
  }

  @Test
  fun `ISO dates sort in the same order as the dates themselves`() {
    // This is what lets MAX(date) and date comparisons work directly in SQL.
    val earlier = converters.fromLocalDate(LocalDate.of(2026, 8, 9))!!
    val later = converters.fromLocalDate(LocalDate.of(2026, 8, 14))!!

    assertEquals(true, earlier < later)
  }

  @Test
  fun `passes nulls through in both directions`() {
    assertNull(converters.fromBigDecimal(null))
    assertNull(converters.toBigDecimal(null))
    assertNull(converters.fromLocalDate(null))
    assertNull(converters.toLocalDate(null))
  }
}
