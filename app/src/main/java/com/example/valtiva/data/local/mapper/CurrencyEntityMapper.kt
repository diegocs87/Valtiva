package com.example.valtiva.data.local.mapper

import com.example.valtiva.data.local.entity.CurrencyEntity
import com.example.valtiva.domain.model.Currency

fun CurrencyEntity.toDomain(): Currency = Currency.of(code = code, name = name, symbol = symbol)

fun Currency.toEntity(): CurrencyEntity = CurrencyEntity(
  code = code,
  name = name,
  symbol = symbol,
)
