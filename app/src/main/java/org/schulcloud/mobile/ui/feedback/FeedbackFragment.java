package org.schulcloud.mobile.ui.feedback;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.ui.base.BaseFragment;
import org.schulcloud.mobile.util.DialogFactory;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedbackFragment extends BaseFragment implements FeedbackMvpView {

    String mContextName;
    String mCurrentUser;

    @Inject
    FeedbackPresenter mFeedbackPresenter;

    @BindView(R.id.email)
    EditText mEmail;
    @BindView(R.id.opinion)
    EditText mOpinion;
    @BindView(R.id.send_feedback)
    BootstrapButton mSendFeedback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activityComponent().inject(this);
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);
        ButterKnife.bind(this, view);
        Bundle args = getArguments();

        mContextName = args.getString("contextName");
        mCurrentUser = args.getString("currentUser");

        mFeedbackPresenter.attachView(this);
        mSendFeedback.setOnClickListener(v -> mFeedbackPresenter.sendFeedback(mEmail.getText().toString().trim(), mOpinion.getText().toString().trim(), mContextName, mCurrentUser));

        return view;
    }

    @Override
    public void showContentHint() {
        mOpinion.setHint("Bitte ausfüllen");
        mOpinion.setError("Bitte ausfüllen");
    }

    /**
     * Hides Keyboard, shows Toast and closes Fragment
     */
    @Override
    public void showFeedbackSent() {
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        inputManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

        DialogFactory.createSuperToast(getActivity(), "Feedback erfolgreich gesendet", PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_GREEN)).show();

        getActivity().onBackPressed();
    }

    @Override
    public void goToSignIn() {

    }
}
