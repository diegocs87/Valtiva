package com.example.valtiva.analytics

/**
 * The one door Presentation (and anything above it) uses to report product usage.
 *
 * Modelled after the same inversion [com.example.valtiva.domain.scheduler.RatesRefreshScheduler]
 * gives the daily refresh: callers depend on this port only, never on Firebase Analytics or any
 * other vendor SDK, so the analytics backend can change without touching a ViewModel.
 *
 * It lives outside `domain` on purpose. Reporting that a button was tapped is not a business rule
 * the domain would ever need to state — it is presentation behaviour worth measuring — so folding
 * it into `domain` would misrepresent what the domain models. [analytics] is its own boundary,
 * exactly as [com.example.valtiva.worker] is its own boundary for background scheduling mechanics.
 */
interface AnalyticsTracker {

  /** Reports that [event] happened. Implementations must not block the caller. */
  fun trackEvent(event: AnalyticsEvent)
}
