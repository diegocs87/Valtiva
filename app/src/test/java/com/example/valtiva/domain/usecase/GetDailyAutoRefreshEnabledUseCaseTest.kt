package com.example.valtiva.domain.usecase

import app.cash.turbine.test
import com.example.valtiva.domain.repository.RatesRefreshSettingsRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetDailyAutoRefreshEnabledUseCaseTest {

  private val repository = mockk<RatesRefreshSettingsRepository>()
  private val getDailyAutoRefreshEnabled = GetDailyAutoRefreshEnabledUseCase(repository)

  @Test
  fun `emits the value published by the repository`() = runTest {
    every { repository.getDailyAutoRefreshEnabled() } returns flowOf(false)

    getDailyAutoRefreshEnabled().test {
      assertEquals(false, awaitItem())
      awaitComplete()
    }
  }

  @Test
  fun `re-emits every update of the preference stream`() = runTest {
    every { repository.getDailyAutoRefreshEnabled() } returns flowOf(true, false)

    getDailyAutoRefreshEnabled().test {
      assertEquals(true, awaitItem())
      assertEquals(false, awaitItem())
      awaitComplete()
    }
  }
}
