package com.example.valtiva.presentation.converter

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import com.example.valtiva.R
import com.example.valtiva.domain.model.Currency

@Composable
fun AmountInput(
  amount: String,
  onAmountChanged: (String) -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
) {
  OutlinedTextField(
    value = amount,
    onValueChange = onAmountChanged,
    label = { Text(stringResource(R.string.amount_label)) },
    enabled = enabled,
    singleLine = true,
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
    modifier = modifier.fillMaxWidth(),
  )
}

/**
 * A dropdown to pick one [Currency] out of [currencies].
 *
 * Currencies are shown by their ISO code only: the API's currency catalogue (names, symbols) has
 * no domain contract exposing it yet, so this selector uses the only identifier the domain
 * guarantees.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencySelector(
  label: String,
  selected: Currency?,
  currencies: List<Currency>,
  onSelected: (Currency) -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
) {
  var expanded by remember { mutableStateOf(false) }

  ExposedDropdownMenuBox(
    expanded = expanded,
    onExpandedChange = { if (enabled) expanded = it },
    modifier = modifier,
  ) {
    OutlinedTextField(
      value = selected?.code ?: stringResource(R.string.target_currency_placeholder),
      onValueChange = {},
      readOnly = true,
      enabled = enabled,
      label = { Text(label) },
      trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
      modifier = Modifier
        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
        .fillMaxWidth(),
    )
    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
      currencies.forEach { currency ->
        DropdownMenuItem(
          text = { Text(currency.code) },
          onClick = {
            onSelected(currency)
            expanded = false
          },
        )
      }
    }
  }
}

@Composable
fun SwapCurrenciesButton(onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
  val description = stringResource(R.string.swap_currencies_content_description)
  IconButton(onClick = onClick, enabled = enabled, modifier = modifier.semantics { contentDescription = description }) {
    Text("⇄")
  }
}

@Composable
fun ConvertButton(onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
  Button(onClick = onClick, enabled = enabled, modifier = modifier) {
    Text(stringResource(R.string.convert_button))
  }
}

@Composable
fun ToggleFavoriteButton(
  isFavorite: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
) {
  val description = stringResource(
    if (isFavorite) R.string.favorite_remove_content_description else R.string.favorite_add_content_description,
  )
  IconButton(
    onClick = onClick,
    enabled = enabled,
    modifier = modifier.semantics { contentDescription = description },
  ) {
    Text(if (isFavorite) "★" else "☆")
  }
}

@Composable
fun RefreshRatesButton(onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
  OutlinedButton(onClick = onClick, enabled = enabled, modifier = modifier) {
    Text(stringResource(R.string.refresh_rates_button))
  }
}

@Composable
fun RetryButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
  TextButton(onClick = onClick, modifier = modifier) {
    Text(stringResource(R.string.retry_button))
  }
}

@Composable
fun ErrorMessage(message: String, modifier: Modifier = Modifier) {
  Text(text = message, color = MaterialTheme.colorScheme.error, modifier = modifier.fillMaxWidth())
}
