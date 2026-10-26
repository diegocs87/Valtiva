package com.example.valtiva.data.repository

import com.example.valtiva.data.source.LocalRatesRefreshSettingsDataSource
import com.example.valtiva.domain.repository.RatesRefreshSettingsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class RatesRefreshSettingsRepositoryImpl @Inject constructor(
  private val localDataSource: LocalRatesRefreshSettingsDataSource,
) : RatesRefreshSettingsRepository {

  override fun getDailyAutoRefreshEnabled(): Flow<Boolean> =
    localDataSource.getDailyAutoRefreshEnabled()

  override suspend fun setDailyAutoRefreshEnabled(enabled: Boolean) {
    localDataSource.setDailyAutoRefreshEnabled(enabled)
  }
}
