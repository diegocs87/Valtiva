package com.example.valtiva.domain.service

import com.example.valtiva.domain.COP
import com.example.valtiva.domain.EUR
import com.example.valtiva.domain.RATE_DATE
import com.example.valtiva.domain.USD
import com.example.valtiva.domain.model.ExchangeRate
import com.example.valtiva.domain.model.MonetaryPrecision
import com.example.valtiva.domain.model.Money
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class CurrencyConversionServiceTest {

  private val service = CurrencyConversionService()

  private fun usdToCop(rate: String) = ExchangeRate(USD, COP, BigDecimal(rate), RATE_DATE)

  private fun convertUsdToCop(amount: String, rate: String) =
    service.convert(Money(BigDecimal(amount), USD), COP, usdToCop(rate))

  @Test
  fun `converts 100 USD to COP at 3980_25`() {
    val result = convertUsdToCop(amount = "100", rate = "3980.25")

    assertEquals("398025.00", result.target.amount.toPlainString())
    assertEquals(COP, result.target.currency)
  }

  @Test
  fun `carries the source amount rate and date into the result`() {
    val result = convertUsdToCop(amount = "100", rate = "3980.25")

    assertEquals(Money(BigDecimal("100"), USD), result.source)
    assertEquals(BigDecimal("3980.25"), result.exchangeRate)
    assertEquals(RATE_DATE, result.rateDate)
  }

  @Test
  fun `converts an amount with decimals`() {
    // 100.50 x 3980.25 = 400015.1250, an exact tie on the third decimal.
    val result = convertUsdToCop(amount = "100.50", rate = "3980.25")

    assertEquals("400015.12", result.target.amount.toPlainString())
  }

  @Test
  fun `converts zero to zero`() {
    val result = convertUsdToCop(amount = "0", rate = "3980.25")

    assertEquals("0.00", result.target.amount.toPlainString())
  }

  @Test
  fun `converting a currency into itself leaves the amount unchanged`() {
    val result = service.convert(
      money = Money(BigDecimal("100"), USD),
      targetCurrency = USD,
      rate = ExchangeRate(USD, USD, BigDecimal.ONE, RATE_DATE),
    )

    assertEquals("100.00", result.target.amount.toPlainString())
    assertEquals(USD, result.target.currency)
  }

  @Test
  fun `a rate of zero cannot even be built so it never reaches the service`() {
    assertThrows(IllegalArgumentException::class.java) { usdToCop("0") }
  }

  @Test
  fun `a negative rate cannot even be built so it never reaches the service`() {
    assertThrows(IllegalArgumentException::class.java) { usdToCop("-3980.25") }
  }

  @Test
  fun `keeps precision when the amount has many decimals`() {
    // 123.456789 x 3980.251234 = 491389.036762927626 before rounding.
    val result = convertUsdToCop(amount = "123.456789", rate = "3980.251234")

    assertEquals("491389.04", result.target.amount.toPlainString())
  }

  @Test
  fun `uses BigDecimal arithmetic where Double would give a different answer`() {
    val amount = BigDecimal("2.675")
    val rate = BigDecimal("1")

    val result = service.convert(Money(amount, USD), COP, ExchangeRate(USD, COP, rate, RATE_DATE))

    // BigDecimal sees an exact tie at 2.675 and HALF_EVEN rounds it up to 2.68.
    assertEquals("2.68", result.target.amount.toPlainString())

    // A Double implementation stores 2.67499999... and would round down to 2.67 instead.
    val viaDouble = BigDecimal(amount.toDouble() * rate.toDouble())
      .setScale(MonetaryPrecision.SCALE, MonetaryPrecision.ROUNDING)
    assertEquals("2.67", viaDouble.toPlainString())
    assertNotEquals(viaDouble, result.target.amount)
  }

  @Test
  fun `rounds ties to the even neighbour in both directions`() {
    // 0.005 rounds down to 0.00 because 0 is even; 0.015 rounds up to 0.02 for the same reason.
    assertEquals("0.00", convertUsdToCop(amount = "0.005", rate = "1").target.amount.toPlainString())
    assertEquals("0.02", convertUsdToCop(amount = "0.015", rate = "1").target.amount.toPlainString())
  }

  @Test
  fun `always returns the result with the documented scale`() {
    val result = convertUsdToCop(amount = "7", rate = "1")

    assertEquals(MonetaryPrecision.SCALE, result.target.amount.scale())
  }

  @Test
  fun `rejects a rate whose base is not the currency being converted`() {
    val error = assertThrows(IllegalArgumentException::class.java) {
      service.convert(Money(BigDecimal("100"), EUR), COP, usdToCop("3980.25"))
    }

    assertEquals(true, error.message?.contains("cannot convert"))
  }

  @Test
  fun `rejects a rate whose quote is not the requested target currency`() {
    val error = assertThrows(IllegalArgumentException::class.java) {
      service.convert(Money(BigDecimal("100"), USD), EUR, usdToCop("3980.25"))
    }

    assertEquals(true, error.message?.contains("does not produce"))
  }
}
