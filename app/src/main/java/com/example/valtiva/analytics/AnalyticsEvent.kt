package com.example.valtiva.analytics

/** The event names Valtiva reports, kept as constants so no call site retypes a string literal. */
object AnalyticsEventName {
  const val CONVERSION_COMPLETED = "conversion_completed"
  const val CURRENCY_SELECTED = "currency_selected"
  const val CURRENCIES_SWAPPED = "currencies_swapped"
  const val FAVORITE_ADDED = "favorite_added"
  const val FAVORITE_REMOVED = "favorite_removed"
  const val FAVORITE_SELECTED = "favorite_selected"
  const val RATES_REFRESHED = "rates_refreshed"
  const val HISTORY_OPENED = "history_opened"
  const val HISTORY_CONVERSION_SELECTED = "history_conversion_selected"
  const val SETTINGS_OPENED = "settings_opened"
  const val DAILY_REFRESH_COMPLETED = "daily_refresh_completed"
}

/** The parameter keys [AnalyticsEvent]s attach, kept as constants for the same reason as the names. */
object AnalyticsParam {
  const val BASE_CURRENCY = "base_currency"
  const val TARGET_CURRENCY = "target_currency"
  const val CURRENCY = "currency"
  const val FIELD = "field"
}

/** Which form field a [AnalyticsEvent.CurrencySelected] came from. */
enum class CurrencyField(val paramValue: String) {
  BASE("base"),
  TARGET("target"),
}

/**
 * Every event Valtiva can report, each carrying exactly the parameters it needs.
 *
 * A closed hierarchy instead of a single `trackEvent(name, params)` call: adding a new event means
 * adding a case here, so a caller can never send a name Firebase doesn't recognise or a parameter
 * spelled two different ways in two screens.
 *
 * Only currency codes and screen navigation facts travel as parameters. Nothing here carries a
 * monetary amount, a conversion result, or anything that identifies the user — those are
 * deliberately impossible to construct through this type, not just avoided by convention.
 */
sealed class AnalyticsEvent(val name: String, val params: Map<String, String> = emptyMap()) {

  /** A conversion finished. Carries only the currencies involved, never the amount or the result. */
  data class ConversionCompleted(
    val baseCurrency: String,
    val targetCurrency: String,
  ) : AnalyticsEvent(
    name = AnalyticsEventName.CONVERSION_COMPLETED,
    params = mapOf(
      AnalyticsParam.BASE_CURRENCY to baseCurrency,
      AnalyticsParam.TARGET_CURRENCY to targetCurrency,
    ),
  )

  /** The user picked a currency for the base or the target field of the converter form. */
  data class CurrencySelected(
    val currencyCode: String,
    val field: CurrencyField,
  ) : AnalyticsEvent(
    name = AnalyticsEventName.CURRENCY_SELECTED,
    params = mapOf(
      AnalyticsParam.CURRENCY to currencyCode,
      AnalyticsParam.FIELD to field.paramValue,
    ),
  )

  /** The user swapped the base and target currencies. Carries the pair as it reads after the swap. */
  data class CurrenciesSwapped(
    val baseCurrency: String,
    val targetCurrency: String,
  ) : AnalyticsEvent(
    name = AnalyticsEventName.CURRENCIES_SWAPPED,
    params = mapOf(
      AnalyticsParam.BASE_CURRENCY to baseCurrency,
      AnalyticsParam.TARGET_CURRENCY to targetCurrency,
    ),
  )

  /** The user saved a pair as a favorite. */
  data class FavoriteAdded(
    val baseCurrency: String,
    val targetCurrency: String,
  ) : AnalyticsEvent(
    name = AnalyticsEventName.FAVORITE_ADDED,
    params = mapOf(
      AnalyticsParam.BASE_CURRENCY to baseCurrency,
      AnalyticsParam.TARGET_CURRENCY to targetCurrency,
    ),
  )

  /** The user removed a pair from their favorites. */
  data class FavoriteRemoved(
    val baseCurrency: String,
    val targetCurrency: String,
  ) : AnalyticsEvent(
    name = AnalyticsEventName.FAVORITE_REMOVED,
    params = mapOf(
      AnalyticsParam.BASE_CURRENCY to baseCurrency,
      AnalyticsParam.TARGET_CURRENCY to targetCurrency,
    ),
  )

  /** The user picked one of their favorites to load it onto the converter form. */
  data class FavoriteSelected(
    val baseCurrency: String,
    val targetCurrency: String,
  ) : AnalyticsEvent(
    name = AnalyticsEventName.FAVORITE_SELECTED,
    params = mapOf(
      AnalyticsParam.BASE_CURRENCY to baseCurrency,
      AnalyticsParam.TARGET_CURRENCY to targetCurrency,
    ),
  )

  /** The user asked for a manual rates refresh and it completed. */
  data class RatesRefreshed(
    val baseCurrency: String,
  ) : AnalyticsEvent(
    name = AnalyticsEventName.RATES_REFRESHED,
    params = mapOf(AnalyticsParam.BASE_CURRENCY to baseCurrency),
  )

  /** The history screen was opened. */
  data object HistoryOpened : AnalyticsEvent(AnalyticsEventName.HISTORY_OPENED)

  /** The user picked a past conversion from the history screen to reload it on the converter. */
  data class HistoryConversionSelected(
    val baseCurrency: String,
    val targetCurrency: String,
  ) : AnalyticsEvent(
    name = AnalyticsEventName.HISTORY_CONVERSION_SELECTED,
    params = mapOf(
      AnalyticsParam.BASE_CURRENCY to baseCurrency,
      AnalyticsParam.TARGET_CURRENCY to targetCurrency,
    ),
  )

  /** The settings screen was opened. */
  data object SettingsOpened : AnalyticsEvent(AnalyticsEventName.SETTINGS_OPENED)

  /** The periodic background refresh completed successfully. */
  data class DailyRefreshCompleted(
    val baseCurrency: String,
  ) : AnalyticsEvent(
    name = AnalyticsEventName.DAILY_REFRESH_COMPLETED,
    params = mapOf(AnalyticsParam.BASE_CURRENCY to baseCurrency),
  )
}
