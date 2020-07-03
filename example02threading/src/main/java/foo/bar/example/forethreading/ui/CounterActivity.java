package foo.bar.example.forethreading.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import co.early.fore.core.observer.Observer;
import foo.bar.example.forethreading.OG;
import foo.bar.example.forethreading.R;
import foo.bar.example.forethreading.feature.counter.CounterWithLambdas;
import foo.bar.example.forethreading.feature.counter.CounterWithProgress;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;


public class CounterActivity extends FragmentActivity {

    //models that we need to sync with
    private CounterWithProgress counterWithProgress = OG.get(CounterWithProgress.class);
    private CounterWithLambdas counterWithLambdas = OG.get(CounterWithLambdas.class);


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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_counter);

        ButterKnife.bind(this);

        setupButtonClickListeners();
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
    protected void onStart() {
        super.onStart();
        counterWithLambdas.addObserver(observer);
        counterWithProgress.addObserver(observer);
        syncView(); //<-- don't forget this
    }

    @Override
    protected void onStop() {
        super.onStop();
        counterWithLambdas.removeObserver(observer);
        counterWithProgress.removeObserver(observer);
    }

}
