package com.example.valtiva.data.remote.serializer

import java.math.BigDecimal
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonUnquotedLiteral
import kotlinx.serialization.json.jsonPrimitive

/**
 * Reads and writes a [BigDecimal] without ever going through a `Double`.
 *
 * kotlinx.serialization has no built-in [BigDecimal] support, and decoding a JSON number with
 * `decodeDouble` would round the value to the nearest binary fraction before we ever see it. This
 * serializer instead grabs the *raw* JSON literal and hands it to [BigDecimal] as text, so the
 * exact digits sent by the server survive, including their scale.
 *
 * On the way out, [JsonUnquotedLiteral] keeps the value a JSON number rather than turning it into a
 * quoted string, so a decode/encode round trip reproduces the original payload.
 */
object BigDecimalSerializer : KSerializer<BigDecimal> {

  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("java.math.BigDecimal", PrimitiveKind.STRING)

  override fun deserialize(decoder: Decoder): BigDecimal {
    val literal = when (decoder) {
      is JsonDecoder -> decoder.decodeJsonElement().jsonPrimitive.content
      else -> decoder.decodeString()
    }
    return BigDecimal(literal)
  }

  @OptIn(ExperimentalSerializationApi::class)
  override fun serialize(encoder: Encoder, value: BigDecimal) {
    when (encoder) {
      is JsonEncoder -> encoder.encodeJsonElement(JsonUnquotedLiteral(value.toPlainString()))
      else -> encoder.encodeString(value.toPlainString())
    }
  }
}
