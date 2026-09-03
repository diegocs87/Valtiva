package com.example.valtiva.data.local.converter

import androidx.room.TypeConverter
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

/**
 * Converters for the types Room cannot persist on its own.
 *
 * **[BigDecimal] is stored as TEXT, never as REAL.** SQLite's REAL is a binary double, so
 * persisting a rate through it would reintroduce exactly the rounding error the domain avoids by
 * using [BigDecimal] in the first place. Text keeps every digit *and* the scale, so a value read
 * back is `equals` to the one written, not merely numerically close.
 *
 * **[LocalDate] is stored as an ISO-8601 TEXT** (`2026-08-14`). Zero-padded ISO dates sort
 * lexicographically in the same order as chronologically, so `MAX(date)` and `date` comparisons work
 * directly in SQL without a numeric encoding, and rows stay readable when inspecting the database.
 *
 * **[Instant] is stored as an epoch-millisecond INTEGER, not TEXT.** [Instant.toString] trims
 * trailing fractional-second digits, so two timestamps a millisecond apart can produce strings of
 * different lengths whose lexicographic order does not match their chronological order. An INTEGER
 * column sorts numerically, which is always correct, and it is what `ORDER BY timestamp` in
 * [com.example.valtiva.data.local.dao.ConversionHistoryDao] relies on.
 */
class ValtivaTypeConverters {

  @TypeConverter
  fun fromBigDecimal(value: BigDecimal?): String? = value?.toPlainString()

  @TypeConverter
  fun toBigDecimal(value: String?): BigDecimal? = value?.let(::BigDecimal)

  @TypeConverter
  fun fromLocalDate(value: LocalDate?): String? = value?.toString()

  @TypeConverter
  fun toLocalDate(value: String?): LocalDate? = value?.let(LocalDate::parse)

  @TypeConverter
  fun fromInstant(value: Instant?): Long? = value?.toEpochMilli()

  @TypeConverter
  fun toInstant(value: Long?): Instant? = value?.let(Instant::ofEpochMilli)
}
