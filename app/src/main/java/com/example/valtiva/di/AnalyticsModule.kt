package com.example.valtiva.di

import android.content.Context
import com.example.valtiva.analytics.AnalyticsTracker
import com.example.valtiva.analytics.FirebaseAnalyticsTracker
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Binds the [AnalyticsTracker] port to the Firebase-backed implementation, so ViewModels and
 * workers depend on the port only — the same inversion [RepositoryModule] gives repositories.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AnalyticsModule {

  @Binds
  abstract fun bindAnalyticsTracker(implementation: FirebaseAnalyticsTracker): AnalyticsTracker

  companion object {

    @Provides
    @Singleton
    fun provideFirebaseAnalytics(@ApplicationContext context: Context): FirebaseAnalytics =
      FirebaseAnalytics.getInstance(context)
  }
}
