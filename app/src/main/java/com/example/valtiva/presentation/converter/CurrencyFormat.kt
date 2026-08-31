package com.example.valtiva.presentation.converter

import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Formats a monetary amount for display, e.g. `4093.2` -> `"4,093.20"`.
 *
 * `Locale.US` is fixed rather than the device locale: none of the app's strings are localized yet
 * (a single, English-only `strings.xml`), so a locale-driven decimal separator here would read
 * inconsistently against text that never changes with it.
 */
fun BigDecimal.toDisplayAmount(): String =
  DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale.US)).format(this)

private val RateDateFormatter = DateTimeFormatter.ofPattern("MMM d", Locale.US)

/** Formats a rate's publish date for display, e.g. `2026-08-30` -> `"Aug 30"`. */
fun LocalDate.toDisplayDate(): String = format(RateDateFormatter)
