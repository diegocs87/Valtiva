package com.example.valtiva.data.local.mapper

import com.example.valtiva.data.local.entity.ConversionHistoryEntity
import com.example.valtiva.domain.COP
import com.example.valtiva.domain.RATE_DATE
import com.example.valtiva.domain.USD
import com.example.valtiva.domain.model.ConversionResult
import com.example.valtiva.domain.model.Money
import java.math.BigDecimal
import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Test

class ConversionHistoryEntityMapperTest {

  private val conversion = ConversionResult(
    source = Money(BigDecimal("100.00"), USD),
    target = Money(BigDecimal("398025.00"), COP),
    exchangeRate = BigDecimal("3980.25"),
    rateDate = RATE_DATE,
  )
  private val performedAt: Instant = Instant.parse("2026-08-14T10:00:00Z")

  @Test
  fun `maps a completed conversion to a new stored row`() {
    val entity = conversion.toEntity(performedAt)

    assertEquals(BigDecimal("100.00"), entity.sourceAmount)
    assertEquals("USD", entity.baseCurrency)
    assertEquals(BigDecimal("398025.00"), entity.targetAmount)
    assertEquals("COP", entity.quoteCurrency)
    assertEquals(BigDecimal("3980.25"), entity.rate)
    assertEquals(RATE_DATE, entity.rateDate)
    assertEquals(performedAt, entity.timestamp)
  }

  @Test
  fun `a new row has no id yet, leaving Room to assign one`() {
    assertEquals(0L, conversion.toEntity(performedAt).id)
  }

  @Test
  fun `maps a stored row back to a domain history entry`() {
    val entity = ConversionHistoryEntity(
      id = 7,
      sourceAmount = BigDecimal("100.00"),
      baseCurrency = "USD",
      targetAmount = BigDecimal("398025.00"),
      quoteCurrency = "COP",
      rate = BigDecimal("3980.25"),
      rateDate = RATE_DATE,
      timestamp = performedAt,
    )

    val mapped = entity.toDomain()

    assertEquals(7L, mapped.id)
    assertEquals(conversion, mapped.conversion)
    assertEquals(performedAt, mapped.performedAt)
  }

  @Test
  fun `round trips a conversion unchanged, aside from the assigned id`() {
    val entity = conversion.toEntity(performedAt).copy(id = 3)

    assertEquals(entity, entity.toDomain().conversion.toEntity(performedAt).copy(id = 3))
  }
}
