package com.example.valtiva.domain.model

import java.math.BigDecimal
import java.time.LocalDate

data class ConversionResult(
  val source: Money,
  val target: Money,
  val exchangeRate: BigDecimal,
  val rateDate: LocalDate,
)
