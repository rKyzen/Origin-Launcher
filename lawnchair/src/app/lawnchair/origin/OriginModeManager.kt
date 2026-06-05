package app.lawnchair.origin

import android.content.Context
import app.lawnchair.icons.shape.IconShape
import app.lawnchair.preferences.PreferenceManager
import app.lawnchair.preferences2.PreferenceManager2
import app.lawnchair.theme.color.ColorOption
import app.lawnchair.theme.color.ColorStyle
import com.android.launcher3.dagger.ApplicationContext
import com.android.launcher3.dagger.LauncherAppComponent
import com.android.launcher3.dagger.LauncherAppSingleton
import com.android.launcher3.util.DaggerSingletonObject
import com.patrykmichalik.opto.core.firstBlocking
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@LauncherAppSingleton
class OriginModeManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val prefs2 = PreferenceManager2.getInstance(context)
    private val prefs1 = PreferenceManager.getInstance(context)

    fun currentMode(): OriginMode = prefs2.originMode.firstBlocking()

    fun currentConfig(): OriginModeConfig = currentMode().config()

    fun applyConfig(config: OriginModeConfig) = runBlocking {
        with(config) {
            // --- Icon appearance ---
            prefs2.showIconLabelsOnHomeScreen.set(showIconLabelsOnHomeScreen)
            prefs2.showIconLabelsOnHomeScreenFolder.set(showIconLabelsOnHomeScreenFolder)
            prefs2.enableLabelInDock.set(dockLabelEnabled)
            prefs2.homeIconSizeFactor.set(iconSizeFactor)
            IconShape.fromString(iconShapeKey, context)?.let { prefs2.iconShape.set(it) }
            IconShape.fromString(folderShapeKey, context)?.let { prefs2.folderShape.set(it) }

            // --- Drawer ---
            prefs2.showIconLabelsInDrawer.set(showIconLabelsInDrawer)
            prefs2.drawerIconSizeFactor.set(drawerIconSizeFactor)
            prefs2.drawerCellHeightFactor.set(drawerCellHeightFactor)
            prefs2.drawerLeftRightMarginFactor.set(drawerLeftRightMarginFactor)
            prefs2.showSuggestedAppsInDrawer.set(showSuggestedAppsInDrawer)
            prefs2.showScrollbar.set(showScrollbar)
            prefs2.twoLineAllApps.set(twoLineAllApps)

            // --- Color & theme ---
            ColorStyle.fromString(colorStyleKey).let { prefs2.colorStyle.set(it) }
            prefs1.forceIconMonochrome.set(useMonochromeIcons)
            prefs1.drawerThemedIcons.set(useMonochromeIcons)

            // --- Wallpaper blur ---
            prefs1.enableWallpaperBlur.set(enableWallpaperBlur)
            prefs1.wallpaperBlur.set(wallpaperBlurRadius)

            // --- Recents ---
            prefs1.recentsTranslucentBackground.set(recentsTranslucentBackground)

            // --- Chrome ---
            prefs2.showTopShadow.set(showTopShadow)
            prefs2.pageIndicatorHeightFactor.set(pageIndicatorHeightFactor)
            prefs1.wallpaperScrolling.set(wallpaperScrolling)

            // --- Drawer opacity ---
            prefs1.drawerOpacity.set(drawerBackgroundOpacity)

            // --- Dock ---
            prefs2.dockAccentColor.set(dockAccentColor)

            // --- Search bar ---
            prefs2.themedHotseatQsb.set(themedSearchBar)

            // --- Widgets ---
            prefs2.roundedWidgets.set(roundedWidgets)
        }
    }

    fun applyCurrentMode() = runBlocking {
        applyConfig(currentConfig())
    }

    companion object {
        @JvmField
        val INSTANCE = DaggerSingletonObject(LauncherAppComponent::getOriginModeManager)

        @JvmStatic
        fun getInstance(context: Context) = INSTANCE.get(context)!!
    }
}
