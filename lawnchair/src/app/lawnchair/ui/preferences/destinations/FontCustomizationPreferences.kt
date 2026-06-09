package app.lawnchair.ui.preferences.destinations

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.lawnchair.preferences.getAdapter
import app.lawnchair.preferences.preferenceManager
import app.lawnchair.preferences2.preferenceManager2
import app.lawnchair.ui.preferences.LocalIsExpandedScreen
import app.lawnchair.ui.preferences.components.FontPreference
import app.lawnchair.ui.preferences.components.controls.SwitchPreference
import app.lawnchair.ui.preferences.components.layout.PreferenceGroup
import app.lawnchair.ui.preferences.components.layout.PreferenceLayout
import com.android.launcher3.R

@Composable
fun FontCustomizationPreferences(
    modifier: Modifier = Modifier,
) {
    val prefs = preferenceManager()
    val prefs2 = preferenceManager2()

    PreferenceLayout(
        label = stringResource(id = R.string.font_customization_label),
        backArrowVisible = !LocalIsExpandedScreen.current,
        modifier = modifier,
    ) {
        PreferenceGroup {
            Item {
                SwitchPreference(
                    adapter = prefs2.enableFontSelection.getAdapter(),
                    label = stringResource(id = R.string.font_picker_label),
                    description = stringResource(id = R.string.font_picker_description),
                )
            }
        }
        PreferenceGroup(
            heading = stringResource(id = R.string.theme_label),
            modifier = Modifier.padding(top = 8.dp),
        ) {
            Item {
                FontPreference(
                    fontPref = prefs.fontWorkspace,
                    label = "Base Icon",
                )
            }
            Item {
                FontPreference(
                    fontPref = prefs.fontHeading,
                    label = "Heading",
                )
            }
            Item {
                FontPreference(
                    fontPref = prefs.fontHeadingMedium,
                    label = "Heading Medium",
                )
            }
            Item {
                FontPreference(
                    fontPref = prefs.fontBody,
                    label = "Body",
                )
            }
            Item {
                FontPreference(
                    fontPref = prefs.fontBodyMedium,
                    label = "Body Medium",
                )
            }
        }
    }
}
