package com.example.valtiva.domain.exception

import com.example.valtiva.domain.model.Currency

/**
 * Thrown when the rates for [base] could not be brought in from the source of truth: the device is
 * offline, the provider is unreachable or answered with an error, or its payload was unusable.
 *
 * This is deliberately distinct from [ExchangeRateUnavailableException]: that one means "the data
 * we have does not cover this pair", while this one means "we could not get the data at all". The
 * UI needs to tell them apart — the first is a fact about coverage, the second is retryable.
 *
 * The original failure travels as [cause] so the outer layers can distinguish connectivity from
 * server errors if they ever need to, without this contract naming HTTP or any other transport.
 */
class ExchangeRateSyncException(
  val base: Currency,
  cause: Throwable? = null,
  message: String = "Could not sync exchange rates for ${base.code}.",
) : RuntimeException(message, cause)
