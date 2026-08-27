package com.example.valtiva.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Registers [RefreshRatesWorker] as daily unique periodic work.
 *
 * [ExistingPeriodicWorkPolicy.KEEP] leaves an already-scheduled run untouched, so calling
 * [schedule] on every app start — the natural place to do it — never creates duplicate or
 * equivalent workers; WorkManager persists the schedule across process death on its own. The
 * `CONNECTED` constraint only gates *when* the work is allowed to run; it is not a connectivity
 * check the worker performs itself.
 */
class RefreshRatesScheduler
@Inject
constructor(
  @ApplicationContext private val context: Context,
) {

  fun schedule() {
    val constraints = Constraints.Builder()
      .setRequiredNetworkType(NetworkType.CONNECTED)
      .build()

    val request = PeriodicWorkRequestBuilder<RefreshRatesWorker>(1, TimeUnit.DAYS)
      .setConstraints(constraints)
      .build()

    WorkManager.getInstance(context)
      .enqueueUniquePeriodicWork(WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request)
  }

  companion object {
    const val WORK_NAME = "com.example.valtiva.worker.RefreshRatesWorker.daily"
  }
}
