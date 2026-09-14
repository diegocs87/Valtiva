package com.example.valtiva.domain.scheduler

/**
 * Controls the background job that refreshes exchange rates once a day.
 *
 * This is the Domain-side port for the daily automatic refresh preference: it lets a use case
 * start or stop the periodic background refresh without knowing WorkManager (or any other
 * scheduling mechanism) is behind it. The concrete implementation lives next to the existing
 * refresh worker.
 */
interface RatesRefreshScheduler {

  /** Ensures the daily periodic refresh is scheduled. Safe to call repeatedly. */
  fun schedule()

  /** Cancels the daily periodic refresh, if any is scheduled. Safe to call repeatedly. */
  fun cancel()
}
