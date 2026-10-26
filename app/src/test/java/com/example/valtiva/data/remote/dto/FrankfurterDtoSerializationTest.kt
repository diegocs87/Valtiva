package com.example.valtiva.data.remote.dto

import com.example.valtiva.data.remote.FrankfurterJson
import java.math.BigDecimal
import kotlinx.serialization.builtins.ListSerializer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FrankfurterDtoSerializationTest {

  @Test
  fun `decodes a rates payload as returned by the API`() {
    val payload =
      """[{"date":"2026-08-15","base":"USD","quote":"COP","rate":3123.53},""" +
        """{"date":"2026-08-17","base":"USD","quote":"EUR","rate":0.86539}]"""

    val rates = FrankfurterJson.decodeFromString(ListSerializer(APIRateDto.serializer()), payload)

    assertEquals(2, rates.size)
    assertEquals(APIRateDto("2026-08-15", "USD", "COP", BigDecimal("3123.53")), rates.first())
    assertEquals(BigDecimal("0.86539"), rates.last().rate)
  }

  @Test
  fun `decodes a rate without going through a Double`() {
    // A Double cannot hold this many digits; decoding through one would silently round the value.
    val payload = """{"date":"2026-08-14","base":"USD","quote":"COP","rate":3980.2512345678901234567890}"""

    val dto = FrankfurterJson.decodeFromString(APIRateDto.serializer(), payload)

    assertEquals(BigDecimal("3980.2512345678901234567890"), dto.rate)
    assertEquals(BigDecimal("3980.2512345678901234567890").scale(), dto.rate.scale())
  }

  @Test
  fun `keeps the scale the server sent`() {
    val payload = """{"date":"2026-08-14","base":"USD","quote":"COP","rate":1.10}"""

    val dto = FrankfurterJson.decodeFromString(APIRateDto.serializer(), payload)

    // 1.10 and 1.1 are the same number but not the same BigDecimal.
    assertEquals(BigDecimal("1.10"), dto.rate)
    assertEquals(2, dto.rate.scale())
  }

  @Test
  fun `re-encodes a rate as a JSON number rather than a quoted string`() {
    val dto = APIRateDto("2026-08-14", "USD", "COP", BigDecimal("3980.25"))

    val encoded = FrankfurterJson.encodeToString(APIRateDto.serializer(), dto)

    assertEquals(true, encoded.contains("\"rate\":3980.25"))
    assertEquals(dto, FrankfurterJson.decodeFromString(APIRateDto.serializer(), encoded))
  }

  @Test
  fun `decodes a currency payload and ignores the fields the domain does not use`() {
    val payload =
      """{"iso_code":"USD","iso_numeric":"840","name":"United States Dollar","symbol":"$",""" +
        """"start_date":"1948-06-21","end_date":"2026-08-17"}"""

    val dto = FrankfurterJson.decodeFromString(APICurrencyDto.serializer(), payload)

    assertEquals("USD", dto.isoCode)
    assertEquals("United States Dollar", dto.name)
    assertEquals("$", dto.symbol)
  }

  @Test
  fun `decodes a currency that has no symbol`() {
    val payload = """{"iso_code":"XDR","name":"Special Drawing Rights"}"""

    val dto = FrankfurterJson.decodeFromString(APICurrencyDto.serializer(), payload)

    assertNull(dto.symbol)
  }
}
