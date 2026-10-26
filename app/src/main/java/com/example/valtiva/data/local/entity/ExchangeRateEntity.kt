package com.example.valtiva.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.math.BigDecimal
import java.time.LocalDate

@Entity(
  tableName = "exchange_rates",
  primaryKeys = ["base_currency", "quote_currency", "date"],
)
data class ExchangeRateEntity(
  @ColumnInfo(name = "base_currency") val baseCurrency: String,
  @ColumnInfo(name = "quote_currency") val quoteCurrency: String,
  @ColumnInfo(name = "rate") val rate: BigDecimal,
  @ColumnInfo(name = "date") val date: LocalDate,
)
