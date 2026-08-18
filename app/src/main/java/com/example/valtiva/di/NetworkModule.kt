package com.example.valtiva.di

import com.example.valtiva.data.remote.FrankfurterJson
import com.example.valtiva.data.remote.api.FrankfurterApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

/**
 * Wires the remote stack.
 *
 * [OkHttpClient] and [Retrofit] are singletons because they own pooled resources (connections,
 * threads, dispatchers); creating them per-injection would defeat the pooling. The API proxy is a
 * singleton simply so the reflective proxy is built once.
 *
 * [FrankfurterJson] stays a plain top-level value rather than a binding: it is stateless
 * configuration, like the mappers, and Hilt only manages real dependencies.
 *
 * There is deliberately no connectivity abstraction here: the offline-first repository treats a
 * failed request as the signal, so nothing in the graph ever asks whether the device is online.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

  private const val FRANKFURTER_BASE_URL = "https://api.frankfurter.dev/"

  @Provides
  @Singleton
  fun provideOkHttpClient(): OkHttpClient = OkHttpClient()

  @Provides
  @Singleton
  fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
    .baseUrl(FRANKFURTER_BASE_URL)
    .client(okHttpClient)
    .addConverterFactory(FrankfurterJson.asConverterFactory("application/json".toMediaType()))
    .build()

  @Provides
  @Singleton
  fun provideFrankfurterApi(retrofit: Retrofit): FrankfurterApi =
    retrofit.create(FrankfurterApi::class.java)
}
