package foo.bar.example.foreretrofit.ui.fruit;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import co.early.fore.core.callbacks.FailureCallbackWithPayload;
import co.early.fore.core.callbacks.SuccessCallback;
import co.early.fore.core.observer.Observer;
import foo.bar.example.foreretrofit.OG;
import foo.bar.example.foreretrofit.R;
import foo.bar.example.foreretrofit.feature.fruit.FruitFetcher;
import foo.bar.example.foreretrofit.message.UserMessage;
import foo.bar.example.foreretrofit.ui.widgets.AnimatedTastyRatingBar;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;


public class FruitActivity extends FragmentActivity {


    //models that we need to sync with
    private FruitFetcher fruitFetcher = OG.get(FruitFetcher.class);


    //UI elements that we care about
    @BindView(R.id.fruit_fetchsuccess_btn)
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
    Observer observer = this::syncView;



    //just because we re-use these in 3 different button clicks
    //in this example we define them here
    private SuccessCallback successCallback = new SuccessCallback() {
        @Override
        public void success() {
            Toast.makeText(FruitActivity.this, "Success - you can use this trigger to " +
                    "perform a one off action like starting a new activity or " +
                    "something", Toast.LENGTH_SHORT).show();
        }
    };
    private FailureCallbackWithPayload<UserMessage> failureCallback = new FailureCallbackWithPayload<UserMessage>() {
        @Override
        public void fail(UserMessage userMessage) {
            Toast.makeText(FruitActivity.this, "Fail - maybe tell the user to try again, message:" + userMessage.getString(),
                           Toast.LENGTH_SHORT).show();
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fruit);

        ButterKnife.bind(this);

        setupButtonClickListeners();
    }

    private void setupButtonClickListeners() {

        fruitRefreshSuccess.setOnClickListener(v -> fruitFetcher.fetchFruits(successCallback, failureCallback));

        fruitRefreshFailBasic.setOnClickListener(v -> fruitFetcher.fetchFruitsButFailBasic(successCallback, failureCallback));

        fruitRefreshFailAdvanced.setOnClickListener(v -> fruitFetcher.fetchFruitsButFailAdvanced(successCallback, failureCallback));

    }


    //data binding stuff below

    public void syncView(){
        fruitRefreshSuccess.setEnabled(!fruitFetcher.isBusy());
        fruitRefreshFailBasic.setEnabled(!fruitFetcher.isBusy());
        fruitRefreshFailAdvanced.setEnabled(!fruitFetcher.isBusy());
        fruitName.setText(fruitFetcher.getCurrentFruit().name);
        isCitrus.setImageResource(fruitFetcher.getCurrentFruit().isCitrus ? R.drawable.lemon_positive : R.drawable.lemon_negative);
        tasteValuePercentView.setTastyPercent(fruitFetcher.getCurrentFruit().tastyPercentScore);
        tasteScore.setText(String.format(this.getString(R.string.fruit_percent), String.valueOf(fruitFetcher.getCurrentFruit().tastyPercentScore)));
        busy.setVisibility(fruitFetcher.isBusy() ?  VISIBLE : INVISIBLE);
        fruitDetailContainer.setVisibility(fruitFetcher.isBusy() ? GONE : VISIBLE);
    }


    @Override
    protected void onStart() {
        super.onStart();
        fruitFetcher.addObserver(observer);
        syncView(); //  <- don't forget this
    }


    @Override
    protected void onStop() {
        super.onStop();
        fruitFetcher.removeObserver(observer);
    }

}
