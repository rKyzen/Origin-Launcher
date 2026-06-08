package app.lawnchair.ui.preferences.about

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import app.lawnchair.preferences.PreferenceManager
import app.lawnchair.preferences2.PreferenceManager2
import com.android.launcher3.BuildConfig
import com.android.launcher3.R
import com.patrykmichalik.opto.core.firstBlocking
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.create

class AboutViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private val api: GitHubService = gitHubApiRetrofit.create()
    private val prefs: PreferenceManager = PreferenceManager.getInstance(application)
    private val prefs2: PreferenceManager2 = PreferenceManager2.getInstance(application)

    private val nightlyBuildsRepository = NightlyBuildsRepository(
        applicationContext = application,
        api = api,
    )

    private val _uiState = MutableStateFlow(AboutUiState())
    val uiState = _uiState.asStateFlow()
    val updateState = nightlyBuildsRepository.updateState

    init {
        _uiState.update {
            it.copy(
                versionName = if (prefs.hideVersionInfo.get()) {
                    prefs.pseudonymVersion.get() + " (pseudonym)"
                } else {
                    BuildConfig.VERSION_NAME
                },
                commitHash = BuildConfig.COMMIT_HASH,
                creator = Creator(
                    name = application.getString(R.string.about_creator_name),
                    url = AboutUrls.CREATOR_PROFILE,
                ),
                communityLinks = communityLinks,
            )
        }

        if (BuildConfig.APPLICATION_ID.contains("nightly") && prefs2.autoUpdaterNightly.firstBlocking()) {
            nightlyBuildsRepository.checkForUpdate()
            viewModelScope.launch {
                nightlyBuildsRepository.updateState.collect { state ->
                    _uiState.update { it.copy(updateState = state) }
                }
            }
        }
    }

    fun downloadUpdate() {
        nightlyBuildsRepository.downloadUpdate()
    }

    fun installUpdate(file: File, forceInstall: Boolean = false) {
        nightlyBuildsRepository.installUpdate(file, forceInstall)
    }

    fun resetToDownloaded(file: File) {
        nightlyBuildsRepository.resetToDownloaded(file)
    }

    companion object {
        private val communityLinks = listOf(
            Link(
                iconResId = R.drawable.ic_github,
                labelResId = R.string.github,
                url = AboutUrls.GITHUB_REPOSITORY,
            ),
            Link(
                iconResId = R.drawable.ic_discord,
                labelResId = R.string.discord,
                url = AboutUrls.DISCORD,
            ),
        )
    }
}
