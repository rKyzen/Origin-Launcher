package app.lawnchair.origin

import androidx.annotation.StringRes
import com.android.launcher3.R

enum class OriginMode(
    val storageName: String,
    @StringRes val displayName: Int,
    @StringRes val description: Int,
) {
    Pure(
        storageName = "pure",
        displayName = R.string.origin_mode_pure,
        description = R.string.origin_mode_pure_description,
    ),
    Flow(
        storageName = "flow",
        displayName = R.string.origin_mode_flow,
        description = R.string.origin_mode_flow_description,
    );

    fun config(): OriginModeConfig = when (this) {
        Pure -> OriginModeConfig()
        Flow -> OriginModeConfig(
            // Grid: tighter, denser
            workspaceDensity = 0.90f,
            appDrawerDensity = 0.90f,
            iconSizeFactor = 0.85f,
            drawerIconSizeFactor = 0.88f,
            drawerCellHeightFactor = 0.85f,
            drawerLeftRightMarginFactor = 0.70f,

            // Dock: floating glass, accent color tinted
            isFloatingDock = true,
            dockLabelEnabled = false,
            dockAccentColor = true,

            // Search bar: floating glass
            isFloatingSearchBar = true,
            themedSearchBar = true,

            // Surfaces: glassy, rounded, blurred
            cornerRadiusDp = 28f,
            glassOpacity = 0.12f,
            drawerBackgroundOpacity = 0.30f,
            enableWallpaperBlur = true,
            wallpaperBlurRadius = 20,
            recentsTranslucentBackground = true,

            // Icons: minimal, monochrome, no labels on home
            useMonochromeIcons = true,
            iconShapeKey = "roundedSquare",
            folderShapeKey = "roundedSquare",
            showIconLabelsOnHomeScreen = false,
            showIconLabelsOnHomeScreenFolder = false,
            showIconLabelsInDrawer = true,
            showSuggestedAppsInDrawer = false,

            // Color: monochrome palette
            colorStyleKey = "monochromatic",
            notificationDotColorOverride = true,

            // Motion: fluid, slower
            motionScale = 1.2f,
            allAppsOpenDurationScale = 1.3f,
            allAppsCloseDurationScale = 1.3f,

            // Chrome: minimal, functional
            showPageIndicator = false,
            pageIndicatorHeightFactor = 0f,
            showTopShadow = false,
            showScrollbar = true,
            twoLineAllApps = true,
            wallpaperScrolling = false,
            roundedWidgets = false,
        )
    }

    companion object {
        fun fromString(value: String): OriginMode = entries.firstOrNull { it.storageName == value } ?: Pure
    }
}
