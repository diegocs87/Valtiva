package com.example.valtiva.domain.model

/**
 * The app's appearance preference. [SYSTEM] mirrors the OS setting rather than pinning [LIGHT] or
 * [DARK], so the app keeps following the device once the user has not overridden it.
 */
enum class ThemeMode {
  LIGHT,
  DARK,
  SYSTEM,
}
