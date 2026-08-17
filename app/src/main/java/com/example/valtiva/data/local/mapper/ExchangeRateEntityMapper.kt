package com.example.valtiva.data.local.mapper

import com.example.valtiva.data.local.entity.ExchangeRateEntity
import com.example.valtiva.domain.model.Currency
import com.example.valtiva.domain.model.ExchangeRate
import com.example.valtiva.domain.model.ExchangeRateSnapshot

/** Stored row to domain rate. A stored code that is not valid ISO 4217 fails in [Currency.of]. */
fun ExchangeRateEntity.toDomain(): ExchangeRate = ExchangeRate(
  base = Currency.of(baseCurrency),
  quote = Currency.of(quoteCurrency),
  rate = rate,
  date = date,
)

/** Domain rate to stored row. Only the currency codes are persisted, never the display names. */
fun ExchangeRate.toEntity(): ExchangeRateEntity = ExchangeRateEntity(
  baseCurrency = base.code,
  quoteCurrency = quote.code,
  rate = rate,
  date = date,
)

/** Flattens a snapshot into the rows that represent it, one per quoted currency. */
fun ExchangeRateSnapshot.toEntities(): List<ExchangeRateEntity> = rates.map { (quote, rate) ->
  ExchangeRateEntity(
    baseCurrency = baseCurrency.code,
    quoteCurrency = quote.code,
    rate = rate,
    date = date,
  )
}

/**
 * Rebuilds snapshots from stored rows, newest first.
 *
 * Grouping mirrors [com.example.valtiva.data.remote.mapper.toExchangeRateSnapshots]: rows are keyed
 * by base and date, so a query spanning several days still produces one snapshot per day instead of
 * silently mixing them.
 */
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
