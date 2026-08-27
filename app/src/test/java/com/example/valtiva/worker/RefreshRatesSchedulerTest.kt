package com.example.valtiva.worker

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.Configuration
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.testing.WorkManagerTestInitHelper
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Confirms the daily refresh is registered the way UniquePeriodicWork requires. */
@RunWith(RobolectricTestRunner::class)
class RefreshRatesSchedulerTest {

  private val context: Context = ApplicationProvider.getApplicationContext()

  @Before
  fun setUp() {
    WorkManagerTestInitHelper.initializeTestWorkManager(context, Configuration.Builder().build())
  }

  @Test
  fun `schedules exactly one enqueued periodic work under the expected unique name`() {
    RefreshRatesScheduler(context).schedule()

    val workInfos = WorkManager.getInstance(context)
      .getWorkInfosForUniqueWork(RefreshRatesScheduler.WORK_NAME)
      .get()

    assertEquals(1, workInfos.size)
    assertEquals(WorkInfo.State.ENQUEUED, workInfos.single().state)
  }

  @Test
  fun `calling schedule again keeps the existing work instead of duplicating it`() {
    RefreshRatesScheduler(context).schedule()
    RefreshRatesScheduler(context).schedule()

    val workInfos = WorkManager.getInstance(context)
      .getWorkInfosForUniqueWork(RefreshRatesScheduler.WORK_NAME)
      .get()

    assertEquals(1, workInfos.size)
  }
}
