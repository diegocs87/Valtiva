package com.example.valtiva.di

import com.example.valtiva.data.repository.ExchangeRateRepositoryImpl
import com.example.valtiva.data.repository.FavoriteExchangeRepositoryImpl
import com.example.valtiva.domain.repository.ExchangeRateRepository
import com.example.valtiva.domain.repository.FavoriteExchangeRepository
import com.example.valtiva.domain.usecase.ConvertCurrencyUseCase
import com.example.valtiva.domain.usecase.GetFavoriteExchangesUseCase
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
 * requests: with no ViewModels yet, a missing provider anywhere under the repositories would
 * compile fine and only explode at runtime. Injecting the deepest consumers here forces the whole
 * chain — repository -> data sources -> DAO/API -> database/Retrofit — to be resolvable for real.
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
  @Inject lateinit var convertCurrency: ConvertCurrencyUseCase
  @Inject lateinit var getFavoriteExchanges: GetFavoriteExchangesUseCase

  @Before
  fun setUp() {
    hiltRule.inject()
  }

  @Test
  fun `resolves the repository contracts to the data implementations`() {
    assertTrue(exchangeRateRepository is ExchangeRateRepositoryImpl)
    assertTrue(favoriteExchangeRepository is FavoriteExchangeRepositoryImpl)
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
