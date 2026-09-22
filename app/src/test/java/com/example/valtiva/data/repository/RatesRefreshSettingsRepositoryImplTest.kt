package com.example.valtiva.data.repository

import com.example.valtiva.data.source.LocalRatesRefreshSettingsDataSource
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertSame
import org.junit.Test

class RatesRefreshSettingsRepositoryImplTest {

  private val local = mockk<LocalRatesRefreshSettingsDataSource>()
  private val repository = RatesRefreshSettingsRepositoryImpl(local)

  @Test
  fun `exposes the local daily refresh preference stream as-is`() {
    val stream: Flow<Boolean> = flowOf(false)
    every { local.getDailyAutoRefreshEnabled() } returns stream

    assertSame(stream, repository.getDailyAutoRefreshEnabled())
  }

  @Test
  fun `persists the exact preference it was given`() = runTest {
    coJustRun { local.setDailyAutoRefreshEnabled(any()) }

    repository.setDailyAutoRefreshEnabled(false)

    coVerify(exactly = 1) { local.setDailyAutoRefreshEnabled(false) }
  }
}
