package com.hollow.vpn.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.hollow.vpn.data.LinkEditor
import com.hollow.vpn.data.ServerEntity
import com.hollow.vpn.ui.theme.HollowCreamDivider
import com.hollow.vpn.ui.theme.HollowCreamSurface
import com.hollow.vpn.ui.theme.HollowDialog
import com.hollow.vpn.ui.theme.HollowInk
import com.hollow.vpn.ui.theme.HollowInkMuted
import com.hollow.vpn.ui.theme.HollowInkSecondary
import com.hollow.vpn.ui.theme.HollowOrange
import com.hollow.vpn.ui.theme.HollowRadius
import com.hollow.vpn.ui.theme.HollowSansFamily
import com.hollow.vpn.ui.theme.HollowSpace
import com.hollow.vpn.ui.theme.HollowText

/**
 * "Edit server" window. Same fields as Outrun: name, server, port, the credential field(s) for the
 * protocol, and an editable list of link parameters. [onSave] receives the rebuilt share link and
 * returns an error message to show in the window, or null once it has been saved.
 */
@Composable
fun EditServerDialog(
    server: ServerEntity,
    onDismiss: () -> Unit,
    onSave: (newLink: String) -> String?
) {
    val fromSubscription = server.subscriptionUrl.isNotBlank()
    val model = remember(server.id, server.shareUri) { LinkEditor.parse(server.shareUri) }
    if (model == null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            containerColor = HollowDialog.containerColor,
            shape = HollowDialog.Shape,
            title = { Text("Can't edit this server", style = HollowText.Heading, color = HollowInk) },
            text = {
                Text(
                    "The editor can't read this link. Delete it and add it again.",
                    style = HollowText.Small,
                    color = HollowInk
                )
            },
            confirmButton = {
                TextButton(onClick = onDismiss) { Text("Close", style = HollowText.ButtonSmall, color = HollowInk) }
            }
        )
        return
    }

    val labels = LinkEditor.labels(model.protocol)
    var name by remember { mutableStateOf(model.name) }
    var host by remember { mutableStateOf(model.host) }
    var port by remember { mutableStateOf(model.port) }
    var user by remember { mutableStateOf(model.user) }
    var pass by remember { mutableStateOf(model.pass) }
    val params = remember { mutableStateListOf<Pair<String, String>>().apply { addAll(model.params) } }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = HollowDialog.containerColor,
        shape = HollowDialog.Shape,
        title = { Text("Edit server", style = HollowText.Heading, color = HollowInk) },
        text = {
            Column(Modifier.heightIn(max = 460.dp).verticalScroll(rememberScrollState())) {
                Text(
                    LinkEditor.describeProtocol(model.protocol),
                    style = HollowText.Small,
                    color = HollowInkSecondary,
                    modifier = Modifier.padding(bottom = HollowSpace.Md)
                )
                LabeledField("Name", name) { name = it; error = null }
                LabeledField("Server", host) { host = it; error = null }
                LabeledField(
                    "Port", port,
                    keyboard = if (model.protocol == "hysteria2") KeyboardOptions.Default
                    else KeyboardOptions(keyboardType = KeyboardType.Number)
                ) { port = it; error = null }
                LabeledField(labels.user, user) { user = it; error = null }
                labels.pass?.let { label -> LabeledField(label, pass) { v -> pass = v; error = null } }

                Text(
                    LinkEditor.paramsTitle(model.protocol),
                    style = HollowText.SmallMedium,
                    color = HollowInk,
                    modifier = Modifier.padding(top = HollowSpace.Sm, bottom = HollowSpace.Sm)
                )
                params.forEachIndexed { i, (k, v) ->
                    Row(
                        modifier = Modifier.padding(bottom = HollowSpace.Sm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HollowField(
                            value = k,
                            onValueChange = { params[i] = it to v; error = null },
                            modifier = Modifier.weight(0.42f),
                            placeholder = "name"
                        )
                        Spacer(Modifier.width(HollowSpace.Sm))
                        HollowField(
                            value = v,
                            onValueChange = { params[i] = k to it; error = null },
                            modifier = Modifier.weight(0.58f),
                            placeholder = "value"
                        )
                        IconButton(onClick = { params.removeAt(i); error = null }) {
                            Icon(Icons.Outlined.DeleteOutline, contentDescription = "Remove", tint = HollowInkSecondary)
                        }
                    }
                }
                TextButton(onClick = { params += ("" to "") }) {
                    Text("Add parameter", style = HollowText.ButtonSmall, color = HollowOrange)
                }

                if (fromSubscription) {
                    Text(
                        "This server comes from a subscription. Once edited it is no longer updated by it, and the next subscription update adds the original again.",
                        style = HollowText.Caption,
                        color = HollowInkSecondary,
                        modifier = Modifier.padding(top = HollowSpace.Md)
                    )
                }
                error?.let {
                    Text(
                        it,
                        style = HollowText.Caption,
                        color = HollowOrange,
                        modifier = Modifier.padding(top = HollowSpace.Md)
                    )
                }
                Spacer(Modifier.height(HollowSpace.Xs))
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val (link, err) = LinkEditor.finish(
                    model.copy(
                        name = name, host = host, port = port, user = user, pass = pass,
                        params = params.toList()
                    )
                )
                error = err ?: link?.let { onSave(it) }
            }) { Text("Save", style = HollowText.ButtonSmall, color = HollowOrange) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", style = HollowText.ButtonSmall, color = HollowInk) }
        }
    )
}

@Composable
private fun LabeledField(
    label: String,
    value: String,
    keyboard: KeyboardOptions = KeyboardOptions.Default,
    onChange: (String) -> Unit
) {
    Text(
        label,
        style = HollowText.Caption,
        color = HollowInkSecondary,
        modifier = Modifier.padding(bottom = HollowSpace.Xs)
    )
    HollowField(value = value, onValueChange = onChange, keyboardOptions = keyboard)
    Spacer(Modifier.height(HollowSpace.Md))
}

/** Single-line text input in Hollow's card style. Shared by the edit window and the IP address page. */
@Composable
internal fun HollowField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    placeholder: String? = null,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    trailingIcon: (@Composable () -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = placeholder?.let { p ->
            { Text(p, color = HollowInkMuted, fontFamily = HollowSansFamily) }
        },
        isError = isError,
        singleLine = true,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        trailingIcon = trailingIcon,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = HollowCreamSurface,
            unfocusedContainerColor = HollowCreamSurface,
            focusedBorderColor = HollowInk,
            unfocusedBorderColor = HollowCreamDivider,
            focusedTextColor = HollowInk,
            unfocusedTextColor = HollowInk,
            errorBorderColor = HollowOrange,
            errorContainerColor = HollowCreamSurface
        ),
        shape = RoundedCornerShape(HollowRadius.Md)
    )
}
