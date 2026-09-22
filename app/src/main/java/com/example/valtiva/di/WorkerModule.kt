package com.example.valtiva.di

import com.example.valtiva.domain.scheduler.RatesRefreshScheduler
import com.example.valtiva.worker.RefreshRatesScheduler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Binds the Domain-level [RatesRefreshScheduler] port to the WorkManager-backed implementation, so
 * use cases depend on the port only — the same inversion [RepositoryModule] gives repositories.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class WorkerModule {

  @Binds
  abstract fun bindRatesRefreshScheduler(
    implementation: RefreshRatesScheduler,
  ): RatesRefreshScheduler
}
