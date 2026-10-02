package com.example.valtiva.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.example.valtiva.presentation.theme.Dimens

/**
 * The scrolling page every top-level screen is built on: a vertically scrollable column with the
 * screen padding and spacing, capped at [Dimens.ContentMaxWidth] and centered in wider windows.
 *
 * It exists so that the four screens cannot drift apart on those rules, and so a tablet or a phone
 * in landscape gets a readable column rather than cards stretched across the whole window. The
 * scroll is what keeps the content reachable on short windows, large font scales and with the
 * keyboard open; [maxWidth] is only widened by the converter, which lays itself out in two panes.
 */
@Composable
fun ResponsiveScreenColumn(
  modifier: Modifier = Modifier,
  maxWidth: Dp = Dimens.ContentMaxWidth,
  content: @Composable ColumnScope.() -> Unit,
) {
  Box(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background),
    contentAlignment = Alignment.TopCenter,
  ) {
    Column(
      modifier = Modifier
        .widthIn(max = maxWidth)
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .padding(Dimens.ScreenPadding),
      verticalArrangement = Arrangement.spacedBy(Dimens.SpaceXl),
      content = content,
    )
  }
}

/**
 * This width as the user's text sees it: dp divided by the font scale. A row that has to fit text
 * is as cramped at 180dp with normal text as at 360dp with text twice the size, so comparing this
 * against a threshold decides "is there room" better than the raw width does.
 */
@Composable
fun Dp.inTextSize(): Dp = this / LocalDensity.current.fontScale
