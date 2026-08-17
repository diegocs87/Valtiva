package com.example.valtiva.data.local.mapper

import com.example.valtiva.data.local.entity.FavoriteExchangeEntity
import com.example.valtiva.domain.model.Currency
import com.example.valtiva.domain.model.CurrencyPair
import com.example.valtiva.domain.model.FavoriteExchange

/**
 * Stored row to domain favorite.
 *
 * Only the codes are stored, so the rebuilt currencies carry no display name or symbol; those come
 * from the currency catalogue when the UI needs them.
 */
fun FavoriteExchangeEntity.toDomain(): FavoriteExchange = FavoriteExchange(
  pair = CurrencyPair(
    base = Currency.of(baseCurrency),
    quote = Currency.of(quoteCurrency),
  ),
)

/** Domain favorite to stored row. The direction of the pair is preserved. */
fun FavoriteExchange.toEntity(): FavoriteExchangeEntity = FavoriteExchangeEntity(
  baseCurrency = pair.base.code,
  quoteCurrency = pair.quote.code,
)
