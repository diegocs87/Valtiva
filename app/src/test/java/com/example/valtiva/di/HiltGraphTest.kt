package com.example.valtiva.di

import com.example.valtiva.data.repository.ConversionHistoryRepositoryImpl
import com.example.valtiva.data.repository.ExchangeRateRepositoryImpl
import com.example.valtiva.data.repository.FavoriteExchangeRepositoryImpl
import com.example.valtiva.data.repository.RatesRefreshSettingsRepositoryImpl
import com.example.valtiva.data.repository.ThemeRepositoryImpl
import com.example.valtiva.domain.repository.ConversionHistoryRepository
import com.example.valtiva.domain.repository.ExchangeRateRepository
import com.example.valtiva.domain.repository.FavoriteExchangeRepository
import com.example.valtiva.domain.repository.RatesRefreshSettingsRepository
import com.example.valtiva.domain.repository.ThemeRepository
import com.example.valtiva.domain.scheduler.RatesRefreshScheduler
import com.example.valtiva.domain.usecase.ConvertCurrencyUseCase
import com.example.valtiva.domain.usecase.GetConversionHistoryUseCase
import com.example.valtiva.domain.usecase.GetDailyAutoRefreshEnabledUseCase
import com.example.valtiva.domain.usecase.GetFavoriteExchangesUseCase
import com.example.valtiva.domain.usecase.GetThemeModeUseCase
import com.example.valtiva.domain.usecase.SetDailyAutoRefreshEnabledUseCase
import com.example.valtiva.domain.usecase.SyncRatesRefreshSchedulingUseCase
import com.example.valtiva.presentation.converter.ConverterHandoffPublisher
import com.example.valtiva.presentation.converter.ConverterHandoffReceiver
import com.example.valtiva.worker.RefreshRatesScheduler
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import javax.inject.Inject
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Materializes the production Hilt graph end to end.
 *
 * This test exists because Dagger's compile-time validation only covers bindings something actually
 * requests: a missing provider anywhere under the repositories would compile fine and only explode
 * at runtime. Injecting the deepest consumers here forces the whole chain — repository -> data
 * sources -> DAO/API -> database/Retrofit — to be resolvable for real.
 *
 * The ViewModels need no case of their own: `@HiltViewModel` generates a binding that Dagger does
 * validate at compile time, so a use case it could not build would already fail the build.
 */
@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class)
class HiltGraphTest {

  @get:Rule
  val hiltRule = HiltAndroidRule(this)

  @Inject lateinit var exchangeRateRepository: ExchangeRateRepository
  @Inject lateinit var exchangeRateRepositoryAgain: ExchangeRateRepository
  @Inject lateinit var favoriteExchangeRepository: FavoriteExchangeRepository
  @Inject lateinit var conversionHistoryRepository: ConversionHistoryRepository
  @Inject lateinit var themeRepository: ThemeRepository
  @Inject lateinit var ratesRefreshSettingsRepository: RatesRefreshSettingsRepository
  @Inject lateinit var ratesRefreshScheduler: RatesRefreshScheduler
  @Inject lateinit var convertCurrency: ConvertCurrencyUseCase
  @Inject lateinit var getFavoriteExchanges: GetFavoriteExchangesUseCase
  @Inject lateinit var getConversionHistory: GetConversionHistoryUseCase
  @Inject lateinit var getThemeMode: GetThemeModeUseCase
  @Inject lateinit var getDailyAutoRefreshEnabled: GetDailyAutoRefreshEnabledUseCase
  @Inject lateinit var setDailyAutoRefreshEnabled: SetDailyAutoRefreshEnabledUseCase
  @Inject lateinit var syncRatesRefreshScheduling: SyncRatesRefreshSchedulingUseCase
  @Inject lateinit var converterHandoffPublisher: ConverterHandoffPublisher
  @Inject lateinit var converterHandoffReceiver: ConverterHandoffReceiver

  @Before
  fun setUp() {
    hiltRule.inject()
  }

  @Test
  fun `resolves the repository contracts to the data implementations`() {
    assertTrue(exchangeRateRepository is ExchangeRateRepositoryImpl)
    assertTrue(favoriteExchangeRepository is FavoriteExchangeRepositoryImpl)
    assertTrue(conversionHistoryRepository is ConversionHistoryRepositoryImpl)
    assertTrue(themeRepository is ThemeRepositoryImpl)
    assertTrue(ratesRefreshSettingsRepository is RatesRefreshSettingsRepositoryImpl)
    assertTrue(ratesRefreshScheduler is RefreshRatesScheduler)
  }

  @Test
  fun `the history hand-off publisher and receiver share the same coordinator`() {
    // Two interfaces, bound separately, must still resolve to one shared instance: a publisher
    // writing to a different object than the receiver reads from would be a broken hand-off.
    assertSame(converterHandoffPublisher, converterHandoffReceiver)
  }

  @Test
  fun `the exchange rate repository is a singleton so its sync mutex is shared`() {
    assertSame(exchangeRateRepository, exchangeRateRepositoryAgain)
  }

  @Test
  fun `use cases are assembled without the domain knowing any concrete class`() {
    // Reaching this point means Hilt built ConvertCurrencyUseCase from the domain interfaces alone:
    // repository -> data sources -> DAO/API -> database/Retrofit all resolved.
    assertNotSame(convertCurrency, getFavoriteExchanges)
  }
}
