package app.lawnchair.icons

import android.content.Context
import android.graphics.Color
import app.lawnchair.preferences.PreferenceManager
import com.android.launcher3.Item
import com.android.launcher3.LauncherPrefs
import com.android.launcher3.graphics.ThemeManager
import com.android.launcher3.icons.IconThemeController
import com.android.launcher3.icons.mono.MonoIconThemeController
import com.android.launcher3.icons.mono.ThemedIconDrawable
import javax.inject.Inject

class LawnchairIconControllerFactory @Inject constructor(
    prefs: LauncherPrefs,
    private val prefs1: PreferenceManager,
) : ThemeManager.IconControllerFactory(prefs) {

    override val prefKeys: List<Item> = listOf(ThemeManager.THEMED_ICONS)

    override fun createThemeController(): IconThemeController? {
        return if (prefs.get(ThemeManager.THEMED_ICONS) || prefs1.forceIconMonochrome.get()) {
            val colorProvider: (Context) -> IntArray = { context ->
                if (prefs1.forceIconMonochrome.get()) {
                    intArrayOf(Color.TRANSPARENT, Color.WHITE)
                } else {
                    ThemedIconDrawable.getColors(context)
                }
            }
            MonoIconThemeController(
                shouldForceThemeIcon = true,
                colorProvider = colorProvider,
            )
        } else {
            null
        }
    }
}
