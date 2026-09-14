package com.example.valtiva.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.valtiva.domain.scheduler.RatesRefreshScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Registers [RefreshRatesWorker] as daily unique periodic work. The [RatesRefreshScheduler] port it
 * implements is what the Domain and Presentation layers depend on, so nothing above this class
 * needs to know WorkManager is the mechanism behind it.
 *
 * [ExistingPeriodicWorkPolicy.KEEP] leaves an already-scheduled run untouched, so calling
 * [schedule] repeatedly — whether on every app start or every time the user re-enables the
 * preference — never creates duplicate or equivalent workers; WorkManager persists the schedule
 * across process death on its own. The `CONNECTED` constraint only gates *when* the work is
 * allowed to run; it is not a connectivity check the worker performs itself. [cancel] is likewise
 * safe to call when nothing is scheduled.
 */
class RefreshRatesScheduler
@Inject
constructor(
  @ApplicationContext private val context: Context,
) : RatesRefreshScheduler {

  override fun schedule() {
    val constraints = Constraints.Builder()
      .setRequiredNetworkType(NetworkType.CONNECTED)
      .build()

    val request = PeriodicWorkRequestBuilder<RefreshRatesWorker>(1, TimeUnit.DAYS)
      .setConstraints(constraints)
      .build()

    WorkManager.getInstance(context)
      .enqueueUniquePeriodicWork(WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request)
  }

  override fun cancel() {
    WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
  }

  companion object {
    const val WORK_NAME = "com.example.valtiva.worker.RefreshRatesWorker.daily"
  }
}
