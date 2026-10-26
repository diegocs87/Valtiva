package com.example.valtiva.data.local.mapper

import com.example.valtiva.data.local.entity.ConversionHistoryEntity
import com.example.valtiva.domain.model.ConversionHistoryEntry
import com.example.valtiva.domain.model.ConversionResult
import com.example.valtiva.domain.model.Currency
import com.example.valtiva.domain.model.Money
import java.time.Instant


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

fun ConversionResult.toEntity(performedAt: Instant): ConversionHistoryEntity = ConversionHistoryEntity(
  sourceAmount = source.amount,
  baseCurrency = source.currency.code,
  targetAmount = target.amount,
  quoteCurrency = target.currency.code,
  rate = exchangeRate,
  rateDate = rateDate,
  timestamp = performedAt,
)
