package com.example.valtiva.presentation.theme

import androidx.compose.ui.graphics.Color

// Light palette — the app's fintech identity, and the default theme.
val Primary = Color(0xFF2563EB)
val PrimaryDark = Color(0xFF1D4ED8)
val PrimaryContainerLight = Color(0xFFDCE8FD)
val Background = Color(0xFFF8FAFC)
val Surface = Color(0xFFFFFFFF)
val SurfaceVariant = Color(0xFFF1F5F9)
val TextPrimary = Color(0xFF0F172A)
val TextSecondary = Color(0xFF64748B)
val Success = Color(0xFF16A34A)
val SuccessContainer = Color(0xFFDCFCE7)
val ErrorColor = Color(0xFFDC2626)
val ErrorContainer = Color(0xFFFEE2E2)
val Divider = Color(0xFFE2E8F0)

// Dark palette — kept in step with the light one, token for token, so a future dark theme needs
// no component changes: every composable already reads colors through MaterialTheme/ValtivaColors.
val PrimaryDarkTheme = Color(0xFF60A5FA)
val PrimaryContainerDark = Color(0xFF1E3A8A)
val BackgroundDark = Color(0xFF0B1220)
val SurfaceDark = Color(0xFF111827)
val SurfaceVariantDark = Color(0xFF1E293B)
val TextPrimaryDark = Color(0xFFF1F5F9)
val TextSecondaryDark = Color(0xFF94A3B8)
val SuccessDark = Color(0xFF4ADE80)
val SuccessContainerDark = Color(0xFF14532D)
val ErrorDark = Color(0xFFF87171)
val ErrorContainerDark = Color(0xFF7F1D1D)
val DividerDark = Color(0xFF1F2937)
