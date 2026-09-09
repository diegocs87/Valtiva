package com.example.valtiva.domain.usecase

import app.cash.turbine.test
import com.example.valtiva.domain.model.ThemeMode
import com.example.valtiva.domain.repository.ThemeRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetThemeModeUseCaseTest {

  private val repository = mockk<ThemeRepository>()
  private val getThemeMode = GetThemeModeUseCase(repository)

  @Test
  fun `emits the theme mode published by the repository`() = runTest {
    every { repository.getThemeMode() } returns flowOf(ThemeMode.DARK)

    getThemeMode().test {
      assertEquals(ThemeMode.DARK, awaitItem())
      awaitComplete()
    }
  }

  @Test
  fun `re-emits every update of the theme mode stream`() = runTest {
    every { repository.getThemeMode() } returns flowOf(ThemeMode.SYSTEM, ThemeMode.LIGHT)

    getThemeMode().test {
      assertEquals(ThemeMode.SYSTEM, awaitItem())
      assertEquals(ThemeMode.LIGHT, awaitItem())
      awaitComplete()
    }
  }
}
