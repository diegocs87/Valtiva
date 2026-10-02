package com.example.valtiva.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

/**
 * The only class in Valtiva allowed to talk to [FirebaseAnalytics] directly.
 *
 * Its whole job is translation: an [AnalyticsEvent]'s name and string parameters become a
 * [Bundle] and a call to [FirebaseAnalytics.logEvent]. It adds no business rule of its own — which
 * events exist and what they carry is decided by [AnalyticsEvent], not here.
 */
class FirebaseAnalyticsTracker @Inject constructor(
  private val firebaseAnalytics: FirebaseAnalytics,
) : AnalyticsTracker {

  override fun trackEvent(event: AnalyticsEvent) {
    val params = Bundle().apply {
      event.params.forEach { (key, value) -> putString(key, value) }
    }
    firebaseAnalytics.logEvent(event.name, params)
  }
}
