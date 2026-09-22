package com.example.valtiva.diagnostics

import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

/**
 * [FirebaseCrashReporter] is pure delegation, so these tests only assert that each call reaches
 * [FirebaseCrashlytics] unchanged.
 */
class FirebaseCrashReporterTest {

  private val crashlytics = mockk<FirebaseCrashlytics>(relaxed = true)
  private val reporter = FirebaseCrashReporter(crashlytics)

  @Test
  fun `recordException delegates the exact exception to Crashlytics`() {
    val exception = IllegalStateException("boom")

    reporter.recordException(exception)

    verify(exactly = 1) { crashlytics.recordException(exception) }
  }

  @Test
  fun `log delegates the exact message to Crashlytics`() {
    reporter.log("syncing rates: source=remote base=USD")

    verify(exactly = 1) { crashlytics.log("syncing rates: source=remote base=USD") }
  }
}
