package foo.bar.example.foreretrofit.ui.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import foo.bar.example.foreretrofit.CustomApp;
import foo.bar.example.foreretrofit.R;

/**
 *
 */
public class AnimatedTastyRatingBar extends AppCompatImageView {

    private Paint paint;

    private float widthPx;
    private float heightPx;

    private int backgroundColour;
    private int progressColour;

    private float tastyPercent = 0;
    private float currentPercent = 0;
    private float step = 1;
    private boolean increasing = false;

    public AnimatedTastyRatingBar(Context context) {
        super(context);
    }

    public AnimatedTastyRatingBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimatedTastyRatingBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void onFinishInflate() {
        super.onFinishInflate();

        backgroundColour = CustomApp.getInstance().getResources().getColor(R.color.colorPrimary);
        progressColour = CustomApp.getInstance().getResources().getColor(R.color.colorPrimaryDark);

        paint = new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        widthPx = getWidth();
        heightPx = getHeight();

        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //background
        paint.setColor(backgroundColour);
        canvas.drawRect(0, 0, widthPx, heightPx, paint);

        //percent bar
        paint.setColor(progressColour);
        canvas.drawRect(0, 0, (float)((currentPercent * widthPx)/100), heightPx, paint);

        progressAnimation();
    }

    public void setTastyPercent(float tastyPercent){
        this.tastyPercent = tastyPercent;

        if (currentPercent == 0){//don't bother with the animation
            currentPercent = tastyPercent;
        }

        increasing = (tastyPercent > currentPercent);

        invalidate();
    }


    private void progressAnimation() {

        if (increasing && currentPercent < tastyPercent) {

            currentPercent = Math.min(tastyPercent, currentPercent + step);

            invalidate();

        } else if (!increasing && currentPercent > tastyPercent) {

            currentPercent = Math.max(tastyPercent, currentPercent - step);

            invalidate();
        }
    }


}
