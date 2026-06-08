package app.lawnchair.ui.theme

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Typography
import androidx.compose.ui.unit.sp

private val base = Typography()

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
val Typography = Typography(
    displayLarge = base.displayLarge.copy(fontFamily = NType82.Medium),
    displayMedium = base.displayMedium.copy(fontFamily = NType82.Medium),
    displaySmall = base.displaySmall.copy(fontFamily = NType82.Medium),
    headlineLarge = base.headlineLarge.copy(fontFamily = NType82.Regular),
    headlineMedium = base.headlineMedium.copy(fontFamily = NType82.Regular),
    headlineSmall = base.headlineSmall.copy(fontFamily = NType82.Regular),
    titleLarge = base.titleLarge.copy(fontFamily = NType82.Medium),
    titleMedium = base.titleMedium.copy(fontFamily = NType82.Medium),
    titleSmall = base.titleSmall.copy(fontFamily = NType82.Medium),
    bodyLarge = base.bodyLarge.copy(fontFamily = NType82.Regular, letterSpacing = 0.sp),
    bodyMedium = base.bodyMedium.copy(fontFamily = NType82.Regular, letterSpacing = 0.1.sp),
    bodySmall = base.bodySmall.copy(fontFamily = NType82.Regular),
    labelLarge = base.labelLarge.copy(fontFamily = NType82.Medium),
    labelMedium = base.labelMedium.copy(fontFamily = NType82.Medium),
    labelSmall = base.labelSmall.copy(fontFamily = NType82.Medium),
    displayLargeEmphasized = base.displayLargeEmphasized.copy(fontFamily = NType82.SemiBold),
    displayMediumEmphasized = base.displayMediumEmphasized.copy(fontFamily = NType82.SemiBold),
    displaySmallEmphasized = base.displaySmallEmphasized.copy(fontFamily = NType82.SemiBold),
    headlineLargeEmphasized = base.headlineLargeEmphasized.copy(fontFamily = NType82.Medium),
    headlineMediumEmphasized = base.headlineMediumEmphasized.copy(fontFamily = NType82.Medium),
    headlineSmallEmphasized = base.headlineSmallEmphasized.copy(fontFamily = NType82.Medium),
    titleLargeEmphasized = base.titleLargeEmphasized.copy(fontFamily = NType82.SemiBold),
    titleMediumEmphasized = base.titleMediumEmphasized.copy(fontFamily = NType82.SemiBold),
    titleSmallEmphasized = base.titleSmallEmphasized.copy(fontFamily = NType82.SemiBold),
    bodyLargeEmphasized = base.bodyLargeEmphasized.copy(fontFamily = NType82.Medium),
    bodyMediumEmphasized = base.bodyMediumEmphasized.copy(fontFamily = NType82.Medium),
    bodySmallEmphasized = base.bodySmallEmphasized.copy(fontFamily = NType82.Medium),
    labelLargeEmphasized = base.labelLargeEmphasized.copy(fontFamily = NType82.SemiBold),
    labelMediumEmphasized = base.labelMediumEmphasized.copy(fontFamily = NType82.SemiBold),
    labelSmallEmphasized = base.labelSmallEmphasized.copy(fontFamily = NType82.SemiBold),
)
