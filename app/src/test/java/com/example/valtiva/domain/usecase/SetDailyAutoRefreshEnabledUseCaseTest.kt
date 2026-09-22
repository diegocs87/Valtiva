package com.example.valtiva.domain.usecase

import com.example.valtiva.domain.repository.RatesRefreshSettingsRepository
import com.example.valtiva.domain.scheduler.RatesRefreshScheduler
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SetDailyAutoRefreshEnabledUseCaseTest {

  private val repository = mockk<RatesRefreshSettingsRepository>()
  private val scheduler = mockk<RatesRefreshScheduler>()
  private val setDailyAutoRefreshEnabled = SetDailyAutoRefreshEnabledUseCase(repository, scheduler)

  @Test
  fun `enabling persists the flag and schedules the daily job`() = runTest {
    coJustRun { repository.setDailyAutoRefreshEnabled(any()) }
    justRun { scheduler.schedule() }

    setDailyAutoRefreshEnabled(true)

    coVerifyOrder {
      repository.setDailyAutoRefreshEnabled(true)
      scheduler.schedule()
    }
    verify(exactly = 0) { scheduler.cancel() }
  }

  @Test
  fun `disabling persists the flag and cancels the daily job`() = runTest {
    coJustRun { repository.setDailyAutoRefreshEnabled(any()) }
    justRun { scheduler.cancel() }

    setDailyAutoRefreshEnabled(false)

    coVerifyOrder {
      repository.setDailyAutoRefreshEnabled(false)
      scheduler.cancel()
    }
    verify(exactly = 0) { scheduler.schedule() }
  }

  @Test
  fun `toggling repeatedly never leaves more than one call to the scheduler per toggle`() = runTest {
    coJustRun { repository.setDailyAutoRefreshEnabled(any()) }
    justRun { scheduler.schedule() }
    justRun { scheduler.cancel() }

    setDailyAutoRefreshEnabled(true)
    setDailyAutoRefreshEnabled(true)
    setDailyAutoRefreshEnabled(false)

    coVerify(exactly = 2) { scheduler.schedule() }
    coVerify(exactly = 1) { scheduler.cancel() }
  }
}
