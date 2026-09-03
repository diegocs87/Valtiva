package com.example.valtiva.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.valtiva.R
import com.example.valtiva.domain.model.ConversionHistoryEntry
import com.example.valtiva.domain.model.ConversionResult
import com.example.valtiva.domain.model.Currency
import com.example.valtiva.domain.model.Money
import com.example.valtiva.presentation.converter.CurrencyFlag
import com.example.valtiva.presentation.converter.toDisplayAmount
import com.example.valtiva.presentation.history.HistoryUiEvent.ConversionSelected
import com.example.valtiva.presentation.theme.Dimens
import com.example.valtiva.presentation.theme.ValtivaTheme
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

/**
 * Renders [HistoryUiState] and turns user interaction into [HistoryUiEvent]s.
 *
 * Stateless by design, like [com.example.valtiva.presentation.converter.CurrencyConverterScreen]:
 * every value it shows comes from [state], every action it can trigger goes out through [onEvent].
 * It computes no conversion and reads no clock; it only ever repaints what [state] already says.
 */
@Composable
fun HistoryScreen(
  state: HistoryUiState,
  onEvent: (HistoryUiEvent) -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .verticalScroll(rememberScrollState())
      .padding(Dimens.ScreenPadding),
    verticalArrangement = Arrangement.spacedBy(Dimens.SpaceXl),
  ) {
    Text(
      text = stringResource(R.string.history_title),
      style = MaterialTheme.typography.headlineSmall,
      color = MaterialTheme.colorScheme.onBackground,
    )

    when {
      state.isLoading -> HistoryLoading()
      state.isUnavailable -> HistoryMessage(stringResource(R.string.history_unavailable))
      state.conversions.isEmpty() -> HistoryEmptyState()
      else -> HistoryList(
        conversions = state.conversions,
        onSetAsCurrent = { onEvent(ConversionSelected(it)) },
      )
    }
  }
}

@Composable
private fun HistoryLoading(modifier: Modifier = Modifier) {
  val description = stringResource(R.string.loading_content_description)
  Box(modifier = modifier.fillMaxWidth().padding(top = Dimens.SpaceXxl), contentAlignment = Alignment.Center) {
    CircularProgressIndicator(
      color = MaterialTheme.colorScheme.primary,
      modifier = Modifier.semantics { contentDescription = description },
    )
  }
}

@Composable
private fun HistoryEmptyState(modifier: Modifier = Modifier) {
  Column(
    modifier = modifier.fillMaxWidth().padding(top = Dimens.SpaceXxl),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Icon(
      imageVector = Icons.Filled.History,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.size(Dimens.IconSizeMedium),
    )
    Text(
      text = stringResource(R.string.history_empty_title),
      style = MaterialTheme.typography.titleMedium,
      color = MaterialTheme.colorScheme.onBackground,
      modifier = Modifier.padding(top = Dimens.SpaceLg),
    )
    Text(
      text = stringResource(R.string.history_empty_message),
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center,
      modifier = Modifier.padding(top = Dimens.SpaceXs),
    )
  }
}

@Composable
private fun HistoryMessage(message: String, modifier: Modifier = Modifier) {
  Text(
    text = message,
    style = MaterialTheme.typography.bodyMedium,
    color = MaterialTheme.colorScheme.onSurfaceVariant,
    modifier = modifier.fillMaxWidth(),
  )
}

/**
 * A non-lazy list, like [com.example.valtiva.presentation.converter.FavoritesSection]: it lives
 * inside this screen's own scrolling [Column], and the history is capped at ten entries, which is
 * far too small for a [androidx.compose.foundation.lazy.LazyColumn] to earn its keep.
 */
@Composable
private fun HistoryList(
  conversions: List<ConversionHistoryEntry>,
  onSetAsCurrent: (ConversionHistoryEntry) -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Dimens.SpaceMd)) {
    conversions.forEach { entry ->
      ConversionHistoryCard(entry = entry, onSetAsCurrent = { onSetAsCurrent(entry) })
    }
  }
}

@Composable
private fun ConversionHistoryCard(
  entry: ConversionHistoryEntry,
  onSetAsCurrent: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val conversion = entry.conversion
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    modifier = modifier.fillMaxWidth(),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(Dimens.CardPadding),
      verticalArrangement = Arrangement.spacedBy(Dimens.SpaceXs),
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        CurrencyFlag(currency = conversion.source.currency, size = Dimens.CurrencyBadgeSize)
        Spacer(modifier = Modifier.size(Dimens.SpaceMd))
        Text(
          text = stringResource(
            R.string.history_pair_line,
            conversion.source.amount.toDisplayAmount(),
            conversion.source.currency.code,
            conversion.target.currency.code,
          ),
          style = MaterialTheme.typography.labelLarge,
          color = MaterialTheme.colorScheme.onSurface,
        )
      }
      Text(
        text = "${conversion.target.amount.toDisplayAmount()} ${conversion.target.currency.code}",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
      )
      Text(
        text = stringResource(R.string.history_rate_label, conversion.exchangeRate.toDisplayAmount()),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      Text(
        text = entry.performedAt.toDisplayDateTime(),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      TextButton(onClick = onSetAsCurrent, modifier = Modifier.align(Alignment.End)) {
        Text(
          text = stringResource(R.string.set_as_current_button),
          style = MaterialTheme.typography.labelLarge,
        )
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
private fun HistoryScreenPreview() {
  val usd = Currency.of("USD")
  val cop = Currency.of("COP")
  ValtivaTheme {
    HistoryScreen(
      state = HistoryUiState(
        conversions = listOf(
          ConversionHistoryEntry(
            id = 1,
            conversion = ConversionResult(
              source = Money(BigDecimal("100.00"), usd),
              target = Money(BigDecimal("409320.40"), cop),
              exchangeRate = BigDecimal("4093.20"),
              rateDate = LocalDate.of(2026, 8, 30),
            ),
            performedAt = Instant.parse("2026-08-30T15:32:00Z"),
          ),
        ),
        isLoading = false,
      ),
      onEvent = {},
    )
  }
}
