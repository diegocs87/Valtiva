package com.example.valtiva.domain.model

import java.util.Locale

/**
 * A currency identified by its ISO 4217 alphabetic code.
 *
 * Modelled as a data class instead of an enum on purpose: the set of supported currencies is
 * decided by the rates provider at runtime, so supporting a new currency must never require a
 * code change.
 *
 * The primary constructor is strict and only accepts an already canonical code (three uppercase
 * letters). Use [Currency.of] to build a currency from unnormalized input such as an API payload
 * or text typed by the user.
 */
data class Currency(
  val code: String,
  val name: String = code,
  val symbol: String? = null,
) {
  init {
    require(code.isNotBlank()) { "Currency code must not be blank." }
    require(CODE_FORMAT.matches(code)) {
      "Currency code must be three uppercase letters (ISO 4217), but was '$code'."
    }
  }

  companion object {
    private val CODE_FORMAT = Regex("[A-Z]{3}")

    /**
     * Builds a [Currency] from a possibly unnormalized [code], trimming surrounding whitespace
     * and uppercasing it. Throws if the result is still not a valid ISO 4217 code.
     */
    fun of(code: String, name: String? = null, symbol: String? = null): Currency {
      val normalizedCode = code.trim().uppercase(Locale.ROOT)
      return Currency(normalizedCode, name ?: normalizedCode, symbol)
    }
  }
}
