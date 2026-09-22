package com.example.valtiva.data.repository

import com.example.valtiva.data.source.LocalRatesRefreshSettingsDataSource
import com.example.valtiva.domain.repository.RatesRefreshSettingsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * The daily automatic refresh preference is purely local, no remote source and no freshness
 * rules, so this repository is a thin pass-through over the local data source — the same shape as
 * [ThemeRepositoryImpl].
 */
class RatesRefreshSettingsRepositoryImpl @Inject constructor(
  private val localDataSource: LocalRatesRefreshSettingsDataSource,
) : RatesRefreshSettingsRepository {

  override fun getDailyAutoRefreshEnabled(): Flow<Boolean> =
    localDataSource.getDailyAutoRefreshEnabled()

  override suspend fun setDailyAutoRefreshEnabled(enabled: Boolean) {
    localDataSource.setDailyAutoRefreshEnabled(enabled)
  }
}
