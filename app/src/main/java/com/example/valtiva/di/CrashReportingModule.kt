package com.example.valtiva.di

import com.example.valtiva.diagnostics.CrashReporter
import com.example.valtiva.diagnostics.FirebaseCrashReporter
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Binds the [CrashReporter] port to the Firebase-backed implementation, so ViewModels and workers
 * depend on the port only — the same inversion [RepositoryModule] gives repositories.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class CrashReportingModule {

  @Binds
  abstract fun bindCrashReporter(implementation: FirebaseCrashReporter): CrashReporter

  companion object {

    @Provides
    @Singleton
    fun provideFirebaseCrashlytics(): FirebaseCrashlytics = FirebaseCrashlytics.getInstance()
  }
}
