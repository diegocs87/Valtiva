package com.example.valtiva.domain.model

import java.math.BigDecimal
import java.time.LocalDate

/**
 * The outcome of converting [source] into [target].
 *
 * [exchangeRate] and [rateDate] are carried along so the caller can show which rate was applied
 * and how fresh it was, without having to look the rate up again.
 */
data class ConversionResult(
  val source: Money,
  val target: Money,
  val exchangeRate: BigDecimal,
  val rateDate: LocalDate,
)
