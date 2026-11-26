package com.hollow.vpn.ui.components

import com.hollow.vpn.ui.theme.HollowSpace
import com.hollow.vpn.ui.theme.HollowSizes
import android.Manifest
import android.content.pm.PackageManager
import android.util.Size
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.FlashOff
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.hollow.vpn.ui.theme.DotoDisplayFamily
import com.hollow.vpn.ui.theme.HollowCream
import com.hollow.vpn.ui.theme.HollowInk
import com.hollow.vpn.ui.theme.HollowInkSecondary
import com.hollow.vpn.ui.theme.HollowScreenPadding
import com.hollow.vpn.ui.theme.JetBrainsMonoFamily
import com.hollow.vpn.ui.theme.NothingCanvas
import com.hollow.vpn.ui.theme.NothingOnCanvas
import com.hollow.vpn.ui.theme.NothingRed
import com.hollow.vpn.ui.theme.NothingTopBtnBorder
import com.hollow.vpn.ui.theme.NothingTopBtnFill
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Full-screen QR scanner. Header has back / flash / gallery, the camera fills the rest with a
 * dimmed scrim and corner brackets around the scan frame. Calls [onResult] once with the decoded text.
 */
@Composable
fun QrScanScreen(
    onResult: (String) -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    var permissionDenied by remember { mutableStateOf(false) }
    var torchOn by remember { mutableStateOf(false) }
    var camera by remember { mutableStateOf<Camera?>(null) }
    var notice by remember { mutableStateOf<String?>(null) }
    val delivered = remember { AtomicBoolean(false) }

    val deliver: (String) -> Unit = { text ->
        if (delivered.compareAndSet(false, true)) onResult(text)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        permissionDenied = !granted
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val decoded = decodeQrFromUri(context, uri)
            if (!decoded.isNullOrBlank()) deliver(decoded) else notice = "No QR code found in that image"
        }
    }

    LaunchedEffect(Unit) {
        if (!hasPermission) permissionLauncher.launch(Manifest.permission.CAMERA)
    }
    LaunchedEffect(torchOn, camera) {
        runCatching { camera?.cameraControl?.enableTorch(torchOn) }
    }

    val hasFlash = camera?.cameraInfo?.hasFlashUnit() == true

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NothingCanvas)
    ) {
        // Header: back + SCAN QR on the left, flash + gallery on the right
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = HollowScreenPadding)
                .padding(top = HollowSpace.Md, bottom = HollowSpace.Md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HollowTopSquareButton(
                containerColor = NothingTopBtnFill,
                contentColor = NothingOnCanvas,
                borderColor = NothingTopBtnBorder,
                contentDescription = "Back",
                onClick = onBack,
                modifier = Modifier.testTag("btn_scan_back")
            ) { tint ->
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(21.dp)
                )
            }

            Spacer(modifier = Modifier.width(HollowSpace.Md))

            Text(
                text = "SCAN QR",
                fontFamily = DotoDisplayFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                letterSpacing = 0.9.sp,
                maxLines = 1,
                color = NothingOnCanvas,
                modifier = Modifier.weight(1f)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(HollowSpace.Xs)) {
                if (hasPermission && hasFlash) {
                    HollowTopSquareButton(
                        containerColor = NothingTopBtnFill,
                        contentColor = if (torchOn) NothingRed else NothingOnCanvas,
                        borderColor = NothingTopBtnBorder,
                        contentDescription = "Flash",
                        onClick = { torchOn = !torchOn },
                        modifier = Modifier.testTag("btn_scan_flash")
                    ) { tint ->
                        Icon(
                            imageVector = if (torchOn) Icons.Outlined.FlashOn else Icons.Outlined.FlashOff,
                            contentDescription = null,
                            tint = tint,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }
                HollowTopSquareButton(
                    containerColor = NothingTopBtnFill,
                    contentColor = NothingOnCanvas,
                    borderColor = NothingTopBtnBorder,
                    contentDescription = "Pick from gallery",
                    onClick = {
                        notice = null
                        galleryLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier.testTag("btn_scan_gallery")
                ) { tint ->
                    Icon(
                        imageVector = Icons.Outlined.PhotoLibrary,
                        contentDescription = null,
                        tint = tint,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (hasPermission) {
                ScanCameraArea(
                    onDecoded = deliver,
                    onCameraReady = { camera = it },
                    onCameraError = { notice = it }
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.QrCodeScanner,
                        contentDescription = null,
                        tint = NothingRed,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(HollowSpace.Lg))
                    Text(
                        text = "Scan a QR code",
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        color = HollowInk
                    )
                    Spacer(modifier = Modifier.height(HollowSpace.Sm))
                    Text(
                        text = if (permissionDenied) {
                            "Camera permission is needed to scan. You can still pick a QR image from your gallery."
                        } else {
                            "Allow camera access to scan live"
                        },
                        fontSize = 13.sp,
                        color = HollowInkSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = HollowSpace.Xxxl)
                    )
                    Spacer(modifier = Modifier.height(HollowSpace.Xxl))
                    Button(
                        onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HollowInk,
                            contentColor = HollowCream
                        ),
                        modifier = Modifier
                            .height(HollowSizes.Button)
                            .testTag("btn_grant_camera_permission")
                    ) {
                        Text(
                            text = if (permissionDenied) "Allow camera" else "Start scanning",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            notice?.let { msg ->
                Text(
                    text = msg,
                    fontFamily = JetBrainsMonoFamily,
                    fontSize = 12.sp,
                    color = NothingRed,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(horizontal = HollowSpace.Xxl, vertical = HollowSpace.Md)
                )
            }
        }

        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}

@Composable
private fun ScanCameraArea(
    onDecoded: (String) -> Unit,
    onCameraReady: (Camera) -> Unit,
    onCameraError: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val analyzerExecutor = remember { Executors.newSingleThreadExecutor() }
    val alreadyDecoded = remember { AtomicBoolean(false) }
    // Theme colors are composable getters, so read them here rather than inside the draw lambda.
    val scrimColor = NothingCanvas.copy(alpha = 0.6f)

    DisposableEffect(Unit) {
        onDispose {
            try {
                ProcessCameraProvider.getInstance(context).get().unbindAll()
            } catch (_: Exception) {
            }
            analyzerExecutor.shutdown()
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val side = maxWidth * 0.69f
        val labelTop = (maxHeight + side) / 2 + 16.dp

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                val previewView = PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }
                val providerFuture = ProcessCameraProvider.getInstance(ctx)
                providerFuture.addListener({
                    try {
                        val provider = providerFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.surfaceProvider = previewView.surfaceProvider
                        }
                        val analysis = ImageAnalysis.Builder()
                            .setTargetResolution(Size(960, 720))
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                            .also { useCase ->
                                useCase.setAnalyzer(analyzerExecutor) { imageProxy ->
                                    if (!alreadyDecoded.get()) {
                                        val decoded = decodeQrFromImageProxy(imageProxy)
                                        if (!decoded.isNullOrBlank() &&
                                            alreadyDecoded.compareAndSet(false, true)
                                        ) {
                                            ContextCompat.getMainExecutor(ctx).execute { onDecoded(decoded) }
                                        }
                                    }
                                    imageProxy.close()
                                }
                            }
                        provider.unbindAll()
                        val bound = provider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            analysis
                        )
                        onCameraReady(bound)
                    } catch (e: Exception) {
                        onCameraError("Camera unavailable: ${e.message ?: "use a gallery image"}")
                    }
                }, ContextCompat.getMainExecutor(ctx))
                previewView
            }
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val sd = side.toPx()
            val l = (size.width - sd) / 2f
            val t = (size.height - sd) / 2f
            val r = l + sd
            val b = t + sd
            val radius = 20.dp.toPx()
            val len = 36.dp.toPx()

            val scrim = Path().apply {
                fillType = PathFillType.EvenOdd
                addRect(Rect(0f, 0f, size.width, size.height))
                addRoundRect(RoundRect(Rect(l, t, r, b), CornerRadius(radius)))
            }
            drawPath(scrim, scrimColor)

            val corners = Path().apply {
                moveTo(l, t + len); lineTo(l, t + radius)
                arcTo(Rect(l, t, l + 2 * radius, t + 2 * radius), 180f, 90f, false); lineTo(l + len, t)
                moveTo(r - len, t); lineTo(r - radius, t)
                arcTo(Rect(r - 2 * radius, t, r, t + 2 * radius), 270f, 90f, false); lineTo(r, t + len)
                moveTo(r, b - len); lineTo(r, b - radius)
                arcTo(Rect(r - 2 * radius, b - 2 * radius, r, b), 0f, 90f, false); lineTo(r - len, b)
                moveTo(l + len, b); lineTo(l + radius, b)
                arcTo(Rect(l, b - 2 * radius, l + 2 * radius, b), 90f, 90f, false); lineTo(l, b - len)
            }
            drawPath(
                corners,
                NothingRed,
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }

        Text(
            text = "Align the QR code within the frame",
            fontFamily = JetBrainsMonoFamily,
            fontSize = 12.sp,
            color = NothingOnCanvas,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = labelTop)
        )
    }
}

/** Full-screen window hosting [QrScanScreen]. Use this wherever a QR scan is started from a dialog-less screen. */
@Composable
fun QrScanDialog(
    onQrDecoded: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        QrScanScreen(onResult = onQrDecoded, onBack = onDismiss)
    }
}
