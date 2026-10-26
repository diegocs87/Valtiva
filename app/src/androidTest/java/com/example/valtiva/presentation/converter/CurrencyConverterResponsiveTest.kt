package com.example.valtiva.presentation.converter

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.example.valtiva.domain.model.Currency
import com.example.valtiva.domain.model.CurrencyPair
import com.example.valtiva.domain.model.FavoriteExchange
import com.example.valtiva.presentation.theme.ValtivaTheme
import org.junit.Rule
import org.junit.Test

/**
 * Checks that the converter stays usable under the window sizes and font scales that broke it
 * before: every primary control can be reached and none is wider than the window.
 *
 * The window is simulated inside the composition (a fixed-size box and a [Density] with the font
 * scale), so nothing about the device's own settings has to change to run it.
 */
class CurrencyConverterResponsiveTest {

  @get:Rule
  val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  private val usd = Currency.of("USD")
  private val cop = Currency.of("COP")

  private val state = CurrencyConverterUiState(
    amount = "123456789012.50",
    baseCurrency = usd,
    targetCurrency = cop,
    favorites = listOf(FavoriteExchange(CurrencyPair(usd, cop))),
  )

  @Test fun compactPhone() = assertReachable(widthDp = 320, heightDp = 568, fontScale = 1f)

  @Test fun compactPhone_largestFont() = assertReachable(widthDp = 320, heightDp = 568, fontScale = 2f)

  @Test fun regularPhone_largeFont() = assertReachable(widthDp = 411, heightDp = 914, fontScale = 1.5f)

  @Test fun phoneLandscape() = assertReachable(widthDp = 800, heightDp = 360, fontScale = 1f)

  @Test fun tablet() = assertReachable(widthDp = 1280, heightDp = 800, fontScale = 1f)

  private companion object {
    // The state's pair is already a favorite, so the toggle offers to remove it.
    const val FAVORITE_TOGGLE_LABEL = "Quitar de favoritos"
  }

  private fun assertReachable(widthDp: Int, heightDp: Int, fontScale: Float) {
    composeTestRule.setContent {
      val base = LocalDensity.current
      CompositionLocalProvider(LocalDensity provides Density(base.density, fontScale)) {
        ValtivaTheme {
          Box(modifier = Modifier.size(widthDp.dp, heightDp.dp)) {
            CurrencyConverterScreen(state = state, onEvent = {})
          }
        }
      }
    }

    listOf("Convertir", "Actualizar tasas", FAVORITE_TOGGLE_LABEL).forEach { text ->
      val node = composeTestRule.onNodeWithText(text)
      node.performScrollTo().assertIsDisplayed()
      val bounds = node.getUnclippedBoundsInRoot()
      assert(bounds.left >= 0.dp && bounds.right <= widthDp.dp) {
        "\"$text\" spans ${bounds.left}..${bounds.right}, outside a ${widthDp}dp window"
      }
    }
    composeTestRule.onNodeWithContentDescription("Monto").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithText("USD → COP").performScrollTo().assertIsDisplayed()
  }
}
