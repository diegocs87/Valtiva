package com.example.valtiva.data.local.mapper

import com.example.valtiva.data.local.entity.ConversionHistoryEntity
import com.example.valtiva.domain.model.ConversionHistoryEntry
import com.example.valtiva.domain.model.ConversionResult
import com.example.valtiva.domain.model.Currency
import com.example.valtiva.domain.model.Money
import java.time.Instant

/** Stored row to a domain history entry. */
fun ConversionHistoryEntity.toDomain(): ConversionHistoryEntry = ConversionHistoryEntry(
  id = id,
  conversion = ConversionResult(
    source = Money(sourceAmount, Currency.of(baseCurrency)),
    target = Money(targetAmount, Currency.of(quoteCurrency)),
    exchangeRate = rate,
    rateDate = rateDate,
  ),
  performedAt = timestamp,
)

/**
 * A completed conversion, performed at [performedAt], to a new stored row.
 *
 * Takes the pieces rather than a [ConversionHistoryEntry]: a row being saved has no id yet, and
 * [ConversionHistoryEntity]'s default of `0` tells Room to assign one, so nothing here has to
 * fabricate a placeholder id to satisfy the domain type.
 */
fun ConversionResult.toEntity(performedAt: Instant): ConversionHistoryEntity = ConversionHistoryEntity(
  sourceAmount = source.amount,
  baseCurrency = source.currency.code,
  targetAmount = target.amount,
  quoteCurrency = target.currency.code,
  rate = exchangeRate,
  rateDate = rateDate,
  timestamp = performedAt,
)
