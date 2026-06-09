package app.lawnchair.ui.preferences.about

object AboutUrls {
    const val GITHUB_REPOSITORY = "https://github.com/rKyzen/Origin-Launcher"
    const val CREATOR_PROFILE = "https://github.com/rKyzen"
    const val FOLIUS_PROFILE = "https://github.com/shubh72010"
    const val DISCORD = "https://discord.gg/4nCDvCts"
    const val PRIVACY_POLICY = "https://origin-launcher-pap.netlify.app/"

    fun commitUrl(commitSha: String): String = "$GITHUB_REPOSITORY/commit/$commitSha"
}
