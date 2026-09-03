package com.example.valtiva.presentation.converter

import com.example.valtiva.domain.model.Currency
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * A conversion another screen wants applied to the converter's form.
 *
 * Presentation-only: [amount] is the raw text the form would show, exactly like
 * [CurrencyConverterUiState.amount], not a parsed [java.math.BigDecimal]. It deliberately carries no
 * rate — applying one is what [CurrencyConverterViewModel]'s own conversion flow is for.
 */
data class PendingConversion(
  val amount: String,
  val baseCurrency: Currency,
  val targetCurrency: Currency,
)

/**
 * The write side of the handoff between a screen that offers a past conversion (History today) and
 * the converter. Kept separate from [ConverterHandoffReceiver] so a publisher can depend on the
 * ability to *ask* without also being able to *observe* — a screen requesting a hand-off has no
 * reason to see what, if anything, the converter does with it.
 */
interface ConverterHandoffPublisher {

  /** Asks the converter to load [pending] onto its form. */
  fun requestConversion(pending: PendingConversion)
}

/**
 * The read side of the handoff. [CurrencyConverterViewModel] depends on this alone, never on the
 * concrete [ConverterHandoffCoordinator] or on whichever screen published a request — it has no way
 * to know or care who is asking.
 */
interface ConverterHandoffReceiver {

  /** Conversions other screens have asked to load onto the converter's form. */
  val pendingConversion: Flow<PendingConversion>
}

/**
 * The single implementation behind both halves of the handoff.
 *
 * A [MutableSharedFlow] rather than a [kotlinx.coroutines.flow.StateFlow]: a hand-off is a one-time
 * event ("apply this now"), not a piece of state a late subscriber should replay — replaying one
 * after the converter has already moved on would silently overwrite a form the user has since
 * edited. [BufferOverflow.DROP_OLDEST] keeps [requestConversion] non-suspending without ever
 * blocking the caller.
 */
@Singleton
class ConverterHandoffCoordinator @Inject constructor() : ConverterHandoffPublisher, ConverterHandoffReceiver {

  private val _pendingConversion = MutableSharedFlow<PendingConversion>(
    extraBufferCapacity = 1,
    onBufferOverflow = BufferOverflow.DROP_OLDEST,
  )

  override val pendingConversion: Flow<PendingConversion> = _pendingConversion.asSharedFlow()

  override fun requestConversion(pending: PendingConversion) {
    _pendingConversion.tryEmit(pending)
  }
}
