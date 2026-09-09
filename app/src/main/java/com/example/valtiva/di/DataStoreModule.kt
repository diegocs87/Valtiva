package com.example.valtiva.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val PREFERENCES_NAME = "valtiva_preferences"

/**
 * The delegate, not [DataStoreModule.provideThemeDataStore], is what actually guarantees a single
 * DataStore instance per file: it caches by [Context], and [ApplicationContext] is itself a
 * singleton. The [Singleton] scope on the provider only avoids re-reading that cache on every
 * injection.
 */
private val Context.themePreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
  name = PREFERENCES_NAME,
)

/** Wires the local key-value preference store used for simple user settings such as the theme. */
@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

  @Provides
  @Singleton
  fun provideThemeDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
    context.themePreferencesDataStore
}
