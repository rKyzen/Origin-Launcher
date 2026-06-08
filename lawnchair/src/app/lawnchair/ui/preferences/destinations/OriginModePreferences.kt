package app.lawnchair.ui.preferences.destinations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.lawnchair.origin.OriginMode
import app.lawnchair.origin.OriginModeManager
import app.lawnchair.preferences.getAdapter
import app.lawnchair.preferences.preferenceManager
import app.lawnchair.preferences2.PreferenceManager2
import app.lawnchair.preferences2.asState
import app.lawnchair.preferences2.preferenceManager2
import app.lawnchair.ui.preferences.LocalNavController
import app.lawnchair.ui.preferences.components.NavigationActionPreference
import app.lawnchair.ui.preferences.components.OriginModeSelector
import app.lawnchair.ui.preferences.components.colorpreference.ColorPreference
import app.lawnchair.ui.preferences.components.controls.SliderPreference
import app.lawnchair.ui.preferences.components.controls.SwitchPreference
import app.lawnchair.ui.preferences.components.layout.ClickableIcon
import app.lawnchair.ui.preferences.components.layout.PreferenceGroup
import app.lawnchair.ui.preferences.components.layout.PreferenceLayout
import app.lawnchair.ui.preferences.navigation.PersonalizationIconPack
import app.lawnchair.ui.preferences.navigation.PersonalizationIconShape
import app.lawnchair.util.restartLauncher
import com.android.launcher3.R
import kotlinx.coroutines.launch

@Composable
fun OriginModePreferences() {
    val context = LocalContext.current
    val prefs2 = PreferenceManager2.getInstance(context)
    val prefs1 = preferenceManager()
    val modeManager = OriginModeManager.getInstance(context)
    val currentMode by prefs2.originMode.asState()
    var pendingMode by remember { mutableStateOf<OriginMode?>(null) }
    val scope = rememberCoroutineScope()
    val navController = LocalNavController.current

    pendingMode?.let { mode ->
        AlertDialog(
            onDismissRequest = { pendingMode = null },
            title = {
                Text(
                    text = stringResource(R.string.origin_mode_switch_title),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(id = mode.displayName),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                    Text(
                        text = stringResource(id = mode.description),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = stringResource(R.string.origin_mode_switch_restart),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 16.dp),
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    pendingMode = null
                    restartLauncher(context)
                }) {
                    Text(stringResource(R.string.debug_restart_launcher))
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingMode = null }) {
                    Text(stringResource(android.R.string.cancel))
                }
            },
        )
    }

    PreferenceLayout(
        label = stringResource(R.string.origin_label),
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        OriginModeSelector(
            selectedMode = currentMode,
            onModeSelected = { mode ->
                scope.launch {
                    prefs2.originMode.set(mode)
                    modeManager.applyConfig(mode.config())
                    pendingMode = mode
                }
            },
        )

        PreferenceGroup(
            heading = stringResource(R.string.customize),
            modifier = Modifier.padding(top = 16.dp),
        ) {
            Item {
                ColorPreference(preference = prefs2.accentColor)
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
                NavigationActionPreference(
                    label = stringResource(R.string.icon_shape_label),
                    destination = PersonalizationIconShape(),
                )
            }
            Item {
                NavigationActionPreference(
                    label = stringResource(R.string.icon_pack),
                    destination = PersonalizationIconPack,
                )
            }
            Item {
                val monoAdapter = prefs1.forceIconMonochrome.getAdapter()
                SwitchPreference(
                    adapter = monoAdapter,
                    label = stringResource(R.string.monochrome_icons),
                )
            }
        }

        if (currentMode == OriginMode.Flow) {
            PreferenceGroup(
                heading = stringResource(R.string.dock),
                modifier = Modifier.padding(top = 8.dp),
            ) {
                Item {
                    val dockAlphaAdapter = prefs2.dockGlassTransparency.getAdapter()
                    SliderPreference(
                        label = stringResource(R.string.transparency),
                        adapter = dockAlphaAdapter,
                        valueRange = 0f..1f,
                        step = 0.05f,
                        showAsPercentage = true,
                    )
                }
                Item {
                    val blurAdapter = prefs2.dockBlurRadius.getAdapter()
                    SliderPreference(
                        label = stringResource(R.string.blur),
                        adapter = blurAdapter,
                        valueRange = 0..50,
                        step = 1,
                        showUnit = "px",
                    )
                }
                Item {
                    val accentDockAdapter = prefs2.dockAccentColor.getAdapter()
                    SwitchPreference(
                        adapter = accentDockAdapter,
                        label = stringResource(R.string.dock_accent_color),
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.dock_restart_note),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    ClickableIcon(
                        imageVector = Icons.Rounded.Refresh,
                        onClick = { restartLauncher(context) },
                    )
                }
            }
        }
    }
}
