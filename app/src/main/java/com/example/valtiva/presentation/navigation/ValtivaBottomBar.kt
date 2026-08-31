package com.example.valtiva.presentation.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
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
        label = { Text(text = label, style = MaterialTheme.typography.labelSmall) },
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
