package com.example.valtiva.domain.model

import java.math.MathContext
import java.math.RoundingMode

/**
 * Single source of truth for how monetary arithmetic is carried out in the domain.
 *
 * Intermediate arithmetic uses [MATH_CONTEXT] (34 significant digits) so that multiplying an
 * amount by a rate loses no meaningful precision. Only the final monetary value is quantized to
 * [SCALE] decimals using [ROUNDING].
 *
 * [RoundingMode.HALF_EVEN] ("banker's rounding") is preferred over `HALF_UP` because it does not
 * systematically bias results upwards when ties occur repeatedly, which is the standard choice for
 * financial computation.
 *
 * Known limitation: [SCALE] is fixed at two decimals for every currency. Currencies with a
 * different number of minor units (JPY uses 0, KWD uses 3) are still rounded to two decimals.
 * Making the scale currency-aware needs minor-unit metadata that the rates provider does not
 * supply, so it is deliberately deferred rather than guessed.
 */
object MonetaryPrecision {
  /** Number of decimals kept in a final monetary amount. */
  const val SCALE: Int = 2

  /** Rounding applied when quantizing a final monetary amount to [SCALE]. */
  val ROUNDING: RoundingMode = RoundingMode.HALF_EVEN

  /** Precision used for intermediate arithmetic, before the final rounding. */
  val MATH_CONTEXT: MathContext = MathContext.DECIMAL128
}
