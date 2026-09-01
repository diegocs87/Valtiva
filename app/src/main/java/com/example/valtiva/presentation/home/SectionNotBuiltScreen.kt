package com.example.valtiva.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.valtiva.R
import com.example.valtiva.presentation.navigation.ValtivaDestination
import com.example.valtiva.presentation.theme.Dimens

/**
 * The page behind a bottom-bar section that has no feature yet.
 *
 * It says so plainly instead of showing an empty list or a spinner: those read as a working screen
 * with nothing in it, which sends the user looking for the data they think is missing.
 */
@Composable
fun SectionNotBuiltScreen(destination: ValtivaDestination, modifier: Modifier = Modifier) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(Dimens.ScreenPadding),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
  ) {
    Icon(
      imageVector = destination.icon,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.size(48.dp),
    )
    Text(
      text = stringResource(R.string.section_not_built_title),
      style = MaterialTheme.typography.titleMedium,
      color = MaterialTheme.colorScheme.onBackground,
      modifier = Modifier.padding(top = Dimens.SpaceLg),
    )
    Text(
      text = stringResource(R.string.section_not_built_message, stringResource(destination.labelRes)),
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center,
      modifier = Modifier.padding(top = Dimens.SpaceSm),
    )
  }
}
