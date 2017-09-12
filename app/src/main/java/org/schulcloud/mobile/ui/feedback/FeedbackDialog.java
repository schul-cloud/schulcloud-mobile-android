package org.schulcloud.mobile.ui.feedback;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Toast;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.ui.base.BaseDialog;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class FeedbackDialog extends BaseDialog implements FeedbackMvpView {
    private static final String TAG = FeedbackDialog.class.getSimpleName();

    public static final String ARGUMENT_CONTEXT_NAME = "contextName";
    public static final String ARGUMENT_CURRENT_USER = "currentUser";
    public static final String ARGUMENT_EMAIL = "email";
    public static final String ARGUMENT_OPINION = "opinion";

    private String mContextName;
    private String mCurrentUser;

    @Inject
    FeedbackPresenter mFeedbackPresenter;

    @BindView(R.id.email)
    TextInputEditText mEmailInput;
    @BindView(R.id.opinion)
    TextInputEditText mOpinionInput;
    @BindView(R.id.opinionWrapper)
    TextInputLayout mOpinionWrapper;

    private String mEmail;
    private String mOpinion;

    public FeedbackDialog() {
    }
    public static FeedbackDialog newInstance(String contextName, String currentUser) {
        FeedbackDialog feedbackDialog = new FeedbackDialog();

        Bundle arguments = new Bundle();
        arguments.putString(ARGUMENT_CONTEXT_NAME, contextName);
        arguments.putString(ARGUMENT_CURRENT_USER, currentUser);
        feedbackDialog.setArguments(arguments);

        return feedbackDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            if (getArguments() == null
                || getArguments().getString(ARGUMENT_CONTEXT_NAME) == null
                || getArguments().getString(ARGUMENT_CURRENT_USER) == null) {
                Timber.e(TAG, "Argument(s) missing/invalid");
                dismiss();
            }
            mContextName = getArguments().getString(ARGUMENT_CONTEXT_NAME);
            mCurrentUser = getArguments().getString(ARGUMENT_CURRENT_USER);
        } else {
            mContextName = savedInstanceState.getString(ARGUMENT_CONTEXT_NAME);
            mCurrentUser = savedInstanceState.getString(ARGUMENT_CURRENT_USER);

            mEmail = savedInstanceState.getString(ARGUMENT_EMAIL);
            mOpinion = savedInstanceState.getString(ARGUMENT_OPINION);
        }
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        activityComponent().inject(this);
        @SuppressLint("InflateParams")
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_feedback, null);
        ButterKnife.bind(this, view);
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
            .setTitle(R.string.feedback_title)
            .setView(view)
            .setPositiveButton(R.string.feedback_send, null)
            .setNegativeButton(R.string.dialog_action_cancel, null).create();
        dialog.setOnShowListener(dialog1 ->
            dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                .setOnClickListener(v ->
                    mFeedbackPresenter.sendFeedback(
                        getString(R.string.feedback_transmit_format),
                        mEmailInput.getText().toString().trim(),
                        mOpinionInput.getText().toString().trim(),
                        mContextName, mCurrentUser,
                        getString(R.string.feedback_transmit_subject),
                        getString(R.string.feedback_transmit_to))));

        mEmailInput.setText(mEmail);
        mOpinionInput.setText(mOpinion);

        mFeedbackPresenter.attachView(this);

        return dialog;
    }
    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }
    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }
    @Override
    public void showContentHint() {
        mOpinionWrapper.setError(getString(R.string.feedback_error_opinionEmpty));
    }
    @Override
    public void showFeedbackSent() {
        Toast.makeText(getContext(), R.string.feedback_transmit_sent, Toast.LENGTH_SHORT).show();
        dismiss();
    }
    @Override
    public void goToSignIn() {
    }
}
