package com.example.contacts.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp


// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)


val androidx.compose.material3.Typography.mediumTextStyleSize28: TextStyle
    get() = TextStyle(
        fontFamily = poppinsMedium,
        fontWeight = FontWeight.W700,
        fontSize = textSize28
    )

val androidx.compose.material3.Typography.mediumTextStyleSize24: TextStyle
    get() = TextStyle(
        fontFamily = poppinsMedium,
        fontWeight = FontWeight.W700,
        fontSize = textSize24
    )


val androidx.compose.material3.Typography.mediumTextStyleSize20: TextStyle
    get() = TextStyle(
        fontFamily = poppinsMedium,
        fontWeight = FontWeight.W700,
        fontSize = textSize20
    )



val androidx.compose.material3.Typography.mediumTextStyleSize16: TextStyle
    get() = TextStyle(
        fontFamily = poppinsMedium,
        fontWeight = FontWeight.W500,
        fontSize = textSize16
    )


val androidx.compose.material3.Typography.mediumTextStyleSize18: TextStyle
    get() = TextStyle(
        fontFamily = poppinsMedium,
        fontWeight = FontWeight.W500,
        fontSize = textSize18
    )

val androidx.compose.material3.Typography.mediumTextStyleSize12: TextStyle
    get() = TextStyle(
        fontFamily = poppinsRegular,
        fontWeight = FontWeight.W500,
        fontSize = textSize12
    )

val kotlin.text.Typography.mediumTextStyleSize14: TextStyle
    get() = TextStyle(
        fontFamily = poppinsBold,
        fontWeight = FontWeight.W500,
        fontSize = textSize14
    )