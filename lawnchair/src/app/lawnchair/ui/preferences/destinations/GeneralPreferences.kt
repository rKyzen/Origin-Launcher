package app.lawnchair.ui.preferences.destinations

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.lawnchair.preferences.getAdapter
import app.lawnchair.preferences.preferenceManager
import app.lawnchair.preferences2.preferenceManager2
import app.lawnchair.ui.preferences.LocalIsExpandedScreen
import app.lawnchair.ui.preferences.components.NavigationActionPreference
import app.lawnchair.ui.preferences.components.NotificationDotsPreference
import app.lawnchair.ui.preferences.components.colorpreference.ColorPreference
import app.lawnchair.ui.preferences.components.controls.SliderPreference
import app.lawnchair.ui.preferences.components.controls.SwitchPreference
import app.lawnchair.ui.preferences.components.layout.PreferenceGroup
import app.lawnchair.ui.preferences.components.layout.PreferenceLayout
import app.lawnchair.ui.preferences.components.notificationServiceEnabled
import app.lawnchair.ui.preferences.navigation.FontCustomization
import app.lawnchair.ui.preferences.navigation.PersonalizationCustomIconShapeCreator
import app.lawnchair.ui.preferences.navigation.PersonalizationIconPack
import app.lawnchair.ui.preferences.navigation.PersonalizationIconShape
import com.android.launcher3.R
import com.android.launcher3.util.SettingsCache
import com.android.launcher3.util.SettingsCache.NOTIFICATION_BADGING_URI

@Composable
fun GeneralPreferences(
    modifier: Modifier = Modifier,
) {
    val prefs = preferenceManager()
    val prefs2 = preferenceManager2()
    val context = LocalContext.current

    PreferenceLayout(
        label = stringResource(id = R.string.general_label),
        backArrowVisible = !LocalIsExpandedScreen.current,
        modifier = modifier,
    ) {
        PreferenceGroup(heading = stringResource(R.string.theme_label)) {
            Item {
                ColorPreference(preference = prefs2.accentColor)
            }
            Item {
                NavigationActionPreference(
                    label = stringResource(R.string.icon_pack),
                    destination = PersonalizationIconPack,
                )
            }
            Item {
                NavigationActionPreference(
                    label = stringResource(R.string.icon_shape_label),
                    destination = PersonalizationIconShape(),
                )
            }
            Item {
                NavigationActionPreference(
                    label = stringResource(R.string.custom_icon_shape),
                    destination = PersonalizationCustomIconShapeCreator(),
                )
            }
            Item {
                val iconSizeAdapter = prefs2.homeIconSizeFactor.getAdapter()
                SliderPreference(
                    label = stringResource(R.string.icon_size),
                    adapter = iconSizeAdapter,
                    valueRange = 0.5f..1.5f,
                    step = 0.05f,
                    showAsPercentage = true,
                )
            }
            Item {
                val monoAdapter = prefs.forceIconMonochrome.getAdapter()
                SwitchPreference(
                    adapter = monoAdapter,
                    label = stringResource(R.string.monochrome_icons),
                )
            }
            Item {
                NavigationActionPreference(
                    label = stringResource(R.string.font_customization_label),
                    destination = FontCustomization,
                )
            }
        }
        PreferenceGroup(
            heading = stringResource(id = R.string.notification_dots),
            modifier = Modifier.padding(top = 8.dp),
        ) {
            val enabled = SettingsCache.INSTANCE.get(context).getValue(NOTIFICATION_BADGING_URI)
            val serviceEnabled = notificationServiceEnabled()
            Item {
                NotificationDotsPreference(
                    enabled = enabled,
                    serviceEnabled = serviceEnabled,
                )
            }
        }
    }
}
