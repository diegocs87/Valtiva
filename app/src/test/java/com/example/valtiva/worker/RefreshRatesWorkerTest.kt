package com.example.valtiva.worker

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import com.example.valtiva.analytics.AnalyticsEvent
import com.example.valtiva.analytics.AnalyticsTracker
import com.example.valtiva.diagnostics.CrashReporter
import com.example.valtiva.domain.RATE_DATE
import com.example.valtiva.domain.USD
import com.example.valtiva.domain.exception.ExchangeRateSyncException
import com.example.valtiva.domain.model.ExchangeRateSnapshot
import com.example.valtiva.domain.usecase.RefreshRatesUseCase
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * The worker owns exactly one decision: translating [RefreshRatesUseCase]'s outcome into the
 * [ListenableWorker.Result] WorkManager expects. Everything about *whether* a refresh was needed,
 * and whether it came from Room or Frankfurter, is the use case's and the repository's problem, not
 * this class's — so these tests stub the use case directly rather than any lower layer.
 */
@RunWith(RobolectricTestRunner::class)
class RefreshRatesWorkerTest {

  private val refreshRatesUseCase = mockk<RefreshRatesUseCase>()
  private val analyticsTracker = mockk<AnalyticsTracker>(relaxed = true)
  private val crashReporter = mockk<CrashReporter>(relaxed = true)

  private fun buildWorker(): RefreshRatesWorker =
    TestListenableWorkerBuilder<RefreshRatesWorker>(ApplicationProvider.getApplicationContext())
      .setWorkerFactory(
        object : WorkerFactory() {
          override fun createWorker(
            appContext: Context,
            workerClassName: String,
            workerParameters: WorkerParameters,
          ) = RefreshRatesWorker(appContext, workerParameters, refreshRatesUseCase, analyticsTracker, crashReporter)
        },
      )
      .build()

  @Test
  fun `a successful refresh reports success`() = runTest {
    coEvery { refreshRatesUseCase(RefreshRatesWorker.BASE_CURRENCY) } returns
      ExchangeRateSnapshot(USD, RATE_DATE, emptyMap())

    val result = buildWorker().doWork()

    assertTrue(result is ListenableWorker.Result.Success)
  }

  @Test
  fun `a successful refresh reports the daily refresh completed event`() = runTest {
    coEvery { refreshRatesUseCase(RefreshRatesWorker.BASE_CURRENCY) } returns
      ExchangeRateSnapshot(USD, RATE_DATE, emptyMap())

    buildWorker().doWork()

    verify(exactly = 1) {
      analyticsTracker.trackEvent(AnalyticsEvent.DailyRefreshCompleted(RefreshRatesWorker.BASE_CURRENCY.code))
    }
  }

  @Test
  fun `a sync failure is retried`() = runTest {
    coEvery { refreshRatesUseCase(RefreshRatesWorker.BASE_CURRENCY) } throws
      ExchangeRateSyncException(RefreshRatesWorker.BASE_CURRENCY)

    val result = buildWorker().doWork()

    assertTrue(result is ListenableWorker.Result.Retry)
  }

  @Test
  fun `a sync failure is not reported as a non-fatal, only logged`() = runTest {
    coEvery { refreshRatesUseCase(RefreshRatesWorker.BASE_CURRENCY) } throws
      ExchangeRateSyncException(RefreshRatesWorker.BASE_CURRENCY)

    buildWorker().doWork()

    verify(exactly = 0) { crashReporter.recordException(any()) }
    verify(exactly = 1) { crashReporter.log(any()) }
  }

  @Test
  fun `an unexpected failure is not retried`() = runTest {
    coEvery { refreshRatesUseCase(RefreshRatesWorker.BASE_CURRENCY) } throws
      IllegalStateException("unexpected")

    val result = buildWorker().doWork()

    assertTrue(result is ListenableWorker.Result.Failure)
  }

  @Test
  fun `an unexpected failure is reported as a non-fatal`() = runTest {
    val failure = IllegalStateException("unexpected")
    coEvery { refreshRatesUseCase(RefreshRatesWorker.BASE_CURRENCY) } throws failure

    buildWorker().doWork()

    verify(exactly = 1) { crashReporter.recordException(failure) }
  }

  @Test
  fun `no event is reported when the refresh does not succeed`() = runTest {
    coEvery { refreshRatesUseCase(RefreshRatesWorker.BASE_CURRENCY) } throws
      ExchangeRateSyncException(RefreshRatesWorker.BASE_CURRENCY)

    buildWorker().doWork()

    verify(exactly = 0) { analyticsTracker.trackEvent(any()) }
  }
}
