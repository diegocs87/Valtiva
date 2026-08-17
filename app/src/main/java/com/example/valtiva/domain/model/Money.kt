package com.example.valtiva.domain.model

import java.math.BigDecimal

/**
 * An amount of money expressed in a [currency].
 *
 * The amount is a [BigDecimal] because binary floating point (`Double`/`Float`) cannot represent
 * decimal fractions such as 0.1 exactly and accumulates error across operations, which is not
 * acceptable for monetary values.
 *
 * Negative amounts are rejected: in this app a [Money] is either an amount the user asked to
 * convert or the result of a conversion, and neither has a meaningful negative value. Should the
 * domain ever model debts or refunds, this rule has to be revisited.
 *
 * Beware that equality follows [BigDecimal.equals], which takes the scale into account: `10.0` and
 * `10.00` are the same number but *not* the same [Money]. Compare with [BigDecimal.compareTo] when
 * only the numeric value matters. Every amount produced by the domain is scaled through
 * [MonetaryPrecision], so results returned by the domain are always consistent among themselves.
 */
data class Money(
  val amount: BigDecimal,
  val currency: Currency,
) {
  init {
    require(amount.signum() >= 0) {
      "Monetary amount must not be negative, but was $amount ${currency.code}."
    }
  }
}
