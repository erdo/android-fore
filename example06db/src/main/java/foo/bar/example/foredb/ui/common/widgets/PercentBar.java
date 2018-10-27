package foo.bar.example.foredb.ui.common.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import foo.bar.example.foredb.App;
import foo.bar.example.foredb.R;



public class PercentBar extends androidx.appcompat.widget.AppCompatImageView {

    private Paint paint;

    private float widthPx;
    private float heightPx;

    private int backgroundColour;
    private int progressColour;

    private float percentDone = 0;


    public PercentBar(Context context) {
        super(context);
    }

    public PercentBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PercentBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void onFinishInflate() {
        super.onFinishInflate();

        backgroundColour = App.instance().getResources().getColor(R.color.progress_background);
        progressColour = App.instance().getResources().getColor(R.color.progress_done);

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
        canvas.drawRect(0, 0, (float)((percentDone * widthPx)/100), heightPx, paint);

    }

    public void setPercentDone(float percentDone){
        this.percentDone = percentDone;
        invalidate();
    }

}
