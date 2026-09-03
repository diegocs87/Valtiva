package com.example.valtiva.presentation.history

import com.example.valtiva.domain.COP
import com.example.valtiva.domain.RATE_DATE
import com.example.valtiva.domain.USD
import com.example.valtiva.domain.model.ConversionHistoryEntry
import com.example.valtiva.domain.model.ConversionResult
import com.example.valtiva.domain.model.Money
import com.example.valtiva.domain.usecase.GetConversionHistoryUseCase
import com.example.valtiva.presentation.MainDispatcherRule
import com.example.valtiva.presentation.converter.ConverterHandoffPublisher
import com.example.valtiva.presentation.converter.PendingConversion
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.math.BigDecimal
import java.time.Instant
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Behavioural tests for the history ViewModel: the stream emits, and the resulting [HistoryUiState]
 * is asserted. No Room, no Hilt: this class depends only on the domain.
 */
class HistoryViewModelTest {

  @get:Rule
  val mainDispatcherRule = MainDispatcherRule()

  private val getConversionHistory = mockk<GetConversionHistoryUseCase>()
  private val converterHandoff = mockk<ConverterHandoffPublisher>(relaxed = true)
  private val history = MutableSharedFlow<List<ConversionHistoryEntry>>(replay = 1)

  private val entry = ConversionHistoryEntry(
    id = 1,
    conversion = ConversionResult(
      source = Money(BigDecimal("100.00"), USD),
      target = Money(BigDecimal("398025.00"), COP),
      exchangeRate = BigDecimal("3980.25"),
      rateDate = RATE_DATE,
    ),
    performedAt = Instant.parse("2026-08-14T10:00:00Z"),
  )

  @Before
  fun setUp() {
    every { getConversionHistory() } returns history
  }

  private fun viewModel() = HistoryViewModel(getConversionHistory, converterHandoff)

  @Test
  fun `starts loading before the stream emits`() {
    val emptyHistory = MutableSharedFlow<List<ConversionHistoryEntry>>()
    every { getConversionHistory() } returns emptyHistory

    val state = viewModel().uiState.value

    assertTrue(state.isLoading)
    assertTrue(state.conversions.isEmpty())
    assertFalse(state.isUnavailable)
  }

  @Test
  fun `publishes the conversions the stream emits`() = runTest {
    history.emit(listOf(entry))

    val state = viewModel().uiState.value

    assertEquals(listOf(entry), state.conversions)
    assertFalse(state.isLoading)
  }

  @Test
  fun `an empty stream is not the same as an unavailable one`() = runTest {
    history.emit(emptyList())

    val state = viewModel().uiState.value

    assertTrue(state.conversions.isEmpty())
    assertFalse(state.isLoading)
    assertFalse(state.isUnavailable)
  }

  @Test
  fun `marks the history unavailable when the stream fails`() = runTest {
    every { getConversionHistory() } returns flow { throw IllegalStateException("history table is gone") }

    val state = viewModel().uiState.value

    assertTrue(state.isUnavailable)
    assertFalse(state.isLoading)
    assertTrue(state.conversions.isEmpty())
  }

  @Test
  fun `selecting a conversion hands only its amount and currencies to the converter`() = runTest {
    // PendingConversion has no rate field at all: this equality check is itself the proof that
    // the historical rate never travels with the hand-off.
    history.emit(listOf(entry))
    val viewModel = viewModel()

    viewModel.onEvent(HistoryUiEvent.ConversionSelected(entry))

    verify(exactly = 1) {
      converterHandoff.requestConversion(PendingConversion("100.00", USD, COP))
    }
  }
}
