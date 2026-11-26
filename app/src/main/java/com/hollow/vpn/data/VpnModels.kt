package com.hollow.vpn.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "vpn_servers")
data class ServerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val countryName: String,
    val countryCode: String,
    val protocolDisplay: String, // e.g., "V2Ray · Reality · 185.216.34.12"
    val protocolCore: String, // e.g., "VLESS (V2Ray)", "VMess (V2Ray)", "Trojan (V2Ray)"
    val transport: String, // "Reality", "VLESS", "Trojan", "gRPC", "WebSocket", "TCP"
    val host: String,
    val resolvedIp: String = "",
    val port: Int,
    val uuidOrPassword: String,
    val sni: String = "",
    val publicKey: String = "",
    val rawConfigJson: String = "",
    val shareUri: String = "",
    val pingMs: Int,
    val isRecommendedTop15: Boolean = true,
    val rankOrder: Int = 99,
    val isSelected: Boolean = false,
    val isCustom: Boolean = false,
    val subscriptionUrl: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val lastSpeedMbps: Double = 0.0,
    val lastAliveAt: Long = 0L,
    val protocolTier: Int = 5,
    val failCount: Int = 0
)

@Entity(tableName = "vpn_subscriptions")
data class SubscriptionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val url: String,
    val name: String = "Custom Subscription",
    val autoUpdate: Boolean = true,
    val updateIntervalHours: Int = 1,
    val replaceExisting: Boolean = true,
    val lastFetchedAt: Long = System.currentTimeMillis(),
    val serverCount: Int = 0,
    val isEnabled: Boolean = true,
    val successPulls: Int = 0,
    val failedPulls: Int = 0
)

/** A pill on the Servers screen: "All", "Default" (manually added), or one per subscription. */
data class ServerCategory(val key: String, val label: String) {
    companion object {
        const val ALL_KEY = "all"
        const val DEFAULT_KEY = "default"
    }
}

/** Human readable pill label for a subscription (its name, or a name derived from its URL). */
fun SubscriptionEntity.categoryLabel(): String {
    val trimmedName = name.trim()
    if (trimmedName.isNotBlank() && !trimmedName.equals("Custom Subscription", ignoreCase = true)) return trimmedName
    return try {
        val uri = java.net.URI(url.trim())
        val host = uri.host?.removePrefix("www.").orEmpty()
        val segments = uri.path.orEmpty().split('/').filter { it.isNotBlank() }
        when {
            (host == "raw.githubusercontent.com" || host == "github.com") && segments.isNotEmpty() -> segments[0]
            host.isNotBlank() -> host
            else -> "Subscription"
        }
    } catch (_: Exception) {
        "Subscription"
    }
}

enum class FetchingStage {
    IDLE,
    DOWNLOADING,
    TESTING_PIPELINE,
    SPEED_AND_GEO,
    FINALIZING
}

data class FetchingProgressState(
    val isActive: Boolean = false,
    val stage: FetchingStage = FetchingStage.IDLE,
    val headline: String = "Syncing servers…",
    val detail: String = "Auto configuring for you",
    val parsedCount: Int = 0,
    val probedCount: Int = 0,
    val reachableCount: Int = 0,
    val testedCount: Int = 0,
    val workingCount: Int = 0,
    val targetTotalCount: Int = 0,
    val countryCount: Int = 0,
    val fastestPingMs: Int = 0,
    val discoveredCountries: List<Pair<String, String>> = emptyList()
)

data class AppSettingsState(
    val updateIntervalLabel: String = "1 hour",
    val routingMode: String = "Global",
    val dnsMode: String = "Auto (DoH)",
    val tunMode: Boolean = true,
    val ipv6Enabled: Boolean = false,
    val subAutoUpdate: Boolean = true,
    val subReplaceExisting: Boolean = true,
    val activeSubscriptionUrl: String = DEFAULT_SUBSCRIPTION_URL,
    val perAppProxyEnabled: Boolean = false,
    val bypassSelectedApps: Boolean = false,
    /** Split tunneling, "Exclude" mode: these apps connect directly. */
    val splitExcludeApps: Set<String> = emptySet(),
    /** Split tunneling, "Include" mode: only these apps use the VPN. */
    val splitIncludeApps: Set<String> = emptySet(),
    val mirrorsEnabled: Boolean = false,
    val domesticCountryCode: String = "IR",
    val themeMode: String = "Dark",
    /** App-wide display size in percent (80..130), same as Outrun's "Display size". */
    val displayScalePercent: Int = 100,
    /** NetShield: filter ads, trackers and malware by sending DNS lookups to a filtering resolver. */
    val netShieldEnabled: Boolean = false,
    /** NetShield extra: also block adult content (AdGuard "family" resolver). */
    val blockAdultContent: Boolean = false,
    /** Split tunneling, "Skip VPN" mode: these IPs / CIDRs connect directly. */
    val splitExcludeIps: List<String> = emptyList(),
    /** Split tunneling, "VPN only" mode: only these IPs / CIDRs go through the VPN. */
    val splitIncludeIps: List<String> = emptyList(),
    /** Latency test: id of the address every server is tested against (see [TEST_URLS]). */
    val testUrlChoice: String = DEFAULT_TEST_URL_ID,
    /** Advanced: drop QUIC (UDP 443) so apps fall back to TCP, which suits most proxy protocols. */
    val blockQuic: Boolean = true,
    /** Advanced: largest packet the tunnel sends (see [MTU_CHOICES]). */
    val mtu: Int = DEFAULT_MTU,
    /** Advanced: use [customDnsList] instead of the default DNS. */
    val customDnsEnabled: Boolean = false,
    val customDnsList: List<String> = emptyList(),
    /** Advanced: connect the selected server when the phone starts. */
    val autoConnectBoot: Boolean = false,
    /** Advanced: send all traffic through the VPN using root instead of Android's VPN service. */
    val rootMode: Boolean = false
) {
    /** Apps for the active split-tunneling mode (what the VPN service receives). */
    val activeSplitApps: Set<String>
        get() = if (bypassSelectedApps) splitExcludeApps else splitIncludeApps

    companion object {
        const val DEFAULT_TEST_URL_ID = "google"
        const val DEFAULT_MTU = 1500

        /** Addresses used to time a server. Each one answers a tiny request through the proxy. */
        val TEST_URLS = listOf(
            TestUrlOption("google", "Google", "https://www.gstatic.com/generate_204"),
            TestUrlOption("cloudflare", "Cloudflare", "https://cp.cloudflare.com/generate_204"),
            TestUrlOption("apple", "Apple", "https://captive.apple.com/")
        )

        val MTU_CHOICES = listOf(
            1500 to "Default, best throughput",
            1420 to "Good for most mobile networks",
            1400 to "Safer on tunnels and some carriers",
            1380 to "For networks that drop larger packets",
            1280 to "Minimum for IPv6, most compatible"
        )

        fun testUrlOption(id: String): TestUrlOption =
            TEST_URLS.firstOrNull { it.id == id } ?: TEST_URLS.first()

        /** Custom DNS servers that are usable right now (IPv6 resolvers are dropped while IPv6 is off). */
        fun activeCustomDns(settings: AppSettingsState): List<String> =
            if (!settings.customDnsEnabled) emptyList()
            else settings.customDnsList.filter { settings.ipv6Enabled || ':' !in it }

        /** AdGuard DNS: default (ads, trackers, malware) and family (adds adult content). */
        val NETSHIELD_DNS = listOf("94.140.14.14", "94.140.15.15")
        val NETSHIELD_DNS_FAMILY = listOf("94.140.14.15", "94.140.15.16")

        fun netShieldDns(blockAdult: Boolean): List<String> =
            if (blockAdult) NETSHIELD_DNS_FAMILY else NETSHIELD_DNS

        const val DEFAULT_SUBSCRIPTION_URL = ""
        internal const val LEGACY_DEFAULT_SUBSCRIPTION_URL =
            "https://raw.githubusercontent.com/mirrorrepomagic/free-v2ray-public-list/refs/heads/main/V2Ray-Config-By-EbraSha.txt"
    }
}

data class TestUrlOption(val id: String, val name: String, val url: String)

data class ActiveTunnelSnapshot(
    val country: String,
    val host: String,
    val port: Int,
    val protocol: String,
    val xrayJson: String,
    val tunMode: Boolean,
    val enableIpv6: Boolean,
    val dnsMode: String,
    val routingMode: String,
    val perAppProxyEnabled: Boolean,
    val bypassSelectedApps: Boolean,
    val selectedAppPackages: Set<String>,
    val netShieldEnabled: Boolean = false,
    val blockAdultContent: Boolean = false,
    val splitExcludeIps: List<String> = emptyList(),
    val splitIncludeIps: List<String> = emptyList()
)

class VpnPreferencesStore(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("v2ray_vpn_prefs", Context.MODE_PRIVATE)

    fun loadSettings(): AppSettingsState {
        val rawSubUrl = prefs.getString("active_sub_url", "")?.trim().orEmpty()
        val cleanSubUrl = if (rawSubUrl == AppSettingsState.LEGACY_DEFAULT_SUBSCRIPTION_URL) "" else rawSubUrl
        return AppSettingsState(
            updateIntervalLabel = prefs.getString("update_interval", "1 hour") ?: "1 hour",
            // "Direct Only" was a diagnostic mode that has been removed; fall back to Global.
            routingMode = (prefs.getString("routing_mode", "Global") ?: "Global")
                .takeUnless { it.contains("Direct", ignoreCase = true) } ?: "Global",
            tunMode = prefs.getBoolean("tun_mode", true),
            ipv6Enabled = prefs.getBoolean("ipv6_enabled", false),
            // Merged from the old duplicate "auto_configure" toggle: off if either was off.
            subAutoUpdate = prefs.getBoolean("sub_auto_update", true) && prefs.getBoolean("auto_configure", true),
            subReplaceExisting = prefs.getBoolean("sub_replace_existing", true),
            activeSubscriptionUrl = cleanSubUrl,
            perAppProxyEnabled = prefs.getBoolean("per_app_proxy", false),
            bypassSelectedApps = prefs.getBoolean("bypass_selected_apps", false),
            splitExcludeApps = prefs.getStringSet("per_app_packages", emptySet()) ?: emptySet(),
            splitIncludeApps = prefs.getStringSet("split_include_apps", emptySet()) ?: emptySet(),
            mirrorsEnabled = prefs.getBoolean("mirrors_enabled", false),
            domesticCountryCode = prefs.getString("domestic_country_code", "IR") ?: "IR",
            themeMode = prefs.getString("theme_mode", "Dark") ?: "Dark",
            displayScalePercent = prefs.getInt("display_scale_percent", 100).coerceIn(80, 130),
            netShieldEnabled = prefs.getBoolean("netshield_enabled", false),
            blockAdultContent = prefs.getBoolean("netshield_block_adult", false),
            splitExcludeIps = loadLines("split_exclude_ips"),
            splitIncludeIps = loadLines("split_include_ips"),
            testUrlChoice = prefs.getString("test_url_choice", AppSettingsState.DEFAULT_TEST_URL_ID)
                ?: AppSettingsState.DEFAULT_TEST_URL_ID,
            blockQuic = prefs.getBoolean("block_quic", true),
            mtu = prefs.getInt("mtu", AppSettingsState.DEFAULT_MTU).coerceIn(1280, 1500),
            customDnsEnabled = prefs.getBoolean("custom_dns_enabled", false),
            customDnsList = loadLines("custom_dns_list"),
            autoConnectBoot = prefs.getBoolean("auto_connect_boot", false),
            rootMode = prefs.getBoolean("root_mode", false)
        )
    }

    private fun loadLines(key: String): List<String> =
        (prefs.getString(key, "") ?: "").split("\n").map { it.trim() }.filter { it.isNotEmpty() }

    fun saveSettings(state: AppSettingsState) {
        prefs.edit()
            .remove("auto_configure")
            .remove("sub_update_interval")
            // Settings that no longer exist.
            .remove("protocol_pref")
            .remove("dns_mode")
            .remove("start_on_open_v2")
            .remove("hide_recents")
            .remove("has_completed_onboarding")
            .remove("smart_switch_enabled")
            .remove("strict_tls_enabled")
            .putString("update_interval", state.updateIntervalLabel)
            .putString("routing_mode", state.routingMode)
            .putBoolean("tun_mode", state.tunMode)
            .putBoolean("ipv6_enabled", state.ipv6Enabled)
            .putBoolean("sub_auto_update", state.subAutoUpdate)
            .putBoolean("sub_replace_existing", state.subReplaceExisting)
            .putString("active_sub_url", state.activeSubscriptionUrl)
            .putBoolean("per_app_proxy", state.perAppProxyEnabled)
            .putBoolean("bypass_selected_apps", state.bypassSelectedApps)
            .putStringSet("per_app_packages", state.splitExcludeApps)
            .putStringSet("split_include_apps", state.splitIncludeApps)
            .putBoolean("mirrors_enabled", state.mirrorsEnabled)
            .putString("domestic_country_code", state.domesticCountryCode)
            .putString("theme_mode", state.themeMode)
            .putInt("display_scale_percent", state.displayScalePercent.coerceIn(80, 130))
            .putBoolean("netshield_enabled", state.netShieldEnabled)
            .putBoolean("netshield_block_adult", state.blockAdultContent)
            .putString("split_exclude_ips", state.splitExcludeIps.joinToString("\n"))
            .putString("split_include_ips", state.splitIncludeIps.joinToString("\n"))
            .putString("test_url_choice", state.testUrlChoice)
            .putBoolean("block_quic", state.blockQuic)
            .putInt("mtu", state.mtu.coerceIn(1280, 1500))
            .putBoolean("custom_dns_enabled", state.customDnsEnabled)
            .putString("custom_dns_list", state.customDnsList.joinToString("\n"))
            .putBoolean("auto_connect_boot", state.autoConnectBoot)
            .putBoolean("root_mode", state.rootMode)
            .apply()
    }

    fun saveActiveTunnelSnapshot(snapshot: ActiveTunnelSnapshot) {
        prefs.edit()
            .putString("snap_country", snapshot.country)
            .putString("snap_host", snapshot.host)
            .putInt("snap_port", snapshot.port)
            .putString("snap_protocol", snapshot.protocol)
            .putString("snap_xray_json", snapshot.xrayJson)
            .putBoolean("snap_tun_mode", snapshot.tunMode)
            .putBoolean("snap_ipv6", snapshot.enableIpv6)
            .putString("snap_dns_mode", snapshot.dnsMode)
            .putString("snap_routing_mode", snapshot.routingMode)
            .putBoolean("snap_per_app", snapshot.perAppProxyEnabled)
            .putBoolean("snap_bypass_selected", snapshot.bypassSelectedApps)
            .putStringSet("snap_selected_packages", snapshot.selectedAppPackages)
            .putBoolean("snap_netshield", snapshot.netShieldEnabled)
            .putBoolean("snap_block_adult", snapshot.blockAdultContent)
            .putString("snap_split_exclude_ips", snapshot.splitExcludeIps.joinToString("\n"))
            .putString("snap_split_include_ips", snapshot.splitIncludeIps.joinToString("\n"))
            .apply()
    }

    fun loadActiveTunnelSnapshot(): ActiveTunnelSnapshot? {
        val host = prefs.getString("snap_host", "").orEmpty()
        val xrayJson = prefs.getString("snap_xray_json", "").orEmpty()
        if (host.isBlank() || xrayJson.isBlank()) return null
        return ActiveTunnelSnapshot(
            country = prefs.getString("snap_country", "Server") ?: "Server",
            host = host,
            port = prefs.getInt("snap_port", 443),
            protocol = prefs.getString("snap_protocol", "VLESS (V2Ray)") ?: "VLESS (V2Ray)",
            xrayJson = xrayJson,
            tunMode = prefs.getBoolean("snap_tun_mode", true),
            enableIpv6 = prefs.getBoolean("snap_ipv6", false),
            dnsMode = prefs.getString("snap_dns_mode", "Auto (DoH)") ?: "Auto (DoH)",
            routingMode = (prefs.getString("snap_routing_mode", "Global") ?: "Global")
                .takeUnless { it.contains("Direct", ignoreCase = true) } ?: "Global",
            perAppProxyEnabled = prefs.getBoolean("snap_per_app", false),
            bypassSelectedApps = prefs.getBoolean("snap_bypass_selected", false),
            selectedAppPackages = prefs.getStringSet("snap_selected_packages", emptySet()) ?: emptySet(),
            netShieldEnabled = prefs.getBoolean("snap_netshield", false),
            blockAdultContent = prefs.getBoolean("snap_block_adult", false),
            splitExcludeIps = loadLines("snap_split_exclude_ips"),
            splitIncludeIps = loadLines("snap_split_include_ips")
        )
    }

    fun clearActiveTunnelSnapshot() {
        prefs.edit()
            .remove("snap_country")
            .remove("snap_host")
            .remove("snap_port")
            .remove("snap_protocol")
            .remove("snap_xray_json")
            .remove("snap_tun_mode")
            .remove("snap_ipv6")
            .remove("snap_dns_mode")
            .remove("snap_routing_mode")
            .remove("snap_per_app")
            .remove("snap_bypass_selected")
            .remove("snap_selected_packages")
            .remove("snap_smart_switch")
            .remove("snap_server_speed_mbps")
            .remove("snap_netshield")
            .remove("snap_block_adult")
            .remove("snap_split_exclude_ips")
            .remove("snap_split_include_ips")
            .apply()
    }

    fun saveBaselineSpeed(networkKey: String, mbps: Double, nowMillis: Long = System.currentTimeMillis()) {
        prefs.edit()
            .putString("baseline_network_key", networkKey)
            .putFloat("baseline_mbps", mbps.toFloat())
            .putLong("baseline_updated_at", nowMillis)
            .apply()
    }

    fun loadBaselineSpeed(networkKey: String, maxAgeMillis: Long = 6L * 60 * 60 * 1000, nowMillis: Long = System.currentTimeMillis()): Double {
        val savedKey = prefs.getString("baseline_network_key", "").orEmpty()
        val updatedAt = prefs.getLong("baseline_updated_at", 0L)
        if (savedKey != networkKey || updatedAt <= 0L || nowMillis - updatedAt > maxAgeMillis) return 0.0
        return prefs.getFloat("baseline_mbps", 0f).toDouble()
    }

    fun invalidateBaseline() {
        prefs.edit()
            .remove("baseline_network_key")
            .remove("baseline_mbps")
            .remove("baseline_updated_at")
            .apply()
    }

    /** Favorite servers are keyed by "host:port" so they survive re-fetches and DB resets. */
    fun loadFavoriteKeys(): Set<String> =
        prefs.getStringSet("favorite_server_keys", emptySet())?.toSet() ?: emptySet()

    fun saveFavoriteKeys(keys: Set<String>) {
        prefs.edit().putStringSet("favorite_server_keys", keys).apply()
    }
}

/** Stable identity used for the favorites star. */
fun ServerEntity.favoriteKey(): String = "${host.lowercase()}:$port"

@Dao
interface VpnDao {
    @Query("SELECT * FROM vpn_servers ORDER BY isSelected DESC, pingMs ASC")
    fun observeAllServers(): Flow<List<ServerEntity>>

    @Query("SELECT * FROM vpn_servers ORDER BY pingMs ASC")
    suspend fun getAllServersOnce(): List<ServerEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServers(servers: List<ServerEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServer(server: ServerEntity): Long

    @Update
    suspend fun updateServer(server: ServerEntity)

    @Query("UPDATE vpn_servers SET isSelected = 0")
    suspend fun clearSelection()

    @Query("UPDATE vpn_servers SET isSelected = 1 WHERE id = :serverId")
    suspend fun markSelected(serverId: Long)

    @Query("DELETE FROM vpn_servers WHERE id = :serverId")
    suspend fun deleteServerById(serverId: Long)

    @Query("DELETE FROM vpn_servers WHERE isCustom = 0")
    suspend fun deleteFetchedServers()

    @Query("DELETE FROM vpn_servers")
    suspend fun deleteAllServers()

    @Query("SELECT * FROM vpn_subscriptions ORDER BY lastFetchedAt DESC")
    fun observeSubscriptions(): Flow<List<SubscriptionEntity>>

    @Query("SELECT * FROM vpn_subscriptions ORDER BY lastFetchedAt DESC")
    suspend fun getSubscriptionsOnce(): List<SubscriptionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscription(subscription: SubscriptionEntity): Long

    @Update
    suspend fun updateSubscription(subscription: SubscriptionEntity)

    @Query("DELETE FROM vpn_subscriptions WHERE url = :url")
    suspend fun deleteSubscriptionByUrl(url: String)
}

@Database(
    entities = [ServerEntity::class, SubscriptionEntity::class],
    version = 7,
    exportSchema = false
)
abstract class VpnDatabase : RoomDatabase() {
    abstract fun vpnDao(): VpnDao

    companion object {
        @Volatile
        private var INSTANCE: VpnDatabase? = null

        fun getInstance(context: Context): VpnDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VpnDatabase::class.java,
                    "v2ray_xray_vpn.db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
