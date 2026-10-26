package com.example.valtiva.data.local.converter

import androidx.room.TypeConverter
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

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
