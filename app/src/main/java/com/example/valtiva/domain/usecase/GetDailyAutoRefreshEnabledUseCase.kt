package com.example.valtiva.domain.usecase

import com.example.valtiva.domain.repository.RatesRefreshSettingsRepository
import kotlinx.coroutines.flow.Flow

/** Observes the user's daily automatic refresh preference. */
class GetDailyAutoRefreshEnabledUseCase(
  private val ratesRefreshSettingsRepository: RatesRefreshSettingsRepository,
) {
  operator fun invoke(): Flow<Boolean> = ratesRefreshSettingsRepository.getDailyAutoRefreshEnabled()
}
