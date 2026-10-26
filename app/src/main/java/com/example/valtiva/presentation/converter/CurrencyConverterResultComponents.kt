package com.example.valtiva.presentation.converter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.valtiva.R
import com.example.valtiva.domain.model.ConversionResult
import com.example.valtiva.domain.model.FavoriteExchange
import com.example.valtiva.presentation.common.inTextSize
import com.example.valtiva.presentation.theme.Dimens

/**
 * The rate a [ConversionResult] was computed with, plus how fresh it is.
 *
 * [ConversionResult.rateDate] is the only place the domain exposes when a rate was published, so
 * the "daily exchange rate" note only appears alongside an actual conversion; there is nothing
 * reliable to show it against beforehand, and inventing a date would violate the one thing this
 * note exists to be honest about.
 */
@Composable
fun RateSummary(conversion: ConversionResult, modifier: Modifier = Modifier) {
  Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Dimens.SpaceXs)) {
    Text(
      text = stringResource(
        R.string.rate_line,
        conversion.source.currency.code,
        conversion.exchangeRate.toDisplayAmount(),
        conversion.target.currency.code,
      ),
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurface,
    )
    Text(
      text = stringResource(R.string.daily_rate_label),
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Text(
      text = stringResource(R.string.rate_updated_on, conversion.rateDate.toDisplayDate()),
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}

/**
 * A non-lazy list of favorites: it lives inside the screen's own scrolling
 * [androidx.compose.foundation.layout.Column], and the favorites count is small enough that a
 * [androidx.compose.foundation.lazy.LazyColumn] (which needs its own bounded height in that
 * context) would buy nothing.
 */
@Composable
fun FavoritesSection(
  favorites: List<FavoriteExchange>,
  unavailable: Boolean,
  onFavoriteSelected: (FavoriteExchange) -> Unit,
  onFavoriteRemoved: (FavoriteExchange) -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Dimens.SpaceSm)) {
    Text(
      text = stringResource(R.string.favorites_title),
      style = MaterialTheme.typography.titleMedium,
      color = MaterialTheme.colorScheme.onBackground,
    )
    when {
      unavailable -> Text(
        text = stringResource(R.string.favorites_unavailable),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      favorites.isEmpty() -> Text(
        text = stringResource(R.string.favorites_empty),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      else -> Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpaceSm)) {
        favorites.forEach { favorite ->
          FavoriteExchangeItem(
            favorite = favorite,
            onClick = { onFavoriteSelected(favorite) },
            onRemove = { onFavoriteRemoved(favorite) },
          )
        }
      }
    }
  }
}

/**
 * A saved pair rendered as a selectable card rather than plain text with a delete icon: the whole
 * row picks the pair, a trailing icon only removes it.
 *
 * No rate is shown for the pair, unlike [RateSummary]: a [FavoriteExchange] stores only the pair
 * ([com.example.valtiva.domain.model.FavoriteExchange]), never a rate, since a saved rate would be
 * stale the moment it was written. Showing one here would mean fetching it specially for this list,
 * which is a data concern this etapa leaves untouched.
 */
@Composable
fun FavoriteExchangeItem(
  favorite: FavoriteExchange,
  onClick: () -> Unit,
  onRemove: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Card(
    onClick = onClick,
    shape = MaterialTheme.shapes.medium,
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    modifier = modifier.fillMaxWidth(),
  ) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
      // The sparkline and the star are ornament; the pair and the remove button are the content.
      // When the row is too tight for all of them it is the ornament that goes, not the text
      // squeezed down to a sliver beside it.
      val showOrnaments = maxWidth.inTextSize() >= Dimens.CompactRowWidth
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = Dimens.SpaceMd, vertical = Dimens.SpaceMd),
      ) {
        CurrencyFlag(currency = favorite.pair.base, size = Dimens.CurrencyBadgeSize)
        Spacer(modifier = Modifier.size(Dimens.SpaceMd))
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "${favorite.pair.base.code} → ${favorite.pair.quote.code}",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
          )
          Text(
            text = stringResource(R.string.favorite_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
        if (showOrnaments) {
          FavoriteRateSparkline()
          Spacer(modifier = Modifier.size(Dimens.SpaceSm))
          Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(Dimens.IconSizeMedium),
          )
        }
        val deleteDescription = stringResource(R.string.favorite_delete_content_description)
        IconButton(
          onClick = onRemove,
          modifier = Modifier.semantics { contentDescription = deleteDescription },
        ) {
          Icon(imageVector = Icons.Filled.Close, contentDescription = null)
        }
      }
    }
  }
}
