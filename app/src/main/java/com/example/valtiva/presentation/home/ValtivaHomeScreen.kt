package com.example.valtiva.presentation.home

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.valtiva.presentation.converter.CurrencyConverterRoute
import com.example.valtiva.presentation.favorites.FavoritesRoute
import com.example.valtiva.presentation.history.HistoryRoute
import com.example.valtiva.presentation.navigation.ValtivaBottomBar
import com.example.valtiva.presentation.navigation.ValtivaDestination
import com.example.valtiva.presentation.navigation.ValtivaNavigationRail
import com.example.valtiva.presentation.settings.SettingsRoute
import com.example.valtiva.presentation.theme.Dimens
import kotlinx.coroutines.launch

/**
 * The app's home shell: the top-level sections as pages the user can swipe between, over a bottom
 * bar that reflects and changes which one is showing.
 *
 * A [HorizontalPager] rather than a `NavHost` per tab, because the two have different contracts and
 * only one of them can be swiped. The pager's [rememberPagerState] is the single source of truth for
 * the selected section, so a swipe and a tap on the bar are the same state change — the bar cannot
 * drift out of step with the page, which is the usual bug when a tab index is tracked separately.
 *
 * Insets are the [Scaffold]'s job here: the activity draws edge to edge, so the pages are padded by
 * the content padding it computes rather than each screen reaching for the window insets itself. It
 * also keeps clear of the display cutout, which matters once the phone is turned sideways, and it
 * pads the pages by the keyboard so a focused field is never left underneath it.
 *
 * The section switcher adapts to the window: a bottom bar while it is narrow, a side rail from
 * [Dimens.NavigationRailBreakpoint] up, where a bottom bar would only eat vertical space (landscape
 * phones) or sit far from the content (tablets).
 */
@Composable
fun ValtivaHomeScreen(modifier: Modifier = Modifier) {
  val destinations = remember { ValtivaDestination.entries }
  val pagerState = rememberPagerState { destinations.size }
  val scope = rememberCoroutineScope()

  // Animated rather than instant, so a tap lands the user on the page with the same motion a swipe
  // would have given them.
  val onDestinationSelected: (Int) -> Unit = { index ->
    scope.launch { pagerState.animateScrollToPage(index) }
  }

  BoxWithConstraints(modifier = modifier.fillMaxSize()) {
    val useRail = maxWidth >= Dimens.NavigationRailBreakpoint

    Scaffold(
      containerColor = MaterialTheme.colorScheme.background,
      contentWindowInsets = WindowInsets.systemBars.union(WindowInsets.displayCutout),
      bottomBar = {
        if (!useRail) {
          ValtivaBottomBar(
            destinations = destinations,
            selectedIndex = pagerState.currentPage,
            onDestinationSelected = onDestinationSelected,
          )
        }
      },
    ) { contentPadding ->
      Row(modifier = Modifier.fillMaxSize()) {
        if (useRail) {
          // The rail brings its own insets for the edge it sits on, so only the content beside it
          // takes the Scaffold's padding on the other sides.
          ValtivaNavigationRail(
            destinations = destinations,
            selectedIndex = pagerState.currentPage,
            onDestinationSelected = onDestinationSelected,
          )
        }
        // Beside a rail, the start edge is the rail's to inset: padding the pages by it too would
        // leave an empty strip between the two on a phone whose system bar is on that side.
        val layoutDirection = LocalLayoutDirection.current
        val pagePadding = if (useRail) {
          PaddingValues(
            top = contentPadding.calculateTopPadding(),
            end = if (layoutDirection == LayoutDirection.Ltr) {
              contentPadding.calculateRightPadding(layoutDirection)
            } else {
              contentPadding.calculateLeftPadding(layoutDirection)
            },
            bottom = contentPadding.calculateBottomPadding(),
          )
        } else {
          contentPadding
        }
        HorizontalPager(
          state = pagerState,
          modifier = Modifier
            .weight(1f)
            .fillMaxSize()
            .padding(pagePadding)
            // Declared as consumed first so imePadding() only adds what the keyboard takes beyond
            // the bar and system insets already accounted for, instead of stacking on top of them.
            .consumeWindowInsets(pagePadding)
            .imePadding(),
        ) { page ->
          when (destinations[page]) {
            ValtivaDestination.CONVERTER -> CurrencyConverterRoute(modifier = Modifier.fillMaxSize())
            ValtivaDestination.FAVORITES -> FavoritesRoute(modifier = Modifier.fillMaxSize())
            ValtivaDestination.HISTORY -> HistoryRoute(
              modifier = Modifier.fillMaxSize(),
              // "Set as current" hands the conversion to the converter's ViewModel on its own (see
              // ConverterHandoffPublisher/Receiver); this callback only brings that tab into view,
              // which is the pager's job, not the history feature's.
              onConversionSelected = {
                onDestinationSelected(destinations.indexOf(ValtivaDestination.CONVERTER))
              },
            )
            ValtivaDestination.SETTINGS -> SettingsRoute(modifier = Modifier.fillMaxSize())
          }
        }
      }
    }
  }
}
