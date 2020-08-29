package foo.bar.example.forereactiveui.ui.wallet;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import co.early.fore.core.observer.Observer;
import foo.bar.example.forereactiveui.OG;
import foo.bar.example.forereactiveui.R;
import foo.bar.example.forereactiveui.feature.wallet.Wallet;


public class WalletsActivity extends FragmentActivity {

    //models that we need to sync with
    private Wallet wallet = OG.get(Wallet.class);


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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wallet);

        ButterKnife.bind(this);

        setupButtonClickListeners();
    }

    private void setupButtonClickListeners() {
        increaseMobileWalletBtn.setOnClickListener(v -> {
            wallet.increaseMobileWallet();//notice how the data binding takes care of updating the view for you
        });
        decreaseMobileWalletBtn.setOnClickListener(v -> {
            wallet.decreaseMobileWallet();//notice how the data binding takes care of updating the view for you
        });
    }


    //reactive UI stuff below

    public void syncView(){
        increaseMobileWalletBtn.setEnabled(wallet.canIncrease());
        decreaseMobileWalletBtn.setEnabled(wallet.canDecrease());
        mobileWalletAmount.setText("" + wallet.getMobileWalletAmount());
        savingsWalletAmount.setText("" + wallet.getSavingsWalletAmount());
    }

    @Override
    protected void onStart() {
        super.onStart();
        wallet.addObserver(observer);
        syncView(); //  <- don't forget this
    }


    @Override
    protected void onStop() {
        super.onStop();
        wallet.removeObserver(observer);
    }

}
