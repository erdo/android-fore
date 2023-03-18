package foo.bar.example.forecompose.ui.screens.home

import androidx.annotation.StringRes
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.*
import co.early.fore.compose.observeAsState
import co.early.fore.kt.core.delegate.Fore
import co.early.fore.ui.size.*
import foo.bar.example.forecompose.OG
import foo.bar.example.forecompose.R
import foo.bar.example.forecompose.feature.counter.CounterModel
import foo.bar.example.forecompose.feature.counter.CounterState
import foo.bar.example.forecompose.ui.screens.common.toLabel

@Composable
fun HomeScreen(
    size: WindowSize = WindowSize(),
    counterModel: CounterModel = OG[CounterModel::class.java],
) {

    Fore.getLogger().i("HomeScreen $size")

    showHideWrapper(size) {

        val counterState by counterModel.observeAsState("FOO") { counterModel.state }

        HomeView(
            size = size,
            counterState = counterState,
            increaseCallback = { counterModel.increase() },
            decreaseCallback = { counterModel.decrease() },
        )
    }
}

@Composable
fun HomeView(
    size: WindowSize,
    counterState: CounterState,
    increaseCallback: () -> Unit = {},
    decreaseCallback: () -> Unit = {},
) {

    Fore.getLogger().i("HomeView")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CounterView(
            size = size,
            counterState = counterState,
            increaseCallback = { increaseCallback() },
            decreaseCallback = { decreaseCallback() },
        )
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        DiagnosticInfo(size)
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CounterView(
    size: WindowSize,
    counterState: CounterState,
    increaseCallback: () -> Unit,
    decreaseCallback: () -> Unit,
) {

    Fore.getLogger().i("CounterView")

    val minimumDimension = size.dpSize.minimumDimension()
    val borderThickness = minimumDimension * 0.1f
    val boxHeight = minimumDimension * 0.5f
    val numberFontSize = (minimumDimension / 5f).value.sp
    val buttonFontSize = (minimumDimension / 8f).value.sp
    val buttonSize = max(borderThickness * 3, 50.dp)
    val color = WidthBasedValue(
        xs = Color.Red,
        s = Color.Green,
        m = Color.Blue,
        l = Color.Magenta,
        xl = Color.Gray
    )
    val shape = AspectBasedValue(
        port = CircleShape,
        land = CircleShape,
        squarish = RectangleShape
    )

    Box(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(boxHeight)
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = borderThickness, end = borderThickness)
                .border(width = borderThickness, color = color(size), shape = shape(size)),
        )

        Box(modifier = Modifier.fillMaxSize()) {

            CustomButton(
                Modifier.align(Alignment.CenterStart),
                R.string.decrease,
                counterState.canDecrease(),
                buttonSize,
                buttonFontSize,
                decreaseCallback,
            )

            if (counterState.loading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(borderThickness),
                )
            } else {
                Text(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(borderThickness / 2),
                    text = counterState.amount.toString(),
                    style = TextStyle(fontSize = numberFontSize)
                )
            }

            CustomButton(
                Modifier.align(Alignment.CenterEnd),
                R.string.increase,
                counterState.canIncrease(),
                buttonSize,
                buttonFontSize,
                increaseCallback,
            )
        }
    }
}

@Composable
fun CustomButton(
    modifier: Modifier = Modifier,
    @StringRes labelResId: Int,
    enabled: Boolean,
    buttonSize: Dp,
    buttonFontSize: TextUnit,
    callback: () -> Unit,
) {

    Fore.getLogger().i("CustomButton")

    Button(
        modifier = modifier.size(buttonSize),
        onClick = { callback() },
        enabled = enabled
    ) {
        Text(
            text = stringResource(id = labelResId),
            style = TextStyle(fontSize = buttonFontSize)
        )
    }
}

@Composable
fun BoxScope.DisplayToggleView(
    size: WindowSize,
    btnColor: Color,
    displayed: Boolean,
    toggleDisplayCallback: () -> Unit,
) {

    Fore.getLogger().i("DisplayToggleView")

    val minimumDimension = size.dpSize.minimumDimension()
    val toggleBtnFontSize = (minimumDimension / 30f).value.sp
    val label = stringResource(id = if (displayed) R.string.hide else R.string.show)
    val alignment = if (size.isRound) Alignment.TopCenter else Alignment.TopEnd

    Button(
        modifier = Modifier.align(alignment),
        colors = ButtonDefaults.textButtonColors(contentColor = btnColor),
        onClick = { toggleDisplayCallback() },
        shape = ButtonDefaults.textShape,
    ) {
        Text(
            text = label,
            style = TextStyle(fontSize = toggleBtnFontSize),
        )
    }
}

@Composable
fun BoxScope.DiagnosticInfo(size: WindowSize) {
    // Note: you don't really need two different composables here
    // as they are so similar, this code serves as the "adaptive"
    // vs "responsive" example. In this case "adaptive" meaning to
    // display a completely different UI based on the size class
    // see: https://dev.to/erdo/jetpack-compose-and-windowsize-classes-gb4
    WidthBasedComposable(
        xs = { sz -> MiniDiagnostics(sz) },
        m = { sz -> MiniDiagnostics(sz) },
        l = { sz -> RegularDiagnostics(sz) },
    )(size)
}

@Composable
fun BoxScope.RegularDiagnostics(size: WindowSize) {

    Fore.getLogger().i("RegularDiagnostics ${size.toLabel()}")

    val diagnosticsFontSize = (size.dpSize.width / 60f).value.sp
    val alignment = if (size.isRound) Alignment.BottomCenter else Alignment.BottomStart
    val diagnosticText = size.toLabel(extended = true, multipleLines = size.isRound)

    Text(
        modifier = Modifier
            .align(alignment)
            .padding(start = 10.dp, end = 10.dp)
            .background(color = Color.Yellow),
        text = diagnosticText,
        style = TextStyle(color = Color.Red, fontSize = diagnosticsFontSize)
    )
}

@Composable
fun BoxScope.MiniDiagnostics(size: WindowSize) {

    Fore.getLogger().i("MiniDiagnostics ${size.toLabel()}")

    val diagnosticsFontSize = (size.dpSize.width / 30f).value.sp
    val alignment = if (size.isRound) Alignment.BottomCenter else Alignment.BottomStart
    val diagnosticText = size.toLabel(multipleLines = size.isRound)

    Text(
        modifier = Modifier
            .align(alignment)
            .padding(start = 10.dp, end = 10.dp)
            .background(color = Color.Yellow),
        text = diagnosticText,
        style = TextStyle(color = Color.Blue, fontSize = diagnosticsFontSize)
    )
}

/**
 * This is mainly to demonstrate that [ObservableGroup.observeAsState()] works as intended,
 * as the app is backgrounded or the composable is hidden, the logs show the fore
 * observer being added / removed as appropriate. See the observeAsState code comments for a
 * full explanation
 */
@Composable
fun showHideWrapper(size: WindowSize, content: @Composable () -> Unit) {

    val show = remember { mutableStateOf(true) }
    val btnColor by animateColorAsState(
        targetValue = if (show.value) Color.Red else Color.Green,
        animationSpec = tween(durationMillis = 500),
    )

    AnimatedVisibility(
        visible = show.value,
        enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut(),
    ) {
        content()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        DisplayToggleView(
            size = size,
            displayed = show.value,
            btnColor = btnColor,
            toggleDisplayCallback = { show.value = !show.value },
        )
    }
}
