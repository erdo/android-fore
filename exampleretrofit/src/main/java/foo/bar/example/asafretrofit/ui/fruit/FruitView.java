package foo.bar.example.asafretrofit.ui.fruit;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.early.asaf.core.callbacks.FailureCallbackWithPayload;
import co.early.asaf.core.callbacks.SuccessCallBack;
import co.early.asaf.core.observer.Observer;
import foo.bar.example.asafretrofit.CustomApp;
import foo.bar.example.asafretrofit.R;
import foo.bar.example.asafretrofit.feature.fruit.FruitFetcher;
import foo.bar.example.asafretrofit.message.UserMessage;
import foo.bar.example.asafretrofit.ui.widgets.AnimatedTastyRatingBar;

/**
 *
 */
public class FruitView extends ScrollView {

    //models that we need to sync with
    private FruitFetcher fruitFetcher;


    //UI elements that we care about
    @BindView(R.id.fruit_fetchok_btn)
    public Button fruitRefreshSuccess;

    @BindView(R.id.fruit_fetchfailbasic_btn)
    public Button fruitRefreshFailBasic;

    @BindView(R.id.fruit_fetchfailadvanced_btn)
    public Button fruitRefreshFailAdvanced;

    @BindView(R.id.fruit_name_textview)
    public TextView fruitName;

    @BindView(R.id.fruit_citrus_img)
    public ImageView isCitrus;

    @BindView(R.id.fruit_tastyrating_tastybar)
    public AnimatedTastyRatingBar tasteValuePercentView;

    @BindView(R.id.fruit_tastyrating_textview)
    public TextView tasteScore;

    @BindView(R.id.fruit_busy_progbar)
    public ProgressBar busy;

    @BindView(R.id.fruit_detailcontainer_linearlayout)
    public View fruitDetailContainer;


    //single observer reference
    Observer observer = new Observer() {
        @Override
        public void somethingChanged() {
            syncView();
        }
    };



    //just because we re-use these in 3 different button clicks
    //in this example we define them here
    private SuccessCallBack successCallBack = new SuccessCallBack() {
        @Override
        public void success() {
            Toast.makeText(getContext(), "Success - you can use this trigger to " +
                    "perform a one off action like starting a new activity or " +
                    "something", Toast.LENGTH_SHORT).show();
        }
    };
    private FailureCallbackWithPayload<UserMessage> failureCallback = new FailureCallbackWithPayload<UserMessage>() {
        @Override
        public void fail(UserMessage userMessage) {
            Toast.makeText(getContext(), "Fail - maybe tell the user to try again, message:" + userMessage.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    };





    public FruitView(Context context) {
        super(context);
    }

    public FruitView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FruitView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        fruitFetcher = CustomApp.get(FruitFetcher.class);
    }

    private void setupButtonClickListeners() {

        fruitRefreshSuccess.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                fruitFetcher.fetchFruits(successCallBack, failureCallback);
            }
        });

        fruitRefreshFailBasic.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                fruitFetcher.fetchFruitsButFail(successCallBack, failureCallback);

            }
        });

        fruitRefreshFailAdvanced.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                fruitFetcher.fetchFruitsButFailGetCustomError(successCallBack, failureCallback);
            }
        });

    }


    //data binding stuff below

    public void syncView(){
        fruitRefreshSuccess.setEnabled(!fruitFetcher.isBusy());
        fruitRefreshFailBasic.setEnabled(!fruitFetcher.isBusy());
        fruitRefreshFailAdvanced.setEnabled(!fruitFetcher.isBusy());
        fruitName.setText(fruitFetcher.getCurrentFruit().name);
        isCitrus.setImageResource(fruitFetcher.getCurrentFruit().isCitrus ? R.drawable.lemon_positive : R.drawable.lemon_negative);
        tasteValuePercentView.setTastyPercent(fruitFetcher.getCurrentFruit().tastyPercentScore);
        tasteScore.setText(String.format(getContext().getString(R.string.fruit_percent), String.valueOf(fruitFetcher.getCurrentFruit().tastyPercentScore)));
        busy.setVisibility(fruitFetcher.isBusy() ?  VISIBLE : INVISIBLE);
        fruitDetailContainer.setVisibility(fruitFetcher.isBusy() ? GONE : VISIBLE);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        fruitFetcher.addObserver(observer);
        syncView(); //  <- don't forget this
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        fruitFetcher.removeObserver(observer);
    }
}
