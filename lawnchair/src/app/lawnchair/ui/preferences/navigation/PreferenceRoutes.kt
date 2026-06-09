package app.lawnchair.ui.preferences.navigation

import app.lawnchair.ui.preferences.components.search.SearchProviderId
import app.lawnchair.ui.preferences.destinations.SearchRoute
import app.lawnchair.ui.preferences.destinations.ShapeRoute
import kotlinx.serialization.Serializable

private const val URI = "lawnchair://settings"

@Serializable
sealed interface PreferenceRoute

@Serializable
sealed interface PreferenceRootRoute : PreferenceRoute

@Serializable
sealed interface PreferenceDeepLink {
    val deepLink: String
}

@Serializable
data object Root : PreferenceRootRoute

@Serializable
data object Dummy : PreferenceRootRoute

@Serializable
data object OriginModes : PreferenceRootRoute, PreferenceDeepLink {
    override val deepLink = "$URI/origin-modes"
}

@Serializable
data object Personalization : PreferenceRootRoute, PreferenceDeepLink {
    override val deepLink = "$URI/personalization"
}

@Serializable
data object HomeScreen : PreferenceRootRoute, PreferenceDeepLink {
    override val deepLink = "$URI/home-screen"
}

@Serializable
data object Dock : PreferenceRootRoute, PreferenceDeepLink {
    override val deepLink = "$URI/dock"
}

@Serializable
data object AppDrawer : PreferenceRootRoute, PreferenceDeepLink {
    override val deepLink = "$URI/app-drawer"
}

@Serializable
data class Search(val selectedId: SearchRoute = SearchRoute.DOCK_SEARCH) :
    PreferenceRootRoute,
    PreferenceDeepLink {
    override val deepLink = "$URI/search"
}

@Serializable
data object Folders : PreferenceRootRoute, PreferenceDeepLink {
    override val deepLink = "$URI/folders"
}

@Serializable
data object Quickstep : PreferenceRootRoute, PreferenceDeepLink {
    override val deepLink = "$URI/quickstep"
}

@Serializable
data object BackupAndRestore : PreferenceRootRoute, PreferenceDeepLink {
    override val deepLink = "$URI/backup-restore"
}

@Serializable
data object Gestures : PreferenceRootRoute, PreferenceDeepLink {
    override val deepLink = "$URI/gestures"
}

@Serializable
data object General : PreferenceRootRoute, PreferenceDeepLink {
    override val deepLink = "$URI/general"
}

@Serializable
data object GoogleFeed : PreferenceRootRoute, PreferenceDeepLink {
    override val deepLink = "$URI/google-feed"
}

@Serializable
data object About : PreferenceRootRoute, PreferenceDeepLink {
    override val deepLink = "$URI/about"
}

@Serializable
data object ExperimentalFeatures : PreferenceRootRoute, PreferenceDeepLink {
    override val deepLink = "$URI/experimental-features"
}

@Serializable
data object DebugMenu : PreferenceRootRoute

@Serializable
data object FeatureFlags : PreferenceRoute

@Serializable
data class PersonalizationFontSelection(val prefKey: String) : PreferenceRoute

@Serializable
data object PersonalizationIconPack : PreferenceRoute, PreferenceDeepLink {
    override val deepLink = "$URI/personalization-iconpack"
}

@Serializable
data class PersonalizationIconShape(val selectedId: ShapeRoute = ShapeRoute.APP_SHAPE) : PreferenceRoute

@Serializable
data class PersonalizationCustomIconShapeCreator(val selectedId: ShapeRoute = ShapeRoute.APP_SHAPE) :
    PreferenceRoute,
    PreferenceDeepLink {
    override val deepLink = "$URI/personalization-icon-shape-creator"
}

@Serializable
data object HomeScreenGrid : PreferenceRoute, PreferenceDeepLink {
    override val deepLink = "$URI/home-screen-grid"
}

@Serializable
data object HomeScreenPopupEditor : PreferenceRoute, PreferenceDeepLink {
    override val deepLink = "$URI/home-screen-popup-editor"
}

@Serializable
data object DockSearchProvider : PreferenceRoute, PreferenceDeepLink {
    override val deepLink = "$URI/dock-search-provider"
}

@Serializable
data object AppDrawerHiddenApps : PreferenceRoute, PreferenceDeepLink {
    override val deepLink = "$URI/app-drawer-hidden-apps"
}

@Serializable
data object AppDrawerFolder : PreferenceRoute, PreferenceDeepLink {
    override val deepLink = "$URI/app-drawer-folder"
}

@Serializable
data class AppDrawerAppListToFolder(val id: Int) : PreferenceRoute

@Serializable
data class SearchProviderPreference(val id: SearchProviderId) :
    PreferenceRoute,
    PreferenceDeepLink {
    override val deepLink = "$URI/search-provider"
}

@Serializable
data object GoogleFeedWidget : PreferenceRoute

@Serializable
data object GesturesPickApp : PreferenceRoute

@Serializable
data object AboutLicenses : PreferenceRoute, PreferenceDeepLink {
    override val deepLink = "$URI/about-licenses"
}

@Serializable
data class SelectIcon(
    val componentKey: String,
) : PreferenceRoute

@Serializable
data class IconPicker(val packageName: String = "") : PreferenceRoute

@Serializable
data class ColorSelection(val prefKey: String) : PreferenceRoute

@Serializable
data object CreateBackup : PreferenceRoute, PreferenceDeepLink {
    override val deepLink = "$URI/create-backup"
}

@Serializable
data class RestoreBackup(val base64Uri: String) : PreferenceRoute
