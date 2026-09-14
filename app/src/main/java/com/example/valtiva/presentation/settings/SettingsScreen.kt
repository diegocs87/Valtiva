package com.example.valtiva.presentation.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import com.example.valtiva.R
import com.example.valtiva.domain.model.ThemeMode
import com.example.valtiva.presentation.settings.SettingsUiEvent.AboutRowClicked
import com.example.valtiva.presentation.settings.SettingsUiEvent.DailyAutoRefreshToggled
import com.example.valtiva.presentation.settings.SettingsUiEvent.PrivacyPolicyRowClicked
import com.example.valtiva.presentation.settings.SettingsUiEvent.SettingsDocumentDismissed
import com.example.valtiva.presentation.settings.SettingsUiEvent.ThemeDialogDismissed
import com.example.valtiva.presentation.settings.SettingsUiEvent.ThemeModeSelected
import com.example.valtiva.presentation.settings.SettingsUiEvent.ThemeRowClicked
import com.example.valtiva.presentation.theme.Dimens
import com.example.valtiva.presentation.theme.ValtivaColors
import com.example.valtiva.presentation.theme.ValtivaTheme

/**
 * Renders [SettingsUiState] and turns user interaction into [SettingsUiEvent]s.
 *
 * Stateless by design, like [com.example.valtiva.presentation.favorites.FavoritesScreen]: every
 * value it shows comes from [state], every action it can trigger goes out through [onEvent]. "About
 * Valtiva" and "Privacy Policy" open [SettingsDocumentDialog] on their bundled copy; the "Theme" row
 * is the one real preference here, and opens [ThemeModeDialog] instead.
 */
@Composable
fun SettingsScreen(
  state: SettingsUiState,
  onEvent: (SettingsUiEvent) -> Unit,
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
    SettingsHeader()

    SettingsSection(title = stringResource(R.string.settings_section_appearance)) {
      SettingsOptionRow(
        icon = Icons.Filled.DarkMode,
        iconTint = MaterialTheme.colorScheme.primary,
        iconContainerColor = MaterialTheme.colorScheme.primaryContainer,
        title = stringResource(R.string.settings_theme_title),
        subtitle = stringResource(state.themeMode.labelRes()),
        onClick = { onEvent(ThemeRowClicked) },
        trailingContent = { SettingsChevron() },
      )
      HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }

    SettingsSection(title = stringResource(R.string.settings_section_rates)) {
      SettingsOptionRow(
        icon = Icons.Filled.Autorenew,
        iconTint = ValtivaColors.current.success,
        iconContainerColor = ValtivaColors.current.successContainer,
        title = stringResource(R.string.settings_daily_refresh_title),
        subtitle = stringResource(R.string.settings_daily_refresh_subtitle),
        trailingContent = {
          Switch(
            checked = state.dailyAutoRefreshEnabled,
            onCheckedChange = { onEvent(DailyAutoRefreshToggled(it)) },
          )
        },
      )
      HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }

    SettingsSection(title = stringResource(R.string.settings_section_about)) {
      SettingsOptionRow(
        icon = Icons.Filled.Info,
        iconTint = MaterialTheme.colorScheme.primary,
        iconContainerColor = MaterialTheme.colorScheme.primaryContainer,
        title = stringResource(R.string.settings_about_title),
        subtitle = stringResource(R.string.settings_about_subtitle),
        onClick = { onEvent(AboutRowClicked) },
        trailingContent = { SettingsChevron() },
      )
      HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
      SettingsOptionRow(
        icon = Icons.Filled.VerifiedUser,
        iconTint = ValtivaColors.current.success,
        iconContainerColor = ValtivaColors.current.successContainer,
        title = stringResource(R.string.settings_privacy_title),
        subtitle = stringResource(R.string.settings_privacy_subtitle),
        onClick = { onEvent(PrivacyPolicyRowClicked) },
        trailingContent = { SettingsChevron() },
      )
      HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }

    SettingsFooter()
  }

  if (state.themeDialogVisible) {
    ThemeModeDialog(
      selected = state.themeMode,
      onThemeModeSelected = { onEvent(ThemeModeSelected(it)) },
      onDismiss = { onEvent(ThemeDialogDismissed) },
    )
  }

  state.visibleDocument?.let { kind ->
    SettingsDocumentDialog(kind = kind, onDismiss = { onEvent(SettingsDocumentDismissed) })
  }
}

@Composable
private fun SettingsHeader(modifier: Modifier = Modifier) {
  Column(modifier = modifier.fillMaxWidth()) {
    Text(
      text = stringResource(R.string.settings_title),
      style = MaterialTheme.typography.titleLarge,
      color = MaterialTheme.colorScheme.onBackground,
    )
    Text(
      text = stringResource(R.string.settings_subtitle),
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.padding(top = Dimens.SpaceXs),
    )
  }
}

/**
 * A labeled group of [SettingsOptionRow]s, mirroring the "APPEARANCE" / "RATES" / "ABOUT" groupings
 * of the settings screen. Only the header is this composable's own concern; each row and the
 * divider under it are left to the caller, the same way [ColumnScope] content is composed anywhere
 * else in this file.
 */
@Composable
private fun SettingsSection(
  title: String,
  modifier: Modifier = Modifier,
  content: @Composable ColumnScope.() -> Unit,
) {
  Column(modifier = modifier.fillMaxWidth()) {
    Text(
      text = title.uppercase(),
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.padding(bottom = Dimens.SpaceMd),
    )
    content()
  }
}

/**
 * One settings row: an icon in a tinted circle, a title and subtitle, and whatever
 * [trailingContent] the row ends in — a chevron for a row that opens something, a [Switch] for one
 * that is itself a preference. [onClick] is only ever set on the former; the switch reports its own
 * changes directly through [trailingContent] instead.
 */
@Composable
private fun SettingsOptionRow(
  icon: ImageVector,
  iconTint: Color,
  iconContainerColor: Color,
  title: String,
  subtitle: String,
  modifier: Modifier = Modifier,
  onClick: (() -> Unit)? = null,
  trailingContent: @Composable () -> Unit,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
      .fillMaxWidth()
      .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
      .padding(vertical = Dimens.SpaceSm),
  ) {
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier
        .size(Dimens.SettingsIconContainerSize)
        .clip(CircleShape)
        .background(iconContainerColor),
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = iconTint,
        modifier = Modifier.size(Dimens.IconSizeMedium),
      )
    }
    Column(
      modifier = Modifier
        .weight(1f)
        .padding(horizontal = Dimens.SpaceMd),
    ) {
      Text(text = title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
      Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
    trailingContent()
  }
}

@Composable
private fun SettingsChevron(modifier: Modifier = Modifier) {
  Icon(
    imageVector = Icons.Filled.ChevronRight,
    contentDescription = null,
    tint = MaterialTheme.colorScheme.onSurfaceVariant,
    modifier = modifier.size(Dimens.IconSizeMedium),
  )
}

@Composable
private fun SettingsFooter(modifier: Modifier = Modifier) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier.fillMaxWidth(),
  ) {
    Image(
      painter = painterResource(R.mipmap.ic_launcher_foreground),
      contentDescription = stringResource(R.string.settings_logo_content_description),
      modifier = Modifier.size(Dimens.SettingsLogoSize),
    )
    Text(
      text = stringResource(R.string.app_name),
      style = MaterialTheme.typography.headlineSmall,
      color = MaterialTheme.colorScheme.onBackground,
      modifier = Modifier.padding(top = Dimens.SpaceMd),
    )
    Text(
      text = stringResource(R.string.settings_tagline),
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.padding(top = Dimens.SpaceXs),
    )
    Text(
      text = stringResource(R.string.settings_version),
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.padding(top = Dimens.SpaceSm),
    )
  }
}

/**
 * The theme submenu: one radio option per [ThemeMode]. Picking an option applies it and closes the
 * dialog immediately, the same one-tap behavior as the platform's own theme picker — there is
 * nothing to confirm, so no confirm button is shown.
 */
@Composable
private fun ThemeModeDialog(
  selected: ThemeMode,
  onThemeModeSelected: (ThemeMode) -> Unit,
  onDismiss: () -> Unit,
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    confirmButton = {},
    title = { Text(text = stringResource(R.string.settings_theme_title)) },
    text = {
      Column(modifier = Modifier.selectableGroup()) {
        ThemeModeOption(
          label = stringResource(R.string.theme_option_light),
          icon = Icons.Filled.LightMode,
          selected = selected == ThemeMode.LIGHT,
          onClick = { onThemeModeSelected(ThemeMode.LIGHT) },
        )
        ThemeModeOption(
          label = stringResource(R.string.theme_option_dark),
          icon = Icons.Filled.DarkMode,
          selected = selected == ThemeMode.DARK,
          onClick = { onThemeModeSelected(ThemeMode.DARK) },
        )
        ThemeModeOption(
          label = stringResource(R.string.theme_option_system),
          icon = Icons.Filled.BrightnessAuto,
          selected = selected == ThemeMode.SYSTEM,
          onClick = { onThemeModeSelected(ThemeMode.SYSTEM) },
        )
      }
    },
  )
}

@Composable
private fun ThemeModeOption(
  label: String,
  icon: ImageVector,
  selected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
      .fillMaxWidth()
      .selectable(selected = selected, onClick = onClick, role = Role.RadioButton)
      .padding(vertical = Dimens.SpaceSm),
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.padding(end = Dimens.SpaceMd),
    )
    Text(
      text = label,
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.onSurface,
      modifier = Modifier.weight(1f),
    )
    RadioButton(selected = selected, onClick = null)
  }
}

/** The row's own subtitle, mirroring back which option [ThemeMode] the user picked. */
private fun ThemeMode.labelRes(): Int = when (this) {
  ThemeMode.LIGHT -> R.string.theme_option_light
  ThemeMode.DARK -> R.string.theme_option_dark
  ThemeMode.SYSTEM -> R.string.theme_option_system
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
  ValtivaTheme {
    SettingsScreen(state = SettingsUiState(), onEvent = {})
  }
}
