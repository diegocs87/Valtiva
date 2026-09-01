package com.example.valtiva.presentation.converter

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.valtiva.domain.model.Currency
import com.example.valtiva.presentation.theme.Dimens

/**
 * The circular flag for a currency, used by [CurrencySelectorChip] and [FavoriteExchangeItem].
 *
 * The flag is the emoji [Currency.flagEmoji] derives from the ISO code rather than bundled artwork:
 * it needs no assets, no network and no per-country image source, and it stays correct for any
 * currency the provider starts publishing. Currencies that name no country fall back to
 * [CurrencyFlagPlaceholder].
 */
@Composable
fun CurrencyFlag(
  currency: Currency?,
  modifier: Modifier = Modifier,
  size: Dp = Dimens.CurrencyBadgeSize,
) {
  val flag = currency?.flagEmoji()
  if (flag == null) {
    CurrencyFlagPlaceholder(modifier = modifier, size = size)
    return
  }
  val glyphSize = with(LocalDensity.current) { (size * FLAG_GLYPH_RATIO).toSp() }
  Box(
    modifier = modifier
      .size(size)
      .clip(CircleShape)
      .background(MaterialTheme.colorScheme.surfaceVariant),
    contentAlignment = Alignment.Center,
  ) {
    Text(text = flag, fontSize = glyphSize, lineHeight = glyphSize)
  }
}

/**
 * How much of the circle the flag glyph fills. Emoji carry their own side bearings, so a glyph sized
 * to the full diameter would overflow the circle it is centred in.
 */
private const val FLAG_GLYPH_RATIO = 0.82f

/**
 * The neutral stand-in for a currency with no flag: none is selected yet, or the code names no
 * country (see [Currency.flagEmoji]).
 */
@Composable
fun CurrencyFlagPlaceholder(modifier: Modifier = Modifier, size: Dp = Dimens.CurrencyBadgeSize) {
  Box(
    modifier = modifier
      .size(size)
      .clip(CircleShape)
      .background(MaterialTheme.colorScheme.surfaceVariant),
    contentAlignment = Alignment.Center,
  ) {
    Icon(
      imageVector = Icons.Filled.Public,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.size(size * 0.6f),
    )
  }
}

/**
 * Reserves the small trend-line slot inside a favorite row.
 *
 * A [com.example.valtiva.domain.model.FavoriteExchange] stores only the pair, never a rate
 * history, so there is no real series to plot yet. This draws a fixed placeholder line so the
 * layout already has the size and shape a real sparkline will need.
 */
@Composable
fun FavoriteRateSparkline(modifier: Modifier = Modifier) {
  val lineColor = MaterialTheme.colorScheme.primary
  Canvas(modifier = modifier.size(width = 56.dp, height = 28.dp)) {
    val points = listOf(0.7f, 0.5f, 0.6f, 0.3f, 0.4f, 0.15f, 0.25f)
    val stepX = size.width / (points.size - 1)
    val path = Path().apply {
      points.forEachIndexed { index, value ->
        val x = index * stepX
        val y = size.height * value
        if (index == 0) moveTo(x, y) else lineTo(x, y)
      }
    }
    drawPath(path = path, color = lineColor, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round))
  }
}
