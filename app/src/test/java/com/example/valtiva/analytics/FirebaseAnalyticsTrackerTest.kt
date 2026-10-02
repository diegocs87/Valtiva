package com.example.valtiva.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * [FirebaseAnalyticsTracker] is the only class allowed to touch [FirebaseAnalytics] directly, so
 * these tests assert exactly what crosses that boundary: the event name and its parameters, read
 * back from the real [Bundle] the tracker builds. A real `Bundle` needs Robolectric, the same way
 * [com.example.valtiva.worker.RefreshRatesWorkerTest] needs it for a real `Context`.
 */
@RunWith(RobolectricTestRunner::class)
class FirebaseAnalyticsTrackerTest {

  private val firebaseAnalytics = mockk<FirebaseAnalytics>(relaxed = true)
  private val tracker = FirebaseAnalyticsTracker(firebaseAnalytics)

  @Test
  fun `logs the event under its own name`() {
    tracker.trackEvent(AnalyticsEvent.HistoryOpened)

    verify(exactly = 1) { firebaseAnalytics.logEvent(AnalyticsEventName.HISTORY_OPENED, any()) }
  }

  @Test
  fun `sends the event's parameters as strings`() {
    val bundle = slot<Bundle>()

    tracker.trackEvent(AnalyticsEvent.ConversionCompleted(baseCurrency = "USD", targetCurrency = "COP"))

    verify(exactly = 1) { firebaseAnalytics.logEvent(AnalyticsEventName.CONVERSION_COMPLETED, capture(bundle)) }
    assertEquals("USD", bundle.captured.getString(AnalyticsParam.BASE_CURRENCY))
    assertEquals("COP", bundle.captured.getString(AnalyticsParam.TARGET_CURRENCY))
  }

  @Test
  fun `sends no parameter beyond what the event declares`() {
    val bundle = slot<Bundle>()

    tracker.trackEvent(AnalyticsEvent.SettingsOpened)

    verify(exactly = 1) { firebaseAnalytics.logEvent(AnalyticsEventName.SETTINGS_OPENED, capture(bundle)) }
    assertTrue(bundle.captured.keySet().isEmpty())
  }

  @Test
  fun `never sends a monetary amount, only currency codes`() {
    val bundle = slot<Bundle>()

    tracker.trackEvent(AnalyticsEvent.RatesRefreshed(baseCurrency = "USD"))

    verify(exactly = 1) { firebaseAnalytics.logEvent(AnalyticsEventName.RATES_REFRESHED, capture(bundle)) }
    assertEquals(setOf(AnalyticsParam.BASE_CURRENCY), bundle.captured.keySet())
    assertNull(bundle.captured.getString("amount"))
  }
}
