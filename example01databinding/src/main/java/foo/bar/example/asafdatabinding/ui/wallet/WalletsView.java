package foo.bar.example.asafdatabinding.ui.wallet;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.early.fore.core.observer.Observer;
import foo.bar.example.asafdatabinding.CustomApp;
import foo.bar.example.asafdatabinding.R;
import foo.bar.example.asafdatabinding.feature.wallet.Wallet;

/**
 *
 */
public class WalletsView extends ScrollView {

    //models that we need to sync with
    private Wallet wallet;


    //UI elements that we care about
    @BindView(R.id.wallet_increase_btn)
    public Button increaseMobileWalletBtn;

    @BindView(R.id.wallet_decrease_btn)
    public Button decreaseMobileWalletBtn;

    @BindView(R.id.wallet_mobileamount_txt)
    public TextView mobileWalletAmount;

    @BindView(R.id.wallet_savingsamount_txt)
    public TextView savingsWalletAmount;



    //single observer reference
    Observer observer = this::syncView;



    public WalletsView(Context context) {
        super(context);
    }

    public WalletsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WalletsView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        wallet = CustomApp.get(Wallet.class);
    }

    private void setupButtonClickListeners() {
        increaseMobileWalletBtn.setOnClickListener(v -> {
            wallet.increaseMobileWallet();//notice how the data binding takes care of updating the view for you
        });
        decreaseMobileWalletBtn.setOnClickListener(v -> {
            wallet.decreaseMobileWallet();//notice how the data binding takes care of updating the view for you
        });
    }


    //data binding stuff below

    public void syncView(){
        increaseMobileWalletBtn.setEnabled(wallet.canIncrease());
        decreaseMobileWalletBtn.setEnabled(wallet.canDecrease());
        mobileWalletAmount.setText("" + wallet.getMobileWalletAmount());
        savingsWalletAmount.setText("" + wallet.getSavingsWalletAmount());
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        wallet.addObserver(observer);
        syncView(); //  <- don't forget this
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        wallet.removeObserver(observer);
    }
}
