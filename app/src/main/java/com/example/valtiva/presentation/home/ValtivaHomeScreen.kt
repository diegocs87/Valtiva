package com.example.valtiva.presentation.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.example.valtiva.presentation.converter.CurrencyConverterRoute
import com.example.valtiva.presentation.navigation.ValtivaBottomBar
import com.example.valtiva.presentation.navigation.ValtivaDestination
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
 * the content padding it computes rather than each screen reaching for the window insets itself.
 */
@Composable
fun ValtivaHomeScreen(modifier: Modifier = Modifier) {
  val destinations = remember { ValtivaDestination.entries }
  val pagerState = rememberPagerState { destinations.size }
  val scope = rememberCoroutineScope()

  Scaffold(
    modifier = modifier,
    containerColor = MaterialTheme.colorScheme.background,
    bottomBar = {
      ValtivaBottomBar(
        destinations = destinations,
        selectedIndex = pagerState.currentPage,
        onDestinationSelected = { index ->
          // Animated rather than instant, so a tap lands the user on the page with the same motion
          // a swipe would have given them.
          scope.launch { pagerState.animateScrollToPage(index) }
        },
      )
    },
  ) { contentPadding ->
    HorizontalPager(
      state = pagerState,
      modifier = Modifier
        .fillMaxSize()
        .padding(contentPadding),
    ) { page ->
      when (val destination = destinations[page]) {
        ValtivaDestination.CONVERTER -> CurrencyConverterRoute(modifier = Modifier.fillMaxSize())
        else -> SectionNotBuiltScreen(destination = destination)
      }
    }
  }
}
