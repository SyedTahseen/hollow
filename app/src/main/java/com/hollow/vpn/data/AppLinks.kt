package com.hollow.vpn.data

internal object AppLinks {
    const val REPO_DISPLAY = "github.com/irexanon/V2ray"
    const val REPO_URL = "https://github.com/irexanon/V2ray"
    const val LICENSE_URL = "$REPO_URL/blob/HEAD/LICENSE"
    const val RELEASES_URL = "https://github.com/irexanon/V2ray/releases"
    const val LATEST_RELEASE_API = "https://api.github.com/repos/irexanon/V2ray/releases/latest"
    const val LATEST_TAG_API = "https://api.github.com/repos/irexanon/V2ray/tags?per_page=1"
}

internal object ProbeUrls {
    const val PRIMARY = "https://www.gstatic.com/generate_204"
    const val BACKUP = "https://cp.cloudflare.com/generate_204"

    fun primaryFor(settings: AppSettingsState): String = AppSettingsState.testUrlOption(settings.testUrlChoice).url

    fun backupFor(settings: AppSettingsState): String =
        if (AppSettingsState.testUrlOption(settings.testUrlChoice).url == BACKUP) PRIMARY else BACKUP
}
