package foo.bar.example.forethreading.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.early.fore.core.observer.Observer;
import foo.bar.example.forethreading.OG;
import foo.bar.example.forethreading.R;
import foo.bar.example.forethreading.feature.counter.CounterWithLambdas;
import foo.bar.example.forethreading.feature.counter.CounterWithProgress;

/**
 *
 */
public class CounterView extends ScrollView {

    //models that we need to sync with
    private CounterWithProgress counterWithProgress;
    private CounterWithLambdas counterWithLambdas;


    //UI elements that we care about
    @BindView(R.id.counterwprog_increase_btn)
    public Button increaseBy20Prog;

    @BindView(R.id.counterwprog_busy_progress)
    public ProgressBar busyProg;

    @BindView(R.id.counterwprog_progress_txt)
    public TextView progressNumberProg;

    @BindView(R.id.counterwprog_current_txt)
    public TextView currentNumberProg;

    @BindView(R.id.counterwlambda_increase_btn)
    public Button increaseBy20Basic;

    @BindView(R.id.counterwlambda_busy_progress)
    public ProgressBar busyBasic;

    @BindView(R.id.counterwlambda_current_txt)
    public TextView currentNumberBasic;



    //single observer reference
    Observer observer = this::syncView;



    public CounterView(Context context) {
        super(context);
    }

    public CounterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CounterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }



    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.bind(this, this);

        getModelReferences();

        setupButtonClickListeners();
    }


    private void getModelReferences(){
        counterWithProgress = OG.get(CounterWithProgress.class);
        counterWithLambdas = OG.get(CounterWithLambdas.class);
    }

    private void setupButtonClickListeners() {
        increaseBy20Prog.setOnClickListener(v -> counterWithProgress.increaseBy20());
        increaseBy20Basic.setOnClickListener(v -> counterWithLambdas.increaseBy20());
    }


    //data binding stuff below

    public void syncView(){
        increaseBy20Prog.setEnabled(!counterWithProgress.isBusy());
        busyProg.setVisibility(counterWithProgress.isBusy() ? VISIBLE : INVISIBLE);
        progressNumberProg.setText("" + counterWithProgress.getProgress());
        currentNumberProg.setText("" + counterWithProgress.getCount());

        increaseBy20Basic.setEnabled(!counterWithLambdas.isBusy());
        busyBasic.setVisibility(counterWithLambdas.isBusy() ? VISIBLE : INVISIBLE);
        currentNumberBasic.setText("" + counterWithLambdas.getCount());
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        counterWithLambdas.addObserver(observer);
        counterWithProgress.addObserver(observer);
        syncView(); //<-- don't forget this
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        counterWithLambdas.removeObserver(observer);
        counterWithProgress.removeObserver(observer);
    }
}
