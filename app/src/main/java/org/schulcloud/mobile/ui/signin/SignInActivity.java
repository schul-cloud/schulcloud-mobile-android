package org.schulcloud.mobile.ui.signin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.ui.base.BaseActivity;
import org.schulcloud.mobile.ui.dashboard.DashboardActivity;
import org.schulcloud.mobile.util.DialogFactory;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignInActivity extends BaseActivity implements SignInMvpView {

    @Inject
    SignInPresenter mSignInPresenter;

    @BindView(R.id.btn_login)
    Button btn_login;
    @BindView(R.id.input_username)
    EditText username;
    @BindView(R.id.input_password)
    EditText password;

    /**
     * Return an Intent to start this Activity.
     * triggerDataSyncOnCreate allows disabling the background sync service onCreate. Should
     * only be set to false during testing.
     */
    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, SignInActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        setContentView(R.layout.activity_signin);
        ButterKnife.bind(this);

        mSignInPresenter.attachView(this);
        btn_login.setOnClickListener(v -> {
            mSignInPresenter.signIn(username.getText().toString(), password.getText().toString());
        });
    }


    @Override
    protected void onDestroy() {
        mSignInPresenter.detachView();
        super.onDestroy();
    }


    @Override
    public void showSignInSuccessful() {
        DialogFactory.createSimpleOkErrorDialog(this, R.string.login_title, R.string.login_successful).
                show();
        Intent intent = new Intent(this, DashboardActivity.class);
        this.startActivity(intent);
    }

    @Override
    public void showSignInFailed() {
        DialogFactory.createGenericErrorDialog(this, getString(R.string.login_error))
                .show();
    }

    @Override
    public void goToSignIn() {
        // obsolete in SignInActivity
    }
}
