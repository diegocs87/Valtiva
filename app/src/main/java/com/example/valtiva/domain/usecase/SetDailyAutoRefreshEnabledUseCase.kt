package com.example.valtiva.domain.usecase

import com.example.valtiva.domain.repository.RatesRefreshSettingsRepository
import com.example.valtiva.domain.scheduler.RatesRefreshScheduler

/**
 * Changes the user's daily automatic refresh preference and keeps the background refresh job in
 * sync with it: enabling persists the flag and schedules the daily job, disabling persists the
 * flag and cancels it. Disabling never touches the offline-first read path — it only stops the
 * automatic background refresh.
 */
class SetDailyAutoRefreshEnabledUseCase(
  private val ratesRefreshSettingsRepository: RatesRefreshSettingsRepository,
  private val ratesRefreshScheduler: RatesRefreshScheduler,
) {
  suspend operator fun invoke(enabled: Boolean) {
    ratesRefreshSettingsRepository.setDailyAutoRefreshEnabled(enabled)
    if (enabled) ratesRefreshScheduler.schedule() else ratesRefreshScheduler.cancel()
  }
}
