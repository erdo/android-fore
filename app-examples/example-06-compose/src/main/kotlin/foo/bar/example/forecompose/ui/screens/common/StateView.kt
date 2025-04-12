package foo.bar.example.forecompose.ui.screens.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import co.early.fore.ui.size.WidthBasedTextUnit
import co.early.fore.ui.size.WindowSize
import foo.bar.example.forecompose.ui.screens.home.DisplayToggleView


/**
 * This is mainly to demonstrate that [ObservableGroup.observeAsState()] works as intended,
 * as the app is backgrounded or the composable is hidden, the logs show the fore
 * observer being added / removed as appropriate. See the observeAsState code comments for a
 * full explanation
 */
@Composable
fun ShowHideWrapper(state: Any, size: WindowSize, content: @Composable () -> Unit) {

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

    val stateFontSize = WidthBasedTextUnit(
        xs = 12.sp,
        m = 20.sp,
        l = 35.sp
    )

    val stateAsString = state.prettyPrint()

    AnimatedVisibility(
        visible = !show.value,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stateAsString,
                style = TextStyle(fontSize = stateFontSize(size))
            )
        }
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

// https://gist.github.com/Mayankmkh/92084bdf2b59288d3e74c3735cccbf9f
fun Any.prettyPrint(): String {

    var indentLevel = 0
    val indentWidth = 4

    fun padding() = "".padStart(indentLevel * indentWidth)

    val toString = toString()//.replace("foo.bar.clean.domain.features.", "")

    val stringBuilder = StringBuilder(toString.length)

    var i = 0
    while (i < toString.length) {
        when (val char = toString[i]) {
            '(', '[', '{' -> {
                indentLevel++
                stringBuilder.appendLine(char).append(padding())
            }

            ')', ']', '}' -> {
                indentLevel--
                stringBuilder.appendLine().append(padding()).append(char)
            }

            ',' -> {
                stringBuilder.appendLine(char).append(padding())
                // ignore space after comma as we have added a newline
                val nextChar = toString.getOrElse(i + 1) { char }
                if (nextChar == ' ') i++
            }

            else -> {
                stringBuilder.append(char)
            }
        }
        i++
    }

    return stringBuilder.toString().replace("=", " = ")
}
