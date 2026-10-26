package com.example.valtiva.data.remote.dto

import com.example.valtiva.data.remote.serializer.BigDecimalSerializer
import java.math.BigDecimal
import kotlinx.serialization.Serializable

/**
 * One exchange rate as published by Frankfurter v2.
 *
 * The wire format is flat, and every entry carries its own [date] — the API returns the last day
 * each individual currency was published, so a single response can mix several dates unless the
 * request pins one with `date`.
 *
 * ```json
 * {"date":"2026-08-15","base":"USD","quote":"COP","rate":3123.53}
 * ```
 *
 * Fields stay as raw wire types (`String` for the date, ISO codes as text); turning them into
 * domain types is the mapper's job.
 */
@Serializable
data class APIRateDto(
  val date: String,
  val base: String,
  val quote: String,
  @Serializable(with = BigDecimalSerializer::class)
  val rate: BigDecimal,
)
