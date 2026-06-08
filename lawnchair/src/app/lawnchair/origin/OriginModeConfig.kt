package app.lawnchair.origin

data class OriginModeConfig(
    // --- Grid & Density ---
    val workspaceDensity: Float = 1f,
    val appDrawerDensity: Float = 1f,
    val iconSizeFactor: Float = 1f,
    val drawerIconSizeFactor: Float = 1f,
    val drawerCellHeightFactor: Float = 1f,
    val drawerLeftRightMarginFactor: Float = 1f,

    // --- Dock ---
    val isFloatingDock: Boolean = false,
    val dockLabelEnabled: Boolean = false,
    val dockAccentColor: Boolean = false,

    // --- Search Bar ---
    val isFloatingSearchBar: Boolean = false,
    val themedSearchBar: Boolean = false,

    // --- Surfaces & Glass ---
    val cornerRadiusDp: Float = 12f,
    val glassOpacity: Float = 0f,
    val drawerBackgroundOpacity: Float = 0.5f,
    val enableWallpaperBlur: Boolean = false,
    val wallpaperBlurRadius: Int = 25,
    val recentsTranslucentBackground: Boolean = false,

    // --- Icons ---
    val useMonochromeIcons: Boolean = false,
    val iconShapeKey: String = "circle",
    val folderShapeKey: String = "circle",
    val showIconLabelsOnHomeScreen: Boolean = true,
    val showIconLabelsOnHomeScreenFolder: Boolean = true,
    val showIconLabelsInDrawer: Boolean = true,
    val showSuggestedAppsInDrawer: Boolean = true,

    // --- Color ---
    val colorStyleKey: String = "tonal_spot",
    val notificationDotColorOverride: Boolean = false,

    // --- Motion ---
    val motionScale: Float = 1f,
    val allAppsOpenDurationScale: Float = 1f,
    val allAppsCloseDurationScale: Float = 1f,

    // --- Chrome ---
    val showPageIndicator: Boolean = true,
    val pageIndicatorHeightFactor: Float = 1f,
    val showTopShadow: Boolean = true,
    val showScrollbar: Boolean = false,
    val twoLineAllApps: Boolean = false,
    val wallpaperScrolling: Boolean = true,
    val roundedWidgets: Boolean = true,
)
