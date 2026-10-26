package com.example.valtiva.data.repository

import com.example.valtiva.data.source.LocalThemeDataSource
import com.example.valtiva.domain.model.ThemeMode
import com.example.valtiva.domain.repository.ThemeRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ThemeRepositoryImpl @Inject constructor(
  private val localDataSource: LocalThemeDataSource,
) : ThemeRepository {

  override fun getThemeMode(): Flow<ThemeMode> = localDataSource.getThemeMode()

  override suspend fun setThemeMode(mode: ThemeMode) {
    localDataSource.setThemeMode(mode)
  }
}
