package com.example.valtiva.di

import com.example.valtiva.domain.repository.ConversionHistoryRepository
import com.example.valtiva.domain.repository.ExchangeRateRepository
import com.example.valtiva.domain.repository.FavoriteExchangeRepository
import com.example.valtiva.domain.service.CurrencyConversionService
import com.example.valtiva.domain.usecase.AddFavoriteExchangeUseCase
import com.example.valtiva.domain.usecase.ConvertCurrencyUseCase
import com.example.valtiva.domain.usecase.GetConversionHistoryUseCase
import com.example.valtiva.domain.usecase.GetFavoriteExchangesUseCase
import com.example.valtiva.domain.usecase.GetHistoricalRatesUseCase
import com.example.valtiva.domain.usecase.GetLatestRatesUseCase
import com.example.valtiva.domain.usecase.RefreshRatesUseCase
import com.example.valtiva.domain.usecase.RemoveFavoriteExchangeUseCase
import com.example.valtiva.domain.usecase.SaveConversionHistoryUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.Clock

/**
 * Provides the domain use cases and the domain service.
 *
 * These are [Provides] factories instead of `@Inject` constructors by design: the domain module
 * stays free of every DI annotation (no javax.inject, no Dagger), and this composition module —
 * which lives in the app's wiring layer — is the only place that knows how to assemble it. The use
 * cases receive the *domain interfaces*; Hilt resolves them to the data implementations through
 * [RepositoryModule] without any consumer naming a concrete class.
 *
 * Everything here is stateless and cheap, hence unscoped.
 */
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

  @Provides
  fun provideCurrencyConversionService(): CurrencyConversionService = CurrencyConversionService()

  @Provides
  fun provideConvertCurrencyUseCase(
    exchangeRateRepository: ExchangeRateRepository,
    conversionService: CurrencyConversionService,
  ): ConvertCurrencyUseCase = ConvertCurrencyUseCase(exchangeRateRepository, conversionService)

  @Provides
  fun provideGetLatestRatesUseCase(
    exchangeRateRepository: ExchangeRateRepository,
  ): GetLatestRatesUseCase = GetLatestRatesUseCase(exchangeRateRepository)

  @Provides
  fun provideGetHistoricalRatesUseCase(
    exchangeRateRepository: ExchangeRateRepository,
  ): GetHistoricalRatesUseCase = GetHistoricalRatesUseCase(exchangeRateRepository)

  @Provides
  fun provideRefreshRatesUseCase(
    exchangeRateRepository: ExchangeRateRepository,
  ): RefreshRatesUseCase = RefreshRatesUseCase(exchangeRateRepository)

  @Provides
  fun provideGetFavoriteExchangesUseCase(
    favoriteExchangeRepository: FavoriteExchangeRepository,
  ): GetFavoriteExchangesUseCase = GetFavoriteExchangesUseCase(favoriteExchangeRepository)

  @Provides
  fun provideAddFavoriteExchangeUseCase(
    favoriteExchangeRepository: FavoriteExchangeRepository,
  ): AddFavoriteExchangeUseCase = AddFavoriteExchangeUseCase(favoriteExchangeRepository)

  @Provides
  fun provideRemoveFavoriteExchangeUseCase(
    favoriteExchangeRepository: FavoriteExchangeRepository,
  ): RemoveFavoriteExchangeUseCase = RemoveFavoriteExchangeUseCase(favoriteExchangeRepository)

  @Provides
  fun provideGetConversionHistoryUseCase(
    conversionHistoryRepository: ConversionHistoryRepository,
  ): GetConversionHistoryUseCase = GetConversionHistoryUseCase(conversionHistoryRepository)

  @Provides
  fun provideSaveConversionHistoryUseCase(
    conversionHistoryRepository: ConversionHistoryRepository,
    clock: Clock,
  ): SaveConversionHistoryUseCase = SaveConversionHistoryUseCase(conversionHistoryRepository, clock)
}
