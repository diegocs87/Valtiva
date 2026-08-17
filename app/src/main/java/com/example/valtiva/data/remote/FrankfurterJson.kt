package com.example.valtiva.data.remote

import kotlinx.serialization.json.Json

/**
 * JSON configuration used to talk to the Frankfurter API.
 *
 * `ignoreUnknownKeys` is required rather than convenient: the DTOs model only the fields the domain
 * needs, so every response already carries keys we do not declare, and the API is free to add more
 * without that breaking deserialization.
 */
val FrankfurterJson: Json = Json { ignoreUnknownKeys = true }
