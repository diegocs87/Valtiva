package com.example.valtiva.domain.model

import java.util.Locale

private const val REGEX_CURRENCY = "[A-Z]{3}"

private const val ERROR_MSG_ON_BLANK_CURRENCY_CODE = "Currency code must not be blank."

data class Currency(
  val code: String,
  val name: String = code,
  val symbol: String? = null,
) {
  init {
    checkForAValidCurrency()
  }

  // Currency identity is defined by its ISO code.
  override fun equals(other: Any?): Boolean =
    this === other || (other is Currency && code == other.code)

  override fun hashCode(): Int = code.hashCode()

  companion object {
    private val CODE_FORMAT = Regex(REGEX_CURRENCY)

    fun of(code: String, name: String? = null, symbol: String? = null): Currency {
      val normalizedCode = code.trim().uppercase(Locale.ROOT)
      return Currency(normalizedCode, name ?: normalizedCode, symbol)
    }
  }

  private fun checkForAValidCurrency() {
    require(code.isNotBlank()) { ERROR_MSG_ON_BLANK_CURRENCY_CODE }
    require(CODE_FORMAT.matches(code)) {
      "Currency code must be three uppercase letters (ISO 4217), but was '$code'."
    }
  }
}
