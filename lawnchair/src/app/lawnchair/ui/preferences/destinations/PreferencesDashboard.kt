package app.lawnchair.ui.preferences.destinations

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.LauncherApps
import android.os.Process
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.TipsAndUpdates
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.getSystemService
import app.lawnchair.LawnchairLauncher
import app.lawnchair.ui.preferences.components.controls.PreferenceCategory
import app.lawnchair.ui.preferences.components.controls.WarningPreference
import app.lawnchair.ui.preferences.components.layout.PreferenceGroup
import app.lawnchair.ui.preferences.components.layout.PreferenceTemplate
import app.lawnchair.ui.preferences.components.layout.TwoTabPreferenceLayout
import app.lawnchair.origin.OriginModeManager
import app.lawnchair.ui.preferences.navigation.About
import app.lawnchair.ui.preferences.navigation.AppDrawer
import app.lawnchair.ui.preferences.navigation.BackupAndRestore
import app.lawnchair.ui.preferences.navigation.Dock
import app.lawnchair.ui.preferences.navigation.ExperimentalFeatures
import app.lawnchair.ui.preferences.navigation.Folders
import app.lawnchair.ui.preferences.navigation.General
import app.lawnchair.ui.preferences.navigation.Gestures
import app.lawnchair.ui.preferences.navigation.GoogleFeed
import app.lawnchair.ui.preferences.navigation.HomeScreen
import app.lawnchair.ui.preferences.navigation.OriginModes
import app.lawnchair.ui.preferences.navigation.Personalization
import app.lawnchair.ui.preferences.navigation.PreferenceRootRoute
import app.lawnchair.ui.preferences.navigation.Quickstep
import app.lawnchair.ui.preferences.navigation.Search
import app.lawnchair.util.isDefaultLauncher
import com.android.launcher3.BuildConfig
import com.android.launcher3.R

@Composable
fun PreferencesDashboard(
    currentRoute: PreferenceRootRoute,
    onNavigate: (PreferenceRootRoute) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val originDesc = try {
        val originModeManager = OriginModeManager.getInstance(context)
        context.getString(originModeManager.currentMode().displayName)
    } catch (_: Exception) {
        context.getString(R.string.origin_mode_pure)
    }

    val aboutDescrption = "${context.getString(R.string.derived_app_name)} ${BuildConfig.MAJOR_VERSION}"

    TwoTabPreferenceLayout(
        label = stringResource(id = R.string.settings),
        modifier = modifier,
        backArrowVisible = false,
        firstPageLabel = stringResource(R.string.simple),
        firstPageContent = {
            if (BuildConfig.APPLICATION_ID.contains("nightly") || BuildConfig.DEBUG) {
                PreferencesDebugWarning()
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (!context.isDefaultLauncher()) {
                PreferencesSetDefaultLauncherWarning()
                Spacer(modifier = Modifier.height(8.dp))
            }

            PreferenceGroup {
                Item {
                    PreferenceCategory(
                        label = stringResource(R.string.general_label),
                        iconResource = R.drawable.ic_general,
                        onNavigate = { onNavigate(General) },
                        isSelected = currentRoute is General,
                        isFirst = it.isFirst,
                        isLast = false,
                    )
                }

                Item {
                    PreferenceCategory(
                        label = stringResource(R.string.smartspace_widget),
                        iconResource = R.drawable.ic_smartspace,
                        onNavigate = { onNavigate(GoogleFeed) },
                        isSelected = currentRoute is GoogleFeed,
                        isFirst = it.isFirst,
                        isLast = false,
                    )
                }

                Item {
                    PreferenceCategory(
                        label = stringResource(R.string.app_drawer_label),
                        iconResource = R.drawable.ic_apps,
                        onNavigate = { onNavigate(AppDrawer) },
                        isSelected = currentRoute is AppDrawer,
                        isFirst = it.isFirst,
                        isLast = false,
                    )
                }

                Item {
                    PreferenceCategory(
                        label = stringResource(R.string.origin_label),
                        description = originDesc,
                        iconResource = R.drawable.ic_lightbulb,
                        onNavigate = { onNavigate(OriginModes) },
                        isSelected = currentRoute is OriginModes,
                        isFirst = it.isFirst,
                        isLast = false,
                    )
                }

                Item {
                    PreferenceCategory(
                        label = stringResource(R.string.about_label),
                        description = aboutDescrption,
                        iconResource = R.drawable.ic_about,
                        onNavigate = { onNavigate(About) },
                        isSelected = currentRoute is About,
                        isFirst = it.isFirst,
                        isLast = it.isLast,
                    )
                }
            }
        },
        secondPageLabel = stringResource(R.string.advanced),
        secondPageContent = {
            if (BuildConfig.APPLICATION_ID.contains("nightly") || BuildConfig.DEBUG) {
                PreferencesDebugWarning()
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (!context.isDefaultLauncher()) {
                PreferencesSetDefaultLauncherWarning()
                Spacer(modifier = Modifier.height(8.dp))
            }

            PreferenceGroup {
                Item {
                    PreferenceCategory(
                        label = stringResource(R.string.general_label),
                        iconResource = R.drawable.ic_general,
                        onNavigate = { onNavigate(General) },
                        isSelected = currentRoute is General,
                        isFirst = it.isFirst,
                        isLast = false,
                    )
                }

                Item {
                    PreferenceCategory(
                        label = stringResource(R.string.smartspace_widget),
                        iconResource = R.drawable.ic_smartspace,
                        onNavigate = { onNavigate(GoogleFeed) },
                        isSelected = currentRoute is GoogleFeed,
                        isFirst = it.isFirst,
                        isLast = false,
                    )
                }

                Item {
                    PreferenceCategory(
                        label = stringResource(R.string.app_drawer_label),
                        iconResource = R.drawable.ic_apps,
                        onNavigate = { onNavigate(AppDrawer) },
                        isSelected = currentRoute is AppDrawer,
                        isFirst = it.isFirst,
                        isLast = false,
                    )
                }

                Item {
                    PreferenceCategory(
                        label = stringResource(R.string.home_screen_label),
                        iconResource = R.drawable.ic_home_screen,
                        onNavigate = { onNavigate(HomeScreen) },
                        isSelected = currentRoute is HomeScreen,
                        isFirst = it.isFirst,
                        isLast = false,
                    )
                }

                Item {
                    PreferenceCategory(
                        label = stringResource(R.string.dock_label),
                        description = stringResource(R.string.dock_description),
                        iconResource = R.drawable.ic_dock,
                        onNavigate = { onNavigate(Dock) },
                        isSelected = currentRoute is Dock,
                        isFirst = it.isFirst,
                        isLast = false,
                    )
                }

                Item {
                    PreferenceCategory(
                        label = stringResource(R.string.search_bar_label),
                        iconResource = R.drawable.ic_search,
                        onNavigate = { onNavigate(Search()) },
                        isSelected = currentRoute is Search,
                        isFirst = it.isFirst,
                        isLast = false,
                    )
                }

                Item {
                    PreferenceCategory(
                        label = stringResource(R.string.folders_label),
                        iconResource = R.drawable.ic_folder,
                        onNavigate = { onNavigate(Folders) },
                        isSelected = currentRoute is Folders,
                        isFirst = it.isFirst,
                        isLast = false,
                    )
                }

                Item {
                    PreferenceCategory(
                        label = stringResource(R.string.gestures_label),
                        description = stringResource(R.string.gestures_description),
                        iconResource = R.drawable.ic_gestures,
                        onNavigate = { onNavigate(Gestures) },
                        isSelected = currentRoute is Gestures,
                        isFirst = it.isFirst,
                        isLast = false,
                    )
                }

                Item {
                    PreferenceCategory(
                        label = stringResource(R.string.quickstep_label),
                        description = stringResource(R.string.quickstep_description),
                        iconResource = R.drawable.ic_quickstep,
                        onNavigate = { onNavigate(Quickstep) },
                        isSelected = currentRoute is Quickstep,
                        isFirst = it.isFirst,
                        isLast = false,
                    )
                }

                Item {
                    PreferenceCategory(
                        label = stringResource(R.string.backup_restore_label),
                        description = stringResource(R.string.system_description),
                        iconResource = R.drawable.ic_download,
                        onNavigate = { onNavigate(BackupAndRestore) },
                        isSelected = currentRoute is BackupAndRestore,
                        isFirst = it.isFirst,
                        isLast = false,
                    )
                }

                Item {
                    PreferenceCategory(
                        label = stringResource(R.string.personalization_label),
                        iconResource = R.drawable.ic_wallpaper,
                        onNavigate = { onNavigate(Personalization) },
                        isSelected = currentRoute is Personalization,
                        isFirst = it.isFirst,
                        isLast = false,
                    )
                }

                Item {
                    PreferenceCategory(
                        label = stringResource(R.string.experimental_features_label),
                        iconResource = R.drawable.ic_new_releases,
                        onNavigate = { onNavigate(ExperimentalFeatures) },
                        isSelected = currentRoute is ExperimentalFeatures,
                        isFirst = it.isFirst,
                        isLast = it.isLast,
                    )
                }
            }
        },
    )
}

@Composable
fun PreferencesDebugWarning(
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.padding(horizontal = 16.dp),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.errorContainer,
    ) {
        WarningPreference(
            text = "You are using a development build, which may contain bugs and broken features. Use at your own risk!",
        )
    }
}

@Composable
fun PreferencesSetDefaultLauncherWarning(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    Surface(
        modifier = modifier.padding(horizontal = 16.dp),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        PreferenceTemplate(
            modifier = Modifier.clickable {
                Intent(Settings.ACTION_HOME_SETTINGS)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .let { context.startActivity(it) }
                (context as? Activity)?.finish()
            },
            title = {},
            description = {
                Text(
                    text = stringResource(id = R.string.set_default_launcher_tip),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            startWidget = {
                Icon(
                    imageVector = Icons.Rounded.TipsAndUpdates,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    contentDescription = null,
                )
            },
        )
    }
}

fun openAppInfo(context: Context) {
    val launcherApps = context.getSystemService<LauncherApps>()
    val componentName = ComponentName(context, LawnchairLauncher::class.java)
    launcherApps?.startAppDetailsActivity(componentName, Process.myUserHandle(), null, null)
}
