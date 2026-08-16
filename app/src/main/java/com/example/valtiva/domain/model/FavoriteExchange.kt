package com.example.valtiva.domain.model

/**
 * A currency pair the user marked as a favorite.
 *
 * A favorite is a [CurrencyPair] and never a rate: rates change every day, so storing one would
 * make the favorite stale the moment it is saved. The pair is the stable thing the user actually
 * cares about, and the current rate for it is resolved on demand.
 */
data class FavoriteExchange(
  val pair: CurrencyPair,
)
