package app.lawnchair.ui.preferences.navigation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import app.lawnchair.ui.preferences.LocalIsExpandedScreen
import app.lawnchair.ui.preferences.about.About
import app.lawnchair.ui.preferences.about.acknowledgements.Acknowledgements
import app.lawnchair.ui.preferences.components.colorpreference.ColorPreferenceModelList
import app.lawnchair.ui.preferences.components.colorpreference.ColorSelection
import app.lawnchair.backup.ui.CreateBackupScreen
import app.lawnchair.ui.preferences.destinations.AppDrawerPreferences
import app.lawnchair.ui.preferences.destinations.BackupAndRestorePreference
import app.lawnchair.ui.preferences.destinations.CustomIconShapePreference
import app.lawnchair.ui.preferences.destinations.DockPreferences
import app.lawnchair.ui.preferences.destinations.DummyPreference
import app.lawnchair.preferences.preferenceManager
import app.lawnchair.ui.preferences.destinations.ExperimentalFeaturesPreferences
import app.lawnchair.ui.preferences.destinations.FontCustomizationPreferences
import app.lawnchair.ui.preferences.destinations.FontSelection
import app.lawnchair.ui.preferences.destinations.FolderPreferences
import app.lawnchair.ui.preferences.destinations.GeneralPreferences
import app.lawnchair.ui.preferences.destinations.GesturePreferences
import app.lawnchair.ui.preferences.destinations.GoogleFeedPreferences
import app.lawnchair.ui.preferences.destinations.HiddenAppsPreferences
import app.lawnchair.ui.preferences.destinations.HomeScreenGridPreferences
import app.lawnchair.ui.preferences.destinations.HomeScreenPreferences
import app.lawnchair.ui.preferences.destinations.IconPackPreferences
import app.lawnchair.ui.preferences.destinations.OriginModePreferences
import app.lawnchair.ui.preferences.destinations.PersonalizationPreferences
import app.lawnchair.ui.preferences.destinations.PreferencesDashboard
import app.lawnchair.ui.preferences.destinations.QuickstepPreferences
import app.lawnchair.ui.preferences.destinations.SearchPreferences
import app.lawnchair.ui.preferences.destinations.SearchProviderPreferences
import app.lawnchair.ui.preferences.destinations.ShapePreference
import androidx.lifecycle.viewmodel.compose.viewModel
import soup.compose.material.motion.animation.materialSharedAxisXIn
import soup.compose.material.motion.animation.materialSharedAxisXOut
import soup.compose.material.motion.animation.rememberSlideDistance

inline fun <reified T> getDeepLink(route: T) where T : PreferenceRoute, T : PreferenceDeepLink = listOf(navDeepLink<T>(basePath = route.deepLink))

@Composable
fun PreferenceNavigation(
    navController: NavHostController,
    startDestination: PreferenceRoute,
    intent: Intent? = null,
) {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val slideDistance = rememberSlideDistance()

    LaunchedEffect(intent) {
        intent?.let { navController.handleDeepLink(it) }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { materialSharedAxisXIn(!isRtl, slideDistance) },
        exitTransition = { materialSharedAxisXOut(!isRtl, slideDistance) },
        popEnterTransition = { materialSharedAxisXIn(isRtl, slideDistance) },
        popExitTransition = { materialSharedAxisXOut(isRtl, slideDistance) },
    ) {
        composable<Root> {
            val isExpandedScreen = LocalIsExpandedScreen.current

            PreferencesDashboard(
                currentRoute = Root,
                onNavigate = {
                    navController.navigate(it)
                },
            )

            LaunchedEffect(isExpandedScreen) {
                if (isExpandedScreen) {
                    navController.navigate(About) {
                        launchSingleTop = true
                        popUpTo(navController.graph.id)
                    }
                }
            }
        }
        composable<Dummy> {
            DummyPreference()
        }

        composable<OriginModes>(
            deepLinks = getDeepLink(OriginModes),
        ) { OriginModePreferences() }

        composable<AppDrawer>(
            deepLinks = getDeepLink(AppDrawer),
        ) { AppDrawerPreferences() }
        composable<AppDrawerHiddenApps>(
            deepLinks = getDeepLink(AppDrawerHiddenApps),
        ) { HiddenAppsPreferences() }

        composable<About>(
            deepLinks = getDeepLink(About),
        ) { About() }

        composable<HomeScreen>(
            deepLinks = getDeepLink(HomeScreen),
        ) { HomeScreenPreferences() }
        composable<HomeScreenGrid>(
            deepLinks = getDeepLink(HomeScreenGrid),
        ) { HomeScreenGridPreferences() }

        composable<Dock>(
            deepLinks = getDeepLink(Dock),
        ) { DockPreferences() }
        composable<DockSearchProvider>(
            deepLinks = getDeepLink(DockSearchProvider),
        ) { SearchProviderPreferences() }

        composable<Folders>(
            deepLinks = getDeepLink(Folders),
        ) { FolderPreferences() }

        composable<Gestures>(
            deepLinks = getDeepLink(Gestures),
        ) { GesturePreferences() }

        composable<General>(
            deepLinks = getDeepLink(General),
        ) { GeneralPreferences() }

        composable<Quickstep>(
            deepLinks = getDeepLink(Quickstep),
        ) { QuickstepPreferences() }

        composable<BackupAndRestore>(
            deepLinks = getDeepLink(BackupAndRestore),
        ) { BackupAndRestorePreference() }
        composable<CreateBackup>(
            deepLinks = getDeepLink(CreateBackup),
        ) { CreateBackupScreen(viewModel = viewModel()) }

        composable<ExperimentalFeatures>(
            deepLinks = getDeepLink(ExperimentalFeatures),
        ) { ExperimentalFeaturesPreferences() }

        composable<FontCustomization>(
            deepLinks = getDeepLink(FontCustomization),
        ) { FontCustomizationPreferences() }

        composable<Search>(
            deepLinks = getDeepLink(Search()),
        ) { backStackEntry ->
            val route: Search = backStackEntry.toRoute()
            SearchPreferences(currentTab = route.selectedId)
        }
        composable<SearchProviderPreference> { SearchProviderPreferences() }

        composable<GoogleFeed>(
            deepLinks = getDeepLink(GoogleFeed),
        ) { GoogleFeedPreferences(fromWidget = false) }
        composable<GoogleFeedWidget> { GoogleFeedPreferences(fromWidget = true) }

        composable<Personalization>(
            deepLinks = getDeepLink(Personalization),
        ) { PersonalizationPreferences() }

        composable<PersonalizationFontSelection> { backStackEntry ->
            val route: PersonalizationFontSelection = backStackEntry.toRoute()
            val prefs = preferenceManager()
            val fontPref = when (route.prefKey) {
                prefs.fontWorkspace.key -> prefs.fontWorkspace
                prefs.fontHeading.key -> prefs.fontHeading
                prefs.fontHeadingMedium.key -> prefs.fontHeadingMedium
                prefs.fontBody.key -> prefs.fontBody
                prefs.fontBodyMedium.key -> prefs.fontBodyMedium
                else -> prefs.fontWorkspace
            }
            FontSelection(fontPref = fontPref)
        }

        composable<PersonalizationIconPack>(
            deepLinks = getDeepLink(PersonalizationIconPack),
        ) { IconPackPreferences() }
        composable<PersonalizationIconShape> { backStackEntry ->
            val route: PersonalizationIconShape = backStackEntry.toRoute()
            ShapePreference(currentTab = route.selectedId)
        }
        composable<PersonalizationCustomIconShapeCreator>(
            deepLinks = getDeepLink(PersonalizationCustomIconShapeCreator()),
        ) { backStackEntry ->
            val route: PersonalizationCustomIconShapeCreator = backStackEntry.toRoute()
            CustomIconShapePreference(currentTab = route.selectedId)
        }
        composable<ColorSelection> { backStackEntry ->
            val screen: ColorSelection = backStackEntry.toRoute()
            val modelList = ColorPreferenceModelList.INSTANCE.get(LocalContext.current)
            val model = modelList[screen.prefKey]
            ColorSelection(
                label = stringResource(id = model.labelRes),
                preference = model.prefObject,
                dynamicEntries = model.dynamicEntries,
            )
        }

        composable<AboutLicenses>(
            deepLinks = getDeepLink(AboutLicenses),
        ) { Acknowledgements() }
    }
}
