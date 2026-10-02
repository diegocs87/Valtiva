package com.example.valtiva.diagnostics

import com.google.firebase.crashlytics.FirebaseCrashlytics
import javax.inject.Inject

/**
 * The only class in Valtiva allowed to talk to [FirebaseCrashlytics] directly.
 *
 * Pure delegation: both members forward to the matching Crashlytics call and add no logic of their
 * own, since there is no decision to make here — what counts as worth reporting is decided by the
 * caller, not by this class.
 */
class FirebaseCrashReporter @Inject constructor(
  private val crashlytics: FirebaseCrashlytics,
) : CrashReporter {

  override fun recordException(exception: Throwable) {
    crashlytics.recordException(exception)
  }

  override fun log(message: String) {
    crashlytics.log(message)
  }
}
