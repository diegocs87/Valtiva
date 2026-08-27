package com.example.valtiva.presentation.converter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.valtiva.R
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
      .verticalScroll(rememberScrollState())
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    Text(text = stringResource(R.string.converter_title), style = MaterialTheme.typography.headlineSmall)

    AmountInput(
      amount = state.amount,
      onAmountChanged = { onEvent(AmountChanged(it)) },
      enabled = !state.isLoading,
    )

    CurrencySelector(
      label = stringResource(R.string.base_currency_label),
      selected = state.baseCurrency,
      currencies = state.availableCurrencies,
      onSelected = { onEvent(BaseCurrencySelected(it)) },
      enabled = !state.isLoading,
    )

    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      CurrencySelector(
        label = stringResource(R.string.target_currency_label),
        selected = state.targetCurrency,
        currencies = state.availableCurrencies,
        onSelected = { onEvent(TargetCurrencySelected(it)) },
        enabled = !state.isLoading,
        modifier = Modifier.weight(1f),
      )
      SwapCurrenciesButton(
        onClick = { onEvent(SwapCurrenciesClicked) },
        enabled = !state.isLoading && state.targetCurrency != null,
      )
    }

    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      ConvertButton(onClick = { onEvent(ConvertClicked) }, enabled = state.canConvert)
      ToggleFavoriteButton(
        isFavorite = state.isSelectedPairFavorite,
        onClick = { onEvent(ToggleFavoriteClicked) },
        enabled = !state.isLoading && state.targetCurrency != null,
      )
    }

    RefreshRatesButton(onClick = { onEvent(RefreshRatesClicked) }, enabled = !state.isLoading)

    if (state.isLoading) {
      CircularProgressIndicator()
    }

    state.conversion?.let { conversion -> ConversionResultCard(conversion) }

    state.error?.let { error ->
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        ErrorMessage(message = error.asMessage())
        if (state.isRetryable) {
          RetryButton(onClick = { onEvent(RetryClicked) })
        }
      }
    }

    FavoritesSection(
      favorites = state.favorites,
      unavailable = state.favoritesUnavailable,
      onFavoriteSelected = { onEvent(FavoriteSelected(it)) },
      onFavoriteRemoved = { onEvent(FavoriteRemoved(it)) },
    )
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
