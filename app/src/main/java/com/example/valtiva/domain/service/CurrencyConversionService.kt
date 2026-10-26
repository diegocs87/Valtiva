package com.example.valtiva.domain.service

import com.example.valtiva.domain.model.ConversionResult
import com.example.valtiva.domain.model.Currency
import com.example.valtiva.domain.model.ExchangeRate
import com.example.valtiva.domain.model.MonetaryPrecision
import com.example.valtiva.domain.model.Money
import java.math.BigDecimal

class CurrencyConversionService : CurrencyConverter {

    override fun convert(
        money: Money,
        targetCurrency: Currency,
        rate: ExchangeRate
    ): ConversionResult {
        requireMatchingCurrencies(money, rate, targetCurrency)

        val convertedAmount = calculateConvertedAmount(money, rate)

        val conversionResult = ConversionResult(
            source = money,
            target = Money(convertedAmount, targetCurrency),
            exchangeRate = rate.rate,
            rateDate = rate.date,
        )

        return conversionResult
    }

    private fun calculateConvertedAmount(
        money: Money,
        rate: ExchangeRate
    ): BigDecimal {
        val convertedAmount = money.amount
            .multiply(rate.rate, MonetaryPrecision.MATH_CONTEXT)
            .setScale(MonetaryPrecision.SCALE, MonetaryPrecision.ROUNDING)
        return convertedAmount
    }

    private fun requireMatchingCurrencies(
        money: Money,
        rate: ExchangeRate,
        targetCurrency: Currency
    ) {
        require(money.currency == rate.base) {
            "Rate ${rate.base.code}/${rate.quote.code} cannot convert an amount in ${money.currency.code}."
        }
        require(targetCurrency == rate.quote) {
            "Rate ${rate.base.code}/${rate.quote.code} does not produce ${targetCurrency.code}."
        }
    }
}
