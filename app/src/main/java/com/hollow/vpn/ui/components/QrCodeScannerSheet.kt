package com.hollow.vpn.ui.components

import com.hollow.vpn.ui.theme.HollowDialog
import com.hollow.vpn.ui.theme.HollowSizes
import com.hollow.vpn.ui.theme.HollowRadius
import com.hollow.vpn.ui.theme.HollowSpace
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.camera.core.ImageProxy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.FilterQuality
import com.hollow.vpn.data.ServerEntity
import com.hollow.vpn.ui.theme.DotoDisplayFamily
import com.hollow.vpn.ui.theme.HollowInk
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeWriter

private val qrReaderHints = mapOf(
    DecodeHintType.POSSIBLE_FORMATS to listOf(BarcodeFormat.QR_CODE),
    DecodeHintType.TRY_HARDER to true
)

internal fun decodeQrFromImageProxy(imageProxy: ImageProxy): String? {
    return try {
        val plane = imageProxy.planes.firstOrNull() ?: return null
        val buffer = plane.buffer
        val data = ByteArray(buffer.remaining())
        buffer.get(data)
        val width = imageProxy.width
        val height = imageProxy.height
        val rowStride = plane.rowStride

        val yData = if (rowStride == width) {
            data
        } else {
            ByteArray(width * height).also { compact ->
                for (row in 0 until height) {
                    val srcOffset = row * rowStride
                    val len = minOf(width, data.size - srcOffset)
                    if (len > 0) {
                        System.arraycopy(data, srcOffset, compact, row * width, len)
                    }
                }
            }
        }

        val source = PlanarYUVLuminanceSource(
            yData, width, height, 0, 0, width, height, false
        )
        val bitmap = BinaryBitmap(HybridBinarizer(source))
        MultiFormatReader().apply { setHints(qrReaderHints) }.decodeWithState(bitmap).text
    } catch (_: Exception) {
        null
    }
}

fun decodeQrFromUri(context: Context, uri: Uri): String? {
    return try {
        val boundsOptions = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream, null, boundsOptions)
        }
        var sampleSize = 1
        val maxDim = maxOf(boundsOptions.outWidth, boundsOptions.outHeight)
        while (maxDim / sampleSize > 1200) {
            sampleSize *= 2
        }
        val decodeOptions = BitmapFactory.Options().apply { inSampleSize = sampleSize }
        val bitmap = context.contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream, null, decodeOptions)
        } ?: return null

        val result = decodeQrFromBitmap(bitmap)
        if (!bitmap.isRecycled) bitmap.recycle()
        result
    } catch (_: Exception) {
        null
    }
}

fun decodeQrFromBitmap(bitmap: Bitmap): String? {
    return try {
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        val source = RGBLuminanceSource(width, height, pixels)
        val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
        MultiFormatReader().apply { setHints(qrReaderHints) }.decodeWithState(binaryBitmap).text
    } catch (_: Exception) {
        null
    }
}

fun generateQrBitmap(payload: String, sizePx: Int = 512): Bitmap? {
    val clean = payload.trim()
    if (clean.isEmpty() || clean.length > 2900) return null
    return try {
        val hints = mapOf(
            EncodeHintType.CHARACTER_SET to "UTF-8",
            EncodeHintType.MARGIN to 1
        )
        val matrix = QRCodeWriter().encode(clean, BarcodeFormat.QR_CODE, sizePx, sizePx, hints)
        val width = matrix.width
        val height = matrix.height
        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (matrix.get(x, y)) android.graphics.Color.BLACK else android.graphics.Color.WHITE
            }
        }
        Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
            setPixels(pixels, 0, width, 0, 0, width, height)
        }
    } catch (_: Exception) {
        null
    }
}

@Composable
fun ServerQrShareDialog(
    server: ServerEntity,
    onCopyPayload: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    val sharePayload = remember(server) {
        server.shareUri.ifBlank { server.rawConfigJson }
    }
    val qrBitmap = remember(sharePayload) {
        generateQrBitmap(sharePayload, sizePx = 560)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = HollowDialog.containerColor,
        shape = HollowDialog.Shape,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(HollowSpace.Md)
            ) {
                com.hollow.vpn.ui.components.CountryFlag(countryCode = server.countryCode)
                Column {
                    Text(
                        text = server.countryName,
                        style = com.hollow.vpn.ui.theme.HollowText.Heading,
                        color = HollowInk
                    )
                    Text(
                        text = "${server.protocolCore} · ${server.resolvedIp.ifBlank { server.host }}:${server.port}",
                        style = MaterialTheme.typography.bodySmall,
                        color = scheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(HollowSpace.Md)
            ) {
                if (qrBitmap != null) {
                    Surface(
                        shape = RoundedCornerShape(HollowRadius.Lg),
                        color = Color.White,
                        tonalElevation = 0.dp
                    ) {
                        Image(
                            bitmap = qrBitmap.asImageBitmap(),
                            contentDescription = "QR code for ${server.countryName}",
                            filterQuality = FilterQuality.None,
                            modifier = Modifier
                                .padding(HollowSpace.Lg)
                                .size(220.dp)
                                .testTag("server_qr_image")
                        )
                    }
                    Text(
                        text = if (server.shareUri.isNotBlank()) {
                            "Scan this QR code with Hollow VPN or another Xray client."
                        } else {
                            "Encoded raw Xray config as a QR code."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = scheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                } else {
                    Text(
                        text = "This config is too large for a single QR code. Use Copy Config instead.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = scheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onCopyPayload(sharePayload) },
                enabled = sharePayload.isNotBlank(),
                modifier = Modifier.testTag("btn_copy_qr_payload")
            ) {
                Icon(
                    imageVector = Icons.Outlined.ContentCopy,
                    contentDescription = null,
                    modifier = Modifier.size(HollowSizes.IconSm)
                )
                Spacer(Modifier.width(HollowSpace.Sm))
                Text(if (server.shareUri.isNotBlank()) "Copy Share Link" else "Copy Config")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
