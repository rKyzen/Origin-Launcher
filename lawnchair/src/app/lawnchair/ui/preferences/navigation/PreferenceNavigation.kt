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
import app.lawnchair.ui.preferences.destinations.AppDrawerPreferences
import app.lawnchair.ui.preferences.destinations.BackupAndRestorePreference
import app.lawnchair.ui.preferences.destinations.DockPreferences
import app.lawnchair.ui.preferences.destinations.DummyPreference
import app.lawnchair.ui.preferences.destinations.ExperimentalFeaturesPreferences
import app.lawnchair.ui.preferences.destinations.FolderPreferences
import app.lawnchair.ui.preferences.destinations.GesturePreferences
import app.lawnchair.ui.preferences.destinations.GoogleFeedPreferences
import app.lawnchair.ui.preferences.destinations.HomeScreenPreferences
import app.lawnchair.ui.preferences.destinations.IconPackPreferences
import app.lawnchair.ui.preferences.destinations.OriginModePreferences
import app.lawnchair.ui.preferences.destinations.PersonalizationPreferences
import app.lawnchair.ui.preferences.destinations.PreferencesDashboard
import app.lawnchair.ui.preferences.destinations.QuickstepPreferences
import app.lawnchair.ui.preferences.destinations.SearchPreferences
import app.lawnchair.ui.preferences.destinations.ShapePreference
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

        composable<About>(
            deepLinks = getDeepLink(About),
        ) { About() }

        composable<HomeScreen>(
            deepLinks = getDeepLink(HomeScreen),
        ) { HomeScreenPreferences() }

        composable<Dock>(
            deepLinks = getDeepLink(Dock),
        ) { DockPreferences() }

        composable<Folders>(
            deepLinks = getDeepLink(Folders),
        ) { FolderPreferences() }

        composable<Gestures>(
            deepLinks = getDeepLink(Gestures),
        ) { GesturePreferences() }

        composable<Quickstep>(
            deepLinks = getDeepLink(Quickstep),
        ) { QuickstepPreferences() }

        composable<BackupAndRestore>(
            deepLinks = getDeepLink(BackupAndRestore),
        ) { BackupAndRestorePreference() }

        composable<ExperimentalFeatures>(
            deepLinks = getDeepLink(ExperimentalFeatures),
        ) { ExperimentalFeaturesPreferences() }

        composable<Search>(
            deepLinks = getDeepLink(Search()),
        ) { backStackEntry ->
            val route: Search = backStackEntry.toRoute()
            SearchPreferences(currentTab = route.selectedId)
        }

        composable<GoogleFeed>(
            deepLinks = getDeepLink(GoogleFeed),
        ) { GoogleFeedPreferences(fromWidget = false) }
        composable<GoogleFeedWidget> { GoogleFeedPreferences(fromWidget = true) }

        composable<Personalization>(
            deepLinks = getDeepLink(Personalization),
        ) { PersonalizationPreferences() }

        composable<PersonalizationIconPack>(
            deepLinks = getDeepLink(PersonalizationIconPack),
        ) { IconPackPreferences() }
        composable<PersonalizationIconShape> { backStackEntry ->
            val route: PersonalizationIconShape = backStackEntry.toRoute()
            ShapePreference(currentTab = route.selectedId)
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
