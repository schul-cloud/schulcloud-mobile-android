package org.schulcloud.mobile.ui.PasswordRecovery;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.datamanagers.FeedbackDataManager;
import org.schulcloud.mobile.data.datamanagers.UserDataManager;
import org.schulcloud.mobile.data.model.Account;
import org.schulcloud.mobile.data.model.User;
import org.schulcloud.mobile.data.model.requestBodies.FeedbackRequest;
import org.schulcloud.mobile.data.model.responseBodies.ResetResponse;
import org.schulcloud.mobile.data.sync.UserSyncService;
import org.schulcloud.mobile.ui.base.BaseActivity;



import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PasswordRecovery extends BaseActivity<PasswordRecoveryMvpView,PasswordRecoveryPresenter > implements PasswordRecoveryMvpView

{

    @Inject
    UserDataManager mUserDataManager;
    @Inject
    FeedbackDataManager mFeedbackDataManager;



    @Inject
    PasswordRecoveryPresenter mPasswordRecoveryPresenter;

    @BindView(R.id.btn_recovery_input_confirm)
    Button recoveryButton;
    @BindView(R.id.recovery_input_emailUser)
    EditText input_emailUser;
    @BindView(R.id.recovery_error_message)
    TextView errorMessageView;
    @BindView(R.id.recovery_code_input)
    EditText code_input;
    @BindView(R.id.recovery_pw_input)
    EditText pw_input;
    @BindView(R.id.recovery_pw_input_repeat)
    EditText pw_input_repeat;

    private int step = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        setContentView(R.layout.activity_password_recovery);
        setPresenter(mPasswordRecoveryPresenter);
        ButterKnife.bind(this);
        setStep(0);
        this.startService(UserSyncService.getStartIntent(this));
        recoveryButton.setOnClickListener(v-> executeStep() );

    }

    private void executeStep(){
        if(getStep() == 0) {
            RequestNewPassword(input_emailUser.getText().toString());
        }else if(getStep() == 1){
            CheckCode(code_input.getText().toString());
        }else if(getStep() == 2){
            PasswordsEqual(pw_input.getText().toString(),pw_input_repeat.getText().toString());
        }else if(getStep() == 3){
            PasswordValid(pw_input.getText().toString());
        }
    }

    private void setMessageText(String messageText, int color){
        errorMessageView.setText(messageText);
        errorMessageView.setTextColor(color);
    }

    private void RequestNewPassword(String input) {
        mPasswordRecoveryPresenter.requestReset(input);
    }

    private void PasswordValid(String password){
        if(mPasswordRecoveryPresenter.isNewPasswortValid(password)){
            showSuccessMessage();
            setStep(4);
            showSuccessMessage();
        }
        else{
            showErrorMessage();
        }
    }

    private void CheckCode(String code){
        mPasswordRecoveryPresenter.checkCode(code);
    }
    private void PasswordsEqual(String pw1,String pw2) {
        if (mPasswordRecoveryPresenter.passwordsEqual(pw1, pw2)){
            showSuccessMessage();
            setStep(3);
            executeStep();
        }else{
            showErrorMessage();
        }
    }

    public void setStep(int step){
        if(step==0){
            this.input_emailUser.setVisibility(View.VISIBLE);
            this.code_input.setVisibility(View.GONE);
            this.pw_input.setVisibility(View.GONE);
            this.pw_input_repeat.setVisibility(View.GONE);
        }else if(step==1){
            this.input_emailUser.setVisibility(View.GONE);
            this.code_input.setVisibility(View.VISIBLE);
            this.pw_input.setVisibility(View.GONE);
            this.pw_input_repeat.setVisibility(View.GONE);
        }
        else if(step==2 || step==3){
            this.input_emailUser.setVisibility(View.GONE);
            this.code_input.setVisibility(View.GONE);
            this.pw_input.setVisibility(View.VISIBLE);
            this.pw_input_repeat.setVisibility(View.VISIBLE);
        }
        this.step = step;
    }

    public int getStep(){
        return this.step;
    }

    @Override
    public void showSuccessMessage() {
        int color = getResources().getColor(R.color.hpiYellow);
        if(getStep() == 0)
            setMessageText(getResources().getString(R.string.recovery_success_request), color);
        else if(getStep()==1)
            setMessageText(getResources().getString(R.string.recovery_success_code_input), color);
        else if(getStep()==4)
            setMessageText(getResources().getString(R.string.recovery_success), color);
    }

    @Override
    public void showErrorMessage() {
        int color = getResources().getColor(R.color.hpiRed);
        if(getStep() == 0)
            setMessageText(getResources().getString(R.string.recovery_errorMessage_Email),color );
        else if(getStep()==1)
            setMessageText(getResources().getString(R.string.recovery_errorMessage_code_input), color);
        else if(getStep()==2)
            setMessageText(getResources().getString(R.string.recovery_errorMessage_pw_ungleich), color);
        else if(getStep()==3)
            setMessageText(getResources().getString(R.string.recovery_errorMessage_pw_fehlende_zeichen), color);
        else if(getStep()==4)
            setMessageText(getResources().getString(R.string.recovery_errorMessage_final_error), color);
    }

    @Override
    public void sendEmail(ResetResponse responseBody, Account account){
        String Code = responseBody._id;
        String text = getResources().getString(R.string.recovery_email_text);
        String subject = getResources().getString(R.string.recovery_reset_email_subject);

        String username =  (account.userId.firstName==""||account.userId.firstName == null) ? account.username : account.userId.firstName+" "+ account.userId.lastName;
        text = String.format(text, username,Code, "http://192.168.1.179:3100"+"/pwrecovery/"+Code);
        mFeedbackDataManager.sendEmail(new FeedbackRequest(text,subject,account.userId.email));

    }
}