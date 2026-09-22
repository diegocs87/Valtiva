package com.example.valtiva.domain.usecase

import com.example.valtiva.domain.repository.RatesRefreshSettingsRepository
import com.example.valtiva.domain.scheduler.RatesRefreshScheduler
import kotlinx.coroutines.flow.first

/**
 * Brings the background refresh job in line with the currently persisted preference.
 *
 * Meant to run once at app start: WorkManager persists a scheduled job across process death on its
 * own, but the preference can only be honored by asking it to schedule or cancel again, since
 * nothing else re-applies an OFF preference on a fresh process.
 */
class SyncRatesRefreshSchedulingUseCase(
  private val ratesRefreshSettingsRepository: RatesRefreshSettingsRepository,
  private val ratesRefreshScheduler: RatesRefreshScheduler,
) {
  suspend operator fun invoke() {
    val enabled = ratesRefreshSettingsRepository.getDailyAutoRefreshEnabled().first()
    if (enabled) ratesRefreshScheduler.schedule() else ratesRefreshScheduler.cancel()
  }
}
