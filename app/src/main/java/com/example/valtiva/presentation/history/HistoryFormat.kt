package com.example.valtiva.presentation.history

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * `Locale.US` and the device's own time zone, for the same reason
 * [com.example.valtiva.presentation.converter.toDisplayAmount] fixes its locale: none of the app's
 * strings are localized yet, so a device-locale format next to English labels would read
 * inconsistently. The zone is still the device's, because "when I did this" only means something in
 * the user's own local time.
 */
private val HistoryTimestampFormatter =
  DateTimeFormatter.ofPattern("MMM d, yyyy · HH:mm", Locale.US).withZone(ZoneId.systemDefault())

/** Formats when a conversion was performed, e.g. `"Aug 30, 2026 · 10:32"`. */
fun Instant.toDisplayDateTime(): String = HistoryTimestampFormatter.format(this)
