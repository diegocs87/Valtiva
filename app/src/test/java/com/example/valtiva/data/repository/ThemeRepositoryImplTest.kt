package com.example.valtiva.data.repository

import com.example.valtiva.data.source.LocalThemeDataSource
import com.example.valtiva.domain.model.ThemeMode
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertSame
import org.junit.Test

class ThemeRepositoryImplTest {

  private val local = mockk<LocalThemeDataSource>()
  private val repository = ThemeRepositoryImpl(local)

  @Test
  fun `exposes the local theme mode stream as-is`() {
    val stream: Flow<ThemeMode> = flowOf(ThemeMode.DARK)
    every { local.getThemeMode() } returns stream

    assertSame(stream, repository.getThemeMode())
  }

  @Test
  fun `persists the exact theme mode it was given`() = runTest {
    coJustRun { local.setThemeMode(any()) }

    repository.setThemeMode(ThemeMode.LIGHT)

    coVerify(exactly = 1) { local.setThemeMode(ThemeMode.LIGHT) }
  }
}
