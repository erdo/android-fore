package co.early.fore.ui.size

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Customise the breakpoints like this should you need to (you probably
 * won't need to)
 *
 * ForeSize.overrideBreakPoints(
 *   ViewPortBreakPoints(
 *     widthLDpBelow = 1200.dp,
 *     heightLDpBelow = 1600.dp,
 *     consideredSquarishBelowRatio = 1.2f
 *   )
 * )
 */
class ForeSize {
    companion object {

        var breakPoints: ViewPortBreakPoints = ViewPortBreakPoints()
            private set

        fun overrideBreakPoints(breakPoints: ViewPortBreakPoints){
            Companion.breakPoints = breakPoints
        }
    }
}

/**
 * These constants let us directly reference the breakpoint
 * values in Preview annotations. Example:
 *
 * @Composable
 * fun PreviewWithWindowSize(content: @Composable (size: WindowSize) -> Unit) {
 *   ComposeTheme {
 *     BoxWithConstraints {
 *       content(DpSize(maxWidth, maxHeight).toWindowSize())
 *     }
 *   }
 * }
 *
 * @Preview(showBackground = true, widthDp = wXS_low, heightDp = hXS_low)
 * @Preview(showBackground = true, widthDp = wXS_high, heightDp = hXS_high)
 * @Preview(showBackground = true, widthDp = wS_low, heightDp = hS_low)
 * @Preview(showBackground = true, widthDp = wS_high, heightDp = hS_high)
 * @Preview(showBackground = true, widthDp = wM_low, heightDp = hM_low)
 * @Preview(showBackground = true, widthDp = wM_high, heightDp = hM_high)
 * @Preview(showBackground = true, widthDp = wL_low, heightDp = hL_low)
 * @Preview(showBackground = true, widthDp = wL_high, heightDp = hL_high)
 * @Preview(showBackground = true, widthDp = wXL_low, heightDp = hXL_low)
 * @Preview(showBackground = true, widthDp = wXL_high, heightDp = hXL_high)
 * @Preview(showBackground = true, widthDp = wL_high, heightDp = hXS_low)
 * @Preview(showBackground = true, widthDp = wXS_low, heightDp = hM_high)
 * @Composable
 * fun MyViewPreviews() {
 *   PreviewWithWindowSize {
 *     MyView(size = it)
 *   }
 * }
 *
 * Obviously if you override the break points, you will need to add your
 * own constants to use in your Preview annotations.
 */
const val wXS_low = 150
const val wXS_high = 289
const val wS_low = 290
const val wS_high = 399
const val wM_low = 400
const val wM_high = 499
const val wL_low = 500
const val wL_high = 899
const val wXL_low = 900
const val wXL_high = 1900

const val hXS_low = 150
const val hXS_high = 289
const val hS_low = 290
const val hS_high = 699
const val hM_low = 700
const val hM_high = 899
const val hL_low = 900
const val hL_high = 1279
const val hXL_low = 1280
const val hXL_high = 2000

class ViewPortBreakPoints(
    val widthXSDpBelow: Dp = wS_low.dp,
    val widthSDpBelow: Dp = wM_low.dp,
    val widthMDpBelow: Dp = wL_low.dp,
    val widthLDpBelow: Dp = wXL_low.dp,
    val widthXLDpBelow: Dp = Int.MAX_VALUE.dp,

    val heightXSDpBelow: Dp = hS_low.dp,
    val heightSDpBelow: Dp = hM_low.dp,
    val heightMDpBelow: Dp = hL_low.dp,
    val heightLDpBelow: Dp = hXL_low.dp,
    val heightXLDpBelow: Dp = Int.MAX_VALUE.dp,

    val minDimXSDpBelow: Dp = widthXSDpBelow,
    val minDimSDpBelow: Dp = widthSDpBelow,
    val minDimMDpBelow: Dp = widthMDpBelow,
    val minDimLDpBelow: Dp = widthLDpBelow,
    val minDimXLDpBelow: Dp = widthXLDpBelow,

    val consideredSquarishBelowRatio: Float = 1.1f,
)
