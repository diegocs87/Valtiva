package com.example.valtiva.domain.usecase

import com.example.valtiva.domain.model.ThemeMode
import com.example.valtiva.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow

/** Observes the user's appearance preference. */
class GetThemeModeUseCase(
  private val themeRepository: ThemeRepository,
) {
  operator fun invoke(): Flow<ThemeMode> = themeRepository.getThemeMode()
}
