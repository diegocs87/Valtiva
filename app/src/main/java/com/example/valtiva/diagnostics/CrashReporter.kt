package com.example.valtiva.diagnostics

/**
 * The one door any layer uses to report crashes, non-fatal failures and diagnostic breadcrumbs.
 *
 * Kept separate from [com.example.valtiva.analytics.AnalyticsTracker] on purpose: analytics answers
 * "what did the user do", diagnostics answers "what went wrong technically", and the two must never
 * be allowed to drift into carrying each other's data — an analytics event must stay free of stack
 * traces, and a crash report must stay free of product-usage framing.
 *
 * Lives outside `domain` for the same reason [com.example.valtiva.analytics.AnalyticsTracker] does:
 * reporting a technical failure to a crash-reporting backend is an infrastructure concern, not a
 * business rule.
 */
interface CrashReporter {

  /** Records [exception] as a non-fatal event, without altering the caller's own error handling. */
  fun recordException(exception: Throwable)

  /** Attaches [message] as a breadcrumb to whatever report — fatal or not — is filed next. */
  fun log(message: String)
}
