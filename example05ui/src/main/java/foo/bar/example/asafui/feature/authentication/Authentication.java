package foo.bar.example.asafui.feature.authentication;

import co.early.asaf.core.Affirm;
import co.early.asaf.core.WorkMode;
import co.early.asaf.core.callbacks.FailureCallbackWithPayload;
import co.early.asaf.core.callbacks.SuccessCallBack;
import co.early.asaf.core.logging.Logger;
import co.early.asaf.core.observer.ObservableImp;
import co.early.asaf.retrofit.CallProcessor;
import foo.bar.example.asafui.api.authentication.AuthenticationService;
import foo.bar.example.asafui.api.authentication.SessionRequestPojo;
import foo.bar.example.asafui.message.UserMessage;

/**
 *
 */
public class Authentication extends ObservableImp{

    private static final String TAG = Authentication.class.getSimpleName();

    private final AuthenticationService authenticationService;
    private final CallProcessor<UserMessage> callProcessor;
    private final WorkMode workMode;
    private final Logger logger;

    private String sessionToken = "";
    private boolean isBusy = false;

    public Authentication(AuthenticationService authenticationService, CallProcessor<UserMessage> callProcessor, WorkMode workMode, Logger logger) {
        super(workMode);
        this.authenticationService = Affirm.notNull(authenticationService);
        this.callProcessor = Affirm.notNull(callProcessor);
        this.workMode = Affirm.notNull(workMode);
        this.logger = Affirm.notNull(logger);
    }

    public void login(String username, String password, SuccessCallBack successCallBack, FailureCallbackWithPayload<UserMessage> failureCallbackWithPayload){

        logger.i(TAG, "login()");

        Affirm.notNull(username);
        Affirm.notNull(password);
        Affirm.notNull(failureCallbackWithPayload);

        if (isBusy){
            failureCallbackWithPayload.fail(UserMessage.ERROR_BUSY);
            return;
        }

        isBusy = true;
        notifyObservers();

        callProcessor.processCall(authenticationService.getSessionToken(new SessionRequestPojo(username, password), "3s"), workMode,
                successResponse -> {
                    sessionToken = successResponse.sessionToken;
                    successCallBack.success();
                    complete();
                },
                failureMessage -> {
                    failureCallbackWithPayload.fail(failureMessage);
                    complete();
                });

    }

    private void complete(){

        logger.i(TAG, "complete()");

        isBusy = false;
        notifyObservers();
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public boolean hasSessionToken(){
        return sessionToken.length()!=0;
    }

    public boolean isBusy() {
        return isBusy;
    }

    public boolean isEmail(String emailCandidate) {
        return (emailCandidate == null ? false : (emailCandidate.contains("@") && emailCandidate.contains(".") && (emailCandidate.length()>4)));
    }

    public boolean isPassword(String passwordCandidate) {
        return (passwordCandidate == null ? false : passwordCandidate.length() > 0);
    }

}
