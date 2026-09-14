package com.example.valtiva.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.valtiva.R
import com.example.valtiva.presentation.theme.Dimens

/**
 * The "About Valtiva" / "Privacy Policy" reader: a scrollable window over one bundled [kind], closed
 * from its own "X" rather than by tapping outside, since a document this long invites an accidental
 * outside tap while scrolling.
 *
 * [kind] is rendered, not stored: the title and the parsed [MarkdownBlock]s it maps to are pure
 * functions of the enum, so [SettingsUiState.visibleDocument] staying a bare [SettingsDocumentKind]
 * remains the single source of truth for which document is open — this composable never needs a copy
 * of its own.
 */
@Composable
fun SettingsDocumentDialog(kind: SettingsDocumentKind, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false),
  ) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surface,
      modifier = modifier
        .fillMaxWidth()
        .fillMaxHeight(DIALOG_HEIGHT_FRACTION)
        .padding(Dimens.SpaceLg),
    ) {
      Column(modifier = Modifier.fillMaxSize().padding(Dimens.CardPadding)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.fillMaxWidth(),
        ) {
          Text(
            text = stringResource(kind.titleRes()),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
          )
          IconButton(onClick = onDismiss) {
            Icon(
              imageVector = Icons.Filled.Close,
              contentDescription = stringResource(R.string.settings_document_close_content_description),
            )
          }
        }
        HorizontalDivider(
          color = MaterialTheme.colorScheme.outlineVariant,
          modifier = Modifier.padding(vertical = Dimens.SpaceMd),
        )
        MarkdownDocument(
          blocks = kind.blocks(),
          modifier = Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState()),
        )
      }
    }
  }
}

/** How much of the screen's height [SettingsDocumentDialog] takes up. */
private const val DIALOG_HEIGHT_FRACTION = 0.85f

private fun SettingsDocumentKind.titleRes(): Int = when (this) {
  SettingsDocumentKind.ABOUT -> R.string.settings_about_title
  SettingsDocumentKind.PRIVACY_POLICY -> R.string.settings_privacy_title
}

private fun SettingsDocumentKind.blocks(): List<MarkdownBlock> = when (this) {
  SettingsDocumentKind.ABOUT -> AboutValtivaBlocks
  SettingsDocumentKind.PRIVACY_POLICY -> PrivacyPolicyBlocks
}

/**
 * Renders parsed [blocks] as plain, read-only text — the counterpart to [parseMarkdown], which reads
 * the same [MarkdownBlock]s out of a document's raw text.
 */
@Composable
fun MarkdownDocument(blocks: List<MarkdownBlock>, modifier: Modifier = Modifier) {
  Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(Dimens.SpaceSm)) {
    blocks.forEach { block ->
      when (block) {
        is MarkdownBlock.Heading -> Text(
          text = block.spans.toAnnotatedString(),
          style = block.level.headingStyle(),
          color = MaterialTheme.colorScheme.onSurface,
          modifier = Modifier.padding(top = Dimens.SpaceSm),
        )

        is MarkdownBlock.Paragraph -> Text(
          text = block.spans.toAnnotatedString(),
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        is MarkdownBlock.BulletItem -> Row(modifier = Modifier.padding(start = Dimens.SpaceSm)) {
          Text(
            text = "•",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(end = Dimens.SpaceSm),
          )
          Text(
            text = block.spans.toAnnotatedString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }

        MarkdownBlock.Divider -> HorizontalDivider(
          color = MaterialTheme.colorScheme.outlineVariant,
          modifier = Modifier.padding(vertical = Dimens.SpaceSm),
        )
      }
    }
  }
}

/** [MaterialTheme.typography] scale for a heading, coarser the fewer `#`s it had. */
@Composable
private fun Int.headingStyle() = when (this) {
  1 -> MaterialTheme.typography.titleLarge
  2 -> MaterialTheme.typography.titleMedium
  else -> MaterialTheme.typography.titleSmall
}

private fun List<MarkdownSpan>.toAnnotatedString(): AnnotatedString = AnnotatedString.Builder().apply {
  forEach { span ->
    when (span) {
      is MarkdownSpan.Plain -> append(span.text)
      is MarkdownSpan.Bold -> withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(span.text) }
    }
  }
}.toAnnotatedString()
