package com.hollow.vpn.ui

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hollow.vpn.data.AppSettingsState
import com.hollow.vpn.data.FetchingProgressState
import com.hollow.vpn.data.FetchingStage
import com.hollow.vpn.data.ServerEntity
import com.hollow.vpn.data.ServerCategory
import com.hollow.vpn.data.categoryLabel
import com.hollow.vpn.data.SubscriptionEntity
import com.hollow.vpn.data.VpnDatabase
import com.hollow.vpn.data.VpnPreferencesStore
import com.hollow.vpn.data.favoriteKey
import com.hollow.vpn.vpn.HollowJobScheduler
import com.hollow.vpn.vpn.V2RayParserAndFetcher
import com.hollow.vpn.vpn.XrayProbeResult
import com.hollow.vpn.vpn.XrayVpnService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.max

enum class VpnScreen {
    SPLASH_FETCHING,
    SERVER_LIST,
    CONNECTED_HOME,
    SETTINGS
}

/** What the server list should open when another screen asks the user to add servers. */
enum class AddServerRequest { MENU, SUBSCRIPTION, MANUAL }

data class ConnectionTelemetryState(
    val isConnected: Boolean = false,
    val isConnecting: Boolean = false,
    val connectingStage: Int = 1,
    val elapsedSeconds: Long = 0L,
    val uploadBytes: Long = 0L,
    val downloadBytes: Long = 0L,
    val uploadBytesPerSec: Long = 0L,
    val downloadBytesPerSec: Long = 0L,
    val serverIpLabel: String = "—",
    val realPublicIpLabel: String = "Detecting…",
    val realPublicCountryCode: String = "XX",
    val realPublicCity: String = "",
    val realPublicLat: Double? = null,
    val realPublicLon: Double? = null,
    val realIspLabel: String = "Detecting…",
    val preConnectionIp: String = ""
)

class VpnViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = VpnDatabase.getInstance(application).vpnDao()
    private val prefsStore = VpnPreferencesStore(application)
    private val fetcher = V2RayParserAndFetcher()

    private val _settings = MutableStateFlow(prefsStore.loadSettings())
    val settings: StateFlow<AppSettingsState> = _settings.asStateFlow()

    private val _screenStack = MutableStateFlow(listOf(VpnScreen.CONNECTED_HOME))
    private val _currentScreen = MutableStateFlow(VpnScreen.CONNECTED_HOME)
    val currentScreen: StateFlow<VpnScreen> = _currentScreen.asStateFlow()


    private val _selectedCategory = MutableStateFlow(ServerCategory.ALL_KEY)
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _telemetry = MutableStateFlow(ConnectionTelemetryState())
    val telemetry: StateFlow<ConnectionTelemetryState> = _telemetry.asStateFlow()

    private val _fetchingStatusMessage = MutableStateFlow("Auto configuring for you")
    val fetchingStatusMessage: StateFlow<String> = _fetchingStatusMessage.asStateFlow()

    private val _fetchingProgress = MutableStateFlow(FetchingProgressState())
    val fetchingProgress: StateFlow<FetchingProgressState> = _fetchingProgress.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    private val _addServerRequest = MutableStateFlow<AddServerRequest?>(null)
    val addServerRequest: StateFlow<AddServerRequest?> = _addServerRequest.asStateFlow()

    private val _inspectingJsonServer = MutableStateFlow<ServerEntity?>(null)
    val inspectingJsonServer: StateFlow<ServerEntity?> = _inspectingJsonServer.asStateFlow()

    private val _sharingQrServer = MutableStateFlow<ServerEntity?>(null)
    val sharingQrServer: StateFlow<ServerEntity?> = _sharingQrServer.asStateFlow()

    val allServers: StateFlow<List<ServerEntity>> = dao.observeAllServers()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allSubscriptions: StateFlow<List<SubscriptionEntity>> = dao.observeSubscriptions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /** Pills for the Servers screen: All, Default (manual), then one per subscription. */
    val serverCategories: StateFlow<List<ServerCategory>> = allSubscriptions
        .map { subs ->
            val labels = subs.map { it.categoryLabel() }
            val seen = mutableMapOf<String, Int>()
            val subCategories = subs.mapIndexed { i, sub ->
                val base = labels[i]
                val n = (seen[base] ?: 0) + 1
                seen[base] = n
                val label = if (labels.count { it == base } > 1) "$base $n" else base
                ServerCategory(key = sub.url.trim(), label = label)
            }
            listOf(
                ServerCategory(ServerCategory.ALL_KEY, "All"),
                ServerCategory(ServerCategory.DEFAULT_KEY, "Default")
            ) + subCategories
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = listOf(
                ServerCategory(ServerCategory.ALL_KEY, "All"),
                ServerCategory(ServerCategory.DEFAULT_KEY, "Default")
            )
        )

    val filteredServers: StateFlow<List<ServerEntity>> = combine(
        allServers,
        _selectedCategory,
        _settings,
        allSubscriptions
    ) { servers, category, settings, subs ->
        val sorted = servers.sortedWith(
            compareByDescending<ServerEntity> { it.isSelected }
                .thenBy { if (it.pingMs > 0) it.pingMs else Int.MAX_VALUE }
        )
        val knownSubscription = subs.any { it.url.trim() == category }
        when {
            category == ServerCategory.DEFAULT_KEY ->
                sorted.filter { it.isCustom || it.subscriptionUrl.isBlank() }
            knownSubscription ->
                sorted.filter { !it.isCustom && it.subscriptionUrl.trim() == category }
            else -> sorted
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val selectedServer: StateFlow<ServerEntity?> = allServers.map { list ->
        list.firstOrNull { it.isSelected } ?: list.firstOrNull()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    private var autoUpdateJob: Job? = null
    private var ipRefreshJob: Job? = null
    private var connectJob: Job? = null
    private var repingJob: Job? = null
    private var fastestJob: Job? = null

    private val _favoriteKeys = MutableStateFlow(prefsStore.loadFavoriteKeys())
    val favoriteKeys: StateFlow<Set<String>> = _favoriteKeys.asStateFlow()

    private val _isRepingAll = MutableStateFlow(false)
    val isRepingAll: StateFlow<Boolean> = _isRepingAll.asStateFlow()

    private val _isTestingFastest = MutableStateFlow(false)
    val isTestingFastest: StateFlow<Boolean> = _isTestingFastest.asStateFlow()

    fun toggleFavorite(server: ServerEntity) {
        val key = server.favoriteKey()
        val updated = _favoriteKeys.value.let { if (key in it) it - key else it + key }
        _favoriteKeys.value = updated
        prefsStore.saveFavoriteKeys(updated)
    }

    /**
     * Tests every given server, saves the fresh pings, and hands the fastest working one to
     * [onFastest] so the UI can connect to it (the UI owns the VPN-permission flow).
     */
    fun testAndPickFastest(candidates: List<ServerEntity>, onFastest: (ServerEntity) -> Unit) {
        if (fastestJob?.isActive == true) return
        if (candidates.isEmpty()) {
            showMessage("No servers to test")
            return
        }
        fastestJob = viewModelScope.launch {
            _isTestingFastest.value = true
            try {
                showMessage("Testing ${candidates.size} server(s)…")
                val semaphore = Semaphore(8)
                val tested = coroutineScope {
                    candidates.map { server ->
                        async {
                            semaphore.withPermit {
                                val ping = verifyServerLatencyOrNull(server) ?: 0
                                val updated = server.copy(pingMs = ping)
                                dao.updateServer(updated)
                                updated
                            }
                        }
                    }.awaitAll()
                }
                val fastest = tested.filter { it.pingMs > 0 }.minByOrNull { it.pingMs }
                if (fastest != null) {
                    showMessage("Fastest: ${fastest.countryName} (${fastest.pingMs} ms)")
                    onFastest(fastest)
                } else {
                    showMessage("No server passed the connection test")
                }
            } finally {
                _isTestingFastest.value = false
            }
        }
    }
    private var fetchSweepJob: Job? = null

    init {
        observeVpnServiceState()
        refreshRealPublicIp()
        initializeLiveServersOnStartup()
        startSubscriptionAutoUpdateLoop()
        HollowJobScheduler.schedulePeriodicMaintenance(getApplication(), _settings.value)
    }

    /**
     * Keeps UI connection state and Xray outbound traffic counters synchronized with XrayVpnService.
     */
    private fun observeVpnServiceState() {
        var wasRunning = false
        viewModelScope.launch {
            XrayVpnService.isServiceRunning.collect { running ->
                if (running) {
                    wasRunning = true
                    fetcher.evictConnectionPools()
                    _telemetry.update {
                        it.copy(
                            isConnected = true,
                            isConnecting = false,
                            connectingStage = 1,
                            elapsedSeconds = 0L,
                            uploadBytes = 0L,
                            downloadBytes = 0L,
                            uploadBytesPerSec = 0L,
                            downloadBytesPerSec = 0L
                        )
                    }
                    // Verify live exit IP through the active tunnel once routes settle
                    ipRefreshJob?.cancel()
                    ipRefreshJob = viewModelScope.launch {
                        delay(1500L)
                        refreshRealPublicIp()
                    }
                } else {
                    _telemetry.update {
                        it.copy(
                            isConnected = false,
                            isConnecting = false,
                            connectingStage = 1,
                            uploadBytesPerSec = 0L,
                            downloadBytesPerSec = 0L
                        )
                    }
                    if (wasRunning) {
                        wasRunning = false
                        fetcher.evictConnectionPools()
                        ipRefreshJob?.cancel()
                        ipRefreshJob = viewModelScope.launch {
                            delay(600L)
                            refreshRealPublicIp()
                        }
                    }
                }
            }
        }
        viewModelScope.launch {
            XrayVpnService.connectStage.collect { stage ->
                if (stage >= 2 && _telemetry.value.isConnecting) {
                    _telemetry.update {
                        it.copy(connectingStage = max(it.connectingStage, stage))
                    }
                }
            }
        }
        viewModelScope.launch {
            XrayVpnService.trafficSnapshot.collect { snapshot ->
                if (_telemetry.value.isConnected && !_telemetry.value.isConnecting) {
                    _telemetry.update {
                        it.copy(
                            elapsedSeconds = snapshot.elapsedSeconds,
                            uploadBytes = snapshot.uploadBytes,
                            downloadBytes = snapshot.downloadBytes,
                            uploadBytesPerSec = snapshot.uploadBytesPerSec,
                            downloadBytesPerSec = snapshot.downloadBytesPerSec
                        )
                    }
                }
            }
        }
        viewModelScope.launch {
            XrayVpnService.lastError.collect { err ->
                if (!err.isNullOrBlank()) {
                    if (!XrayVpnService.isServiceRunning.value) {
                        _telemetry.update {
                            it.copy(
                                isConnecting = false,
                                isConnected = false,
                                connectingStage = 1,
                                uploadBytesPerSec = 0L,
                                downloadBytesPerSec = 0L
                            )
                        }
                    }
                    showMessage(err)
                    XrayVpnService.clearLastError()
                }
            }
        }
        viewModelScope.launch {
            XrayVpnService.serviceNotice.collect { notice ->
                if (!notice.isNullOrBlank()) {
                    showMessage(notice)
                    XrayVpnService.clearServiceNotice()
                }
            }
        }
    }

    suspend fun loadServerIpDetails(ip: String): com.hollow.vpn.vpn.ServerIpDetails? =
        fetcher.fetchServerIpDetails(ip)

    fun refreshRealPublicIp() {
        viewModelScope.launch {
            val wasTunnelActive = _telemetry.value.isConnected || _telemetry.value.isConnecting
            val proxyPort = if (_telemetry.value.isConnected && !_settings.value.tunMode) 10808 else null
            val info = fetcher.fetchDevicePublicIpInfo(socksProxyPort = proxyPort)
            _telemetry.update { prev ->
                val savedPreIp = if (!wasTunnelActive && info.ip.isNotBlank() && !info.ip.equals("Detecting…", ignoreCase = true)) {
                    info.ip
                } else {
                    prev.preConnectionIp
                }
                prev.copy(
                    realPublicIpLabel = "${info.ip} · ${info.country}",
                    realPublicCountryCode = info.countryCode,
                    realPublicCity = info.city,
                    realPublicLat = info.latitude,
                    realPublicLon = info.longitude,
                    realIspLabel = info.isp,
                    preConnectionIp = savedPreIp
                )
            }
        }
    }

    /**
     * On startup, loads saved servers or refreshes the user's configured subscription URL quietly
     * in the background without blocking the user behind a splash fetching screen.
     */
    private fun initializeLiveServersOnStartup() {
        viewModelScope.launch {
            val existing = dao.getAllServersOnce()
            val subs = dao.getSubscriptionsOnce()
            val configuredSubUrl = subs.firstOrNull()?.url?.takeIf { it.isNotBlank() }
                ?: _settings.value.activeSubscriptionUrl.trim()

            _screenStack.value = listOf(VpnScreen.CONNECTED_HOME)
            _currentScreen.value = VpnScreen.CONNECTED_HOME

            if (configuredSubUrl.isNotBlank() && (existing.isEmpty() || _settings.value.subAutoUpdate)) {
                runFetcherAutoFetchAndPingSweep(
                    returnScreen = VpnScreen.CONNECTED_HOME,
                    showLoadingScreen = false
                )
            }
        }
    }

    private fun startSubscriptionAutoUpdateLoop() {
        autoUpdateJob?.cancel()
        autoUpdateJob = viewModelScope.launch {
            while (true) {
                delay(60_000L)
                val current = _settings.value
                if (!current.subAutoUpdate) continue
                if (_telemetry.value.isConnecting || _currentScreen.value == VpnScreen.SPLASH_FETCHING) continue

                val intervalMs = parseIntervalMillis(current.updateIntervalLabel)
                val subs = dao.getSubscriptionsOnce()
                val enabledSubs = subs.filter { it.isEnabled && it.url.isNotBlank() }
                val latestSub = enabledSubs.maxByOrNull { it.lastFetchedAt } ?: subs.firstOrNull()
                val lastFetched = latestSub?.lastFetchedAt ?: 0L
                val fallbackUrls = listOfNotNull(current.activeSubscriptionUrl.trim().takeIf { it.isNotBlank() })

                if (lastFetched > 0L && System.currentTimeMillis() - lastFetched >= intervalMs &&
                    (enabledSubs.isNotEmpty() || fallbackUrls.isNotEmpty())
                ) {
                    val result = fetcher.fetchMultipleSubscriptionsAndRankTop15(
                        subscriptions = enabledSubs,
                        fallbackUrls = fallbackUrls,
                        context = getApplication(),
                        settings = current,
                        onSubscriptionStats = { sub, ok, count ->
                            val updatedSub = if (ok) {
                                sub.copy(
                                    successPulls = sub.successPulls + 1,
                                    lastFetchedAt = System.currentTimeMillis(),
                                    serverCount = count
                                )
                            } else {
                                sub.copy(failedPulls = sub.failedPulls + 1)
                            }
                            dao.updateSubscription(updatedSub)
                        }
                    )
                    result.onSuccess { ranked ->
                        if (current.subReplaceExisting) {
                            dao.deleteFetchedServers()
                        }
                        dao.clearSelection()
                        val marked = ranked.mapIndexed { index, srv -> srv.copy(isSelected = index == 0) }
                        dao.insertServers(marked)
                    }
                }
            }
        }
    }

    private fun parseIntervalMillis(label: String): Long {
        val lower = label.lowercase()
        val num = Regex("\\d+").find(lower)?.value?.toLongOrNull() ?: 1L
        return if (lower.contains("min")) {
            num * 60_000L
        } else {
            num * 3_600_000L
        }
    }

    fun navigateTo(screen: VpnScreen) {
        // The full-screen fetching / proxy-testing page is gone: progress is shown as a thin loading bar instead.
        if (screen == VpnScreen.SPLASH_FETCHING) return
        if (_currentScreen.value == screen) return
        _screenStack.update { it + screen }
        _currentScreen.value = screen
    }

    fun hideFetchingScreen() {
        if (_currentScreen.value == VpnScreen.SPLASH_FETCHING) {
            if (!navigateBack()) {
                val target = if (allServers.value.isNotEmpty()) VpnScreen.SERVER_LIST else VpnScreen.CONNECTED_HOME
                _screenStack.value = listOf(target)
                _currentScreen.value = target
            }
            if (_fetchingProgress.value.isActive) {
                showMessage("Fetching & testing in background")
            }
        }
    }

    fun reopenFetchingScreen() {
        if (_fetchingProgress.value.isActive) {
            navigateTo(VpnScreen.SPLASH_FETCHING)
        }
    }

    fun navigateBack(): Boolean {
        val stack = _screenStack.value
        if (stack.size > 1) {
            val newStack = stack.dropLast(1)
            _screenStack.value = newStack
            _currentScreen.value = newStack.last()
            return true
        }
        return false
    }

    fun setCategory(key: String) {
        _selectedCategory.value = key
    }

    /** Opens the server list with the "+" menu (or its subscription / manual surface) ready. */
    fun requestAddServer(request: AddServerRequest = AddServerRequest.MENU) {
        _addServerRequest.value = request
        navigateTo(VpnScreen.SERVER_LIST)
    }

    fun consumeAddServerRequest() {
        _addServerRequest.value = null
    }

    /** Selects a server without starting the VPN. Used while Android shows its consent dialog. */
    fun selectServer(server: ServerEntity, openConnectedScreen: Boolean = true) {
        connectJob?.cancel()
        connectJob = null
        viewModelScope.launch {
            dao.clearSelection()
            dao.markSelected(server.id)
            if (openConnectedScreen) navigateTo(VpnScreen.CONNECTED_HOME)
        }
    }

    fun selectAndConnectServer(server: ServerEntity, openConnectedScreen: Boolean = true) {
        connectJob?.cancel()
        connectJob = viewModelScope.launch {
            dao.clearSelection()
            dao.markSelected(server.id)
            if (openConnectedScreen) {
                navigateTo(VpnScreen.CONNECTED_HOME)
            }
            connectToServer(server)
        }
    }

    fun toggleConnection() {
        if (_telemetry.value.isConnecting) {
            connectJob?.cancel()
            connectJob = null
            _telemetry.update { it.copy(isConnecting = false, isConnected = false, connectingStage = 1) }
            stopAndroidVpnService()
            return
        }
        val currentlyConnected = _telemetry.value.isConnected
        if (currentlyConnected) {
            connectJob?.cancel()
            connectJob = null
            _telemetry.update {
                it.copy(
                    isConnected = false,
                    isConnecting = false,
                    connectingStage = 1
                )
            }
            stopAndroidVpnService()
            val name = selectedServer.value?.countryName ?: "VPN"
            showMessage("Disconnected from $name")
        } else {
            val active = selectedServer.value
            if (active == null) {
                val configuredSub = _settings.value.activeSubscriptionUrl.trim()
                if (configuredSub.isNotBlank()) {
                    showMessage("Fetching servers from your subscription…")
                    runFetcherAutoFetchAndPingSweep(
                        returnScreen = VpnScreen.CONNECTED_HOME,
                        autoConnectAfterFetch = true
                    )
                } else {
                    requestAddServer(AddServerRequest.MENU)
                    showMessage("Add a subscription URL, server link, or QR code first")
                }
                return
            }
            connectJob?.cancel()
            connectJob = viewModelScope.launch {
                connectToServer(active)
            }
        }
    }

    private suspend fun connectToServer(server: ServerEntity) {
        // Never claim Connected if Android OS VPN consent hasn't been granted yet (when TUN Mode is on)
        if (_settings.value.tunMode && !_settings.value.rootMode && android.net.VpnService.prepare(getApplication()) != null) {
            _telemetry.update { it.copy(isConnecting = false, isConnected = false) }
            showMessage("Tap Connect and accept the Android VPN prompt to start the tunnel")
            return
        }

        XrayVpnService.clearLastError()
        _telemetry.update { it.copy(isConnecting = true, connectingStage = 1) }

        val livePing: Int? = when (val probe = fetcher.measureRealXrayDelayOrNull(getApplication(), server, _settings.value)) {
            is XrayProbeResult.Success -> probe.delayMs
            XrayProbeResult.NativeUnavailable -> withContext(Dispatchers.IO) {
                fetcher.measureRealTcpLatencyOrNull(server.host, server.port, timeoutMs = 2200)
            }
            XrayProbeResult.Unreachable -> null
        }

        currentCoroutineContext().ensureActive()
        if (livePing == null) {
            dao.updateServer(server.copy(failCount = server.failCount + 1))
            _telemetry.update { it.copy(isConnecting = false, isConnected = false, connectingStage = 1) }
            showMessage("${server.countryName} (${server.host}:${server.port}) failed Xray proxy verification")
            return
        }

        val resolvedIp = server.resolvedIp.ifBlank { fetcher.resolveServerIpAddress(server.host) }
        val updatedServer = server.copy(
            pingMs = livePing,
            resolvedIp = resolvedIp,
            isSelected = true,
            lastAliveAt = System.currentTimeMillis(),
            failCount = 0
        )
        dao.updateServer(updatedServer)
        currentCoroutineContext().ensureActive()

        _telemetry.update { prev ->
            prev.copy(
                serverIpLabel = resolvedIp,
                connectingStage = 2
            )
        }
        startAndroidVpnService(updatedServer)
        showMessage("Connecting to ${server.countryName} ($livePing ms)…")
    }

    /**
     * Runs the real Fetcher Auto-Fetcher & Concurrent Xray Ping Ranker, saving the Top 15
     * fastest verified servers to the Room database.
     */
    fun runFetcherAutoFetchAndPingSweep(
        returnScreen: VpnScreen = VpnScreen.SERVER_LIST,
        autoConnectAfterFetch: Boolean = false,
        showLoadingScreen: Boolean = false
    ) {
        fetchSweepJob?.cancel()
        fetchSweepJob = viewModelScope.launch {
            try {
                val subs = dao.getSubscriptionsOnce().filter { it.isEnabled && it.url.isNotBlank() }
                val fallbackUrls = listOfNotNull(_settings.value.activeSubscriptionUrl.trim().takeIf { it.isNotBlank() })

                if (subs.isEmpty() && fallbackUrls.isEmpty()) {
                    val existing = dao.getAllServersOnce()
                    if (existing.isNotEmpty()) {
                        if (showLoadingScreen) {
                            _currentScreen.value = returnScreen
                        }
                        repingAllSavedServers()
                    } else if (showLoadingScreen) {
                        requestAddServer(AddServerRequest.MENU)
                        showMessage("Add a subscription URL, server link, or QR code first")
                    }
                    return@launch
                }

                if (showLoadingScreen) {
                    navigateTo(VpnScreen.SPLASH_FETCHING)
                }
                _fetchingStatusMessage.value = "Fetching your subscription…"
                _fetchingProgress.value = FetchingProgressState(
                    isActive = true,
                    stage = FetchingStage.DOWNLOADING,
                    headline = "Fetching & testing live",
                    detail = "Fetching your subscription…"
                )

                val subResult = fetcher.fetchMultipleSubscriptionsAndRankTop15(
                    subscriptions = subs,
                    fallbackUrls = fallbackUrls,
                    context = getApplication(),
                    settings = _settings.value,
                    onProgressState = { progress ->
                        _fetchingProgress.value = progress
                        _fetchingStatusMessage.value = progress.detail
                    },
                    onProgress = { step -> _fetchingStatusMessage.value = step },
                    onSubscriptionStats = { sub, ok, count ->
                        val updatedSub = if (ok) {
                            sub.copy(
                                successPulls = sub.successPulls + 1,
                                lastFetchedAt = System.currentTimeMillis(),
                                serverCount = count
                            )
                        } else {
                            sub.copy(failedPulls = sub.failedPulls + 1)
                        }
                        dao.updateSubscription(updatedSub)
                    }
                )

                subResult.fold(
                    onSuccess = { rankedTop15 ->
                        if (_settings.value.subReplaceExisting) {
                            dao.deleteFetchedServers()
                        }
                        dao.clearSelection()
                        val withSelected = rankedTop15.mapIndexed { idx, srv ->
                            srv.copy(isSelected = idx == 0)
                        }
                        dao.insertServers(withSelected)

                        if (showLoadingScreen && _currentScreen.value == VpnScreen.SPLASH_FETCHING) {
                            _screenStack.value = listOf(VpnScreen.CONNECTED_HOME, returnScreen).distinct()
                            _currentScreen.value = returnScreen
                        }

                        val fastest = withSelected.firstOrNull()
                        if (autoConnectAfterFetch && fastest != null) {
                            val savedFirst = dao.getAllServersOnce().firstOrNull { it.isSelected } ?: fastest
                            connectToServer(savedFirst)
                        } else if (fastest != null) {
                            val ip = fetcher.resolveServerIpAddress(fastest.host)
                            _telemetry.update { it.copy(serverIpLabel = ip) }
                            val countryCount = withSelected.map { it.countryName }.distinct().size
                            showMessage(
                                "Ranked ${withSelected.size} verified servers across $countryCount countries · #1 ${fastest.countryName} (${fastest.pingMs} ms)"
                            )
                        }
                    },
                    onFailure = { error ->
                        val existing = dao.getAllServersOnce()
                        if (existing.isNotEmpty()) {
                            _fetchingStatusMessage.value = "Re-testing saved servers…"
                            val reRanked = fetcher.benchmarkAndRankTop15(
                                servers = existing,
                                context = getApplication(),
                                settings = _settings.value
                            ) { step ->
                                _fetchingStatusMessage.value = step
                            }
                            if (reRanked.isNotEmpty()) {
                                dao.deleteAllServers()
                                dao.insertServers(
                                    reRanked.mapIndexed { index, srv -> srv.copy(isSelected = index == 0) }
                                )
                            }
                            if (showLoadingScreen && _currentScreen.value == VpnScreen.SPLASH_FETCHING) {
                                _currentScreen.value = returnScreen
                            }
                            showMessage("Updated latency for ${reRanked.size} reachable servers")
                        } else {
                            if (showLoadingScreen) {
                                _currentScreen.value = VpnScreen.SERVER_LIST
                                _addServerRequest.value = AddServerRequest.SUBSCRIPTION
                            }
                            showMessage("Fetch failed: ${error.message ?: "Check subscription URL"}")
                        }
                    }
                )
            } finally {
                _fetchingProgress.value = FetchingProgressState(isActive = false, stage = FetchingStage.IDLE)
            }
        }
    }

    /**
     * Adds a user-provided V2Ray Subscription URL (or inline URI/Base64 payload), streams nodes,
     * and tests reachability + Xray handshakes concurrently in a producer-consumer pipeline.
     */
    fun addAndFetchSubscriptionUrl(rawUrlOrLinks: String) {
        val input = rawUrlOrLinks.trim()
            .replaceFirst(Regex("^https?://(?=https?://)", RegexOption.IGNORE_CASE), "")
        if (input.isBlank()) {
            showMessage("Please enter a valid subscription URL or server link")
            return
        }

        if (isDirectServerInput(input)) {
            importFromTextOrClipboard(input)
            return
        }

        updateSettings { it.copy(activeSubscriptionUrl = input) }
        navigateTo(VpnScreen.SPLASH_FETCHING)

        fetchSweepJob?.cancel()
        fetchSweepJob = viewModelScope.launch {
            try {
                _fetchingStatusMessage.value = "Connecting to subscription feed…"
                _fetchingProgress.value = FetchingProgressState(
                    isActive = true,
                    stage = FetchingStage.DOWNLOADING,
                    headline = "Fetching & testing live",
                    detail = "Connecting to subscription feed…"
                )
                dao.deleteSubscriptionByUrl(input)
                dao.insertSubscription(
                    SubscriptionEntity(
                        url = input,
                        autoUpdate = _settings.value.subAutoUpdate,
                        replaceExisting = _settings.value.subReplaceExisting
                    )
                )

                val fetchResult = fetcher.fetchSubscriptionAndRankTop15(
                    subscriptionUrl = input,
                    context = getApplication(),
                    settings = _settings.value,
                    onProgressState = { progress ->
                        _fetchingProgress.value = progress
                        _fetchingStatusMessage.value = progress.detail
                    },
                    onProgress = { status ->
                        _fetchingStatusMessage.value = status
                    }
                )

                fetchResult.fold(
                    onSuccess = { topNodes ->
                        if (_settings.value.subReplaceExisting) {
                            dao.deleteFetchedServers()
                        }
                        dao.clearSelection()
                        val marked = topNodes.mapIndexed { index, srv ->
                            srv.copy(isSelected = index == 0)
                        }
                        dao.insertServers(marked)
                        val fastest = marked.firstOrNull()
                        if (fastest != null) {
                            val ip = fetcher.resolveServerIpAddress(fastest.host)
                            _telemetry.update { it.copy(serverIpLabel = ip) }
                        }
                        if (_currentScreen.value == VpnScreen.SPLASH_FETCHING) {
                            _screenStack.value = listOf(VpnScreen.CONNECTED_HOME, VpnScreen.SERVER_LIST)
                            _currentScreen.value = VpnScreen.SERVER_LIST
                        }
                        val countryCount = marked.map { it.countryName }.distinct().size
                        showMessage(
                            "Saved ${marked.size} verified servers across $countryCount countries · Fastest: ${fastest?.countryName ?: ""} (${fastest?.pingMs ?: 0} ms)"
                        )
                    },
                    onFailure = { err ->
                        _addServerRequest.value = AddServerRequest.SUBSCRIPTION
                        showMessage("Could not fetch servers: ${err.message}")
                    }
                )
            } finally {
                _fetchingProgress.value = FetchingProgressState(isActive = false, stage = FetchingStage.IDLE)
            }
        }
    }

    /**
     * Imports V2Ray/Xray servers from clipboard text and runs real Xray outbound latency checks.
     */
    fun importFromTextOrClipboard(rawText: String) {
        val trimmed = rawText.trim()
        if (trimmed.isBlank()) {
            showMessage("Clipboard is empty — copy a vless://, vmess://, trojan://, ss://, hy2://, or JSON config first")
            return
        }
        viewModelScope.launch {
            val parsed = fetcher.parseImportPayload(trimmed, isCustom = true)
            if (parsed.isEmpty()) {
                showMessage("No valid supported server link or JSON config found")
                return@launch
            }
            val candidatePool = fetcher.selectDiverseCandidatePool(parsed, maxCandidates = 360)
            showMessage("Verifying latency for ${candidatePool.size} imported server(s)…")
            val benchmarked = fetcher.benchmarkAndRankTop15(
                servers = candidatePool,
                context = getApplication(),
                settings = _settings.value
            )
            if (benchmarked.isEmpty()) {
                showMessage("Tested ${candidatePool.size} server(s), but none passed Xray proxy verification")
                return@launch
            }
            dao.clearSelection()
            val toInsert = benchmarked.mapIndexed { idx, server ->
                server.copy(isSelected = idx == 0)
            }
            dao.insertServers(toInsert)
            navigateTo(VpnScreen.SERVER_LIST)
            val best = toInsert.first()
            showMessage("Imported ${toInsert.size} verified server(s) · Fastest: ${best.countryName} (${best.pingMs} ms)")
        }
    }

    /**
     * Validates, verifies via Xray outbound probe, and saves a manual custom V2Ray/Xray server or Raw Xray JSON config.
     */
    fun addManualCustomServer(
        countryName: String,
        host: String,
        port: Int,
        protocol: String,
        transport: String,
        uuidOrPassword: String,
        sni: String,
        rawJsonInput: String,
        publicKey: String = "",
        shortId: String = "",
        wsPath: String = "",
        wsHost: String = "",
        grpcServiceName: String = ""
    ) {
        viewModelScope.launch {
            if (rawJsonInput.trim().startsWith("{")) {
                val parsedFromJson = fetcher.parseRawXrayJson(rawJsonInput.trim(), isCustom = true)
                if (parsedFromJson.isEmpty()) {
                    showMessage("Invalid JSON configuration — check outbounds syntax")
                    return@launch
                }
                val candidate = parsedFromJson.first()
                val verifiedPing = verifyServerLatencyOrNull(candidate)
                if (verifiedPing == null) {
                    showMessage("Server ${candidate.host}:${candidate.port} failed proxy verification")
                    return@launch
                }
                dao.clearSelection()
                val verified = candidate.copy(pingMs = verifiedPing, isSelected = true, isCustom = true)
                dao.insertServer(verified)
                _selectedCategory.value = ServerCategory.DEFAULT_KEY
                navigateTo(VpnScreen.SERVER_LIST)
                showMessage("Added custom server (${verified.countryName} · $verifiedPing ms)")
                return@launch
            }

            val cleanHost = host.trim()
            if (cleanHost.isBlank()) {
                showMessage("Please enter a valid server hostname or IP address")
                return@launch
            }
            if (uuidOrPassword.isBlank()) {
                showMessage("Please enter a UUID or password for the server")
                return@launch
            }

            val normalizedProtocol = if (protocol.contains("Hysteria", ignoreCase = true)) {
                "Hysteria2 (Xray)"
            } else if (protocol.contains("Shadowsocks", ignoreCase = true)) {
                "Shadowsocks (Xray)"
            } else {
                "$protocol (V2Ray)"
            }
            val normalizedTransport = when {
                protocol.contains("Hysteria", ignoreCase = true) -> "Hysteria2"
                transport.equals("WS", ignoreCase = true) -> "WebSocket"
                else -> transport
            }

            val baseServer = ServerEntity(
                countryName = countryName.trim(),
                countryCode = "XX",
                protocolDisplay = "V2Ray · $normalizedTransport",
                protocolCore = normalizedProtocol,
                transport = normalizedTransport,
                host = cleanHost,
                port = port.coerceIn(1, 65535),
                uuidOrPassword = uuidOrPassword.trim(),
                sni = sni.trim(),
                publicKey = publicKey.trim(),
                pingMs = 0,
                isRecommendedTop15 = true,
                rankOrder = 1,
                isSelected = true,
                isCustom = true
            )
            val generatedJson = fetcher.generateXrayJsonForServer(
                server = baseServer,
                wsPath = wsPath.trim(),
                wsHost = wsHost.trim(),
                grpcServiceName = grpcServiceName.trim(),
                shortId = shortId.trim()
            )
            val testableServer = baseServer.copy(rawConfigJson = generatedJson)

            showMessage("Verifying proxy connection to $cleanHost:$port…")
            val verifiedPing = verifyServerLatencyOrNull(testableServer)
            if (verifiedPing == null) {
                showMessage("Host $cleanHost:$port failed Xray proxy handshake")
                return@launch
            }

            val enriched = fetcher.enrichServerWithIpAndCountry(
                testableServer.copy(pingMs = verifiedPing),
                rank = 1
            )
            val finalServer = if (countryName.trim().isNotBlank()) {
                enriched.copy(countryName = countryName.trim(), rawConfigJson = generatedJson, isCustom = true)
            } else {
                enriched.copy(rawConfigJson = generatedJson, isCustom = true)
            }
            dao.clearSelection()
            dao.insertServer(finalServer)
            _selectedCategory.value = ServerCategory.DEFAULT_KEY
            navigateTo(VpnScreen.SERVER_LIST)
            showMessage("Added ${finalServer.countryName} · ${finalServer.resolvedIp} ($verifiedPing ms)")
        }
    }

    fun pingSingleServer(server: ServerEntity) {
        viewModelScope.launch {
            val livePing = verifyServerLatencyOrNull(server)
            if (livePing != null) {
                dao.updateServer(server.copy(pingMs = livePing))
                showMessage("${server.countryName} verified: $livePing ms")
            } else {
                dao.updateServer(server.copy(pingMs = 0))
                showMessage("${server.countryName} (${server.host}:${server.port}) failed proxy handshake")
            }
        }
    }

    fun repingAllSavedServers() {
        if (repingJob?.isActive == true) {
            showMessage("Speed test already in progress…")
            return
        }
        repingJob = viewModelScope.launch {
            _isRepingAll.value = true
            try {
            val existing = dao.getAllServersOnce()
            if (existing.isEmpty()) {
                showMessage("No saved servers to test")
                return@launch
            }
            showMessage("Testing ${existing.size} saved server(s)…")
            val semaphore = Semaphore(8)
            val updated = coroutineScope {
                existing.map { server ->
                    async {
                        semaphore.withPermit {
                            val ping = verifyServerLatencyOrNull(server) ?: 0
                            val updatedEntity = server.copy(pingMs = ping)
                            dao.updateServer(updatedEntity)
                            updatedEntity
                        }
                    }
                }.awaitAll()
            }
            val reachable = updated.filter { it.pingMs > 0 }
            val fastest = reachable.minByOrNull { it.pingMs }
            if (fastest != null) {
                showMessage(
                    "Verified ${reachable.size}/${existing.size} servers · Fastest: ${fastest.countryName} (${fastest.pingMs} ms)"
                )
            } else {
                showMessage("Tested ${existing.size} server(s), none passed proxy check")
            }
            } finally {
                _isRepingAll.value = false
            }
        }
    }

    private suspend fun verifyServerLatencyOrNull(server: ServerEntity): Int? {
        return when (val probe = fetcher.measureRealXrayDelayOrNull(getApplication(), server, _settings.value)) {
            is XrayProbeResult.Success -> probe.delayMs
            XrayProbeResult.NativeUnavailable -> withContext(Dispatchers.IO) {
                fetcher.measureRealTcpLatencyOrNull(server.host, server.port, 1800)
            }
            XrayProbeResult.Unreachable -> null
        }
    }

    fun deleteServer(server: ServerEntity) {
        viewModelScope.launch {
            dao.deleteServerById(server.id)
            showMessage("Removed ${server.countryName}")
        }
    }

    fun deleteServersByIds(ids: Set<Long>) {
        if (ids.isEmpty()) return
        viewModelScope.launch {
            ids.forEach { dao.deleteServerById(it) }
            showMessage("Removed ${ids.size} server(s)")
        }
    }

    fun deleteDuplicateServers() {
        viewModelScope.launch {
            val all = dao.getAllServersOnce()
            val seen = HashSet<String>()
            val duplicates = mutableListOf<ServerEntity>()
            all.sortedByDescending { it.isSelected }
                .forEach { server ->
                    val key = "${server.host.lowercase()}:${server.port}:${server.protocolCore.lowercase()}:${server.uuidOrPassword}"
                    if (!seen.add(key)) {
                        duplicates += server
                    }
                }
            if (duplicates.isEmpty()) {
                showMessage("No duplicate servers found")
            } else {
                duplicates.forEach { dao.deleteServerById(it.id) }
                showMessage("Removed ${duplicates.size} duplicate server(s)")
            }
        }
    }

    fun deleteInvalidServers() {
        viewModelScope.launch {
            val invalid = dao.getAllServersOnce().filter { it.pingMs <= 0 }
            if (invalid.isEmpty()) {
                showMessage("No invalid or unreachable servers found")
            } else {
                invalid.forEach { dao.deleteServerById(it.id) }
                showMessage("Removed ${invalid.size} invalid server(s)")
            }
        }
    }

    fun openQrShare(server: ServerEntity) {
        val raw = fetcher.resolveConfigJson(server)
        _sharingQrServer.value = server.copy(rawConfigJson = raw)
    }

    fun closeQrShare() {
        _sharingQrServer.value = null
    }

    fun buildExportBackupPayload(servers: List<ServerEntity> = allServers.value): String =
        Companion.buildExportBackupPayload(servers, fetcher)

    companion object {
        fun buildExportBackupPayload(
            servers: List<ServerEntity>,
            fetcher: V2RayParserAndFetcher = V2RayParserAndFetcher()
        ): String {
            if (servers.isEmpty()) return ""
            val uris = servers.mapNotNull { it.shareUri.trim().takeIf { u -> u.isNotEmpty() } }
            if (uris.isNotEmpty() && uris.size == servers.size) {
                return uris.joinToString("\n")
            }
            if (uris.isNotEmpty() && servers.none { it.rawConfigJson.isNotBlank() }) {
                return uris.joinToString("\n")
            }
            val outbounds = JSONArray()
            servers.forEach { server ->
                val raw = fetcher.resolveConfigJson(server)
                try {
                    val root = JSONObject(raw)
                    val obs = root.optJSONArray("outbounds")
                    if (obs != null && obs.length() > 0) {
                        val primary = obs.optJSONObject(0)
                        if (primary != null) outbounds.put(primary)
                    }
                } catch (_: Exception) {
                }
            }
            return if (outbounds.length() > 0) {
                JSONObject().put("outbounds", outbounds).toString(2)
            } else {
                uris.joinToString("\n")
            }
        }
    }

    fun clearXrayLogs() {
        XrayVpnService.clearLogs()
        showMessage("Cleared Xray diagnostic logs")
    }

    fun openJsonInspector(server: ServerEntity) {
        val json = fetcher.resolveConfigJson(server)
        _inspectingJsonServer.value = server.copy(rawConfigJson = json)
    }

    fun closeJsonInspector() {
        _inspectingJsonServer.value = null
    }

    fun saveEditedServerJson(server: ServerEntity, updatedJson: String) {
        val validationError = validateXrayConfigJson(updatedJson)
        if (validationError != null) {
            showMessage(validationError)
            return
        }
        viewModelScope.launch {
            dao.updateServer(server.copy(rawConfigJson = updatedJson.trim()))
            _inspectingJsonServer.value = null
            showMessage("Saved config for ${server.countryName}")
        }
    }

    private fun validateXrayConfigJson(rawJson: String): String? {
        return try {
            val root = JSONObject(rawJson.trim())
            val outbounds: JSONArray? = root.optJSONArray("outbounds")
            if (outbounds == null || outbounds.length() == 0) {
                return "Invalid Xray JSON: missing 'outbounds' array"
            }
            val hasProxyOutbound = (0 until outbounds.length()).any { idx ->
                val proto = outbounds.optJSONObject(idx)?.optString("protocol", "").orEmpty().lowercase()
                proto.isNotBlank() && proto !in listOf("freedom", "blackhole", "dns", "direct", "block")
            }
            if (!hasProxyOutbound) {
                return "Invalid Xray JSON: at least one proxy outbound is required"
            }
            null
        } catch (e: Exception) {
            "Invalid JSON syntax: ${e.message ?: "parse error"}"
        }
    }

    private fun isDirectServerInput(input: String): Boolean {
        val lower = input.trim().lowercase()
        return lower.startsWith("vmess://") ||
            lower.startsWith("vless://") ||
            lower.startsWith("trojan://") ||
            lower.startsWith("ss://") ||
            lower.startsWith("hy2://") ||
            lower.startsWith("hysteria2://") ||
            lower.startsWith("wireguard://") ||
            lower.startsWith("socks://") ||
            lower.startsWith("socks5://") ||
            lower.startsWith("{")
    }

    fun updateSettings(transform: (AppSettingsState) -> AppSettingsState) {
        var subAutoUpdateChanged = false
        var perAppChanged = false
        var routingChanged = false
        var latestSettings = _settings.value
        _settings.update { current ->
            val updated = transform(current)
            subAutoUpdateChanged = updated.subAutoUpdate != current.subAutoUpdate
            perAppChanged = updated.perAppProxyEnabled != current.perAppProxyEnabled ||
                updated.bypassSelectedApps != current.bypassSelectedApps ||
                updated.splitExcludeApps != current.splitExcludeApps ||
                updated.splitIncludeApps != current.splitIncludeApps
            routingChanged = updated.netShieldEnabled != current.netShieldEnabled ||
                updated.blockAdultContent != current.blockAdultContent ||
                updated.splitExcludeIps != current.splitExcludeIps ||
                updated.splitIncludeIps != current.splitIncludeIps
            latestSettings = updated
            prefsStore.saveSettings(updated)
            updated
        }
        if (subAutoUpdateChanged) {
            HollowJobScheduler.schedulePeriodicMaintenance(getApplication(), latestSettings)
        }
        if ((perAppChanged || routingChanged) && (_telemetry.value.isConnected || _telemetry.value.isConnecting)) {
            showMessage(
                if (perAppChanged) "Split tunneling updated — reconnect VPN to apply"
                else "Settings updated — reconnect VPN to apply"
            )
        }
    }

    fun clearServerCacheAndReset() {
        connectJob?.cancel()
        connectJob = null
        viewModelScope.launch {
            stopAndroidVpnService()
            prefsStore.clearActiveTunnelSnapshot()
            _telemetry.value = ConnectionTelemetryState()
            dao.deleteAllServers()
            showMessage("Cleared all cached servers")
        }
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun showMessage(msg: String) {
        _snackbarMessage.value = msg
    }

    private fun startAndroidVpnService(server: ServerEntity) {
        try {
            val ctx = getApplication<Application>()
            val currentSettings = _settings.value
            val baseJson = fetcher.resolveConfigJson(server)
            val xrayJson = fetcher.applyRuntimeSettingsToXrayJson(baseJson, currentSettings)
            val intent = XrayVpnService.connectIntent(ctx, server, currentSettings, xrayJson)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                ctx.startForegroundService(intent)
            } else {
                ctx.startService(intent)
            }
        } catch (e: Exception) {
            _telemetry.update { it.copy(isConnecting = false, isConnected = false) }
            showMessage("Could not start VPN service: ${e.message}")
        }
    }

    private fun stopAndroidVpnService() {
        try {
            val ctx = getApplication<Application>()
            val intent = Intent(ctx, XrayVpnService::class.java).apply {
                action = XrayVpnService.ACTION_DISCONNECT
            }
            ctx.startService(intent)
        } catch (_: Exception) {
        }
    }
}
