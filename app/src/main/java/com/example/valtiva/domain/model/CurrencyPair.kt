package com.example.valtiva.domain.model

/**
 * An ordered pair of currencies.
 *
 * [base] is the currency being converted *from*; [quote] is the currency in which the value of one
 * unit of [base] is expressed. For USD/COP with a rate of 3980.25, `1 USD = 3980.25 COP`.
 *
 * The order carries meaning: USD/COP and COP/USD are different pairs and never compare equal.
 *
 * A pair whose [base] and [quote] are the same currency is allowed; it simply describes the
 * identity conversion, whose rate is always one.
 */
data class CurrencyPair(
  val base: Currency,
  val quote: Currency,
)
