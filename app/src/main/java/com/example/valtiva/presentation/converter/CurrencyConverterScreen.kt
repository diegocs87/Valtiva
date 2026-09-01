package com.example.valtiva.presentation.converter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.valtiva.R
import com.example.valtiva.domain.model.Currency
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
 */
@Composable
fun CurrencyConverterScreen(
  state: CurrencyConverterUiState,
  onEvent: (CurrencyConverterUiEvent) -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(MaterialTheme.colorScheme.background)
      .verticalScroll(rememberScrollState())
      .padding(Dimens.ScreenPadding),
    verticalArrangement = Arrangement.spacedBy(Dimens.SpaceXl),
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Text(
        text = stringResource(R.string.converter_title),
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.onBackground,
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
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
      ) {
        RefreshRatesButton(
          onClick = { onEvent(RefreshRatesClicked) },
          loading = state.isLoading,
          enabled = !state.isLoading,
        )
        ToggleFavoriteButton(
          isFavorite = state.isSelectedPairFavorite,
          onClick = { onEvent(ToggleFavoriteClicked) },
          enabled = !state.isLoading && state.targetCurrency != null,
        )
      }
    }

    state.error?.let { error ->
      ErrorBanner(
        message = error.asMessage(),
        onRetry = if (state.isRetryable) ({ onEvent(RetryClicked) }) else null,
      )
    }

    FavoritesSection(
      favorites = state.favorites,
      unavailable = state.favoritesUnavailable,
      onFavoriteSelected = { onEvent(FavoriteSelected(it)) },
      onFavoriteRemoved = { onEvent(FavoriteRemoved(it)) },
    )
  }
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
    shape = RoundedCornerShape(24.dp),
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
      Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        amountContent()
        Column(
          horizontalAlignment = Alignment.End,
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
              Text(
                text = name,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            }
          }
        }
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
private fun CurrencyConverterScreenPreview() {
  ValtivaTheme {
    CurrencyConverterScreen(
      state = CurrencyConverterUiState(
        amount = "100",
        baseCurrency = CurrencyConverterUiState.DEFAULT_BASE_CURRENCY,
      ),
      onEvent = {},
    )
  }
}
