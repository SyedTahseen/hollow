package com.hollow.vpn.ui.screens

import com.hollow.vpn.ui.theme.HollowMotion
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.animation.fadeOut
import androidx.compose.animation.fadeIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.expandVertically
import androidx.compose.animation.core.tween
import androidx.compose.animation.AnimatedVisibility
import com.hollow.vpn.ui.theme.HollowDialog
import com.hollow.vpn.ui.theme.HollowSansFamily
import com.hollow.vpn.ui.theme.HollowText
import com.hollow.vpn.ui.theme.HollowTracking
import com.hollow.vpn.ui.theme.HollowLineHeight
import com.hollow.vpn.ui.theme.HollowRadius
import com.hollow.vpn.ui.theme.HollowType
import com.hollow.vpn.ui.theme.HollowStroke
import com.hollow.vpn.ui.theme.HollowSpace
import com.hollow.vpn.ui.theme.HollowSizes
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.hollow.vpn.R
import com.hollow.vpn.ui.theme.LocalHollowIsDark
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material.icons.outlined.PublicOff
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hollow.vpn.data.ServerEntity
import com.hollow.vpn.data.ServerCategory
import com.hollow.vpn.data.SubscriptionEntity
import com.hollow.vpn.data.categoryLabel
import androidx.compose.runtime.SideEffect
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.IconButton
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.shape.CircleShape
import com.hollow.vpn.ui.theme.HollowAmber
import java.util.Locale
import com.hollow.vpn.data.favoriteKey
import com.hollow.vpn.ui.components.HollowTopSquareButton
import com.hollow.vpn.ui.components.NothingDottedDivider
import com.hollow.vpn.ui.components.NothingPillTabRow
import com.hollow.vpn.ui.theme.HollowScreenPadding
import com.hollow.vpn.ui.theme.DotoDisplayFamily
import com.hollow.vpn.ui.theme.HollowCream
import com.hollow.vpn.ui.theme.HollowCreamDivider
import com.hollow.vpn.ui.theme.HollowCreamSurface
import com.hollow.vpn.ui.theme.HollowInk
import com.hollow.vpn.ui.theme.HollowInkSecondary
import com.hollow.vpn.ui.theme.HollowOrange
import com.hollow.vpn.ui.theme.NothingBorder
import com.hollow.vpn.ui.theme.NothingCanvas
import com.hollow.vpn.ui.theme.NothingInvertedSurface
import com.hollow.vpn.ui.theme.NothingOnCanvas
import com.hollow.vpn.ui.theme.NothingOnInvertedSurface
import com.hollow.vpn.ui.theme.NothingSurfaceElevated
import com.hollow.vpn.ui.theme.NothingTopBtnBorder
import com.hollow.vpn.ui.theme.NothingTopBtnFill

@Composable
fun CountryServerListScreen(
    servers: List<ServerEntity>,
    categories: List<ServerCategory> = listOf(
        ServerCategory(ServerCategory.ALL_KEY, "All"),
        ServerCategory(ServerCategory.DEFAULT_KEY, "Default")
    ),
    selectedCategoryKey: String = ServerCategory.ALL_KEY,
    bottomContentPadding: Dp = 0.dp,
    onSelectCategory: (String) -> Unit = {},
    favoriteKeys: Set<String> = emptySet(),
    isTestingFastest: Boolean = false,
    isLoading: Boolean = false,
    onToggleFavorite: (ServerEntity) -> Unit = {},
    subscriptions: List<SubscriptionEntity> = emptyList(),
    onDeleteSubscription: (String) -> Unit = {},
    onCopySubscription: (String) -> Unit = {},
    onConnectFastest: (List<ServerEntity>) -> Unit = {},
    onSelectServer: (ServerEntity) -> Unit,
    onRefresh: () -> Unit,
    onOpenSettings: () -> Unit,
    addServerActions: AddServerActions,
    onPingServer: (ServerEntity) -> Unit,
    onRepingAll: () -> Unit = {},
    onShowMessage: (String) -> Unit = {},
    onShareQr: (ServerEntity) -> Unit = {},
    onInspectJson: (ServerEntity) -> Unit,
    onDeleteServer: (ServerEntity) -> Unit,
    onEditServer: (ServerEntity, String) -> String? = { _, _ -> null },
    totalServerCount: Int = servers.size,
    onDeleteServersByIds: (Set<Long>) -> Unit = {},
    onDeleteDuplicates: () -> Unit = {},
    onDeleteInvalid: () -> Unit = {},
    onExportAllServers: () -> Unit = {},
    onDeleteAllServers: () -> Unit = {},
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    var overflowMenuExpanded by remember { mutableStateOf(false) }
    val addServerState = rememberAddServerState()
    var isSelectionMode by remember { mutableStateOf(false) }
    var selectedIds by remember { mutableStateOf(emptySet<Long>()) }
    var sortMode by remember { mutableStateOf(ServerSortMode.Latency) }
    var searching by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    var menuPage by remember { mutableStateOf("main") }
    var confirmDel by remember { mutableStateOf<ServerDelAction?>(null) }
    var deletingSubscription by remember { mutableStateOf<SubscriptionEntity?>(null) }
    var editingServer by remember { mutableStateOf<ServerEntity?>(null) }
    val searchFocus = remember { FocusRequester() }
    LaunchedEffect(searching) { if (searching) runCatching { searchFocus.requestFocus() } }

    val expanded = remember { mutableStateMapOf<String, Boolean>() }

    var favoritesOnly by remember { mutableStateOf(false) }
    val hasFavorites = favoriteKeys.isNotEmpty()
    val favOnly = favoritesOnly && hasFavorites

    val displayedServers = remember(servers, sortMode, favoriteKeys, favOnly, query) {
        val pool = (if (favOnly) servers.filter { it.favoriteKey() in favoriteKeys } else servers)
            .filter { query.isBlank() || it.countryName.contains(query, true) || it.host.contains(query, true) }
        when (sortMode) {
            ServerSortMode.Default -> pool
            ServerSortMode.Name -> pool.sortedBy { it.countryName.lowercase() }
            ServerSortMode.Protocol -> pool.sortedBy { it.protocolCore }
            ServerSortMode.Latency -> pool.sortedBy { if (it.pingMs > 0) it.pingMs else Int.MAX_VALUE }
        }
    }

    val selectedTabIndex = categories.indexOfFirst { it.key == selectedCategoryKey }.let { if (it >= 0) it else 0 }
    val activeCategory = categories.getOrNull(selectedTabIndex)

    AddServerOverlays(state = addServerState, actions = addServerActions)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NothingCanvas)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = HollowSizes.MaxContent)
                .fillMaxSize()
                .align(Alignment.TopCenter)
                .padding(horizontal = HollowScreenPadding)
        ) {
            if (searching) {
                Row(
                    modifier = Modifier.fillMaxWidth().heightIn(min = HollowSizes.TopBar),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicTextField(
                        value = query,
                        onValueChange = { query = it },
                        singleLine = true,
                        textStyle = HollowText.Body.copy(color = NothingOnCanvas),
                        cursorBrush = SolidColor(HollowOrange),
                        modifier = Modifier.weight(1f).focusRequester(searchFocus).testTag("server_search_field"),
                        decorationBox = { inner ->
                            Box {
                                if (query.isEmpty()) {
                                    Text("Search servers", style = HollowText.Body, color = NothingOnCanvas.copy(alpha = 0.5f))
                                }
                                inner()
                            }
                        }
                    )
                    HollowTopSquareButton(
                        containerColor = NothingTopBtnFill,
                        contentColor = NothingOnCanvas,
                        borderColor = NothingTopBtnBorder,
                        contentDescription = "Close search",
                        onClick = { searching = false; query = "" },
                        modifier = Modifier.testTag("server_search_close")
                    ) { tint ->
                        Icon(Icons.Rounded.Close, null, tint = tint, modifier = Modifier.size(HollowSizes.Icon))
                    }
                }
            } else Row(
                modifier = Modifier.fillMaxWidth().heightIn(min = HollowSizes.TopBar),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "SERVERS",
                        style = HollowText.Headline,
                        maxLines = 1,
                        color = NothingOnCanvas
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(HollowSpace.Xs),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HollowTopSquareButton(
                        containerColor = NothingTopBtnFill,
                        contentColor = NothingOnCanvas,
                        borderColor = NothingTopBtnBorder,
                        contentDescription = "Search servers",
                        onClick = { searching = true },
                        modifier = Modifier.testTag("server_search")
                    ) { tint ->
                        Icon(Icons.Rounded.Search, null, tint = tint, modifier = Modifier.size(HollowSizes.Icon))
                    }

                    AddServerButton(state = addServerState, actions = addServerActions)

                    Box {
                        HollowTopSquareButton(
                            containerColor = if (overflowMenuExpanded) NothingInvertedSurface else NothingTopBtnFill,
                            contentColor = if (overflowMenuExpanded) NothingOnInvertedSurface else NothingOnCanvas,
                            borderColor = if (overflowMenuExpanded) NothingInvertedSurface else NothingTopBtnBorder,
                            contentDescription = "More server actions",
                            onClick = { menuPage = "main"; overflowMenuExpanded = true },
                            modifier = Modifier.testTag("country_more_menu")
                        ) { tint ->
                            Icon(
                                imageVector = Icons.Rounded.MoreHoriz,
                                contentDescription = null,
                                tint = tint,
                                modifier = Modifier.size(HollowSizes.Icon)
                            )
                        }

                        DropdownMenu(
                            expanded = overflowMenuExpanded,
                            onDismissRequest = { overflowMenuExpanded = false },
                            offset = DpOffset(x = 0.dp, y = 8.dp),
                            containerColor = NothingSurfaceElevated,
                            shape = RoundedCornerShape(HollowRadius.Lg),
                            modifier = Modifier.widthIn(min = 220.dp)
                        ) {
                            when (menuPage) {
                                "sort" -> {
                                    ServerMenuHeader("Sort") { menuPage = "main" }
                                    ServerSortMode.values().forEach { m ->
                                        ServerMenuChoice(m.label, m == sortMode) {
                                            sortMode = m
                                            overflowMenuExpanded = false
                                        }
                                    }
                                }
                                "delete" -> {
                                    ServerMenuHeader("Delete") { menuPage = "main" }
                                    val invalidCount = servers.count { it.pingMs <= 0 }
                                    val dupeCount = servers.size - servers
                                        .distinctBy { "${it.host.lowercase()}:${it.port}:${it.protocolCore.lowercase()}:${it.uuidOrPassword}" }
                                        .size
                                    ServerOverflowMenuItem(
                                        icon = Icons.Outlined.DeleteOutline,
                                        label = "Invalid / unreachable",
                                        color = HollowOrange,
                                        enabled = invalidCount > 0,
                                        trailing = { ServerMenuCount(invalidCount) },
                                        onClick = {
                                            overflowMenuExpanded = false
                                            confirmDel = ServerDelAction(
                                                "Delete $invalidCount invalid server(s)?",
                                                "These servers failed the last test or can't be turned into a working configuration.",
                                                onDeleteInvalid
                                            )
                                        }
                                    )
                                    ServerOverflowMenuItem(
                                        icon = Icons.Outlined.DeleteOutline,
                                        label = "Duplicates",
                                        color = HollowOrange,
                                        enabled = dupeCount > 0,
                                        trailing = { ServerMenuCount(dupeCount) },
                                        onClick = {
                                            overflowMenuExpanded = false
                                            confirmDel = ServerDelAction(
                                                "Delete $dupeCount duplicate(s)?",
                                                "One copy of each server is kept. The selected server is kept first.",
                                                onDeleteDuplicates
                                            )
                                        }
                                    )
                                    ServerOverflowMenuItem(
                                        icon = Icons.Outlined.DeleteOutline,
                                        label = "All servers",
                                        color = HollowOrange,
                                        onClick = {
                                            overflowMenuExpanded = false
                                            confirmDel = ServerDelAction(
                                                "Delete all servers?",
                                                "All ${servers.size} server(s) will be removed from your device. You can re-fetch from your subscription anytime.",
                                                onDeleteAllServers
                                            )
                                        }
                                    )
                                }
                                else -> {
                                    ServerOverflowMenuItem(
                                        icon = Icons.Rounded.Bolt,
                                        label = if (isTestingFastest) "Testing…" else "Test latency",
                                        color = HollowInk,
                                        onClick = {
                                            overflowMenuExpanded = false
                                            if (!isTestingFastest) onRepingAll()
                                        }
                                    )
                                    ServerOverflowMenuItem(
                                        icon = Icons.Rounded.Tune,
                                        label = "Sort",
                                        color = HollowInk,
                                        trailing = { ServerMenuChevron() },
                                        onClick = { menuPage = "sort" }
                                    )
                                    ServerOverflowMenuItem(
                                        icon = Icons.Rounded.Refresh,
                                        label = "Update subscription",
                                        color = HollowInk,
                                        onClick = {
                                            overflowMenuExpanded = false
                                            onRefresh()
                                        }
                                    )
                                    ServerOverflowMenuItem(
                                        icon = Icons.Outlined.DeleteOutline,
                                        label = "Delete",
                                        color = HollowOrange,
                                        trailing = { ServerMenuChevron() },
                                        onClick = { menuPage = "delete" }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(HollowSpace.Lg))

            val favTabKey = "__favorites__"
            val tabEntries = remember(categories, hasFavorites) {
                val base = categories.map { it.key to it.label }
                if (hasFavorites) base.toMutableList().apply { add(minOf(1, size), favTabKey to "Favorites") } else base
            }
            val pillIndex = if (favOnly) tabEntries.indexOfFirst { it.first == favTabKey }.coerceAtLeast(0)
            else tabEntries.indexOfFirst { it.first == selectedCategoryKey }.coerceAtLeast(0)
            NothingPillTabRow(
                tabs = tabEntries.map { it.second },
                selectedIndex = pillIndex,
                onSelectTab = { idx ->
                    val key = tabEntries.getOrNull(idx)?.first
                    if (key == favTabKey) {
                        favoritesOnly = true
                        if (selectedCategoryKey != ServerCategory.ALL_KEY) onSelectCategory(ServerCategory.ALL_KEY)
                    } else if (key != null) {
                        favoritesOnly = false
                        onSelectCategory(key)
                    }
                },
                tabTestTags = tabEntries.map { if (it.first == favTabKey) "server_category_favorites" else "server_category_${it.first.hashCode()}" },
                onLongPressTab = { idx ->
                    val key = tabEntries.getOrNull(idx)?.first
                    deletingSubscription = subscriptions.firstOrNull { it.url.trim() == key }
                }
            )

            AnimatedVisibility(
                visible = isLoading,
                enter = fadeIn(tween(HollowMotion.Standard)) + expandVertically(tween(HollowMotion.Standard)),
                exit = fadeOut(tween(HollowMotion.Fast)) + shrinkVertically(tween(HollowMotion.Fast))
            ) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth().padding(top = HollowSpace.Sm).height(HollowSizes.Spinner),
                    color = HollowOrange,
                    trackColor = NothingBorder
                )
            }

            if (isSelectionMode) {
                Spacer(modifier = Modifier.height(HollowSpace.Md))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(HollowRadius.Md),
                    color = NothingSurfaceElevated,
                    border = BorderStroke(HollowStroke.Hairline, NothingBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = HollowSpace.Lg, vertical = HollowSpace.Md),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${selectedIds.size} selected",
                            style = MaterialTheme.typography.titleSmall,
                            color = HollowInk
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(HollowSpace.Sm)) {
                            TextButton(
                                onClick = {
                                    isSelectionMode = false
                                    selectedIds = emptySet()
                                }
                            ) {
                                Text("Cancel", color = HollowInkSecondary)
                            }
                            Button(
                                onClick = {
                                    if (selectedIds.isNotEmpty()) {
                                        onDeleteServersByIds(selectedIds)
                                    }
                                    isSelectionMode = false
                                    selectedIds = emptySet()
                                },
                                enabled = selectedIds.isNotEmpty(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = HollowOrange,
                                    contentColor = Color.White
                                )
                            ) {
                                Text("Delete")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(HollowSpace.Lg))

            val isDefaultCategory = selectedCategoryKey == ServerCategory.DEFAULT_KEY

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .testTag("server_lazy_column"),
                contentPadding = PaddingValues(bottom = HollowSpace.Xxl + bottomContentPadding)
            ) {
                if (displayedServers.isEmpty()) {
                    item(key = "empty_state") {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = HollowSpace.Md),
                            shape = RoundedCornerShape(HollowRadius.Lg),
                            color = HollowCreamSurface,
                            border = BorderStroke(HollowStroke.Hairline, HollowCreamDivider)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(HollowSpace.Xxl),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(HollowSpace.Md)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.PublicOff,
                                    contentDescription = null,
                                    tint = HollowInkSecondary,
                                    modifier = Modifier.size(HollowSizes.IconLg)
                                )
                                Text(
                                    text = if (isDefaultCategory) "No default servers" else "No matching servers",
                                    style = HollowText.BodyMedium,
                                    color = HollowInk
                                )
                                val emptyMsg = when {
                                    query.isNotBlank() -> "No servers match."
                                    isDefaultCategory ->
                                        "Manually added servers, pasted config keys, and scanned QR nodes will show up here."
                                    else ->
                                        "Import a subscription link, server config, or QR code to populate servers."
                                }
                                Text(
                                    text = emptyMsg,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = HollowInkSecondary,
                                    textAlign = TextAlign.Center
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(HollowSpace.Md)) {
                                    Button(
                                        onClick = {
                                            if (isDefaultCategory) addServerState.showManual = true
                                            else addServerState.menuOpen = true
                                        },
                                        shape = RoundedCornerShape(HollowRadius.Md),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = HollowInk,
                                            contentColor = HollowCream
                                        ),
                                        modifier = Modifier.testTag("empty_add_server_button")
                                    ) {
                                        Text(if (isDefaultCategory) "Add custom server" else "Add server")
                                    }
                                }
                            }
                        }
                    }
                } else {
                    item(key = "list_header") {
                        Text(
                            text = "All servers (${displayedServers.size})",
                            style = HollowText.Small,
                            color = HollowInkSecondary,
                            modifier = Modifier.padding(bottom = HollowSpace.Sm)
                        )
                    }
                    if (query.isBlank() && !favOnly && selectedCategoryKey == ServerCategory.ALL_KEY) {
                        item(key = "fastest_server") {
                            FastestServerRow(
                                isTesting = isTestingFastest,
                                onClick = { onConnectFastest(displayedServers) }
                            )
                            NothingDottedDivider()
                        }
                    }
                    itemsIndexed(displayedServers, key = { _, server -> server.id }) { idx, server ->
                        val isChecked = server.id in selectedIds
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (isSelectionMode) {
                                            selectedIds = if (isChecked) {
                                                selectedIds - server.id
                                            } else {
                                                selectedIds + server.id
                                            }
                                        } else {
                                            onSelectServer(server)
                                        }
                                    }
                                    .testTag("server_row_${server.id}")
                            ) {
                                ServerItemRowContent(
                                    server = server,
                                    showLightningBadge = server.isSelected,
                                    onSelect = { onSelectServer(server) },
                                    onPing = { onPingServer(server) },
                                    onInspectJson = { onInspectJson(server) },
                                    onDelete = { onDeleteServer(server) },
                                    onCopyFeedback = onShowMessage,
                                    onShareQr = { onShareQr(server) },
                                    onEdit = { editingServer = server },
                                    isSelectionMode = isSelectionMode,
                                    isCheckedForBatch = isChecked,
                                    onToggleBatchCheck = {
                                        selectedIds = if (isChecked) {
                                            selectedIds - server.id
                                        } else {
                                            selectedIds + server.id
                                        }
                                    },
                                    showCountryBadge = true,
                                    isFavorite = server.favoriteKey() in favoriteKeys,
                                    onToggleFavorite = { onToggleFavorite(server) }
                                )
                            }
                            if (idx < displayedServers.lastIndex) {
                                NothingDottedDivider()
                            }
                        }
                    }
                }
            }

            val activeSub = if (favOnly) null else subscriptions.firstOrNull { it.url.trim() == selectedCategoryKey }
            val lastSub = remember { mutableStateOf<SubscriptionEntity?>(null) }
            SideEffect { if (activeSub != null) lastSub.value = activeSub }
            AnimatedVisibility(
                modifier = Modifier.padding(bottom = bottomContentPadding),
                visible = activeSub != null,
                enter = fadeIn(tween(HollowMotion.Standard)) + expandVertically(tween(HollowMotion.Standard)),
                exit = fadeOut(tween(HollowMotion.Fast)) + shrinkVertically(tween(HollowMotion.Fast))
            ) {
                (activeSub ?: lastSub.value)?.let { sub ->
                    SubscriptionInfoBar(sub = sub, onCopy = { onCopySubscription(sub.url.trim()) })
                }
            }
        }
    }

    editingServer?.let { srv ->
        EditServerDialog(
            server = srv,
            onDismiss = { editingServer = null },
            onSave = { newLink -> onEditServer(srv, newLink).also { err -> if (err == null) editingServer = null } }
        )
    }

    deletingSubscription?.let { sub ->
        AlertDialog(
            onDismissRequest = { deletingSubscription = null },
            containerColor = HollowDialog.containerColor,
            shape = HollowDialog.Shape,
            title = { Text(text = "Delete subscription?", style = HollowText.Heading, color = HollowInk) },
            text = {
                Text(
                    text = "${sub.categoryLabel()} and its servers will be removed.",
                    style = HollowText.Small,
                    color = HollowInk
                )
            },
            confirmButton = {
                TextButton(onClick = { onDeleteSubscription(sub.url.trim()); deletingSubscription = null }) {
                    Text("Delete", style = HollowText.ButtonSmall, color = HollowOrange)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingSubscription = null }) {
                    Text("Cancel", style = HollowText.ButtonSmall, color = HollowInk)
                }
            }
        )
    }

    confirmDel?.let { d ->
        AlertDialog(
            onDismissRequest = { confirmDel = null },
            containerColor = HollowDialog.containerColor,
            shape = HollowDialog.Shape,
            title = { Text(text = d.title, style = HollowText.Heading, color = HollowInk) },
            text = { Text(text = d.text, style = HollowText.Small, color = HollowInkSecondary) },
            confirmButton = {
                Button(
                    onClick = { confirmDel = null; d.onConfirm() },
                    colors = ButtonDefaults.buttonColors(containerColor = HollowOrange, contentColor = Color.White)
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { confirmDel = null }) { Text("Cancel", color = HollowInk) }
            }
        )
    }
}

enum class ServerSortMode(val label: String) {
    Default("Default"), Name("Name"), Latency("Latency"), Protocol("Protocol")
}

private class ServerDelAction(val title: String, val text: String, val onConfirm: () -> Unit)

@Composable
private fun ServerMenuChevron() {
    Icon(Icons.Rounded.ChevronRight, null, tint = HollowInkSecondary, modifier = Modifier.size(HollowSizes.Icon))
}

@Composable
private fun ServerMenuCount(n: Int) {
    Text("$n", style = HollowText.Small, color = HollowInkSecondary)
}

@Composable
private fun ServerMenuHeader(title: String, onBack: () -> Unit) {
    DropdownMenuItem(
        text = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(HollowSpace.Md)
            ) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, null, tint = HollowInk, modifier = Modifier.size(HollowSizes.IconSm))
                Text(title, style = HollowText.BodyMedium, color = HollowInk)
            }
        },
        onClick = onBack
    )
}

@Composable
private fun ServerMenuChoice(label: String, selected: Boolean, onClick: () -> Unit) {
    DropdownMenuItem(
        text = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(HollowSpace.Md)
            ) {
                Text(label, style = HollowText.Body, color = if (selected) HollowOrange else HollowInk, modifier = Modifier.weight(1f, fill = false))
                if (selected) Icon(Icons.Rounded.Check, null, tint = HollowOrange, modifier = Modifier.size(HollowSizes.IconSm))
            }
        },
        onClick = onClick
    )
}

@Composable
private fun FastestServerRow(
    isTesting: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = HollowSizes.ServerRow)
            .clickable(enabled = !isTesting, onClick = onClick)
            .padding(start = 0.dp, end = 0.dp, top = HollowSpace.Md, bottom = HollowSpace.Md)
            .testTag("server_fastest_row"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(width = 38.dp, height = 26.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(if (LocalHollowIsDark.current) R.drawable.flag_fastest_dark else R.drawable.flag_fastest),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.width(HollowSpace.Lg))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Fastest server", style = HollowText.Body, color = HollowInk, maxLines = 1)
            if (isTesting) {
                Text(text = "Testing servers…", style = HollowText.Caption, color = HollowInkSecondary, maxLines = 1)
            }
        }
    }
}

@Composable
private fun ServerOverflowMenuItem(
    icon: ImageVector,
    label: String,
    color: Color,
    enabled: Boolean = true,
    trailing: (@Composable () -> Unit)? = null,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        enabled = enabled,
        text = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(HollowSpace.Md),
                modifier = Modifier.padding(vertical = HollowSpace.Xxs)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(HollowSizes.IconSm)
                )
                Text(
                    text = label,
                    style = HollowText.Body,
                    color = color,
                    modifier = if (trailing != null) Modifier.weight(1f) else Modifier
                )
                trailing?.invoke()
            }
        },
        onClick = onClick
    )
}

private fun fmtBinarySize(bytes: Long): String {
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    var v = bytes.toDouble()
    var i = 0
    while (v >= 1024 && i < units.lastIndex) { v /= 1024; i++ }
    return if (i == 0) "$bytes B" else String.format(Locale.US, if (v >= 100) "%.0f %s" else "%.1f %s", v, units[i])
}

@Composable
private fun SubscriptionInfoBar(sub: SubscriptionEntity, onCopy: () -> Unit) {
    val now = System.currentTimeMillis()
    val hasExpiry = sub.expireAt > 0L
    val left = sub.expireAt * 1000L - now
    val days = (left + 86_399_999L) / 86_400_000L
    val expired = hasExpiry && left <= 0L
    val exhausted = sub.totalBytes > 0L && sub.usedBytes >= sub.totalBytes
    val expText = when {
        !hasExpiry -> null
        expired -> "Expired"
        left < 86_400_000L -> "${maxOf(1L, left / 3_600_000L)}h left"
        else -> "$days days left"
    }
    val expColor = when {
        expired -> HollowOrange
        days <= 7 -> HollowAmber
        else -> HollowInkSecondary
    }

    Surface(
        modifier = Modifier.fillMaxWidth().padding(top = HollowSpace.Sm),
        shape = RoundedCornerShape(HollowRadius.Md),
        color = NothingSurfaceElevated,
        border = BorderStroke(HollowStroke.Hairline, NothingBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = HollowSpace.Lg, end = HollowSpace.Xs, top = HollowSpace.Sm, bottom = HollowSpace.Sm)
                .testTag("subscription_info_bar"),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = sub.categoryLabel(),
                        style = HollowText.Small,
                        color = HollowInk,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (expText != null) {
                        Spacer(Modifier.width(HollowSpace.Sm))
                        Text(text = expText, style = HollowText.SmallMedium, color = expColor, maxLines = 1)
                    }
                }
                if (sub.totalBytes > 0L || sub.usedBytes > 0L) {
                    Text(
                        text = if (sub.totalBytes > 0L) {
                            "${fmtBinarySize(sub.usedBytes)} of ${fmtBinarySize(sub.totalBytes)} used"
                        } else {
                            "${fmtBinarySize(sub.usedBytes)} used · Unlimited"
                        },
                        style = HollowText.Caption,
                        color = if (exhausted) HollowOrange else HollowInkSecondary,
                        maxLines = 1
                    )
                    if (sub.totalBytes > 0L) {
                        val frac = (sub.usedBytes.toFloat() / sub.totalBytes).coerceIn(0f, 1f)
                        Spacer(Modifier.height(HollowSpace.Xs))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(HollowSpace.Xs)
                                .clip(CircleShape)
                                .background(NothingBorder)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(frac)
                                    .height(HollowSpace.Xs)
                                    .background(if (exhausted) HollowOrange else if (frac > 0.9f) HollowAmber else HollowInk)
                            )
                        }
                    }
                }
            }
            IconButton(onClick = onCopy) {
                Icon(
                    imageVector = Icons.Outlined.ContentCopy,
                    contentDescription = "Copy subscription link",
                    tint = HollowInkSecondary,
                    modifier = Modifier.size(HollowSizes.IconSm)
                )
            }
        }
    }
}
