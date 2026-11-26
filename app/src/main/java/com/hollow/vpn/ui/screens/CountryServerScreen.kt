package com.hollow.vpn.ui.screens

import com.hollow.vpn.ui.theme.HollowDialog
import com.hollow.vpn.ui.theme.HollowTracking
import com.hollow.vpn.ui.theme.HollowLineHeight
import com.hollow.vpn.ui.theme.HollowRadius
import com.hollow.vpn.ui.theme.HollowType
import com.hollow.vpn.ui.theme.HollowStroke
import com.hollow.vpn.ui.theme.HollowSpace
import com.hollow.vpn.ui.theme.HollowSizes
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.MoreVert
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
    onToggleFavorite: (ServerEntity) -> Unit = {},
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
    var sortByPingResults by remember { mutableStateOf(true) }
    var showDeleteAllConfirm by remember { mutableStateOf(false) }

    val expanded = remember { mutableStateMapOf<String, Boolean>() }

    // "Favorites" pill: only offered once something is starred; shows starred servers from every category.
    var favoritesOnly by remember { mutableStateOf(false) }
    val hasFavorites = favoriteKeys.isNotEmpty()
    val favOnly = favoritesOnly && hasFavorites

    val displayedServers = remember(servers, sortByPingResults, favoriteKeys, favOnly) {
        val pool = if (favOnly) servers.filter { it.favoriteKey() in favoriteKeys } else servers
        val base = if (sortByPingResults) {
            pool.sortedBy { if (it.pingMs > 0) it.pingMs else Int.MAX_VALUE }
        } else {
            pool
        }
        // Starred servers float to the top, keeping the current order inside each group.
        base.sortedByDescending { it.favoriteKey() in favoriteKeys }
    }

    val selectedTabIndex = categories.indexOfFirst { it.key == selectedCategoryKey }.let { if (it >= 0) it else 0 }
    val activeCategory = categories.getOrNull(selectedTabIndex)

    AddServerOverlays(state = addServerState, actions = addServerActions)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NothingCanvas)
            .statusBarsPadding()
            .padding(bottom = bottomContentPadding)
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = HollowSizes.MaxContent)
                .fillMaxSize()
                .align(Alignment.TopCenter)
                .padding(horizontal = HollowScreenPadding)
        ) {
            // Nothing OS Header: Doto ALL-CAPS SERVERS + monospace count on left, circular hairline action buttons on right
            Row(
                modifier = Modifier.fillMaxWidth().heightIn(min = HollowSizes.TopBar),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "SERVERS",
                        fontFamily = DotoDisplayFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = HollowType.Display,
                        lineHeight = HollowLineHeight.Display,
                        letterSpacing = HollowTracking.Display,
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
                        contentDescription = "Test all saved servers",
                        onClick = onRepingAll,
                        modifier = Modifier.testTag("country_reping_all")
                    ) { tint ->
                        Icon(
                            imageVector = Icons.Outlined.Speed,
                            contentDescription = null,
                            tint = tint,
                            modifier = Modifier.size(HollowSizes.Icon)
                        )
                    }

                    // "+" opens the add menu: Scan QR, Paste, Import file, Add manually, Add subscription
                    AddServerButton(state = addServerState, actions = addServerActions)

                    Box {
                        HollowTopSquareButton(
                            containerColor = if (overflowMenuExpanded) NothingInvertedSurface else NothingTopBtnFill,
                            contentColor = if (overflowMenuExpanded) NothingOnInvertedSurface else NothingOnCanvas,
                            borderColor = if (overflowMenuExpanded) NothingInvertedSurface else NothingTopBtnBorder,
                            contentDescription = "More server actions",
                            onClick = { overflowMenuExpanded = true },
                            modifier = Modifier.testTag("country_more_menu")
                        ) { tint ->
                            Icon(
                                imageVector = Icons.Rounded.MoreVert,
                                contentDescription = null,
                                tint = tint,
                                modifier = Modifier.size(HollowSizes.Icon)
                            )
                        }

                        // Page 7: Nothing Surface Overflow Action Menu
                        DropdownMenu(
                            expanded = overflowMenuExpanded,
                            onDismissRequest = { overflowMenuExpanded = false },
                            offset = DpOffset(x = 0.dp, y = 8.dp),
                            containerColor = NothingSurfaceElevated,
                            shape = RoundedCornerShape(HollowRadius.Lg),
                            modifier = Modifier.width(212.dp)
                        ) {
                            ServerOverflowMenuItem(
                                icon = Icons.Outlined.Schedule,
                                label = "Update all",
                                color = HollowInk,
                                onClick = {
                                    overflowMenuExpanded = false
                                    onRefresh()
                                }
                            )
                            ServerOverflowMenuItem(
                                icon = Icons.Outlined.CheckCircle,
                                label = "Select several",
                                color = HollowInk,
                                onClick = {
                                    overflowMenuExpanded = false
                                    isSelectionMode = true
                                    selectedIds = emptySet()
                                }
                            )
                            ServerOverflowMenuItem(
                                icon = Icons.Rounded.Tune,
                                label = "Sort by test results",
                                color = HollowInk,
                                onClick = {
                                    overflowMenuExpanded = false
                                    sortByPingResults = true
                                    onRepingAll()
                                }
                            )
                            HorizontalDivider(
                                thickness = 1.dp,
                                color = NothingBorder,
                                modifier = Modifier.padding(vertical = HollowSpace.Xs)
                            )
                            ServerOverflowMenuItem(
                                icon = Icons.Outlined.DeleteOutline,
                                label = "Delete duplicates",
                                color = HollowOrange,
                                onClick = {
                                    overflowMenuExpanded = false
                                    onDeleteDuplicates()
                                }
                            )
                            ServerOverflowMenuItem(
                                icon = Icons.Outlined.DeleteOutline,
                                label = "Delete invalid",
                                color = HollowOrange,
                                onClick = {
                                    overflowMenuExpanded = false
                                    onDeleteInvalid()
                                }
                            )
                            ServerOverflowMenuItem(
                                icon = Icons.Outlined.FileUpload,
                                label = "Export all",
                                color = HollowInk,
                                onClick = {
                                    overflowMenuExpanded = false
                                    onExportAllServers()
                                }
                            )
                            ServerOverflowMenuItem(
                                icon = Icons.Outlined.DeleteOutline,
                                label = "Delete all servers",
                                color = HollowOrange,
                                onClick = {
                                    overflowMenuExpanded = false
                                    showDeleteAllConfirm = true
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(HollowSpace.Lg))

            // Category pills: All, Default (manually added), then one per subscription
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
                        // Starred servers can live in any category, so look across all of them.
                        if (selectedCategoryKey != ServerCategory.ALL_KEY) onSelectCategory(ServerCategory.ALL_KEY)
                    } else if (key != null) {
                        favoritesOnly = false
                        onSelectCategory(key)
                    }
                },
                tabTestTags = tabEntries.map { if (it.first == favTabKey) "server_category_favorites" else "server_category_${it.first.hashCode()}" },
                fontSize = HollowType.Caption
            )


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
            val listTitle = when {
                favOnly -> "Favorite servers"
                selectedCategoryKey == ServerCategory.ALL_KEY || activeCategory == null -> "All servers"
                isDefaultCategory -> "Default servers"
                else -> "${activeCategory.label} servers"
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .testTag("server_lazy_column"),
                contentPadding = PaddingValues(bottom = HollowSpace.Xxl)
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
                                    fontFamily = DotoDisplayFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = HollowType.Heading,
                                    lineHeight = HollowLineHeight.Heading,
                                    letterSpacing = HollowTracking.Heading,
                                    color = HollowInk
                                )
                                val emptyMsg = when {
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
                            text = "$listTitle (${displayedServers.size})",
                            fontFamily = DotoDisplayFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = HollowType.Caption,
                            lineHeight = HollowLineHeight.Caption,
                            letterSpacing = HollowTracking.Caption,
                            color = HollowInkSecondary,
                            modifier = Modifier.padding(bottom = HollowSpace.Sm)
                        )
                    }
                    item(key = "fastest_server") {
                        FastestServerRow(
                            isTesting = isTestingFastest,
                            onClick = { onConnectFastest(displayedServers) }
                        )
                        NothingDottedDivider()
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
        }
    }

    if (showDeleteAllConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteAllConfirm = false },
            containerColor = HollowDialog.containerColor,
            shape = HollowDialog.Shape,
            title = {
                Text(
                    text = "Delete all servers?",
                    fontFamily = DotoDisplayFamily,
                    fontWeight = FontWeight.Bold,
                    color = HollowInk
                )
            },
            text = {
                Text(
                    text = "This removes all saved servers from your device. You can re-fetch from your subscription anytime.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = HollowInkSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteAllConfirm = false
                        onDeleteAllServers()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HollowOrange,
                        contentColor = Color.White
                    )
                ) {
                    Text("Delete all")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAllConfirm = false }) {
                    Text("Cancel", color = HollowInk)
                }
            }
        )
    }
}

/** "Fastest server" entry: tests every listed server, then connects to the quickest one. */
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
            modifier = Modifier
                .size(width = HollowSizes.FlagWidth, height = HollowSizes.FlagHeight)
                .clip(RoundedCornerShape(HollowRadius.Xs))
                .background(HollowOrange),
            contentAlignment = Alignment.Center
        ) {
            if (isTesting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(HollowSizes.IconSm),
                    strokeWidth = 2.dp,
                    color = Color.White
                )
            } else {
                Icon(
                    imageVector = Icons.Rounded.Bolt,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(HollowSizes.IconSm)
                )
            }
        }
        Spacer(modifier = Modifier.width(HollowSpace.Lg))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Fastest server",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = HollowType.Title,
                    lineHeight = HollowLineHeight.Title,
                    letterSpacing = HollowTracking.Title,
                    fontWeight = FontWeight.Medium
                ),
                color = HollowInk
            )
            if (isTesting) {
                Text(
                    text = "Testing all servers…",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = HollowType.Small),
                    color = HollowInkSecondary
                )
            }
        }
    }
}

@Composable
private fun ServerOverflowMenuItem(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    DropdownMenuItem(
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
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = HollowType.BodyStrong,
                        lineHeight = HollowLineHeight.BodyStrong,
                        letterSpacing = HollowTracking.BodyStrong,
                        fontWeight = FontWeight.Medium
                    ),
                    color = color
                )
            }
        },
        onClick = onClick
    )
}
