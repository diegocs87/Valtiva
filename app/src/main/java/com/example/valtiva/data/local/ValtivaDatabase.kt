package com.example.valtiva.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.valtiva.data.local.converter.ValtivaTypeConverters
import com.example.valtiva.data.local.dao.ConversionHistoryDao
import com.example.valtiva.data.local.dao.CurrencyDao
import com.example.valtiva.data.local.dao.ExchangeRateDao
import com.example.valtiva.data.local.dao.FavoriteExchangeDao
import com.example.valtiva.data.local.entity.ConversionHistoryEntity
import com.example.valtiva.data.local.entity.CurrencyEntity
import com.example.valtiva.data.local.entity.ExchangeRateEntity
import com.example.valtiva.data.local.entity.FavoriteExchangeEntity

/**
 * The local database, which is the source the app reads from while the network only refreshes it.
 *
 * `exportSchema` is on and the Room Gradle plugin writes the schema to `app/schemas`, so every
 * version is committed and migrations can be diffed and tested against them. Version 2 added
 * [ConversionHistoryEntity]; see [com.example.valtiva.data.local.migration.MIGRATION_1_2].
 */
@Database(
  entities = [
    ExchangeRateEntity::class,
    FavoriteExchangeEntity::class,
    CurrencyEntity::class,
    ConversionHistoryEntity::class,
  ],
  version = 2,
  exportSchema = true,
)
@TypeConverters(ValtivaTypeConverters::class)
abstract class ValtivaDatabase : RoomDatabase() {
  abstract fun exchangeRateDao(): ExchangeRateDao
  abstract fun favoriteExchangeDao(): FavoriteExchangeDao
  abstract fun currencyDao(): CurrencyDao
  abstract fun conversionHistoryDao(): ConversionHistoryDao
}
