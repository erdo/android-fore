package foo.bar.example.foreadapters.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

import foo.bar.example.foreadapters.App;
import foo.bar.example.foreadapters.R;

/**
 * Just a quick demo of animation when used in adapters, a lottie file would work well here
 */
public class PercentPie extends androidx.appcompat.widget.AppCompatImageView {

    private Paint paint;
    private RectF backgroundRect;

    private float widthPx;
    private float heightPx;

    private int backgroundColour;
    private int progressColour;

    private float targetPercent = 0;
    private float currentPercent = -1;
    private float step = 3;
    private boolean increasing = false;

    private long itemId = -1;

    public PercentPie(Context context) {
        super(context);
    }

    public PercentPie(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PercentPie(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void onFinishInflate() {
        super.onFinishInflate();

        backgroundColour = App.getInst().getResources().getColor(R.color.colorPrimary);
        progressColour = App.getInst().getResources().getColor(R.color.colorAccent);

        paint = new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        backgroundRect = new RectF(0, 0, widthPx, heightPx);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        widthPx = getWidth();
        heightPx = getHeight();

        backgroundRect.set(0, 0, widthPx, heightPx);

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //background
        paint.setColor(backgroundColour);
        canvas.drawArc(backgroundRect, 0, 360, true, paint);

        //percent progress
        paint.setColor(progressColour);
        canvas.drawArc(backgroundRect, 270, (float)((currentPercent * 360)/100), true, paint);

        progressAnimation();
    }

    /**
     * @param itemId this helps us know if the view is in an adapter and is being recycled
     *               we don't want to animate if we are being recycled
     * @param percentDone
     */
    public void setPercentDone(long itemId, float percentDone){

        this.targetPercent = percentDone;

        if (currentPercent == -1 || this.itemId != itemId){//don't bother with the animation
            currentPercent = targetPercent;
        }

        this.itemId = itemId;
        increasing = (targetPercent > currentPercent);

        invalidate();
    }

    private void progressAnimation() {

        if (increasing && currentPercent < targetPercent) {

            currentPercent = Math.min(targetPercent, currentPercent + step);

            invalidate();

        } else if (!increasing && currentPercent > targetPercent) {

            currentPercent = Math.max(targetPercent, currentPercent - step);

            invalidate();
        }
    }
}
