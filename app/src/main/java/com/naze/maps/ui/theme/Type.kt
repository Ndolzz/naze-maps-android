package com.naze.maps.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Sistem font default Android (Roboto) dipakai sebagai pengganti Inter/Manrope
// agar tidak menambah bundle size; ganti ke font kustom lewat res/font jika dibutuhkan nanti.
val NazeTypography = Typography(
    headlineSmall = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 20.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 17.sp),
    bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.5.sp),
    bodyMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 13.5.sp),
    labelSmall = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 11.5.sp),
)
