package com.example.valtiva.presentation.navigation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * The app's top-level section switcher.
 *
 * Stateless like every other composable in this package: it is told which index is [selectedIndex]
 * and reports taps through [onDestinationSelected]. The pager it sits under owns that state, which
 * is what keeps tapping a tab and swiping to it the same action.
 */
@Composable
fun ValtivaBottomBar(
  destinations: List<ValtivaDestination>,
  selectedIndex: Int,
  onDestinationSelected: (Int) -> Unit,
  modifier: Modifier = Modifier,
) {
  NavigationBar(
    containerColor = MaterialTheme.colorScheme.surface,
    // The bar already sits on a tinted background; Material's default tonal overlay on top of it
    // reads as a second, slightly-off surface colour.
    tonalElevation = 0.dp,
    modifier = modifier,
  ) {
    destinations.forEachIndexed { index, destination ->
      val label = stringResource(destination.labelRes)
      val isSelected = index == selectedIndex
      NavigationBarItem(
        selected = isSelected,
        onClick = { onDestinationSelected(index) },
        icon = { Icon(imageVector = destination.icon, contentDescription = null) },
        label = {
          Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
        },
        colors = NavigationBarItemDefaults.colors(
          selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
          selectedTextColor = MaterialTheme.colorScheme.primary,
          indicatorColor = MaterialTheme.colorScheme.primaryContainer,
          unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
          unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        // The icon carries no description of its own, so the label is the item's accessible name.
        modifier = Modifier.semantics { selected = isSelected },
      )
    }
  }
}

/**
 * The same section switcher for windows wide enough that a bottom bar would only cost vertical
 * space: a landscape phone, a tablet, an unfolded foldable.
 *
 * It takes the same state and reports the same event as [ValtivaBottomBar], so the pager stays the
 * one source of truth whichever of the two the shell is showing.
 */
@Composable
fun ValtivaNavigationRail(
  destinations: List<ValtivaDestination>,
  selectedIndex: Int,
  onDestinationSelected: (Int) -> Unit,
  modifier: Modifier = Modifier,
) {
  NavigationRail(
    containerColor = MaterialTheme.colorScheme.surface,
    // The rail sits on the window's start edge, so it is the one that keeps clear of a camera
    // cutout or a system bar there, on top of the status and navigation bars it already handles.
    windowInsets = WindowInsets.systemBars.union(WindowInsets.displayCutout)
      .only(WindowInsetsSides.Start + WindowInsetsSides.Vertical),
    modifier = modifier,
  ) {
    // Centered in the rail rather than packed under its top edge, which is where a tall, narrow
    // strip of four items looks adrift.
    Spacer(modifier = Modifier.weight(1f))
    destinations.forEachIndexed { index, destination ->
      val label = stringResource(destination.labelRes)
      val isSelected = index == selectedIndex
      NavigationRailItem(
        selected = isSelected,
        onClick = { onDestinationSelected(index) },
        icon = { Icon(imageVector = destination.icon, contentDescription = null) },
        label = {
          Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
        },
        colors = NavigationRailItemDefaults.colors(
          selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
          selectedTextColor = MaterialTheme.colorScheme.primary,
          indicatorColor = MaterialTheme.colorScheme.primaryContainer,
          unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
          unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        modifier = Modifier.semantics { selected = isSelected },
      )
    }
    Spacer(modifier = Modifier.weight(1f))
  }
}
