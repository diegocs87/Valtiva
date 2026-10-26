package com.example.valtiva.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

@Entity(tableName = "conversion_history")
data class ConversionHistoryEntity(
  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name = "id")
  val id: Long = 0,
  @ColumnInfo(name = "source_amount") val sourceAmount: BigDecimal,
  @ColumnInfo(name = "base_currency") val baseCurrency: String,
  @ColumnInfo(name = "target_amount") val targetAmount: BigDecimal,
  @ColumnInfo(name = "quote_currency") val quoteCurrency: String,
  @ColumnInfo(name = "rate") val rate: BigDecimal,
  @ColumnInfo(name = "rate_date") val rateDate: LocalDate,
  @ColumnInfo(name = "timestamp") val timestamp: Instant,
)
