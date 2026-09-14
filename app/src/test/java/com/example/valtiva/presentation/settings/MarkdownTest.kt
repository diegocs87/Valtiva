package com.example.valtiva.presentation.settings

import org.junit.Assert.assertEquals
import org.junit.Test

/** Unit tests for [parseMarkdown], the pure parsing logic behind [SettingsDocumentDialog]. */
class MarkdownTest {

  @Test
  fun `blank lines are dropped`() {
    val blocks = parseMarkdown("Uno\n\n\nDos")

    assertEquals(
      listOf(
        MarkdownBlock.Paragraph(listOf(MarkdownSpan.Plain("Uno"))),
        MarkdownBlock.Paragraph(listOf(MarkdownSpan.Plain("Dos"))),
      ),
      blocks,
    )
  }

  @Test
  fun `reads headings by their leading hashes`() {
    val blocks = parseMarkdown("# Uno\n## Dos\n### Tres")

    assertEquals(
      listOf(
        MarkdownBlock.Heading(1, listOf(MarkdownSpan.Plain("Uno"))),
        MarkdownBlock.Heading(2, listOf(MarkdownSpan.Plain("Dos"))),
        MarkdownBlock.Heading(3, listOf(MarkdownSpan.Plain("Tres"))),
      ),
      blocks,
    )
  }

  @Test
  fun `reads a bullet item`() {
    val blocks = parseMarkdown("* Primer punto")

    assertEquals(listOf(MarkdownBlock.BulletItem(listOf(MarkdownSpan.Plain("Primer punto")))), blocks)
  }

  @Test
  fun `reads a horizontal rule`() {
    val blocks = parseMarkdown("---")

    assertEquals(listOf(MarkdownBlock.Divider), blocks)
  }

  @Test
  fun `splits a line at its bold runs, keeping the plain text around them`() {
    val blocks = parseMarkdown("**Última actualización:** septiembre de 2026")

    assertEquals(
      listOf(
        MarkdownBlock.Paragraph(
          listOf(
            MarkdownSpan.Bold("Última actualización:"),
            MarkdownSpan.Plain(" septiembre de 2026"),
          ),
        ),
      ),
      blocks,
    )
  }

  @Test
  fun `a line that is entirely bold has no surrounding plain spans`() {
    val blocks = parseMarkdown("**Valtiva**")

    assertEquals(listOf(MarkdownBlock.Paragraph(listOf(MarkdownSpan.Bold("Valtiva")))), blocks)
  }

  @Test
  fun `trims leading and trailing whitespace from a line before classifying it`() {
    val blocks = parseMarkdown("   # Título   ")

    assertEquals(listOf(MarkdownBlock.Heading(1, listOf(MarkdownSpan.Plain("Título")))), blocks)
  }
}
