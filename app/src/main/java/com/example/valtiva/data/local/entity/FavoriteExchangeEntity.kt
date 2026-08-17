package com.example.valtiva.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

/**
 * A currency pair the user starred.
 *
 * Deliberately stores no rate and no date. A favorite is the pair itself; the rate changes daily and
 * lives in `exchange_rates`, so copying it here would go stale immediately and duplicate the source
 * of truth.
 *
 * The composite key `(baseCurrency, quoteCurrency)` makes the pair unique by construction, so
 * starring the same pair twice cannot create a duplicate row.
 */
@Entity(
  tableName = "favorite_exchanges",
  primaryKeys = ["base_currency", "quote_currency"],
)
data class FavoriteExchangeEntity(
  @ColumnInfo(name = "base_currency") val baseCurrency: String,
  @ColumnInfo(name = "quote_currency") val quoteCurrency: String,
)
