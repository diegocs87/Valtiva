package com.example.valtiva.domain.usecase

import com.example.valtiva.domain.model.ThemeMode
import com.example.valtiva.domain.repository.ThemeRepository
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SetThemeModeUseCaseTest {

  private val repository = mockk<ThemeRepository>()
  private val setThemeMode = SetThemeModeUseCase(repository)

  @Test
  fun `persists exactly the requested theme mode`() = runTest {
    coJustRun { repository.setThemeMode(any()) }

    setThemeMode(ThemeMode.DARK)

    coVerify(exactly = 1) { repository.setThemeMode(ThemeMode.DARK) }
    coVerify(exactly = 0) { repository.setThemeMode(ThemeMode.LIGHT) }
  }
}
