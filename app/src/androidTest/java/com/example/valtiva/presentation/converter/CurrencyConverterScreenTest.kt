package com.example.valtiva.presentation.converter

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.valtiva.domain.model.ConversionResult
import com.example.valtiva.domain.model.Currency
import com.example.valtiva.domain.model.CurrencyPair
import com.example.valtiva.domain.model.FavoriteExchange
import com.example.valtiva.domain.model.Money
import com.example.valtiva.presentation.theme.ValtivaTheme
import java.math.BigDecimal
import java.time.LocalDate
import org.junit.Rule
import org.junit.Test

/**
 * UI tests for [CurrencyConverterScreen], covering the critical functional flows this etapa
 * exists to prove: the screen renders, the form can be edited, and the events the ViewModel
 * expects are actually the ones the composables send.
 *
 * The screen is exercised as a stateless composable fed fixed [CurrencyConverterUiState]s: no
 * ViewModel, no Hilt, no Room, no Retrofit.
 */
class CurrencyConverterScreenTest {

  @get:Rule
  val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  private val usd = Currency.of("USD")
  private val cop = Currency.of("COP")

  @Test
  fun initialState_showsForm() {
    setScreen(CurrencyConverterUiState(baseCurrency = usd))

    composeTestRule.onNodeWithText("Conversor de divisas").assertExists()
  }

  @Test
  fun enteringAmount_sendsAmountChangedEvent() {
    var lastEvent: CurrencyConverterUiEvent? = null
    setScreen(
      state = CurrencyConverterUiState(baseCurrency = usd),
      onEvent = { lastEvent = it },
    )

    composeTestRule.onNodeWithContentDescription("Monto").performTextInput("100")

    assert(lastEvent == CurrencyConverterUiEvent.AmountChanged("100"))
  }

  @Test
  fun convertClicked_sendsConvertClickedEvent() {
    var lastEvent: CurrencyConverterUiEvent? = null
    setScreen(
      state = CurrencyConverterUiState(amount = "100", baseCurrency = usd, targetCurrency = cop),
      onEvent = { lastEvent = it },
    )

    composeTestRule.onNodeWithText("Convertir").performClick()

    assert(lastEvent == CurrencyConverterUiEvent.ConvertClicked)
  }

  @Test
  fun successStatus_showsConversionResult() {
    val conversion = ConversionResult(
      source = Money(BigDecimal("100"), usd),
      target = Money(BigDecimal("398000"), cop),
      exchangeRate = BigDecimal("3980"),
      rateDate = LocalDate.of(2026, 8, 14),
    )
    setScreen(
      CurrencyConverterUiState(
        amount = "100",
        baseCurrency = usd,
        targetCurrency = cop,
        status = CurrencyConverterUiStatus.Success(conversion),
      ),
    )

    composeTestRule.onNodeWithText("398,000.00").assertExists()
    composeTestRule.onNodeWithText("1 USD = 3,980.00 COP").assertExists()
  }

  @Test
  fun favorites_areShownAndSelectable() {
    var lastEvent: CurrencyConverterUiEvent? = null
    val favorite = FavoriteExchange(CurrencyPair(usd, cop))
    setScreen(
      state = CurrencyConverterUiState(baseCurrency = usd, favorites = listOf(favorite)),
      onEvent = { lastEvent = it },
    )

    composeTestRule.onNodeWithText("USD → COP").assertExists().performClick()

    assert(lastEvent == CurrencyConverterUiEvent.FavoriteSelected(favorite))
  }

  private fun setScreen(
    state: CurrencyConverterUiState,
    onEvent: (CurrencyConverterUiEvent) -> Unit = {},
  ) {
    composeTestRule.setContent {
      ValtivaTheme {
        CurrencyConverterScreen(state = state, onEvent = onEvent)
      }
    }
  }
}
