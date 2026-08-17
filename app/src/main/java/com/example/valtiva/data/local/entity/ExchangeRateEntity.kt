package com.example.valtiva.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.math.BigDecimal
import java.time.LocalDate

/**
 * One stored rate: what one unit of [baseCurrency] was worth in [quoteCurrency] on [date].
 *
 * The primary key is the composite `(baseCurrency, quoteCurrency, date)`. A surrogate id would need
 * a separate unique index to express the same rule, and the natural key is what every query filters
 * by anyway. Because the key includes the date, the same pair can be stored once per day, which is
 * what makes historical lookups and a daily refresh possible; keying on the pair alone would
 * overwrite yesterday's rate.
 *
 * Currencies are persisted as plain ISO codes rather than as embedded domain objects: the table
 * only needs the identity of the currency, and the display name lives in `currencies`.
 */
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
