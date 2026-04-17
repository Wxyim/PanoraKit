/*
 * This file is part of MonadBox - A customized edition of YumeBox.
 *
 * MonadBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) YumeLira 2025 - 2026
 * Copyright (c) MonadBox Contributors 2026 - Present
 *
 */

package com.github.nomadboxlab.monadbox.feature.profiles

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.net.Uri
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.LuminanceSource
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import java.nio.ByteBuffer
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import timber.log.Timber

private val qrDecodeHints =
    mapOf(
        DecodeHintType.POSSIBLE_FORMATS to listOf(BarcodeFormat.QR_CODE),
        DecodeHintType.TRY_HARDER to true,
        DecodeHintType.CHARACTER_SET to Charsets.UTF_8.name(),
    )

@Composable
internal fun StableQrScanner(onScanned: (String) -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    val currentOnScanned by rememberUpdatedState(onScanned)
    val hasScanned = remember { java.util.concurrent.atomic.AtomicBoolean(false) }

    DisposableEffect(cameraExecutor) { onDispose { cameraExecutor.shutdown() } }

    AndroidView(
        modifier = Modifier.fillMaxSize().clipToBounds(),
        factory = { context ->
            val previewView =
                PreviewView(context).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    layoutParams =
                        ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                        )
                }

            val previewUseCase =
                Preview.Builder().build().also { it.surfaceProvider = previewView.surfaceProvider }

            val imageAnalysisUseCase =
                ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor) { imageProxy ->
                            processStableQrImage(imageProxy) { text ->
                                if (hasScanned.compareAndSet(false, true)) {
                                    currentOnScanned(text)
                                }
                            }
                        }
                    }

            coroutineScope.launch {
                try {
                    val cameraProvider = context.getStableCameraProvider()
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        previewUseCase,
                        imageAnalysisUseCase,
                    )
                } catch (e: Exception) {
                    Timber.w(e, "Failed to bind camera lifecycle")
                }
            }

            previewView
        },
        onRelease = { previewView ->
            try {
                val context = previewView.context
                ProcessCameraProvider.getInstance(context).get().unbindAll()
            } catch (_: Exception) {}
        },
    )
}

@SuppressLint("UnsafeOptInUsageError")
internal fun processStableQrImage(imageProxy: ImageProxy, onScanned: (String) -> Unit) {
    try {
        decodeQrFromImageProxy(imageProxy)?.let(onScanned)
    } catch (e: Exception) {
        Timber.v(e, "QR decode failed for camera frame")
    } finally {
        imageProxy.close()
    }
}

internal suspend fun Context.getStableCameraProvider(): ProcessCameraProvider =
    suspendCancellableCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { future ->
            future.addListener(
                { continuation.resume(future.get()) },
                ContextCompat.getMainExecutor(this),
            )
        }
    }

internal suspend fun readQrFromImage(context: Context, uri: Uri): String? =
    withContext(Dispatchers.IO) {
        runCatching {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val bitmap = BitmapFactory.decodeStream(inputStream) ?: return@use null
                    decodeQrFromBitmap(bitmap)
                }
            }
            .getOrNull()
    }

@ExperimentalGetImage
private fun decodeQrFromImageProxy(imageProxy: ImageProxy): String? {
    if (imageProxy.image == null || imageProxy.format != ImageFormat.YUV_420_888) {
        return null
    }

    val luminance = extractLuminance(imageProxy.planes[0], imageProxy.width, imageProxy.height)
    val source =
        createLuminanceSource(
            data = luminance,
            width = imageProxy.width,
            height = imageProxy.height,
            rotationDegrees = imageProxy.imageInfo.rotationDegrees,
        )

    return decodeQrFromSource(source)
}

private fun extractLuminance(plane: ImageProxy.PlaneProxy, width: Int, height: Int): ByteArray {
    val rowStride = plane.rowStride
    val pixelStride = plane.pixelStride
    val buffer = plane.buffer.duplicate().apply { rewind() }

    if (pixelStride == 1 && rowStride == width) {
        return ByteArray(buffer.remaining()).also(buffer::get)
    }

    return ByteArray(width * height).also { result ->
        copyPlane(buffer, rowStride, pixelStride, width, height, result)
    }
}

private fun copyPlane(
    buffer: ByteBuffer,
    rowStride: Int,
    pixelStride: Int,
    width: Int,
    height: Int,
    out: ByteArray,
) {
    for (row in 0 until height) {
        val rowOffset = row * rowStride
        for (column in 0 until width) {
            out[row * width + column] = buffer.get(rowOffset + column * pixelStride)
        }
    }
}

private fun createLuminanceSource(
    data: ByteArray,
    width: Int,
    height: Int,
    rotationDegrees: Int,
): PlanarYUVLuminanceSource {
    val normalizedRotation = ((rotationDegrees % 360) + 360) % 360
    val rotatedData =
        when (normalizedRotation) {
            90 -> rotateLuminance90(data, width, height)
            180 -> rotateLuminance180(data)
            270 -> rotateLuminance270(data, width, height)
            else -> data
        }
    val rotatedWidth = if (normalizedRotation == 90 || normalizedRotation == 270) height else width
    val rotatedHeight = if (normalizedRotation == 90 || normalizedRotation == 270) width else height

    return PlanarYUVLuminanceSource(
        rotatedData,
        rotatedWidth,
        rotatedHeight,
        0,
        0,
        rotatedWidth,
        rotatedHeight,
        false,
    )
}

private fun rotateLuminance90(data: ByteArray, width: Int, height: Int): ByteArray {
    val rotated = ByteArray(data.size)
    var index = 0
    for (x in 0 until width) {
        for (y in height - 1 downTo 0) {
            rotated[index++] = data[y * width + x]
        }
    }
    return rotated
}

private fun rotateLuminance180(data: ByteArray): ByteArray {
    val rotated = ByteArray(data.size)
    data.indices.forEach { index -> rotated[data.lastIndex - index] = data[index] }
    return rotated
}

private fun rotateLuminance270(data: ByteArray, width: Int, height: Int): ByteArray {
    val rotated = ByteArray(data.size)
    var index = 0
    for (x in width - 1 downTo 0) {
        for (y in 0 until height) {
            rotated[index++] = data[y * width + x]
        }
    }
    return rotated
}

private fun decodeQrFromBitmap(bitmap: Bitmap): String? {
    val pixels = IntArray(bitmap.width * bitmap.height)
    bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
    val source = RGBLuminanceSource(bitmap.width, bitmap.height, pixels)
    return decodeQrFromSource(source)
}

private fun decodeQrFromSource(source: LuminanceSource): String? {
    val reader = MultiFormatReader().apply { setHints(qrDecodeHints) }
    return try {
        reader.decodeWithState(BinaryBitmap(HybridBinarizer(source))).text
    } catch (_: Exception) {
        null
    } finally {
        reader.reset()
    }
}
