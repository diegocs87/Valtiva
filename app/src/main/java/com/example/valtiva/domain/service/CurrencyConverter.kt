package com.example.valtiva.domain.service

import com.example.valtiva.domain.model.ConversionResult
import com.example.valtiva.domain.model.Currency
import com.example.valtiva.domain.model.ExchangeRate
import com.example.valtiva.domain.model.Money

interface CurrencyConverter {
    fun convert(money: Money, targetCurrency: Currency, rate: ExchangeRate): ConversionResult
}