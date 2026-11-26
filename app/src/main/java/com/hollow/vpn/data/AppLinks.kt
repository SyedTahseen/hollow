package com.hollow.vpn.data

/** Shared external endpoints, defined once. */
internal object AppLinks {
    const val REPO_DISPLAY = "github.com/irexanon/V2ray"
    const val RELEASES_URL = "https://github.com/irexanon/V2ray/releases"
    const val LATEST_RELEASE_API = "https://api.github.com/repos/irexanon/V2ray/releases/latest"
    const val LATEST_TAG_API = "https://api.github.com/repos/irexanon/V2ray/tags?per_page=1"
}

/** Lightweight reachability endpoints used to verify the tunnel and measure delay. */
internal object ProbeUrls {
    const val PRIMARY = "https://www.gstatic.com/generate_204"
    const val BACKUP = "https://cp.cloudflare.com/generate_204"
}
