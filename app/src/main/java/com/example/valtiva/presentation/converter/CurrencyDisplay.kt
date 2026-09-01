package com.example.valtiva.presentation.converter

import com.example.valtiva.domain.model.Currency
import java.util.Locale
import java.util.Currency as IsoCurrency

/** The first code point of the Unicode regional indicators, i.e. the letter `A`. */
private const val REGIONAL_INDICATOR_A = 0x1F1E6

/**
 * ISO 4217 reserves codes starting with `X` for things that are not a country's money — metals
 * (XAU), special drawing rights (XDR), supranational funds. Their first two letters are not a
 * country code, so no flag can be derived from them.
 */
private const val NON_COUNTRY_PREFIX = "X"

/**
 * The full name to show under a currency selector, e.g. `COP` -> `"Colombian Peso"`.
 *
 * [Currency.name] is preferred and is what the provider's currency catalogue fills in, but nothing
 * wires that catalogue to this screen yet: the picker is fed from a rate snapshot, whose quotes are
 * bare ISO codes, so [Currency.name] currently falls back to the code for every entry. The JDK's own
 * ISO 4217 table answers in that case — it is a real source, needs no network and no new domain
 * contract. Preferring [Currency.name] first is what makes wiring the catalogue later an upgrade
 * this function does not have to be changed for.
 *
 * Falls back to the bare code when neither source knows the currency, which reads better than an
 * empty line under the selector.
 *
 * `Locale.US` is fixed for the same reason [toDisplayAmount] fixes it: none of the app's strings are
 * localized yet, so a device-locale name next to English labels would read inconsistently.
 */
fun Currency.displayName(): String {
  if (name != code) return name
  return runCatching { IsoCurrency.getInstance(code).getDisplayName(Locale.US) }
    .getOrNull()
    ?.takeIf { it != code }
    ?: code
}

/**
 * The flag emoji for a currency, e.g. `COP` -> 🇨🇴, or `null` when the code names no country.
 *
 * ISO 4217 builds a currency code from its country's ISO 3166-1 alpha-2 code plus a letter for the
 * currency itself, and the Unicode regional indicators turn exactly that two-letter code into a
 * flag. `EUR` needs no special case: it yields `EU`, which is an exceptionally reserved country
 * code with a flag of its own.
 *
 * Returning `null` rather than a bogus pair matters — an unassigned pair renders as two letter
 * tiles, not a flag — so callers fall back to a neutral placeholder instead.
 *
 * No validation of the letters is needed: [Currency] rejects anything that is not three uppercase
 * letters at construction, so the first two are always regional-indicator material.
 */
fun Currency.flagEmoji(): String? {
  if (code.startsWith(NON_COUNTRY_PREFIX)) return null
  return buildString {
    code.take(2).forEach { letter -> appendCodePoint(REGIONAL_INDICATOR_A + (letter - 'A')) }
  }
}
