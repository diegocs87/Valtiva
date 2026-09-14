package com.example.valtiva.domain.usecase

import com.example.valtiva.domain.repository.RatesRefreshSettingsRepository
import com.example.valtiva.domain.scheduler.RatesRefreshScheduler
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SyncRatesRefreshSchedulingUseCaseTest {

  private val repository = mockk<RatesRefreshSettingsRepository>()
  private val scheduler = mockk<RatesRefreshScheduler>()
  private val syncRatesRefreshScheduling = SyncRatesRefreshSchedulingUseCase(repository, scheduler)

  @Test
  fun `schedules the daily job when the preference is enabled`() = runTest {
    every { repository.getDailyAutoRefreshEnabled() } returns flowOf(true)
    justRun { scheduler.schedule() }

    syncRatesRefreshScheduling()

    verify(exactly = 1) { scheduler.schedule() }
    verify(exactly = 0) { scheduler.cancel() }
  }

  @Test
  fun `cancels the daily job when the preference is disabled`() = runTest {
    every { repository.getDailyAutoRefreshEnabled() } returns flowOf(false)
    justRun { scheduler.cancel() }

    syncRatesRefreshScheduling()

    verify(exactly = 1) { scheduler.cancel() }
    verify(exactly = 0) { scheduler.schedule() }
  }
}
