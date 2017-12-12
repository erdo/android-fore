package foo.bar.example.asafui.ui.login;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.early.asaf.core.callbacks.FailureCallbackWithPayload;
import co.early.asaf.core.callbacks.SuccessCallBack;
import co.early.asaf.core.logging.Logger;
import co.early.asaf.core.ui.SyncableView;
import foo.bar.example.asafui.CustomApp;
import foo.bar.example.asafui.feature.authentication.Authentication;
import foo.bar.example.asafui.message.UserMessage;
import foo.bar.example.asafui.ui.ViewUtils;
import foo.bar.example.asafui.ui.fruitcollector.FruitCollectorActivity;


public class LoginView extends ScrollView implements SyncableView {

    public static String TAG = LoginView.class.getSimpleName();

    private Authentication authentication;
    private Logger logger;


    @BindView(R.id.login_logo_imgview)
    public ImageView logo;
    @BindView(R.id.login_email_edittext)
    public EditText email;
    @BindView(R.id.login_email_text)
    public TextView emailNotValid;
    @BindView(R.id.login_password_edittext)
    public EditText password;
    @BindView(R.id.login_showpassword_switch)
    protected Switch showPassword;
    @BindView(R.id.login_login_button)
    public Button loginButton;
    @BindView(R.id.login_loading_progressbar)
    public ProgressBar loadingSpinner;
    @BindView(R.id.login_removablespacer_1)
    public FrameLayout removableView2;
    @BindView(R.id.login_removablespacer_2)
    public FrameLayout removableView1;


    public LoginView(Context context) {
        super(context);
    }

    public LoginView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoginView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    public void onFinishInflate() {// grab a reference to all the view elements, setup buttons listeners
        super.onFinishInflate();

        setupModelReferences();

        ButterKnife.bind(this, this);

        setupClickListeners();

        syncView();
    }

    private void setupModelReferences() {
        authentication = CustomApp.get(Authentication.class);
        logger = CustomApp.get(Logger.class);
    }

    private void setupClickListeners() {

        loginButton.setOnClickListener(v -> {

            logger.i(TAG, "loginButton clicked");

            final String emailStr = email.getText().toString();
            final String passwordStr = password.getText().toString();

            authentication.login(
                    emailStr, passwordStr,
                    () -> FruitCollectorActivity.start(ViewUtils.getActivityFromContext(getContext())),
                    failureMessage -> Toast.makeText(getContext(), "Login Failed, reason:" + failureMessage, Toast.LENGTH_LONG).show());
        });

        email.addTextChangedListener(new SyncerTextWatcher(this));
        password.addTextChangedListener(new SyncerTextWatcher(this));
        showPassword.setOnCheckedChangeListener(new SyncerCheckChanged(this));

    }

    public void syncView() {

        logger.i(TAG, "syncView()");

        if (!isInEditMode()) {

            boolean emailValid = authentication.isEmail(email.getText().toString());
            boolean passwordValid = authentication.isPassword(password.getText().toString());
            boolean enableLoginButton = (emailValid && passwordValid);

            emailNotValid.setVisibility(emailValid ? INVISIBLE : VISIBLE);
            loginButton.setEnabled(enableLoginButton);
            loginButton.setVisibility(!authentication.isBusy() ? VISIBLE : INVISIBLE);
            loadingSpinner.setVisibility(authentication.isBusy() ? VISIBLE : INVISIBLE);
        }
    }


}
