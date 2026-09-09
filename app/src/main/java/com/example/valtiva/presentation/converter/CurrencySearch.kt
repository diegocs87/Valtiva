package com.example.valtiva.presentation.converter

import com.example.valtiva.domain.model.Currency

/**
 * Narrows [this] down to the currencies matching [query] against either the ISO code (`COP`) or the
 * full name (`Colombian Peso`), case-insensitively.
 *
 * A pure function rather than logic inlined in the picker: it needs no `@Composable` context, so it
 * is trivial to unit test on its own, and [CurrencySelectorChip] only has to call it rather than
 * reimplement it — the one place a currency's searchable text is defined.
 *
 * [Currency.displayName] is what supplies the full name here, so this inherits its fallback to the
 * bare code for a currency neither the domain nor the JDK's ISO table can name; matching the code
 * twice in that case is harmless. A blank [query] returns [this] unchanged, which is what keeps the
 * search field optional instead of forcing the user to type before seeing anything.
 */
fun List<Currency>.filterByQuery(query: String): List<Currency> {
  val needle = query.trim()
  if (needle.isEmpty()) return this
  return filter { currency ->
    currency.code.contains(needle, ignoreCase = true) || currency.displayName().contains(needle, ignoreCase = true)
  }
}
