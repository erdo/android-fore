package foo.bar.example.forecompose.ui.screens.home

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import co.early.fore.ui.size.*
import foo.bar.example.forecompose.feature.counter.CounterState
import foo.bar.example.forecompose.ui.theme.ComposeTheme

@Composable
fun PreviewWithWindowSize(isRound: Boolean = false, content: @Composable (size: WindowSize) -> Unit) {
    ComposeTheme {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            content(DpSize(maxWidth, maxHeight).toWindowSize(isRound = isRound))
        }
    }
}

@Preview(showBackground = true, widthDp = wXS_low, heightDp = hXS_low)
@Preview(showBackground = true, widthDp = wXS_high, heightDp = hXS_high)
@Preview(showBackground = true, widthDp = wS_low, heightDp = hS_low)
@Preview(showBackground = true, widthDp = wS_high, heightDp = hS_high)
@Preview(showBackground = true, widthDp = wM_low, heightDp = hM_low)
@Preview(showBackground = true, widthDp = wM_high, heightDp = hM_high)
@Preview(showBackground = true, widthDp = wL_low, heightDp = hL_low)
@Preview(showBackground = true, widthDp = wL_high, heightDp = hL_high)
@Preview(showBackground = true, widthDp = wXL_low, heightDp = hXL_low)
@Preview(showBackground = true, widthDp = wXL_high, heightDp = hXL_high)
@Preview(showBackground = true, widthDp = wL_high, heightDp = hXS_low)
@Preview(showBackground = true, widthDp = wXS_low, heightDp = hM_high)
@Composable
fun MyPreviews() {
    PreviewWithWindowSize {
        HomeView(size = it, counterState = CounterState(3))
    }
}

@Preview(showBackground = true, device = Devices.WEAR_OS_LARGE_ROUND)
@Composable
fun MyRoundPreview() {
    PreviewWithWindowSize(isRound = true) {
        HomeView(size = it, counterState = CounterState(3))
    }
}
