package com.example.valtiva.presentation.converter

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import android.view.Gravity
import com.example.valtiva.R
import com.example.valtiva.domain.model.Currency
import com.example.valtiva.presentation.theme.Dimens

/**
 * The bare amount the user is typing, styled to read as one unit with its currency chip rather
 * than as a traditional bordered text field.
 */
@Composable
fun AmountField(
  amount: String,
  onAmountChanged: (String) -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
) {
  val label = stringResource(R.string.amount_label)
  BasicTextField(
    value = amount,
    onValueChange = onAmountChanged,
    enabled = enabled,
    singleLine = true,
    textStyle = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.onSurface),
    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
    decorationBox = { innerTextField ->
      if (amount.isEmpty()) {
        Text(
          text = stringResource(R.string.receive_placeholder),
          style = MaterialTheme.typography.titleLarge,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
      innerTextField()
    },
    modifier = modifier.semantics { contentDescription = label },
  )
}

/** The read-only counterpart of [AmountField], showing a result or the empty placeholder. */
@Composable
fun AmountDisplay(amount: String?, modifier: Modifier = Modifier) {
  // One line that scrolls sideways, like the AmountField it mirrors: a long figure wrapped at an
  // arbitrary digit reads as two numbers, and the two cards would then differ in height.
  Text(
    text = amount?.takeIf { it.isNotBlank() } ?: stringResource(R.string.receive_placeholder),
    style = MaterialTheme.typography.titleLarge,
    color = if (amount.isNullOrBlank()) {
      MaterialTheme.colorScheme.onSurfaceVariant
    } else {
      MaterialTheme.colorScheme.onSurface
    },
    softWrap = false,
    modifier = modifier.horizontalScroll(rememberScrollState()),
  )
}

/**
 * A pill showing the selected [Currency]'s flag and code that opens a dropdown to pick another one
 * out of [currencies].
 *
 * Sized as a primary control rather than a decorative tag: picking the pair is the main thing the
 * user does on this screen, so the chip carries a full-size flag and a comfortable touch target.
 * Each entry in the dropdown pairs the code with [Currency.displayName], since a list of bare codes
 * is only readable to someone who already knows which one they want.
 *
 * The list opens as a panel from the top of the screen ([CurrencyTopSheet]) with a search field on top
 * so a long catalogue stays usable: typing narrows
 * [currencies] to whatever [filterByQuery] keeps, by code (`COP`) or by name (`Colombian Peso`). The
 * query is local to this one dropdown instance — it is not hoisted, for the same reason [expanded]
 * is not: neither is state any other part of the screen needs to read or survive the popup closing,
 * so keeping it here is the single source of truth for it rather than a duplicate of one. It is
 * cleared on every close so the next time this chip opens, it opens showing the full list again.
 */
@Composable
fun CurrencySelectorChip(
  contentDescriptionLabel: String,
  selected: Currency?,
  currencies: List<Currency>,
  onSelected: (Currency) -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
) {
  var expanded by remember { mutableStateOf(false) }
  var query by remember { mutableStateOf("") }
  val noneSelected = stringResource(R.string.currency_none_selected_content_description)
  val chipDescription = "$contentDescriptionLabel: ${selected?.code ?: noneSelected}"

  fun close() {
    expanded = false
    query = ""
  }

  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSm),
    modifier = modifier
      .heightIn(min = Dimens.CurrencySelectorHeight)
      .clip(RoundedCornerShape(50))
      .background(MaterialTheme.colorScheme.surfaceVariant)
      .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(50))
      .clickable(enabled = enabled) { expanded = true }
      .semantics(mergeDescendants = true) {
        contentDescription = chipDescription
        role = Role.DropdownList
      }
      .padding(horizontal = Dimens.SpaceMd),
  ) {
    CurrencyFlag(currency = selected, size = Dimens.CurrencyFlagChipSize)
    Text(
      text = selected?.code ?: stringResource(R.string.target_currency_placeholder),
      style = MaterialTheme.typography.titleSmall,
      color = MaterialTheme.colorScheme.onSurface,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
      modifier = Modifier.weight(1f, fill = false),
    )
    Icon(
      imageVector = Icons.Filled.KeyboardArrowDown,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.size(Dimens.IconSizeMedium),
    )
  }

  if (expanded) {
    CurrencyTopSheet(
      currencies = currencies,
      query = query,
      onQueryChanged = { query = it },
      onSelected = {
        onSelected(it)
        close()
      },
      onDismiss = { close() },
    )
  }
}

/**
 * The currency list as a panel that drops down from the very top of the screen, used by both the
 * origin and the target chip.
 *
 * A [Dialog] rather than a `DropdownMenu`: a dropdown is positioned against its anchor chip, so how
 * many rows fit depends on where the chip sits — the "receive" card's chip had room for barely a
 * handful. A dialog window pinned to [Gravity.TOP] is independent of the anchor, so both chips get
 * the same panel and roughly [Dimens.CurrencySheetMaxVisibleRows] rows. The panel's height is capped
 * by the screen (short devices, landscape, split screen), so it never runs off the bottom, and its
 * rows are as tall as their text needs rather than a fixed height that clips at large font scales.
 * On wide windows it keeps to [Dimens.DialogMaxWidth] instead of stretching edge to edge. The
 * window's own dim scrim and outside-tap dismissal come for free.
 */
@Composable
private fun CurrencyTopSheet(
  currencies: List<Currency>,
  query: String,
  onQueryChanged: (String) -> Unit,
  onSelected: (Currency) -> Unit,
  onDismiss: () -> Unit,
) {
  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false),
  ) {
    (LocalView.current.parent as? DialogWindowProvider)?.window?.setGravity(Gravity.TOP)

    // Starts hidden and flips on the first frame so the panel slides down instead of popping in.
    val visibleState = remember { MutableTransitionState(false).apply { targetState = true } }
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
      AnimatedVisibility(
        visibleState = visibleState,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
      ) {
        // Same reasoning as before: names are resolved through java.util.Currency, so do it once per
        // list rather than on every search keystroke.
        val displayNames = remember(currencies) { currencies.associateWith { it.displayName() } }
        val filteredCurrencies = remember(currencies, query) { currencies.filterByQuery(query) }

        // Capped by the screen so a short window (landscape, split screen) never gets a panel taller
        // than itself. The keyboard is not accounted for: the dialog window does not receive a
        // reliable IME inset, so while the keyboard is up the lowest rows sit beneath it. Typing in
        // the search field narrows the list, which is what brings the wanted row into view.
        val maxPanelHeight = (LocalConfiguration.current.screenHeightDp.dp - Dimens.CurrencySheetBottomMargin)
          .coerceAtLeast(Dimens.CurrencySheetItemHeight * 2)

        Surface(
          color = MaterialTheme.colorScheme.surface,
          shape = MaterialTheme.shapes.large.copy(topStart = CornerSize(0.dp), topEnd = CornerSize(0.dp)),
          shadowElevation = Dimens.ActionButtonElevation,
          modifier = Modifier
            .widthIn(max = Dimens.DialogMaxWidth)
            .fillMaxWidth()
            .heightIn(max = maxPanelHeight),
        ) {
          Column(
            modifier = Modifier
              .statusBarsPadding()
              .padding(bottom = Dimens.SpaceMd),
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(start = Dimens.SpaceLg, end = Dimens.SpaceSm, top = Dimens.SpaceSm, bottom = Dimens.SpaceSm),
            ) {
              CurrencySearchField(
                query = query,
                onQueryChanged = onQueryChanged,
                modifier = Modifier.weight(1f),
              )
              IconButton(onClick = onDismiss) {
                Icon(
                  imageVector = Icons.Filled.Close,
                  contentDescription = stringResource(R.string.currency_sheet_close_content_description),
                )
              }
            }
            if (filteredCurrencies.isEmpty()) {
              Text(
                text = stringResource(R.string.currency_search_no_results),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = Dimens.SpaceLg, vertical = Dimens.SpaceMd),
              )
            }
            LazyColumn(
              modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .heightIn(max = Dimens.CurrencySheetItemHeight * Dimens.CurrencySheetMaxVisibleRows),
            ) {
              items(filteredCurrencies, key = { it.code }) { currency ->
                CurrencySheetRow(
                  currency = currency,
                  name = displayNames[currency] ?: currency.code,
                  onClick = { onSelected(currency) },
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun CurrencySheetRow(currency: Currency, name: String, onClick: () -> Unit) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceMd),
    modifier = Modifier
      .fillMaxWidth()
      .heightIn(min = Dimens.CurrencySheetItemHeight)
      .clickable(onClick = onClick)
      .padding(horizontal = Dimens.SpaceLg, vertical = Dimens.SpaceXs),
  ) {
    CurrencyFlag(currency = currency, size = Dimens.CurrencySheetFlagSize)
    Text(
      text = currency.code,
      style = MaterialTheme.typography.titleSmall,
      color = MaterialTheme.colorScheme.onSurface,
    )
    // Only worth showing when the name says more than the code already does.
    if (name != currency.code) {
      Text(
        text = name,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
      )
    }
  }
}

/**
 * The search box atop [CurrencySelectorChip]'s dropdown. Purely a text field: the matching it drives
 * lives in [filterByQuery], so this composable only has to render [query] and report edits.
 */
@Composable
private fun CurrencySearchField(
  query: String,
  onQueryChanged: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  OutlinedTextField(
    value = query,
    onValueChange = onQueryChanged,
    modifier = modifier.fillMaxWidth(),
    singleLine = true,
    placeholder = { Text(stringResource(R.string.currency_search_placeholder)) },
    leadingIcon = { Icon(imageVector = Icons.Filled.Search, contentDescription = null) },
    trailingIcon = {
      if (query.isNotEmpty()) {
        IconButton(onClick = { onQueryChanged("") }) {
          Icon(
            imageVector = Icons.Filled.Close,
            contentDescription = stringResource(R.string.currency_search_clear_content_description),
          )
        }
      }
    },
  )
}

/** A circular action, visually anchored between the two currency sections it exchanges. */
@Composable
fun SwapButton(onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
  val description = stringResource(R.string.swap_currencies_content_description)
  IconButton(
    onClick = onClick,
    enabled = enabled,
    colors = IconButtonDefaults.filledIconButtonColors(
      containerColor = MaterialTheme.colorScheme.primary,
      contentColor = MaterialTheme.colorScheme.onPrimary,
      disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
      disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
    ),
    modifier = modifier
      .size(Dimens.SwapButtonSize)
      .semantics { contentDescription = description },
  ) {
    Icon(imageVector = Icons.Filled.SwapVert, contentDescription = null)
  }
}

/** The screen's primary action: convert. Represents enabled, disabled and loading. */
@Composable
fun ConvertButton(
  onClick: () -> Unit,
  loading: Boolean,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
) {
  Button(
    onClick = onClick,
    enabled = enabled,
    shape = MaterialTheme.shapes.medium,
    modifier = modifier
      .fillMaxWidth()
      .heightIn(min = Dimens.PrimaryButtonHeight),
  ) {
    if (loading) {
      val loadingDescription = stringResource(R.string.loading_content_description)
      CircularProgressIndicator(
        modifier = Modifier
          .size(22.dp)
          .semantics { contentDescription = loadingDescription },
        color = MaterialTheme.colorScheme.onPrimary,
        strokeWidth = 2.dp,
      )
    } else {
      Text(text = stringResource(R.string.convert_button), style = MaterialTheme.typography.labelLarge)
    }
  }
}

/** A secondary action to bring the current base currency's rates up to date. */
@Composable
fun RefreshRatesButton(
  onClick: () -> Unit,
  loading: Boolean,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
) {
  OutlinedButton(
    onClick = onClick,
    enabled = enabled,
    shape = MaterialTheme.shapes.medium,
    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
    modifier = modifier.heightIn(min = Dimens.ActionButtonHeight),
  ) {
    if (loading) {
      CircularProgressIndicator(modifier = Modifier.size(Dimens.IconSizeSmall), strokeWidth = 2.dp)
    } else {
      Icon(
        imageVector = Icons.Filled.Refresh,
        contentDescription = null,
        modifier = Modifier.size(Dimens.IconSizeSmall),
      )
    }
    Spacer(modifier = Modifier.size(Dimens.SpaceXs))
    Text(text = stringResource(R.string.refresh_rates_button), style = MaterialTheme.typography.labelLarge)
  }
}

/**
 * The tertiary action to save or unsave the currently selected pair, sitting on its own raised
 * light-blue surface so it reads as a peer of "Refresh rates" rather than as a bare text link.
 *
 * A [androidx.compose.material3.Surface] instead of a filled `Button`: the disabled colours have to
 * drop the tint entirely — a dimmed blue container still looks tappable next to an outlined button.
 */
@Composable
fun ToggleFavoriteButton(
  isFavorite: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
) {
  Surface(
    onClick = onClick,
    enabled = enabled,
    shape = MaterialTheme.shapes.medium,
    color = if (enabled) {
      MaterialTheme.colorScheme.primaryContainer
    } else {
      MaterialTheme.colorScheme.surfaceVariant
    },
    contentColor = if (enabled) {
      MaterialTheme.colorScheme.onPrimaryContainer
    } else {
      MaterialTheme.colorScheme.onSurfaceVariant
    },
    shadowElevation = if (enabled) Dimens.ActionButtonElevation else 0.dp,
    modifier = modifier.heightIn(min = Dimens.ActionButtonHeight),
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center,
      modifier = Modifier.padding(horizontal = Dimens.SpaceLg),
    ) {
      Icon(
        imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
        contentDescription = null,
        modifier = Modifier.size(Dimens.IconSizeMedium),
      )
      Spacer(modifier = Modifier.size(Dimens.SpaceSm))
      Text(
        text = stringResource(
          if (isFavorite) R.string.favorite_remove_content_description else R.string.favorite_add_content_description,
        ),
        style = MaterialTheme.typography.labelLarge,
      )
    }
  }
}

@Composable
fun RetryButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
  TextButton(onClick = onClick, modifier = modifier) {
    Text(stringResource(R.string.retry_button), style = MaterialTheme.typography.labelLarge)
  }
}

/** A visually distinct error banner, shown with an optional [onRetry] action. */
@Composable
fun ErrorBanner(message: String, onRetry: (() -> Unit)?, modifier: Modifier = Modifier) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
    modifier = modifier
      .fillMaxWidth()
      .clip(MaterialTheme.shapes.medium)
      .background(MaterialTheme.colorScheme.errorContainer)
      // Announced when it appears, so a screen-reader user hears that the request failed rather
      // than having to stumble onto the banner.
      .semantics { liveRegion = LiveRegionMode.Polite }
      .padding(horizontal = Dimens.SpaceLg, vertical = Dimens.SpaceMd),
  ) {
    Text(
      text = message,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onErrorContainer,
      modifier = Modifier.weight(1f),
    )
    if (onRetry != null) {
      RetryButton(onClick = onRetry)
    }
  }
}
