package com.example.valtiva

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.example.valtiva.domain.usecase.SyncRatesRefreshSchedulingUseCase
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@HiltAndroidApp
class ValtivaApplication : Application(), Configuration.Provider {

  @Inject lateinit var workerFactory: HiltWorkerFactory

  @Inject lateinit var syncRatesRefreshScheduling: SyncRatesRefreshSchedulingUseCase

  private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

  override val workManagerConfiguration: Configuration
    get() = Configuration.Builder().setWorkerFactory(workerFactory).build()

  override fun onCreate() {
    super.onCreate()
    applicationScope.launch { syncRatesRefreshScheduling() }
  }
}
