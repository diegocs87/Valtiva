package com.example.valtiva.domain.usecase

import com.example.valtiva.domain.model.ThemeMode
import com.example.valtiva.domain.repository.ThemeRepository

/** Changes the user's appearance preference. */
class SetThemeModeUseCase(
  private val themeRepository: ThemeRepository,
) {
  suspend operator fun invoke(mode: ThemeMode) = themeRepository.setThemeMode(mode)
}
