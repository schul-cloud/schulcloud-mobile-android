package org.schulcloud.mobile.ui.signin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.ui.base.BaseActivity;
import org.schulcloud.mobile.ui.main.MainActivity;
import org.schulcloud.mobile.util.dialogs.DialogFactory;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignInActivity extends BaseActivity implements SignInMvpView {

    @Inject
    SignInPresenter mSignInPresenter;

    @BindView(R.id.input_username)
    EditText username;
    @BindView(R.id.input_password)
    EditText password;
    @BindView(R.id.btn_login)
    Button login;
    @BindView(R.id.btn_demo_student)
    Button demoStudent;
    @BindView(R.id.btn_demo_teacher)
    Button demoTeacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);

        setContentView(R.layout.activity_signin);
        ButterKnife.bind(this);

        mSignInPresenter.attachView(this);
        login.setOnClickListener(v -> mSignInPresenter
                .signIn(username.getText().toString(), password.getText().toString(), false));

        demoStudent.setOnClickListener(v -> mSignInPresenter.signIn(
                getString(R.string.login_demo_student_username),
                getString(R.string.login_demo_student_password), true));
        demoTeacher.setOnClickListener(v -> mSignInPresenter.signIn(
                getString(R.string.login_demo_teacher_username),
                getString(R.string.login_demo_teacher_password), true));
    }

    @Override
    protected void onPause() {
        mSignInPresenter.detachView();
        super.onPause();
    }

    @Override
    public void showSignInSuccessful() {
        DialogFactory
                .createSimpleOkErrorDialog(this, R.string.login_title, R.string.login_successful)
                .show();
        startActivity(new Intent(this, MainActivity.class));
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
