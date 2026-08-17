package com.example.valtiva.data.local.mapper

import com.example.valtiva.data.local.entity.CurrencyEntity
import com.example.valtiva.domain.model.Currency

/** Stored row to domain currency. */
fun CurrencyEntity.toDomain(): Currency = Currency.of(code = code, name = name, symbol = symbol)

/** Domain currency to stored row. */
fun Currency.toEntity(): CurrencyEntity = CurrencyEntity(
  code = code,
  name = name,
  symbol = symbol,
)
