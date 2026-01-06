package com.example.expensetracker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

// ============================================================================
// Material 3 Expressive Color Palette - Premium Fintech Theme
// ============================================================================

// --- Primary: Vibrant Indigo ---
val PrimaryLight = Color(0xFF4F46E5)        // Rich indigo - modern & premium
val OnPrimaryLight = Color(0xFFFFFFFF)
val PrimaryContainerLight = Color(0xFFE0E7FF)
val OnPrimaryContainerLight = Color(0xFF1E1B4B)

val PrimaryDark = Color(0xFFA5B4FC)         // Soft lavender for dark
val OnPrimaryDark = Color(0xFF1E1B4B)
val PrimaryContainerDark = Color(0xFF3730A3)
val OnPrimaryContainerDark = Color(0xFFE0E7FF)

// --- Secondary: Vivid Emerald (income/positive) ---
val SecondaryLight = Color(0xFF059669)      // Deep emerald for better visibility
val OnSecondaryLight = Color(0xFFFFFFFF)
val SecondaryContainerLight = Color(0xFFA7F3D0)  // Lighter, more visible green
val OnSecondaryContainerLight = Color(0xFF064E3B)

val SecondaryDark = Color(0xFF34D399)       // Bright mint for dark
val OnSecondaryDark = Color(0xFF064E3B)
val SecondaryContainerDark = Color(0xFF065F46)
val OnSecondaryContainerDark = Color(0xFFD1FAE5)

// --- Tertiary: Rose Pink (expenses/negative) ---
val TertiaryLight = Color(0xFFE11D48)       // Vivid rose for visibility
val OnTertiaryLight = Color(0xFFFFFFFF)
val TertiaryContainerLight = Color(0xFFFECDD3)  // More saturated pink
val OnTertiaryContainerLight = Color(0xFF4C0519)

val TertiaryDark = Color(0xFFFB7185)        // Soft coral for dark
val OnTertiaryDark = Color(0xFF4C0519)
val TertiaryContainerDark = Color(0xFF9F1239)
val OnTertiaryContainerDark = Color(0xFFFFE4E6)

// --- Surface & Background (Light Mode with TINTED surfaces) ---
val SurfaceLight = Color(0xFFF5F3FF)        // Soft lavender tint
val OnSurfaceLight = Color(0xFF1E1B4B)      // Deep indigo for readability
val SurfaceVariantLight = Color(0xFFE8E5F7) // Lavender variant
val OnSurfaceVariantLight = Color(0xFF484565)
val BackgroundLight = Color(0xFFFAF8FF)     // Very soft purple-white
val OnBackgroundLight = Color(0xFF1E1B4B)

// Dark Mode: Deep midnight with blue undertones
val SurfaceDark = Color(0xFF0F172A)         // Deep slate blue
val OnSurfaceDark = Color(0xFFF1F5F9)
val SurfaceVariantDark = Color(0xFF1E293B)  // Slate 800
val OnSurfaceVariantDark = Color(0xFFCBD5E1)
val BackgroundDark = Color(0xFF020617)      // Near black with blue
val OnBackgroundDark = Color(0xFFF1F5F9)

// --- Outline ---
val OutlineLight = Color(0xFF8B85B0)        // Soft purple-gray
val OutlineVariantLight = Color(0xFFCBC6E2) // Lighter purple-gray

val OutlineDark = Color(0xFF475569)         // Slate 600
val OutlineVariantDark = Color(0xFF334155)  // Slate 700

// --- Error ---
val ErrorLight = Color(0xFFDC2626)
val OnErrorLight = Color(0xFFFFFFFF)
val ErrorContainerLight = Color(0xFFFEE2E2)
val OnErrorContainerLight = Color(0xFF7F1D1D)

val ErrorDark = Color(0xFFFCA5A5)
val OnErrorDark = Color(0xFF7F1D1D)
val ErrorContainerDark = Color(0xFF991B1B)
val OnErrorContainerDark = Color(0xFFFEE2E2)

// --- Surface Containers (Material 3 Expressive Elevation) ---
// Light: Lavender-tinted progression for visual hierarchy
val SurfaceContainerLowestLight = Color(0xFFFFFBFF)  // Purest white with hint
val SurfaceContainerLowLight = Color(0xFFF8F5FF)     // Soft lavender
val SurfaceContainerLight_ = Color(0xFFF0EDFA)       // Medium lavender
val SurfaceContainerHighLight = Color(0xFFE8E4F5)    // Stronger lavender
val SurfaceContainerHighestLight = Color(0xFFDDD8ED) // Most saturated

// Dark: Rich elevated surfaces
val SurfaceContainerLowestDark = Color(0xFF020617)
val SurfaceContainerLowDark = Color(0xFF0F172A)
val SurfaceContainerDark_ = Color(0xFF1E293B)
val SurfaceContainerHighDark = Color(0xFF334155)
val SurfaceContainerHighestDark = Color(0xFF475569)

// ============================================================================
// Semantic Color Extensions
// ============================================================================

val ColorScheme.positiveAmount: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) SecondaryDark else Color(0xFF047857) // Deeper green for light

val ColorScheme.negativeAmount: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) TertiaryDark else Color(0xFFBE123C) // Deeper rose for light

val ColorScheme.receiveButton: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) SecondaryDark else SecondaryLight

val ColorScheme.payButton: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) TertiaryDark else TertiaryLight

val ColorScheme.receiveCard: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) Color(0xFF064E3B) else Color(0xFFD1FAE5)

val ColorScheme.payCard: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) Color(0xFF4C0519) else Color(0xFFFECDD3)

val ColorScheme.topBarColor: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) SurfaceContainerDark_ else SurfaceContainerLight_

val ColorScheme.deleteIcon: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) TertiaryDark else TertiaryLight

val ColorScheme.topBarText: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) OnSurfaceDark else OnSurfaceLight

val ColorScheme.dialogButtonReceive: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) SecondaryDark else SecondaryLight

val ColorScheme.dialogButtonPay: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) TertiaryDark else TertiaryLight

val ColorScheme.personTopBarBackground: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) SurfaceContainerHighDark else SurfaceContainerHighLight

val ColorScheme.personTopBarText: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) OnSurfaceDark else OnSurfaceLight

// Card surface - distinct from background in light mode
val ColorScheme.cardSurface: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) SurfaceContainerHighDark else Color(0xFFFFFFFF)

val ColorScheme.subtleDivider: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) OutlineVariantDark else OutlineVariantLight

// Premium gradient accent colors
val ColorScheme.accentGradientStart: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) Color(0xFF6366F1) else Color(0xFF4F46E5)

val ColorScheme.accentGradientEnd: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) Color(0xFF8B5CF6) else Color(0xFF7C3AED)