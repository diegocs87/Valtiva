package com.example.valtiva.domain

import com.example.valtiva.domain.model.Currency
import java.time.LocalDate

internal val USD = Currency.of("USD", "US Dollar", "\$")
internal val COP = Currency.of("COP", "Colombian Peso", "\$")
internal val EUR = Currency.of("EUR", "Euro", "€")

/** An ISO 4217 "X-series" code (gold) that names no country, so no flag can be derived for it. */
internal val XAU = Currency.of("XAU", "Gold")

/** Fixed date used across the domain tests: the domain never reads the system clock. */
internal val RATE_DATE: LocalDate = LocalDate.of(2026, 8, 14)
