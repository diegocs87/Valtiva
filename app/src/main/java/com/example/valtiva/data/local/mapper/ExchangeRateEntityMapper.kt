package com.example.valtiva.data.local.mapper

import com.example.valtiva.data.local.entity.ExchangeRateEntity
import com.example.valtiva.domain.model.Currency
import com.example.valtiva.domain.model.ExchangeRate
import com.example.valtiva.domain.model.ExchangeRateSnapshot

fun ExchangeRateEntity.toDomain(): ExchangeRate = ExchangeRate(
  base = Currency.of(baseCurrency),
  quote = Currency.of(quoteCurrency),
  rate = rate,
  date = date,
)

fun ExchangeRate.toEntity(): ExchangeRateEntity = ExchangeRateEntity(
  baseCurrency = base.code,
  quoteCurrency = quote.code,
  rate = rate,
  date = date,
)

fun ExchangeRateSnapshot.toEntities(): List<ExchangeRateEntity> = rates.map { (quote, rate) ->
  ExchangeRateEntity(
    baseCurrency = baseCurrency.code,
    quoteCurrency = quote.code,
    rate = rate,
    date = date,
  )
}

fun List<ExchangeRateEntity>.toExchangeRateSnapshots(): List<ExchangeRateSnapshot> =
  map { it.toDomain() }
    .filterNot { it.base == it.quote }
    .groupBy { it.base to it.date }
    .map { (key, rates) ->
      ExchangeRateSnapshot(
        baseCurrency = key.first,
        date = key.second,
        rates = rates.associate { it.quote to it.rate },
      )
    }
    .sortedByDescending { it.date }
