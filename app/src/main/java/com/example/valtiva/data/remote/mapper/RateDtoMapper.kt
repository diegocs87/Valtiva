package com.example.valtiva.data.remote.mapper

import com.example.valtiva.data.remote.dto.APIRateDto
import com.example.valtiva.domain.model.Currency
import com.example.valtiva.domain.model.ExchangeRate
import com.example.valtiva.domain.model.ExchangeRateSnapshot
import java.time.LocalDate

/**
 * Turns a wire rate into its domain counterpart.
 *
 * Nothing is swallowed here: an unusable currency code fails in [Currency.of], a malformed date
 * fails in [LocalDate.parse], and a non-positive rate fails in [ExchangeRate]. A bad payload has to
 * surface as an error rather than as a silently wrong rate.
 */
fun APIRateDto.toDomain(): ExchangeRate = ExchangeRate(
  base = Currency.of(base),
  quote = Currency.of(quote),
  rate = rate,
  date = LocalDate.parse(date),
)

/**
 * Groups a Frankfurter response into snapshots, newest first.
 *
 * The API returns one flat entry per currency, each with its own date, so a "latest rates" response
 * legitimately spans several days: a currency whose central bank has not published yet keeps an
 * older date. Grouping by base and date is therefore the only lossless way to reach
 * [ExchangeRateSnapshot], which describes a single base on a single day. A request pinned to a
 * `date` collapses to exactly one snapshot.
 *
 * Entries where base and quote are the same currency are dropped: the API includes `USD -> USD = 1`
 * when no `quotes` filter is given, and a snapshot must not carry a rate for its own base.
 */
fun List<APIRateDto>.toExchangeRateSnapshots(): List<ExchangeRateSnapshot> =
  map { it.toDomain() }
    .filterNot { it.base == it.quote }
    .groupBy { it.base to it.date }
    .map { (key, rates) ->
      ExchangeRateSnapshot(
        baseCurrency = key.first,
        date = key.second,
        rates = rates.associate { it.quote to it.rate },
      )
    }
    .sortedByDescending { it.date }
