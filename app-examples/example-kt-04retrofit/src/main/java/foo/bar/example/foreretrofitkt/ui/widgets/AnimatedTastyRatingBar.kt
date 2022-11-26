package foo.bar.example.foreretrofitkt.ui.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import foo.bar.example.foreretrofitkt.App
import foo.bar.example.foreretrofitkt.R


@Suppress("DEPRECATION")
class AnimatedTastyRatingBar @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private lateinit var paint: Paint

    private var widthPx: Float = 0.toFloat()
    private var heightPx: Float = 0.toFloat()

    private var backgroundColour: Int = 0
    private var progressColour: Int = 0

    private var tastyPercent = 0f
    private var currentPercent = 0f
    private val step = 1f
    private var increasing = false


    public override fun onFinishInflate() {
        super.onFinishInflate()

        backgroundColour = App.inst.getResources().getColor(R.color.colorPrimary)
        progressColour = App.inst.getResources().getColor(R.color.colorPrimaryDark)

        paint = Paint()
        paint.flags = Paint.ANTI_ALIAS_FLAG
        paint.style = Paint.Style.FILL_AND_STROKE
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        widthPx = width.toFloat()
        heightPx = height.toFloat()

        invalidate()
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //background
        paint.color = backgroundColour
        canvas.drawRect(0f, 0f, widthPx, heightPx, paint)

        //percent bar
        paint.color = progressColour
        canvas.drawRect(0f, 0f, currentPercent * widthPx / 100, heightPx, paint)

        progressAnimation()
    }

    fun setTastyPercent(tastyPercent: Float) {
        this.tastyPercent = tastyPercent

        if (currentPercent == 0f) {//don't bother with the animation
            currentPercent = tastyPercent
        }

        increasing = tastyPercent > currentPercent

        invalidate()
    }


    private fun progressAnimation() {

        if (increasing && currentPercent < tastyPercent) {

            currentPercent = Math.min(tastyPercent, currentPercent + step)

            invalidate()

        } else if (!increasing && currentPercent > tastyPercent) {

            currentPercent = Math.max(tastyPercent, currentPercent - step)

            invalidate()
        }
    }

}
