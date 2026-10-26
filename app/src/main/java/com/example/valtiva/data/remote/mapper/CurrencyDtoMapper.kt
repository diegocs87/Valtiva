package com.example.valtiva.data.remote.mapper

import com.example.valtiva.data.remote.dto.APICurrencyDto
import com.example.valtiva.domain.model.Currency

/**
 * Turns a wire currency into its domain counterpart.
 *
 * [Currency.of] normalizes and validates the ISO code, so a malformed one fails loudly here instead
 * of travelling further. A blank symbol is treated as absent: the API sends an empty string for
 * currencies without one, and the domain expresses "no symbol" as `null`.
 */
fun APICurrencyDto.toDomain(): Currency = Currency.of(
  code = isoCode,
  name = name,
  symbol = symbol?.takeIf { it.isNotBlank() },
)
