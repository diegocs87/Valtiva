package com.example.valtiva.presentation.settings

/**
 * One line-level unit of a parsed document, in the order it should render.
 *
 * Deliberately narrow: it only names the constructs the settings documents ([SettingsDocumentKind])
 * actually use — headings, paragraphs, bullets and a rule — not a general-purpose Markdown grammar.
 * Growing it to cover links, nested lists or tables belongs to whichever document first needs one.
 */
sealed interface MarkdownBlock {

  /** A `#`/`##`/`###` heading. [level] is how many `#` it had. */
  data class Heading(val level: Int, val spans: List<MarkdownSpan>) : MarkdownBlock

  /** A line of running text. */
  data class Paragraph(val spans: List<MarkdownSpan>) : MarkdownBlock

  /** One `* ` list entry. */
  data class BulletItem(val spans: List<MarkdownSpan>) : MarkdownBlock

  /** A `---` horizontal rule. */
  data object Divider : MarkdownBlock
}

/** A run of text within a [MarkdownBlock], either as written or emphasized with `**`. */
sealed interface MarkdownSpan {
  data class Plain(val text: String) : MarkdownSpan
  data class Bold(val text: String) : MarkdownSpan
}

private val BOLD_PATTERN = Regex("\\*\\*(.+?)\\*\\*")

/**
 * Reads [source] into the [MarkdownBlock]s it describes.
 *
 * A pure function on purpose: it needs no `@Composable` context and nothing Android-specific, so it
 * stays trivial to unit test on its own, with rendering left entirely to
 * [com.example.valtiva.presentation.settings.MarkdownDocument]. One rule per line — blank lines are
 * dropped rather than kept as spacing, since the spacing between blocks is the renderer's job, not
 * the parser's.
 */
fun parseMarkdown(source: String): List<MarkdownBlock> =
  source.lineSequence()
    .map { it.trim() }
    .filter { it.isNotEmpty() }
    .map { line -> line.toBlock() }
    .toList()

private fun String.toBlock(): MarkdownBlock = when {
  this == "---" -> MarkdownBlock.Divider
  startsWith("### ") -> MarkdownBlock.Heading(level = 3, spans = removePrefix("### ").toSpans())
  startsWith("## ") -> MarkdownBlock.Heading(level = 2, spans = removePrefix("## ").toSpans())
  startsWith("# ") -> MarkdownBlock.Heading(level = 1, spans = removePrefix("# ").toSpans())
  startsWith("* ") -> MarkdownBlock.BulletItem(spans = removePrefix("* ").toSpans())
  else -> MarkdownBlock.Paragraph(spans = toSpans())
}

/** Splits a line at its `**bold**` runs, keeping the plain text in between. */
private fun String.toSpans(): List<MarkdownSpan> {
  val spans = mutableListOf<MarkdownSpan>()
  var cursor = 0
  for (match in BOLD_PATTERN.findAll(this)) {
    if (match.range.first > cursor) spans += MarkdownSpan.Plain(substring(cursor, match.range.first))
    spans += MarkdownSpan.Bold(match.groupValues[1])
    cursor = match.range.last + 1
  }
  if (cursor < length) spans += MarkdownSpan.Plain(substring(cursor))
  return spans
}
