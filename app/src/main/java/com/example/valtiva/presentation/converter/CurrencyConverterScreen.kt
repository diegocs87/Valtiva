package com.example.valtiva.presentation.converter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.valtiva.R
import com.example.valtiva.domain.model.Currency
import com.example.valtiva.presentation.common.ResponsiveScreenColumn
import com.example.valtiva.presentation.common.inTextSize
import com.example.valtiva.presentation.converter.CurrencyConverterUiEvent.AmountChanged
import com.example.valtiva.presentation.converter.CurrencyConverterUiEvent.BaseCurrencySelected
import com.example.valtiva.presentation.converter.CurrencyConverterUiEvent.ConvertClicked
import com.example.valtiva.presentation.converter.CurrencyConverterUiEvent.FavoriteRemoved
import com.example.valtiva.presentation.converter.CurrencyConverterUiEvent.FavoriteSelected
import com.example.valtiva.presentation.converter.CurrencyConverterUiEvent.RefreshRatesClicked
import com.example.valtiva.presentation.converter.CurrencyConverterUiEvent.RetryClicked
import com.example.valtiva.presentation.converter.CurrencyConverterUiEvent.SwapCurrenciesClicked
import com.example.valtiva.presentation.converter.CurrencyConverterUiEvent.TargetCurrencySelected
import com.example.valtiva.presentation.converter.CurrencyConverterUiEvent.ToggleFavoriteClicked
import com.example.valtiva.presentation.theme.Dimens
import com.example.valtiva.presentation.theme.ValtivaTheme

/**
 * Renders [CurrencyConverterUiState] and turns user interaction into [CurrencyConverterUiEvent]s.
 *
 * Stateless by design: every value it shows comes from [state], every action it can trigger goes
 * out through [onEvent]. It performs no conversion, no favorite bookkeeping and no network or
 * database access of any kind.
 *
 * The layout follows the window: one scrolling column up to [Dimens.TwoPaneBreakpoint], and from
 * there the conversion form beside the favorites list, each pane scrolling on its own, so a tablet
 * or a landscape phone shows both without the user scrolling past one to reach the other.
 */
@Composable
fun CurrencyConverterScreen(
  state: CurrencyConverterUiState,
  onEvent: (CurrencyConverterUiEvent) -> Unit,
  modifier: Modifier = Modifier,
) {
  BoxWithConstraints(modifier = modifier.fillMaxSize()) {
    if (maxWidth >= Dimens.TwoPaneBreakpoint) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter,
      ) {
        Row(
          horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceXl),
          modifier = Modifier
            .widthIn(max = Dimens.TwoPaneMaxWidth)
            .fillMaxSize(),
        ) {
          Column(
            verticalArrangement = Arrangement.spacedBy(Dimens.SpaceXl),
            modifier = Modifier
              .weight(1f)
              .fillMaxSize()
              .verticalScroll(rememberScrollState())
              .padding(Dimens.ScreenPadding),
          ) {
            ConverterForm(state = state, onEvent = onEvent)
          }
          Column(
            modifier = Modifier
              .weight(1f)
              .fillMaxSize()
              .verticalScroll(rememberScrollState())
              .padding(Dimens.ScreenPadding),
          ) {
            ConverterFavorites(state = state, onEvent = onEvent)
          }
        }
      }
    } else {
      ResponsiveScreenColumn {
        ConverterForm(state = state, onEvent = onEvent)
        ConverterFavorites(state = state, onEvent = onEvent)
      }
    }
  }
}

/** Title, the conversion cards, the actions under them and the error banner: the form's half. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ConverterForm(
  state: CurrencyConverterUiState,
  onEvent: (CurrencyConverterUiEvent) -> Unit,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceMd),
    modifier = Modifier.fillMaxWidth(),
  ) {
    // The title takes whatever the icon leaves, so a long translation wraps instead of pushing
    // the icon out of the row.
    Text(
      text = stringResource(R.string.converter_title),
      style = MaterialTheme.typography.headlineSmall,
      color = MaterialTheme.colorScheme.onBackground,
      modifier = Modifier.weight(1f),
    )
    // Purely decorative for now: there is no notifications feature behind it yet, so it carries
    // no content description and fires no event.
    Icon(
      imageVector = Icons.Filled.Notifications,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.onBackground,
      modifier = Modifier.size(Dimens.IconSizeMedium),
    )
  }

  ConversionSection(state = state, onEvent = onEvent)

  Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpaceMd)) {
    ConvertButton(
      onClick = { onEvent(ConvertClicked) },
      loading = state.isLoading,
      enabled = state.canConvert,
    )
    // A FlowRow rather than a Row: with long labels or a large font the two buttons no longer fit
    // side by side, and the second should drop to its own line instead of being cut off. Each
    // takes its share of its line, so they match in width side by side and when stacked.
    FlowRow(
      horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceMd),
      verticalArrangement = Arrangement.spacedBy(Dimens.SpaceSm),
      itemVerticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.fillMaxWidth(),
    ) {
      RefreshRatesButton(
        onClick = { onEvent(RefreshRatesClicked) },
        loading = state.isLoading,
        enabled = !state.isLoading,
        modifier = Modifier.weight(1f),
      )
      ToggleFavoriteButton(
        isFavorite = state.isSelectedPairFavorite,
        onClick = { onEvent(ToggleFavoriteClicked) },
        enabled = !state.isLoading && state.targetCurrency != null,
        modifier = Modifier.weight(1f),
      )
    }
  }

  state.error?.let { error ->
    ErrorBanner(
      message = error.asMessage(),
      onRetry = if (state.isRetryable) ({ onEvent(RetryClicked) }) else null,
    )
  }
}

@Composable
private fun ConverterFavorites(
  state: CurrencyConverterUiState,
  onEvent: (CurrencyConverterUiEvent) -> Unit,
) {
  FavoritesSection(
    favorites = state.favorites,
    unavailable = state.favoritesUnavailable,
    onFavoriteSelected = { onEvent(FavoriteSelected(it)) },
    onFavoriteRemoved = { onEvent(FavoriteRemoved(it)) },
  )
}

/**
 * The "You send" / swap / "You receive" module: two independent cards with the swap button
 * overlapping the gap between them, plus the rate summary shown outside both cards.
 */
@Composable
private fun ConversionSection(
  state: CurrencyConverterUiState,
  onEvent: (CurrencyConverterUiEvent) -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Dimens.SpaceMd)) {
    Box(modifier = Modifier.fillMaxWidth()) {
      Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpaceLg)) {
        CurrencyCard(
          label = stringResource(R.string.you_send_label),
          contentDescriptionLabel = stringResource(R.string.base_currency_label),
          selectedCurrency = state.baseCurrency,
          currencies = state.availableCurrencies,
          onCurrencySelected = { onEvent(BaseCurrencySelected(it)) },
          enabled = !state.isLoading,
        ) {
          AmountField(
            amount = state.amount,
            onAmountChanged = { onEvent(AmountChanged(it)) },
            enabled = !state.isLoading,
            modifier = Modifier.weight(1f),
          )
        }

        CurrencyCard(
          label = stringResource(R.string.you_receive_label),
          contentDescriptionLabel = stringResource(R.string.target_currency_label),
          selectedCurrency = state.targetCurrency,
          currencies = state.availableCurrencies,
          onCurrencySelected = { onEvent(TargetCurrencySelected(it)) },
          enabled = !state.isLoading,
        ) {
          AmountDisplay(
            amount = state.conversion?.target?.amount?.toDisplayAmount(),
            modifier = Modifier.weight(1f),
          )
        }
      }

      SwapButton(
        onClick = { onEvent(SwapCurrenciesClicked) },
        enabled = !state.isLoading && state.targetCurrency != null,
        modifier = Modifier.align(Alignment.Center),
      )
    }

    state.conversion?.let { conversion ->
      RateSummary(
        conversion = conversion,
        modifier = Modifier.padding(horizontal = Dimens.SpaceXs),
      )
    }
  }
}

/**
 * One side of the conversion: its label, the amount and the currency chip with its name.
 *
 * Amount and chip share a row while that row has room for both. When it does not — a 320dp phone,
 * or a larger font scale that makes a 360dp one just as tight (see [inTextSize]) — they stack, the
 * amount on its own full-width line and the chip under it, rather than squeezing the amount to a
 * sliver beside a chip that cannot shrink.
 */
@Composable
private fun CurrencyCard(
  label: String,
  contentDescriptionLabel: String,
  selectedCurrency: Currency?,
  currencies: List<Currency>,
  onCurrencySelected: (Currency) -> Unit,
  enabled: Boolean,
  modifier: Modifier = Modifier,
  amountContent: @Composable RowScope.() -> Unit,
) {
  Card(
    shape = MaterialTheme.shapes.large,
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = modifier.fillMaxWidth(),
  ) {
    Column(modifier = Modifier.fillMaxWidth().padding(Dimens.CardPadding)) {
      Text(
        text = label,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      Spacer(modifier = Modifier.size(Dimens.SpaceXs))
      BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val stacked = maxWidth.inTextSize() < Dimens.CompactCardWidth
        val chip: @Composable (Alignment.Horizontal) -> Unit = { horizontalAlignment ->
          CurrencyChipWithName(
            contentDescriptionLabel = contentDescriptionLabel,
            selectedCurrency = selectedCurrency,
            currencies = currencies,
            onCurrencySelected = onCurrencySelected,
            enabled = enabled,
            horizontalAlignment = horizontalAlignment,
          )
        }
        if (stacked) {
          Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpaceSm)) {
            Row(modifier = Modifier.fillMaxWidth(), content = amountContent)
            chip(Alignment.Start)
          }
        } else {
          Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            amountContent()
            chip(Alignment.End)
          }
        }
      }
    }
  }
}

/** The currency chip with the currency's name under it, when the name says more than the code. */
@Composable
private fun CurrencyChipWithName(
  contentDescriptionLabel: String,
  selectedCurrency: Currency?,
  currencies: List<Currency>,
  onCurrencySelected: (Currency) -> Unit,
  enabled: Boolean,
  horizontalAlignment: Alignment.Horizontal,
) {
  Column(
    horizontalAlignment = horizontalAlignment,
    verticalArrangement = Arrangement.spacedBy(Dimens.SpaceXs),
  ) {
    CurrencySelectorChip(
      contentDescriptionLabel = contentDescriptionLabel,
      selected = selectedCurrency,
      currencies = currencies,
      onSelected = onCurrencySelected,
      enabled = enabled,
    )
    selectedCurrency?.displayName()?.let { name ->
      // The chip already shows the code; a name that is only the code again would be noise.
      if (name != selectedCurrency.code) {
        // Capped so a long name ("United Arab Emirates Dirham") wraps under the chip instead of
        // widening this column at the expense of the amount beside it.
        Text(
          text = name,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          textAlign = if (horizontalAlignment == Alignment.End) TextAlign.End else TextAlign.Start,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.widthIn(max = Dimens.CurrencyNameMaxWidth),
        )
      }
    }
  }
}

private val PreviewState = CurrencyConverterUiState(
  amount = "100",
  baseCurrency = CurrencyConverterUiState.DEFAULT_BASE_CURRENCY,
)

@Preview(name = "Phone", showBackground = true)
@Composable
private fun CurrencyConverterScreenPreview() {
  ValtivaTheme {
    CurrencyConverterScreen(state = PreviewState, onEvent = {})
  }
}

@Preview(name = "Compact 320dp, font 2x", widthDp = 320, heightDp = 568, fontScale = 2f, showBackground = true)
@Composable
private fun CurrencyConverterScreenCompactLargeFontPreview() {
  ValtivaTheme {
    CurrencyConverterScreen(state = PreviewState, onEvent = {})
  }
}

@Preview(name = "Tablet landscape", widthDp = 1280, heightDp = 800, showBackground = true)
@Composable
private fun CurrencyConverterScreenTabletPreview() {
  ValtivaTheme {
    CurrencyConverterScreen(state = PreviewState, onEvent = {})
  }
}

@Preview(name = "Dark", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun CurrencyConverterScreenDarkPreview() {
  ValtivaTheme(darkTheme = true) {
    CurrencyConverterScreen(state = PreviewState, onEvent = {})
  }
}
