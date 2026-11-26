package com.hollow.vpn

import com.hollow.vpn.ui.theme.HollowRadius
import com.hollow.vpn.ui.theme.HollowType
import com.hollow.vpn.ui.theme.HollowMotion
import com.hollow.vpn.ui.theme.HollowStroke
import com.hollow.vpn.ui.theme.HollowSpace
import com.hollow.vpn.ui.theme.HollowTransitions
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.VpnService
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Dns
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hollow.vpn.data.ServerEntity
import com.hollow.vpn.ui.VpnScreen
import com.hollow.vpn.ui.VpnViewModel
import com.hollow.vpn.ui.components.HollowBlinkingDot
import com.hollow.vpn.ui.components.HollowBottomNavigationBar
import com.hollow.vpn.ui.components.HollowBottomTab
import com.hollow.vpn.ui.components.ServerQrShareDialog
import com.hollow.vpn.ui.screens.AddServerActions
import com.hollow.vpn.ui.screens.ConnectedHomeScreen
import com.hollow.vpn.ui.screens.CountryServerListScreen
import com.hollow.vpn.ui.screens.RawXrayJsonEditorDialog
import com.hollow.vpn.ui.screens.SettingsScreen
import com.hollow.vpn.ui.screens.SplashFetchingScreen
import com.hollow.vpn.ui.theme.HollowAmber
import com.hollow.vpn.ui.theme.HollowCream
import com.hollow.vpn.ui.theme.HollowCreamDivider
import com.hollow.vpn.ui.theme.HollowDarkInk
import com.hollow.vpn.ui.theme.HollowForest
import com.hollow.vpn.ui.theme.HollowForestDivider
import com.hollow.vpn.ui.theme.HollowForestMuted
import com.hollow.vpn.ui.theme.HollowInk
import com.hollow.vpn.ui.theme.HollowInkSecondary
import com.hollow.vpn.ui.theme.HollowLime
import com.hollow.vpn.ui.theme.HollowObsidian
import com.hollow.vpn.ui.theme.HollowObsidianDivider
import com.hollow.vpn.ui.theme.HollowObsidianMuted
import com.hollow.vpn.ui.theme.HollowOrange
import com.hollow.vpn.ui.theme.HollowVpnTheme
import com.hollow.vpn.ui.theme.JetBrainsMonoFamily
import com.hollow.vpn.ui.theme.NothingBorder
import com.hollow.vpn.ui.theme.NothingMatrixGreen
import com.hollow.vpn.ui.theme.NothingMotion
import com.hollow.vpn.ui.theme.NothingSurfaceElevated
import com.hollow.vpn.ui.theme.detectOledDisplay
import com.hollow.vpn.vpn.XrayVpnService

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HollowVpnApp()
        }
    }
}

@Composable
fun HollowVpnApp(
    vpnViewModel: VpnViewModel = viewModel()
) {
    val context = LocalContext.current
    val view = LocalView.current
    val currentScreen by vpnViewModel.currentScreen.collectAsStateWithLifecycle()
    val allServers by vpnViewModel.allServers.collectAsStateWithLifecycle()
    val filteredServers by vpnViewModel.filteredServers.collectAsStateWithLifecycle()
    val favoriteKeys by vpnViewModel.favoriteKeys.collectAsStateWithLifecycle()
    val isTestingFastest by vpnViewModel.isTestingFastest.collectAsStateWithLifecycle()
    val serverCategories by vpnViewModel.serverCategories.collectAsStateWithLifecycle()
    val selectedCategory by vpnViewModel.selectedCategory.collectAsStateWithLifecycle()
    val selectedServer by vpnViewModel.selectedServer.collectAsStateWithLifecycle()
    val telemetry by vpnViewModel.telemetry.collectAsStateWithLifecycle()
    val settings by vpnViewModel.settings.collectAsStateWithLifecycle()
    val fetchingStatus by vpnViewModel.fetchingStatusMessage.collectAsStateWithLifecycle()
    val fetchingProgress by vpnViewModel.fetchingProgress.collectAsStateWithLifecycle()
    val snackbarMessage by vpnViewModel.snackbarMessage.collectAsStateWithLifecycle()
    val addServerRequest by vpnViewModel.addServerRequest.collectAsStateWithLifecycle()
    val addServerActions = AddServerActions(
        settings = settings,
        request = addServerRequest,
        onConsumeRequest = vpnViewModel::consumeAddServerRequest,
        onUpdateSettings = vpnViewModel::updateSettings,
        onAddSubscription = vpnViewModel::addAndFetchSubscriptionUrl,
        onImportText = vpnViewModel::importFromTextOrClipboard,
        onAddManualServer = vpnViewModel::addManualCustomServer,
        onShowMessage = vpnViewModel::showMessage
    )
    val inspectingJsonServer by vpnViewModel.inspectingJsonServer.collectAsStateWithLifecycle()
    val sharingQrServer by vpnViewModel.sharingQrServer.collectAsStateWithLifecycle()
    val xrayLogs by XrayVpnService.logs.collectAsStateWithLifecycle()

    val systemDark = isSystemInDarkTheme()
    val isDarkTheme = when (settings.themeMode.lowercase()) {
        "light" -> false
        "system" -> systemDark
        else -> true
    }
    val isOledHardware = remember(context) { detectOledDisplay(context) }

    val snackbarHostState = remember { SnackbarHostState() }

    val isDarkSurfaceNow = isDarkTheme ||
        (currentScreen == VpnScreen.CONNECTED_HOME && (telemetry.isConnecting || telemetry.isConnected))

    SideEffect {
        val window = (view.context as? Activity)?.window ?: return@SideEffect
        val insetsController = WindowCompat.getInsetsController(window, view)
        insetsController.isAppearanceLightStatusBars = !isDarkSurfaceNow
        insetsController.isAppearanceLightNavigationBars = !isDarkSurfaceNow
    }

    HollowVpnTheme(
        darkTheme = isDarkTheme,
        oledDark = isOledHardware,
        displayScalePercent = settings.displayScalePercent
    ) {

    LaunchedEffect(snackbarMessage) {
        val msg = snackbarMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(msg)
        vpnViewModel.clearSnackbar()
    }

    val vpnPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            vpnViewModel.toggleConnection()
        } else {
            vpnViewModel.showMessage("VPN permission was denied")
        }
    }

    var pendingNotificationAction by remember { mutableStateOf<(() -> Unit)?>(null) }
    var notificationPromptedThisSession by remember { mutableStateOf(false) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ ->
        pendingNotificationAction?.invoke()
        pendingNotificationAction = null
    }

    val runWithNotificationPermission: (() -> Unit) -> Unit = { action ->
        val shouldRequest = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            !notificationPromptedThisSession &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        if (shouldRequest) {
            notificationPromptedThisSession = true
            pendingNotificationAction = action
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            action()
        }
    }

    val startOrToggleVpnConsent = {
        val prepareIntent = try {
            if (settings.tunMode && !settings.rootMode && !telemetry.isConnected && !telemetry.isConnecting) {
                VpnService.prepare(context)
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
        if (prepareIntent != null && context is Activity) {
            vpnPermissionLauncher.launch(prepareIntent)
        } else {
            vpnViewModel.toggleConnection()
        }
    }

    val connectToServerWithPermission: (ServerEntity) -> Unit = { server ->
        runWithNotificationPermission {
            val prepareIntent = try {
                if (settings.tunMode && !settings.rootMode) VpnService.prepare(context) else null
            } catch (_: Exception) {
                null
            }
            if (prepareIntent != null && context is Activity) {
                vpnViewModel.selectServer(server, openConnectedScreen = true)
                vpnPermissionLauncher.launch(prepareIntent)
            } else {
                vpnViewModel.selectAndConnectServer(
                    server = server,
                    openConnectedScreen = true
                )
            }
        }
    }

    val requestOrToggleVpn = {
        if (telemetry.isConnected || telemetry.isConnecting) {
            vpnViewModel.toggleConnection()
        } else {
            runWithNotificationPermission(startOrToggleVpnConsent)
        }
    }

    val exportServersToClipboard: () -> Unit = {
        val servers = allServers
        val payload = vpnViewModel.buildExportBackupPayload(servers)
        if (payload.isBlank()) {
            vpnViewModel.showMessage("No saved servers to export")
        } else {
            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
            cm?.setPrimaryClip(android.content.ClipData.newPlainText("Hollow VPN Servers", payload))
            vpnViewModel.showMessage("Exported ${servers.size} server(s) to clipboard")
        }
    }

    // Floating Nothing OS capsule navigation dock shows on primary top-level tabs only 
    val showBottomNav = currentScreen == VpnScreen.CONNECTED_HOME ||
        currentScreen == VpnScreen.SERVER_LIST ||
        currentScreen == VpnScreen.SETTINGS

    val selectedBottomTab = when (currentScreen) {
        VpnScreen.CONNECTED_HOME -> HollowBottomTab.CONNECT
        VpnScreen.SERVER_LIST -> HollowBottomTab.SERVERS
        VpnScreen.SETTINGS -> HollowBottomTab.SETTINGS
        VpnScreen.SPLASH_FETCHING -> HollowBottomTab.CONNECT
    }

    val navBackgroundColor = when {
        currentScreen == VpnScreen.CONNECTED_HOME && telemetry.isConnecting -> HollowObsidian
        currentScreen == VpnScreen.CONNECTED_HOME && telemetry.isConnected -> HollowForest
        else -> HollowCream
    }
    val navBorderColor = when {
        currentScreen == VpnScreen.CONNECTED_HOME && telemetry.isConnecting -> HollowObsidianDivider
        currentScreen == VpnScreen.CONNECTED_HOME && telemetry.isConnected -> HollowForestDivider
        else -> HollowCreamDivider
    }
    val navIndicatorColor = when {
        currentScreen == VpnScreen.CONNECTED_HOME && telemetry.isConnecting -> HollowAmber
        currentScreen == VpnScreen.CONNECTED_HOME && telemetry.isConnected -> HollowLime
        else -> HollowOrange
    }
    val navActiveTextColor = when {
        currentScreen == VpnScreen.CONNECTED_HOME && (telemetry.isConnecting || telemetry.isConnected) -> HollowDarkInk
        else -> HollowInk
    }
    val navInactiveTextColor = when {
        currentScreen == VpnScreen.CONNECTED_HOME && telemetry.isConnecting -> HollowObsidianMuted
        currentScreen == VpnScreen.CONNECTED_HOME && telemetry.isConnected -> HollowForestMuted
        else -> HollowInkSecondary
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useNavigationRail = showBottomNav && maxWidth >= 600.dp

        Row(modifier = Modifier.fillMaxSize()) {
            if (useNavigationRail) {
                NavigationRail(
                    containerColor = navBackgroundColor,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Spacer(Modifier.weight(1f))
                    val railColors = NavigationRailItemDefaults.colors(
                        selectedIconColor = navActiveTextColor,
                        selectedTextColor = navActiveTextColor,
                        unselectedIconColor = navInactiveTextColor,
                        unselectedTextColor = navInactiveTextColor,
                        indicatorColor = navIndicatorColor.copy(alpha = 0.22f)
                    )
                    NavigationRailItem(
                        selected = selectedBottomTab == HollowBottomTab.CONNECT,
                        onClick = { vpnViewModel.navigateTo(VpnScreen.CONNECTED_HOME) },
                        icon = { Icon(Icons.Rounded.PowerSettingsNew, contentDescription = "Connect") },
                        label = { Text("Connect") },
                        colors = railColors
                    )
                    NavigationRailItem(
                        selected = selectedBottomTab == HollowBottomTab.SERVERS,
                        onClick = { vpnViewModel.navigateTo(VpnScreen.SERVER_LIST) },
                        icon = { Icon(Icons.Outlined.Dns, contentDescription = "Servers") },
                        label = { Text("Servers") },
                        colors = railColors
                    )
                    NavigationRailItem(
                        selected = selectedBottomTab == HollowBottomTab.SETTINGS,
                        onClick = { vpnViewModel.navigateTo(VpnScreen.SETTINGS) },
                        icon = { Icon(Icons.Outlined.Settings, contentDescription = "Settings") },
                        label = { Text("Settings") },
                        colors = railColors
                    )
                    Spacer(Modifier.weight(1f))
                }
            }

            Scaffold(
                modifier = Modifier.weight(1f),
                containerColor = Color.Transparent,
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState) { data ->
                        Snackbar(
                            snackbarData = data,
                            shape = RoundedCornerShape(HollowRadius.Md),
                            containerColor = NothingSurfaceElevated,
                            contentColor = HollowInk,
                            actionColor = HollowOrange
                        )
                    }
                },
                bottomBar = {
                    val showFetchingPill = fetchingProgress.isActive &&
                        currentScreen != VpnScreen.SPLASH_FETCHING
                    val renderNavBar = showBottomNav && !useNavigationRail
                    if (showFetchingPill || renderNavBar) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AnimatedVisibility(
                                visible = showFetchingPill,
                                enter = fadeIn() + slideInVertically { it / 2 },
                                exit = fadeOut() + slideOutVertically { it / 2 }
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(HollowRadius.Pill),
                                    color = NothingSurfaceElevated,
                                    border = BorderStroke(HollowStroke.Hairline, NothingBorder),
                                    shadowElevation = 6.dp,
                                    modifier = Modifier
                                        .padding(
                                            start = HollowSpace.Lg,
                                            end = HollowSpace.Lg,
                                            top = HollowSpace.Xs,
                                            bottom = if (renderNavBar) HollowSpace.Sm else HollowSpace.Md
                                        )
                                        .then(if (!renderNavBar) Modifier.navigationBarsPadding() else Modifier)
                                        .minimumInteractiveComponentSize()
                                        .clip(RoundedCornerShape(HollowRadius.Pill))
                                        .clickable(role = Role.Button) {
                                            vpnViewModel.reopenFetchingScreen()
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(start = HollowSpace.Lg, end = HollowSpace.Sm, top = HollowSpace.Sm, bottom = HollowSpace.Sm),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(HollowSpace.Md)
                                    ) {
                                        HollowBlinkingDot(
                                            color = if (fetchingProgress.workingCount > 0) NothingMatrixGreen else HollowOrange
                                        )
                                        Text(
                                            text = "Fetching · ${fetchingProgress.workingCount} ready",
                                            fontFamily = JetBrainsMonoFamily,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = HollowType.Small,
                                            color = HollowInk
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(HollowRadius.Pill),
                                            color = HollowOrange
                                        ) {
                                            Text(
                                                text = "SHOW",
                                                fontFamily = JetBrainsMonoFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = HollowType.Micro,
                                                letterSpacing = 0.8.sp,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = HollowSpace.Sm, vertical = HollowSpace.Xs)
                                            )
                                        }
                                    }
                                }
                            }

                            AnimatedVisibility(
                                visible = renderNavBar,
                                enter = slideInVertically(
                                    animationSpec = tween(HollowMotion.Standard, easing = NothingMotion.FluidEase)
                                ) { it } + fadeIn(tween(HollowMotion.Fast)),
                                exit = slideOutVertically(
                                    animationSpec = tween(HollowMotion.Standard, easing = NothingMotion.FluidEase)
                                ) { it } + fadeOut(tween(HollowMotion.Fast))
                            ) {
                                HollowBottomNavigationBar(
                                    selectedTab = selectedBottomTab,
                                    backgroundColor = navBackgroundColor,
                                    borderColor = navBorderColor,
                                    activeIndicatorColor = navIndicatorColor,
                                    activeContentColor = navActiveTextColor,
                                    inactiveContentColor = navInactiveTextColor,
                                    onSelectConnect = { vpnViewModel.navigateTo(VpnScreen.CONNECTED_HOME) },
                                    onSelectServers = { vpnViewModel.navigateTo(VpnScreen.SERVER_LIST) },
                                    onSelectSettings = { vpnViewModel.navigateTo(VpnScreen.SETTINGS) },
                                    showBackdrop = currentScreen != VpnScreen.CONNECTED_HOME
                                )
                            }
                        }
                    }
                }
            ) { innerPadding ->
                Box(modifier = Modifier.fillMaxSize()) {
                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = {
                            val isModalFlow = targetState == VpnScreen.SPLASH_FETCHING ||
                                initialState == VpnScreen.SPLASH_FETCHING
                            val isPush = targetState == VpnScreen.SETTINGS ||
                                (targetState == VpnScreen.SERVER_LIST && initialState == VpnScreen.CONNECTED_HOME)
                            val isPop = initialState == VpnScreen.SETTINGS ||
                                (targetState == VpnScreen.CONNECTED_HOME && initialState == VpnScreen.SERVER_LIST)

                            when {
                                isModalFlow -> HollowTransitions.Modal
                                isPush && !isPop -> HollowTransitions.Push
                                isPop && !isPush -> HollowTransitions.Pop
                                else -> HollowTransitions.Fade
                            }
                        },
                        label = "vpn_screen_motion"
                    ) { screen ->
                        when (screen) {
                            VpnScreen.SPLASH_FETCHING -> {
                                SplashFetchingScreen(
                                    statusSubtitle = fetchingStatus,
                                    progress = fetchingProgress,
                                    onCancelOrSkip = vpnViewModel::hideFetchingScreen
                                )
                            }

                            VpnScreen.SERVER_LIST -> {
                                CountryServerListScreen(
                                    servers = filteredServers,
                                    categories = serverCategories,
                                    selectedCategoryKey = selectedCategory,
                                    bottomContentPadding = innerPadding.calculateBottomPadding(),
                                    onSelectCategory = vpnViewModel::setCategory,
                                    favoriteKeys = favoriteKeys,
                                    isTestingFastest = isTestingFastest,
                                    onToggleFavorite = vpnViewModel::toggleFavorite,
                                    onConnectFastest = { candidates ->
                                        vpnViewModel.testAndPickFastest(candidates) { server ->
                                            connectToServerWithPermission(server)
                                        }
                                    },
                                    onSelectServer = { server -> connectToServerWithPermission(server) },
                                    onRefresh = {
                                        vpnViewModel.runFetcherAutoFetchAndPingSweep(
                                            returnScreen = VpnScreen.SERVER_LIST
                                        )
                                    },
                                    onOpenSettings = {
                                        vpnViewModel.navigateTo(VpnScreen.SETTINGS)
                                    },
                                    addServerActions = addServerActions,
                                    onPingServer = vpnViewModel::pingSingleServer,
                                    onRepingAll = vpnViewModel::repingAllSavedServers,
                                    onShowMessage = vpnViewModel::showMessage,
                                    onShareQr = vpnViewModel::openQrShare,
                                    onInspectJson = vpnViewModel::openJsonInspector,
                                    onDeleteServer = vpnViewModel::deleteServer,
                                    onDeleteServersByIds = vpnViewModel::deleteServersByIds,
                                    onDeleteDuplicates = vpnViewModel::deleteDuplicateServers,
                                    onDeleteInvalid = vpnViewModel::deleteInvalidServers,
                                    onExportAllServers = exportServersToClipboard,
                                    onDeleteAllServers = vpnViewModel::clearServerCacheAndReset,
                                    onBack = {
                                        if (!vpnViewModel.navigateBack()) {
                                            vpnViewModel.navigateTo(VpnScreen.CONNECTED_HOME)
                                        }
                                    }
                                )
                            }

                            VpnScreen.CONNECTED_HOME -> {
                                ConnectedHomeScreen(
                                    selectedServer = selectedServer,
                                    telemetry = telemetry,
                                    tunModeEnabled = settings.tunMode,
                                    bottomContentPadding = innerPadding.calculateBottomPadding(),
                                    onToggleConnect = requestOrToggleVpn,
                                    onOpenServerList = {
                                        vpnViewModel.navigateTo(VpnScreen.SERVER_LIST)
                                    },
                                    onOpenSettings = {
                                        vpnViewModel.navigateTo(VpnScreen.SETTINGS)
                                    },
                                    addServerActions = addServerActions,
                                    onRefreshRealIp = vpnViewModel::refreshRealPublicIp,
                                    onLoadServerDetails = vpnViewModel::loadServerIpDetails
                                )
                            }

                            VpnScreen.SETTINGS -> {
                                SettingsScreen(
                                    settings = settings,
                                    xrayLogs = xrayLogs,
                                    serverCount = allServers.size,
                                    bottomContentPadding = innerPadding.calculateBottomPadding(),
                                    onUpdateSettings = vpnViewModel::updateSettings,
                                    onClearXrayLogs = vpnViewModel::clearXrayLogs,
                                    onShowMessage = vpnViewModel::showMessage,
                                    onBack = {
                                        if (!vpnViewModel.navigateBack()) {
                                            vpnViewModel.navigateTo(VpnScreen.CONNECTED_HOME)
                                        }
                                    }
                                )
                            }
                        }
                    }

                    inspectingJsonServer?.let { server ->
                        RawXrayJsonEditorDialog(
                            server = server,
                            onDismiss = vpnViewModel::closeJsonInspector,
                            onSaveJson = { updatedJson ->
                                vpnViewModel.saveEditedServerJson(server, updatedJson)
                            }
                        )
                    }

                    sharingQrServer?.let { server ->
                        ServerQrShareDialog(
                            server = server,
                            onCopyPayload = { payload ->
                                if (payload.isNotBlank()) {
                                    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
                                    cm?.setPrimaryClip(android.content.ClipData.newPlainText("Hollow VPN Server", payload))
                                    vpnViewModel.showMessage("Copied server config for ${server.countryName}")
                                    vpnViewModel.closeQrShare()
                                }
                            },
                            onDismiss = vpnViewModel::closeQrShare
                        )
                    }
                }
            }
        }
    }
    }
}
