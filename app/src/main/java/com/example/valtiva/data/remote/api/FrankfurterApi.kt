package com.example.valtiva.data.remote.api

import com.example.valtiva.data.remote.dto.FrankfurterCurrencyDto
import com.example.valtiva.data.remote.dto.FrankfurterRateDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * The Frankfurter v2 endpoints this app consumes.
 *
 * Base URL: `https://api.frankfurter.dev/`. Only v2 is used; v1 returns a different shape
 * (`{base, date, rates:{...}}`) and is not supported here.
 *
 * `quotes` is a comma-separated list of ISO codes, which is what the API expects; omitting it
 * returns every published currency. The MVP uses the default blended rates, so the `providers`
 * parameter is deliberately not exposed.
 */
interface FrankfurterApi {

  /**
   * Latest rates for [base].
   *
   * Each entry carries the last date that particular currency was published, so the result can span
   * several dates. Pass [quotes] to narrow the response.
   */
  @GET("v2/rates")
  suspend fun getLatestRates(
    @Query("base") base: String,
    @Query("quotes") quotes: String? = null,
  ): List<FrankfurterRateDto>

  /**
   * Rates for [base] on [date] (`YYYY-MM-DD`). Unlike [getLatestRates], every entry shares [date].
   */
  @GET("v2/rates")
  suspend fun getHistoricalRates(
    @Query("base") base: String,
    @Query("date") date: String,
    @Query("quotes") quotes: String? = null,
  ): List<FrankfurterRateDto>

  /** A single pair, optionally on a given [date] rather than the latest one. */
  @GET("v2/rate/{base}/{quote}")
  suspend fun getRate(
    @Path("base") base: String,
    @Path("quote") quote: String,
    @Query("date") date: String? = null,
  ): FrankfurterRateDto

  /** Every currency the API publishes. */
  @GET("v2/currencies")
  suspend fun getCurrencies(): List<FrankfurterCurrencyDto>
}
