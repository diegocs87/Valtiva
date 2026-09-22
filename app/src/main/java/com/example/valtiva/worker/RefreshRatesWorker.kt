package com.example.valtiva.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.valtiva.analytics.AnalyticsEvent
import com.example.valtiva.analytics.AnalyticsTracker
import com.example.valtiva.diagnostics.CrashReporter
import com.example.valtiva.domain.exception.ExchangeRateSyncException
import com.example.valtiva.domain.model.Currency
import com.example.valtiva.domain.usecase.RefreshRatesUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException

/**
 * Periodic entry point for the daily rates refresh.
 *
 * This is deliberately thin: it delegates the whole operation to [RefreshRatesUseCase] — the same
 * use case the manual "Refresh" action in the converter screen calls — and only translates its
 * outcome into the [Result] WorkManager expects. It knows nothing about Retrofit, Room, the DAO or
 * connectivity; those decisions stay with the repository behind the use case, which is also what
 * keeps offline-first and the local/remote choice out of this class entirely.
 *
 * [ExchangeRateSyncException] is the one failure the repository documents as retryable — the source
 * could not be reached this time, but a later attempt might succeed. Anything else is treated as
 * non-retryable: retrying would not change the outcome.
 */
@HiltWorker
class RefreshRatesWorker
@AssistedInject
constructor(
  @Assisted context: Context,
  @Assisted params: WorkerParameters,
  private val refreshRatesUseCase: RefreshRatesUseCase,
  private val analyticsTracker: AnalyticsTracker,
  private val crashReporter: CrashReporter,
) : CoroutineWorker(context, params) {

  @Suppress("TooGenericExceptionCaught", "SwallowedException")
  override suspend fun doWork(): Result =
    try {
      refreshRatesUseCase(BASE_CURRENCY)
      analyticsTracker.trackEvent(AnalyticsEvent.DailyRefreshCompleted(BASE_CURRENCY.code))
      Result.success()
    } catch (retryable: ExchangeRateSyncException) {
      // The one failure the repository documents as worth retrying as-is: the source could not
      // be reached this time, but a later attempt might succeed. Not a defect, so no non-fatal
      // report — just a breadcrumb in case a later, genuine failure needs the history.
      crashReporter.log("Daily rates refresh could not sync; retrying.")
      Result.retry()
    } catch (cancellation: CancellationException) {
      throw cancellation
    } catch (nonRetryable: Exception) {
      // Anything else is a failure retrying would not fix, and worth a non-fatal report: WorkManager's
      // Result carries no cause, so Crashlytics is the only place this exception is still visible.
      crashReporter.recordException(nonRetryable)
      Result.failure()
    }

  companion object {
    /** The base currency the whole app anchors its stored rates to. */
    val BASE_CURRENCY: Currency = Currency.of("USD")
  }
}
