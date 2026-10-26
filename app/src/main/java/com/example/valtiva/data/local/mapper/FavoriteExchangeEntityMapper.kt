package com.example.valtiva.data.local.mapper

import com.example.valtiva.data.local.entity.FavoriteExchangeEntity
import com.example.valtiva.domain.model.Currency
import com.example.valtiva.domain.model.CurrencyPair
import com.example.valtiva.domain.model.FavoriteExchange

fun FavoriteExchangeEntity.toDomain(): FavoriteExchange = FavoriteExchange(
  pair = CurrencyPair(
    base = Currency.of(baseCurrency),
    quote = Currency.of(quoteCurrency),
  ),
)

fun FavoriteExchange.toEntity(): FavoriteExchangeEntity = FavoriteExchangeEntity(
  baseCurrency = pair.base.code,
  quoteCurrency = pair.quote.code,
)
