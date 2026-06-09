package app.lawnchair.ui.theme

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import app.lawnchair.preferences.getAdapter
import app.lawnchair.preferences.preferenceManager

private val base = Typography()

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
val Typography = createTypography(
    headingFamily = NType82.Regular,
    headingMediumFamily = NType82.Medium,
    bodyFamily = NType82.Regular,
    bodyMediumFamily = NType82.Medium,
    headingBoldFamily = NType82.SemiBold,
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun fontTypography(): Typography {
    val prefs = preferenceManager()
    val headingFamily by prefs.fontHeading.getAdapter().state
    val headingMediumFamily by prefs.fontHeadingMedium.getAdapter().state
    val bodyFamily by prefs.fontBody.getAdapter().state
    val bodyMediumFamily by prefs.fontBodyMedium.getAdapter().state
    return createTypography(
        headingFamily = headingFamily.composeFontFamily,
        headingMediumFamily = headingMediumFamily.composeFontFamily,
        bodyFamily = bodyFamily.composeFontFamily,
        bodyMediumFamily = bodyMediumFamily.composeFontFamily,
        headingBoldFamily = headingMediumFamily.composeFontFamily,
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
private fun createTypography(
    headingFamily: FontFamily,
    headingMediumFamily: FontFamily,
    bodyFamily: FontFamily,
    bodyMediumFamily: FontFamily,
    headingBoldFamily: FontFamily,
): Typography = Typography(
    displayLarge = base.displayLarge.copy(fontFamily = headingMediumFamily),
    displayMedium = base.displayMedium.copy(fontFamily = headingMediumFamily),
    displaySmall = base.displaySmall.copy(fontFamily = headingMediumFamily),
    headlineLarge = base.headlineLarge.copy(fontFamily = headingFamily),
    headlineMedium = base.headlineMedium.copy(fontFamily = headingFamily),
    headlineSmall = base.headlineSmall.copy(fontFamily = headingFamily),
    titleLarge = base.titleLarge.copy(fontFamily = headingMediumFamily),
    titleMedium = base.titleMedium.copy(fontFamily = headingMediumFamily),
    titleSmall = base.titleSmall.copy(fontFamily = headingMediumFamily),
    bodyLarge = base.bodyLarge.copy(fontFamily = bodyFamily, letterSpacing = 0.sp),
    bodyMedium = base.bodyMedium.copy(fontFamily = bodyFamily, letterSpacing = 0.1.sp),
    bodySmall = base.bodySmall.copy(fontFamily = bodyFamily),
    labelLarge = base.labelLarge.copy(fontFamily = bodyMediumFamily),
    labelMedium = base.labelMedium.copy(fontFamily = bodyMediumFamily),
    labelSmall = base.labelSmall.copy(fontFamily = bodyMediumFamily),
    displayLargeEmphasized = base.displayLargeEmphasized.copy(fontFamily = headingBoldFamily),
    displayMediumEmphasized = base.displayMediumEmphasized.copy(fontFamily = headingBoldFamily),
    displaySmallEmphasized = base.displaySmallEmphasized.copy(fontFamily = headingBoldFamily),
    headlineLargeEmphasized = base.headlineLargeEmphasized.copy(fontFamily = headingMediumFamily),
    headlineMediumEmphasized = base.headlineMediumEmphasized.copy(fontFamily = headingMediumFamily),
    headlineSmallEmphasized = base.headlineSmallEmphasized.copy(fontFamily = headingMediumFamily),
    titleLargeEmphasized = base.titleLargeEmphasized.copy(fontFamily = headingBoldFamily),
    titleMediumEmphasized = base.titleMediumEmphasized.copy(fontFamily = headingBoldFamily),
    titleSmallEmphasized = base.titleSmallEmphasized.copy(fontFamily = headingBoldFamily),
    bodyLargeEmphasized = base.bodyLargeEmphasized.copy(fontFamily = bodyMediumFamily),
    bodyMediumEmphasized = base.bodyMediumEmphasized.copy(fontFamily = bodyMediumFamily),
    bodySmallEmphasized = base.bodySmallEmphasized.copy(fontFamily = bodyMediumFamily),
    labelLargeEmphasized = base.labelLargeEmphasized.copy(fontFamily = headingBoldFamily),
    labelMediumEmphasized = base.labelMediumEmphasized.copy(fontFamily = headingBoldFamily),
    labelSmallEmphasized = base.labelSmallEmphasized.copy(fontFamily = headingBoldFamily),
)
