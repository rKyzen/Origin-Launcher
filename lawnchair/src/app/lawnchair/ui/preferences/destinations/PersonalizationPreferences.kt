package app.lawnchair.ui.preferences.destinations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import app.lawnchair.preferences.getAdapter
import app.lawnchair.preferences.preferenceManager
import app.lawnchair.preferences2.preferenceManager2
import app.lawnchair.ui.preferences.LocalIsExpandedScreen
import app.lawnchair.ui.preferences.components.NavigationActionPreference
import app.lawnchair.ui.preferences.components.controls.SliderPreference
import app.lawnchair.ui.preferences.components.controls.SwitchPreference
import app.lawnchair.ui.preferences.components.layout.PreferenceGroup
import app.lawnchair.ui.preferences.components.layout.PreferenceLayout
import app.lawnchair.ui.preferences.navigation.PersonalizationCustomIconShapeCreator
import app.lawnchair.ui.preferences.navigation.PersonalizationIconPack
import app.lawnchair.ui.preferences.navigation.PersonalizationIconShape
import com.android.launcher3.R

@Composable
fun PersonalizationPreferences(
    modifier: Modifier = Modifier,
) {
    val prefs = preferenceManager()
    val prefs2 = preferenceManager2()
    val iconSizeAdapter = prefs2.homeIconSizeFactor.getAdapter()
    val monoAdapter = prefs.forceIconMonochrome.getAdapter()

    PreferenceLayout(
        label = stringResource(id = R.string.personalization_label),
        backArrowVisible = !LocalIsExpandedScreen.current,
        modifier = modifier,
        verticalArrangement = Arrangement.Top,
    ) {
        PreferenceGroup(stringResource(R.string.theme_label)) {
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
                SliderPreference(
                    label = stringResource(R.string.icon_size),
                    adapter = iconSizeAdapter,
                    valueRange = 0.5f..1.5f,
                    step = 0.05f,
                    showAsPercentage = true,
                )
            }
            Item {
                SwitchPreference(
                    adapter = monoAdapter,
                    label = stringResource(R.string.monochrome_icons),
                )
            }
        }
    }
}
