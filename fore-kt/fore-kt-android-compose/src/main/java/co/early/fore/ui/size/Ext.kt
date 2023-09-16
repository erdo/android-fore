package co.early.fore.ui.size

import android.app.Activity
import android.graphics.Rect
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.min
import androidx.window.layout.WindowMetricsCalculator

val LocalForeWindowSize =
    compositionLocalOf<WindowSize> { error("To access LocalForeWindowSize, your compose code must be wrapped in a ForeWindowSize{} block, we'd suggest somewhere high up in the UI tree/hierarchy, just inside setContent{}") }

@Composable
fun Activity.rememberWindowSize(includeAlmostSquareAspect: Boolean = true): WindowSize {
    val isRound = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        resources.configuration.isScreenRound
    } else false
    return rememberWindowDpSize().toWindowSize(includeAlmostSquareAspect, isRound)
}

@Composable
private fun Activity.rememberWindowDpSize(): DpSize {
    val boundsRect = rememberWindowMetrics()
    return with(LocalDensity.current) {
        boundsRect.toComposeRect().size.toDpSize()
    }
}

@Composable
fun Rect.toWindowSize(
    includeAlmostSquareAspect: Boolean = true,
    isRound: Boolean = false
): WindowSize {
    val widthPx = right - left
    val heightPx = bottom - top
    return with(LocalDensity.current) {
        DpSize(widthPx.toDp(), heightPx.toDp())
    }.toWindowSize(includeAlmostSquareAspect, isRound)
}

@Composable
fun DpSize.toWindowSize(
    includeAlmostSquareAspect: Boolean = true,
    isRound: Boolean = false
): WindowSize {
    return WindowSize(
        dpSize = this,
        width = Width.width(window = this),
        height = Height.height(window = this),
        minDim = MinDim.minDim(window = this),
        aspect = Aspect.aspect(window = this, includeAlmostSquare = includeAlmostSquareAspect),
        isRound = isRound
    )
}

fun DpSize.minimumDimension(): Dp {
    return min(width, height)
}

@Composable
private fun Activity.rememberWindowMetrics(): Rect {
    return remember(LocalConfiguration.current) {
        WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(this).bounds
    }
}

@Composable
fun Activity.ForeWindowSize(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalForeWindowSize provides rememberWindowSize()) {
        content()
    }
}
