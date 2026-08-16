package com.example.valtiva.domain.exception

import com.example.valtiva.domain.model.Currency

/**
 * Thrown when no rate is published for the requested pair.
 *
 * Modelled as an exception rather than a `null` result so the reason for the failure travels with
 * it: a caller receiving `null` cannot tell "no rates at all" apart from "this pair is not
 * covered", and both need different messages in the UI.
 */
class ExchangeRateUnavailableException(
  val base: Currency,
  val quote: Currency,
) : RuntimeException("No exchange rate available for ${base.code}/${quote.code}.")
