package com.example.valtiva.data.remote.api

import com.example.valtiva.data.remote.dto.APICurrencyDto
import com.example.valtiva.data.remote.dto.APIRateDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


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
  ): List<APIRateDto>

  /**
   * Rates for [base] on [date] (`YYYY-MM-DD`). Unlike [getLatestRates], every entry shares [date].
   */
  @GET("v2/rates")
  suspend fun getHistoricalRates(
    @Query("base") base: String,
    @Query("date") date: String,
    @Query("quotes") quotes: String? = null,
  ): List<APIRateDto>

  /** A single pair, optionally on a given [date] rather than the latest one. */
  @GET("v2/rate/{base}/{quote}")
  suspend fun getRate(
    @Path("base") base: String,
    @Path("quote") quote: String,
    @Query("date") date: String? = null,
  ): APIRateDto

  /** Every currency the API publishes. */
  @GET("v2/currencies")
  suspend fun getCurrencies(): List<APICurrencyDto>
}
