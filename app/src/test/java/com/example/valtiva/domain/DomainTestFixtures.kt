package com.example.valtiva.domain

import com.example.valtiva.domain.model.Currency
import java.time.LocalDate

internal val USD = Currency.of("USD", "US Dollar", "\$")
internal val COP = Currency.of("COP", "Colombian Peso", "\$")
internal val EUR = Currency.of("EUR", "Euro", "€")

/** Fixed date used across the domain tests: the domain never reads the system clock. */
internal val RATE_DATE: LocalDate = LocalDate.of(2026, 8, 14)
