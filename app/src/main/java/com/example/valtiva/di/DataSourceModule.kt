package com.example.valtiva.di

import com.example.valtiva.data.local.source.RoomConversionHistoryDataSource
import com.example.valtiva.data.local.source.RoomExchangeRateDataSource
import com.example.valtiva.data.local.source.RoomFavoriteExchangeDataSource
import com.example.valtiva.data.remote.source.FrankfurterExchangeRateDataSource
import com.example.valtiva.data.source.LocalConversionHistoryDataSource
import com.example.valtiva.data.source.LocalExchangeRateDataSource
import com.example.valtiva.data.source.LocalFavoriteExchangeDataSource
import com.example.valtiva.data.source.RemoteExchangeRateDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Binds each data-source contract to its implementation, which is all the composition the layer
 * needs: the implementations carry their dependencies via constructor injection, so [Binds] is
 * enough and no factory code has to run.
 *
 * Unscoped on purpose: the data sources are stateless wrappers over the DAO/API (which hold the
 * actual shared state and are managed by their own modules), so a fresh instance per injection
 * costs two field writes and keeps the graph free of gratuitous singletons.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

  @Binds
  abstract fun bindLocalExchangeRateDataSource(
    implementation: RoomExchangeRateDataSource,
  ): LocalExchangeRateDataSource

  @Binds
  abstract fun bindRemoteExchangeRateDataSource(
    implementation: FrankfurterExchangeRateDataSource,
  ): RemoteExchangeRateDataSource

  @Binds
  abstract fun bindLocalFavoriteExchangeDataSource(
    implementation: RoomFavoriteExchangeDataSource,
  ): LocalFavoriteExchangeDataSource

  @Binds
  abstract fun bindLocalConversionHistoryDataSource(
    implementation: RoomConversionHistoryDataSource,
  ): LocalConversionHistoryDataSource
}
