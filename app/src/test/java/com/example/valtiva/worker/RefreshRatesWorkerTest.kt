package com.example.valtiva.worker

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import com.example.valtiva.domain.RATE_DATE
import com.example.valtiva.domain.USD
import com.example.valtiva.domain.exception.ExchangeRateSyncException
import com.example.valtiva.domain.model.ExchangeRateSnapshot
import com.example.valtiva.domain.usecase.RefreshRatesUseCase
import io.mockk.coEvery
import io.mockk.mockk
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

  private fun buildWorker(): RefreshRatesWorker =
    TestListenableWorkerBuilder<RefreshRatesWorker>(ApplicationProvider.getApplicationContext())
      .setWorkerFactory(
        object : WorkerFactory() {
          override fun createWorker(
            appContext: Context,
            workerClassName: String,
            workerParameters: WorkerParameters,
          ) = RefreshRatesWorker(appContext, workerParameters, refreshRatesUseCase)
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
  fun `a sync failure is retried`() = runTest {
    coEvery { refreshRatesUseCase(RefreshRatesWorker.BASE_CURRENCY) } throws
      ExchangeRateSyncException(RefreshRatesWorker.BASE_CURRENCY)

    val result = buildWorker().doWork()

    assertTrue(result is ListenableWorker.Result.Retry)
  }

  @Test
  fun `an unexpected failure is not retried`() = runTest {
    coEvery { refreshRatesUseCase(RefreshRatesWorker.BASE_CURRENCY) } throws
      IllegalStateException("unexpected")

    val result = buildWorker().doWork()

    assertTrue(result is ListenableWorker.Result.Failure)
  }
}
