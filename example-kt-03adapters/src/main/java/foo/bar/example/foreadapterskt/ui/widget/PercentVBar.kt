package foo.bar.example.foreadapterskt.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import foo.bar.example.foreadapterskt.App.Companion.inst
import foo.bar.example.foreadapterskt.R
import kotlin.math.max
import kotlin.math.min

/**
 * Just a quick demo of animation when used in adapters, a lottie file would work well here
 */
class PercentVBar @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private val paint = Paint()
    private var widthPx = 0f
    private var heightPx = 0f
    private val progressColour = inst.resources.getColor(R.color.colorAccent)
    private val progressRect = RectF(0f, 0f, widthPx, heightPx)
    private val step = 3f

    private var targetPercent = 0f
    private var currentPercent = -1f
    private var increasing = false
    private var itemId: Long = -1

    init {
        paint.flags = Paint.ANTI_ALIAS_FLAG
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.color = progressColour
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        widthPx = width.toFloat()
        heightPx = height.toFloat()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        progressRect.set(0f, ((100 - currentPercent) * heightPx / 100), widthPx, heightPx)

        //percent progress
        canvas.drawRect(progressRect, paint)
        progressAnimation()
    }

    /**
     * @param itemId this helps us know if the view is in an adapter and is being recycled
     * we don't want to animate if we are being recycled
     * @param percentDone
     */
    fun setPercentDone(itemId: Long, percentDone: Float) {
        targetPercent = percentDone

        if (currentPercent == -1f || this.itemId != itemId) { //don't bother with the animation
            currentPercent = targetPercent
        }
        this.itemId = itemId

        increasing = targetPercent > currentPercent
        invalidate()
    }

    private fun progressAnimation() {
        if (increasing && currentPercent < targetPercent) {
            currentPercent = min(targetPercent, currentPercent + step)
            invalidate()
        } else if (!increasing && currentPercent > targetPercent) {
            currentPercent = max(targetPercent, currentPercent - step)
            invalidate()
        }
    }
}
