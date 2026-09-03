package com.example.valtiva.di

import com.example.valtiva.data.repository.ConversionHistoryRepositoryImpl
import com.example.valtiva.data.repository.ExchangeRateRepositoryImpl
import com.example.valtiva.data.repository.FavoriteExchangeRepositoryImpl
import com.example.valtiva.domain.repository.ConversionHistoryRepository
import com.example.valtiva.domain.repository.ExchangeRateRepository
import com.example.valtiva.domain.repository.FavoriteExchangeRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.Clock
import javax.inject.Singleton

/**
 * Binds the domain repository contracts to their data-layer implementations. Consumers only ever
 * see the domain interfaces; which class satisfies them is decided here and nowhere else.
 *
 * [ExchangeRateRepositoryImpl] **must** be a singleton: its per-day sync idempotency relies on a
 * mutex, and a mutex only serializes callers that share the instance. Two instances would mean two
 * locks and duplicate network calls under concurrency.
 *
 * [FavoriteExchangeRepositoryImpl] and [ConversionHistoryRepositoryImpl] are unscoped: both are
 * stateless pass-throughs, so nothing is gained by pinning one instance.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

  @Binds
  @Singleton
  abstract fun bindExchangeRateRepository(
    implementation: ExchangeRateRepositoryImpl,
  ): ExchangeRateRepository

  @Binds
  abstract fun bindFavoriteExchangeRepository(
    implementation: FavoriteExchangeRepositoryImpl,
  ): FavoriteExchangeRepository

  @Binds
  abstract fun bindConversionHistoryRepository(
    implementation: ConversionHistoryRepositoryImpl,
  ): ConversionHistoryRepository

  companion object {

    /**
     * The clock that defines "today" for rate freshness. Bound here (instead of a default
     * parameter, which Dagger would ignore) so tests keep injecting a fixed clock while production
     * gets the system one.
     */
    @Provides
    fun provideClock(): Clock = Clock.systemDefaultZone()
  }
}
