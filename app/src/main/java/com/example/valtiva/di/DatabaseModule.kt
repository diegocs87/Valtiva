package com.example.valtiva.di

import android.content.Context
import androidx.room.Room
import com.example.valtiva.data.local.ValtivaDatabase
import com.example.valtiva.data.local.dao.ConversionHistoryDao
import com.example.valtiva.data.local.dao.CurrencyDao
import com.example.valtiva.data.local.dao.ExchangeRateDao
import com.example.valtiva.data.local.dao.FavoriteExchangeDao
import com.example.valtiva.data.local.migration.MIGRATION_1_2
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Wires the local stack.
 *
 * The database is the one true singleton here: a second instance would mean a second connection
 * pool and invalidation tracker over the same file. The DAO providers are deliberately unscoped —
 * Room hands back its own cached DAO instances, so scoping them would only duplicate that guarantee.
 *
 * TypeConverters are not Hilt dependencies: they are registered on the database class itself and
 * Room instantiates them; Hilt only provides the configured [ValtivaDatabase].
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

  private const val DATABASE_NAME = "valtiva.db"

  @Provides
  @Singleton
  fun provideDatabase(@ApplicationContext context: Context): ValtivaDatabase =
    Room.databaseBuilder(context, ValtivaDatabase::class.java, DATABASE_NAME)
      .addMigrations(MIGRATION_1_2)
      .build()

  @Provides
  fun provideExchangeRateDao(database: ValtivaDatabase): ExchangeRateDao =
    database.exchangeRateDao()

  @Provides
  fun provideFavoriteExchangeDao(database: ValtivaDatabase): FavoriteExchangeDao =
    database.favoriteExchangeDao()

  @Provides
  fun provideCurrencyDao(database: ValtivaDatabase): CurrencyDao =
    database.currencyDao()

  @Provides
  fun provideConversionHistoryDao(database: ValtivaDatabase): ConversionHistoryDao =
    database.conversionHistoryDao()
}
