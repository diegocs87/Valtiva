package com.example.valtiva.presentation.converter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.valtiva.R
import com.example.valtiva.domain.model.ConversionResult
import com.example.valtiva.domain.model.FavoriteExchange

@Composable
fun ConversionResultCard(conversion: ConversionResult, modifier: Modifier = Modifier) {
  Card(modifier = modifier.fillMaxWidth()) {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
      Text(
        text = stringResource(
          R.string.conversion_result,
          conversion.source.amount.toPlainString(),
          conversion.source.currency.code,
          conversion.target.amount.toPlainString(),
          conversion.target.currency.code,
        ),
        style = MaterialTheme.typography.titleMedium,
      )
      Text(
        text = stringResource(
          R.string.conversion_rate,
          conversion.source.currency.code,
          conversion.exchangeRate.toPlainString(),
          conversion.target.currency.code,
          conversion.rateDate.toString(),
        ),
        style = MaterialTheme.typography.bodySmall,
      )
    }
  }
}

/**
 * A simple, non-lazy list of favorites: it lives inside the screen's own scrolling
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
  Column(modifier = modifier.fillMaxWidth()) {
    Text(stringResource(R.string.favorites_title), style = MaterialTheme.typography.titleMedium)
    when {
      unavailable -> Text(stringResource(R.string.favorites_unavailable))
      favorites.isEmpty() -> Text(stringResource(R.string.favorites_empty))
      else -> Column {
        favorites.forEach { favorite ->
          FavoriteExchangeItem(
            favorite = favorite,
            onClick = { onFavoriteSelected(favorite) },
            onRemove = { onFavoriteRemoved(favorite) },
          )
          HorizontalDivider()
        }
      }
    }
  }
}

@Composable
fun FavoriteExchangeItem(
  favorite: FavoriteExchange,
  onClick: () -> Unit,
  onRemove: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 8.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    TextButton(onClick = onClick) {
      Text("${favorite.pair.base.code} → ${favorite.pair.quote.code}")
    }
    val description = stringResource(R.string.favorite_delete_content_description)
    IconButton(onClick = onRemove, modifier = Modifier.semantics { contentDescription = description }) {
      Text("✕")
    }
  }
}
