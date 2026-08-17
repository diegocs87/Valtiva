package com.example.valtiva.data.remote.api

import com.example.valtiva.data.remote.FrankfurterJson
import java.math.BigDecimal
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

/**
 * Exercises the Retrofit interface against a local server: it verifies the paths and query
 * parameters we actually send, and that a real Frankfurter payload deserializes through the
 * configured converter. No network involved.
 */
class FrankfurterApiTest {

  private lateinit var server: MockWebServer
  private lateinit var api: FrankfurterApi

  @Before
  fun setUp() {
    server = MockWebServer()
    server.start()
    api = Retrofit.Builder()
      .baseUrl(server.url("/"))
      .addConverterFactory(FrankfurterJson.asConverterFactory("application/json".toMediaType()))
      .build()
      .create(FrankfurterApi::class.java)
  }

  @After
  fun tearDown() {
    server.shutdown()
  }

  private fun enqueue(body: String) {
    server.enqueue(MockResponse().setBody(body).setHeader("Content-Type", "application/json"))
  }

  @Test
  fun `requests the latest rates for a base currency`() = runTest {
    enqueue("""[{"date":"2026-08-15","base":"USD","quote":"COP","rate":3123.53}]""")

    val rates = api.getLatestRates(base = "USD")

    assertEquals("/v2/rates?base=USD", server.takeRequest().path)
    assertEquals(BigDecimal("3123.53"), rates.single().rate)
  }

  @Test
  fun `narrows the latest rates with a comma separated quotes list`() = runTest {
    enqueue(
      """[{"date":"2026-08-15","base":"USD","quote":"COP","rate":3123.53},""" +
        """{"date":"2026-08-17","base":"USD","quote":"EUR","rate":0.86539}]""",
    )

    val rates = api.getLatestRates(base = "USD", quotes = "COP,EUR")

    assertEquals("/v2/rates?base=USD&quotes=COP%2CEUR", server.takeRequest().path)
    assertEquals(2, rates.size)
  }

  @Test
  fun `requests historical rates pinned to a date`() = runTest {
    enqueue("""[{"date":"2026-08-14","base":"USD","quote":"COP","rate":3123.64}]""")

    api.getHistoricalRates(base = "USD", date = "2026-08-14")

    assertEquals("/v2/rates?base=USD&date=2026-08-14", server.takeRequest().path)
  }

  @Test
  fun `requests a single pair by path`() = runTest {
    enqueue("""{"date":"2026-08-15","base":"USD","quote":"COP","rate":3123.53}""")

    val rate = api.getRate(base = "USD", quote = "COP")

    assertEquals("/v2/rate/USD/COP", server.takeRequest().path)
    assertEquals("COP", rate.quote)
    assertEquals(BigDecimal("3123.53"), rate.rate)
  }

  @Test
  fun `requests a single pair on a given date`() = runTest {
    enqueue("""{"date":"2026-08-14","base":"USD","quote":"COP","rate":3123.64}""")

    api.getRate(base = "USD", quote = "COP", date = "2026-08-14")

    assertEquals("/v2/rate/USD/COP?date=2026-08-14", server.takeRequest().path)
  }

  @Test
  fun `requests the currency catalogue and ignores unmodelled fields`() = runTest {
    enqueue(
      """[{"iso_code":"USD","iso_numeric":"840","name":"United States Dollar","symbol":"$",""" +
        """"start_date":"1948-06-21","end_date":"2026-08-17"}]""",
    )

    val currencies = api.getCurrencies()

    assertEquals("/v2/currencies", server.takeRequest().path)
    assertEquals("USD", currencies.single().isoCode)
    assertEquals("United States Dollar", currencies.single().name)
  }
}
