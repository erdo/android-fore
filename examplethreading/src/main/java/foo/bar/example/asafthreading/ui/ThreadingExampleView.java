package foo.bar.example.asafthreading.ui;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.early.asaf.framework.observer.Observer;
import foo.bar.example.asafthreading.CustomApp;
import foo.bar.example.asafthreading.R;
import foo.bar.example.asafthreading.feature.CounterBasic;
import foo.bar.example.asafthreading.feature.CounterWithProgress;

/**
 *
 */
public class ThreadingExampleView extends ScrollView {

    //models that we need to sync with
    private CounterWithProgress counterWithProgress;
    private CounterBasic counterBasic;


    //UI elements that we care about
    @BindView(R.id.threadingexample_increaseprog_btn)
    public Button increaseBy20Prog;

    @BindView(R.id.threadingexample_busyprog_progress)
    public ProgressBar busyProg;

    @BindView(R.id.threadingexample_progressprog_txt)
    public TextView progressNumberProg;

    @BindView(R.id.threadingexample_currentprog_txt)
    public TextView currentNumberProg;

    @BindView(R.id.threadingexample_increasebasic_btn)
    public Button increaseBy20Basic;

    @BindView(R.id.threadingexample_busybasic_progress)
    public ProgressBar busyBasic;

    @BindView(R.id.threadingexample_currentbasic_txt)
    public TextView currentNumberBasic;


    //single observer reference
    Observer observer = new Observer() {
        @Override
        public void somethingChanged() {
            syncView();
        }
    };


    public ThreadingExampleView(Context context) {
        super(context);
    }

    public ThreadingExampleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ThreadingExampleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ThreadingExampleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.bind(this, this);

        getModelReferences();

        setupButtonClickListeners();
    }

    private void setupButtonClickListeners() {

        increaseBy20Prog.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                counterWithProgress.increaseBy20();
            }
        });

        increaseBy20Basic.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                counterBasic.increaseBy20();
            }
        });
    }


    private void getModelReferences(){
        counterWithProgress = CustomApp.get(CounterWithProgress.class);
        counterBasic = CustomApp.get(CounterBasic.class);
    }


    //data binding stuff below

    public void syncView(){
        increaseBy20Prog.setEnabled(!counterWithProgress.isBusy());
        busyProg.setVisibility(counterWithProgress.isBusy() ? VISIBLE : INVISIBLE);
        progressNumberProg.setText("" + counterWithProgress.getProgress());
        currentNumberProg.setText("" + counterWithProgress.getCount());

        increaseBy20Basic.setEnabled(!counterBasic.isBusy());
        busyBasic.setVisibility(counterBasic.isBusy() ? VISIBLE : INVISIBLE);
        currentNumberBasic.setText("" + counterBasic.getCount());
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        counterBasic.addObserver(observer);
        counterWithProgress.addObserver(observer);
        syncView();
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        counterBasic.removeObserver(observer);
        counterWithProgress.removeObserver(observer);
    }
}
