package com.example.valtiva.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A currency as published by Frankfurter v2.
 *
 * The API also returns `iso_numeric`, `start_date`, `end_date` and, for a single currency, the list
 * of contributing `providers`. None of them are modelled here: the domain identifies a currency by
 * its ISO 4217 alphabetic code and only needs a display name and symbol. Adding fields the domain
 * cannot use would just be dead weight to keep in sync.
 *
 * ```json
 * {"iso_code":"USD","iso_numeric":"840","name":"United States Dollar","symbol":"$", ...}
 * ```
 */
@Serializable
data class FrankfurterCurrencyDto(
  @SerialName("iso_code")
  val isoCode: String,
  val name: String,
  val symbol: String? = null,
)
