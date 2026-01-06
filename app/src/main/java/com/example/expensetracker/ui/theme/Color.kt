package com.example.expensetracker.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.isSystemInDarkTheme

// Light theme colors - Green spectrum
val Green80 = Color(0xFF28DC26)  // Primary green
val Green60 = Color(0xFF62e260)  // Secondary green  
val Green40 = Color(0xFF88e987)  // Tertiary green
val Green20 = Color(0xFFaff0ae)  // Light green
val Green10 = Color(0xFFd5f7d5)  // Very light green
val Green5 = Color(0xFFfcfefc)   // Almost white green

// Dark theme colors - Darker greens for better contrast
val DarkGreen80 = Color(0xFF2ba829)  // Darker primary
val DarkGreen60 = Color(0xFF4bb24d)  // Darker secondary
val DarkGreen40 = Color(0xFF6bbf6a)  // Darker tertiary
val DarkGreen20 = Color(0xFF1a5c19)  // Dark green background
val DarkGreen10 = Color(0xFF0f2e0f)  // Very dark green
val DarkGreen5 = Color(0xFF0a1a0a)   // Almost black green

// Red spectrum for negative amounts
val Red80 = Color(0xFFe91414)   // Primary red
val Red60 = Color(0xFFee3c3c)   // Secondary red
val Red40 = Color(0xFFf26767)   // Tertiary red  
val Red20 = Color(0xFFf9bbbb)   // Light red
val Red10 = Color(0xFFfef0f0)   // Very light red

// Dark theme reds
val DarkRed80 = Color(0xFFcc1212)   // Darker red
val DarkRed60 = Color(0xFFd42f2f)   // Darker secondary red
val DarkRed40 = Color(0xFFdb5555)   // Darker tertiary red
val DarkRed20 = Color(0xFF4a1111)   // Dark red background
val DarkRed10 = Color(0xFF2a0909)   // Very dark red

// Neutral colors for text and backgrounds
val NeutralLight = Color(0xFFffffff)
val NeutralDark = Color(0xFF121212)
val NeutralGray = Color(0xFF6c6c6c)

// Top bar colors that harmonize with green theme
val TopBarLightTeal = Color(0xFF4db6ac)     // Soft teal - green family, harmonious
val TopBarDarkTeal = Color(0xFF00695c)      // Deep teal - elegant green-blue
val TopBarLightBlue = Color(0xFF42a5f5)     // Soft blue - clean, works great with green
val TopBarDarkNavy = Color(0xFF1565c0)      // Deep navy - professional, complements green

// Extension properties for custom colors
val ColorScheme.positiveAmount: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) DarkGreen80 else Green80

val ColorScheme.negativeAmount: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) DarkRed80 else Red80

val ColorScheme.receiveButton: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) DarkGreen60 else Green60

val ColorScheme.payButton: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) DarkRed60 else Red60

val ColorScheme.receiveCard: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) DarkGreen20 else Green20

val ColorScheme.payCard: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) DarkRed20 else Red20

val ColorScheme.topBarColor: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) TopBarDarkTeal else TopBarLightTeal

val ColorScheme.deleteIcon: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) DarkRed60 else Red60

val ColorScheme.topBarText: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) NeutralLight else NeutralDark

val ColorScheme.dialogButtonReceive: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) DarkGreen60 else Green80

val ColorScheme.dialogButtonPay: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) DarkRed60 else Red80

val ColorScheme.positiveAmountOnSecondary: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) Green40 else Green80

val ColorScheme.personTopBarBackground: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) TopBarDarkNavy else TopBarLightBlue

val ColorScheme.personTopBarText: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) NeutralLight else NeutralDark