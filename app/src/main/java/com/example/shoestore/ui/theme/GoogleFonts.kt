package com.example.shoestore.ui.theme

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.Font
import com.example.shoestore.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

// Sử dụng font Oswald
val OswaldFont = FontFamily(
    Font(
        googleFont = GoogleFont("Oswald"),
        fontProvider = provider
    )
)

// Sử dụng font Roboto
val RobotoFont = FontFamily(
    Font(
        googleFont = GoogleFont("Roboto"),
        fontProvider = provider
    )
)