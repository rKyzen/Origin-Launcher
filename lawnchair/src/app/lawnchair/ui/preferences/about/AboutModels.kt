package app.lawnchair.ui.preferences.about

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import java.io.File

data class AboutUiState(
    val versionName: String = "",
    val commitHash: String = "",
    val creator: Creator? = null,
    val contributors: List<Creator> = emptyList(),
    val communityLinks: List<Link> = emptyList(),
    val updateState: UpdateState = UpdateState.Hidden,
)

data class Creator(
    val name: String,
    val url: String,
)

data class Link(
    @DrawableRes val iconResId: Int,
    @StringRes val labelResId: Int,
    val url: String,
)

sealed interface UpdateState {
    data object Hidden : UpdateState
    data object Checking : UpdateState
    data object UpToDate : UpdateState
    data class Available(
        val name: String,
        val url: String,
        val changelogState: ChangelogState?,
        val expectedSha256: String? = null,
    ) : UpdateState
    data class Downloading(val progress: Float) : UpdateState
    data class Downloaded(val file: File) : UpdateState
    data object Failed : UpdateState
    data class MajorUpdate(val file: File) : UpdateState
    data class Disabled(val reason: UpdateDisabledReason) : UpdateState
}

enum class UpdateDisabledReason {
    MAJOR_IS_NEWER,
}

data class ChangelogState(
    val commits: List<GitHubCommit> = emptyList(),
    val currentBuildNumber: Int = 0,
    val latestBuildNumber: Int = 0,
)
