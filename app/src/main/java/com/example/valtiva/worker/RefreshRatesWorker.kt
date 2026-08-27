package com.example.valtiva.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
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
) : CoroutineWorker(context, params) {

  @Suppress("TooGenericExceptionCaught", "SwallowedException")
  override suspend fun doWork(): Result =
    try {
      refreshRatesUseCase(BASE_CURRENCY)
      Result.success()
    } catch (retryable: ExchangeRateSyncException) {
      // The one failure the repository documents as worth retrying as-is: the source could not
      // be reached this time, but a later attempt might succeed.
      Result.retry()
    } catch (cancellation: CancellationException) {
      throw cancellation
    } catch (nonRetryable: Exception) {
      // Anything else is a failure retrying would not fix. WorkManager's Result carries no cause,
      // and this project has no logging infrastructure to route it through instead.
      Result.failure()
    }

  companion object {
    /** The base currency the whole app anchors its stored rates to. */
    val BASE_CURRENCY: Currency = Currency.of("USD")
  }
}
