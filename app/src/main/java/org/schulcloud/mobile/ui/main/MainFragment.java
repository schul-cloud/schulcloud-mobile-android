package org.schulcloud.mobile.ui.main;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import org.schulcloud.mobile.ui.base.BaseFragment;
import org.schulcloud.mobile.ui.base.MvpView;
import org.schulcloud.mobile.util.PermissionsUtil;

/**
 * Base class for any fragment that will be shown inside {@link MainActivity}.
 */
public abstract class MainFragment extends BaseFragment implements MvpView {
    private MainActivity mMainActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof MainActivity)
            mMainActivity = (MainActivity) context;
        else
            throw new IllegalStateException(MainFragment.class.getName()
                    + " may only be attached to an instance of " + MainActivity.class.getName());
    }

    /**
     * Return the {@link MainActivity} this fragment is associated with. May return {@code null} if
     * the fragment has not yet been associated with it's activity.
     */
    @NonNull
    public final MainActivity getMainActivity() {
        return mMainActivity;
    }

    /**
     * This method is called when the user presses the back button while this fragment is visible.
     *
     * @return True if the back press is handled by the fragment, false otherwise.
     */
    public boolean onBackPressed() {
        return false;
    }

    public void setTitle(@StringRes int titleId) {
        getMainActivity().setTitle(titleId);
    }
    public void addFragment(@NonNull MainFragment fragment) {
        getMainActivity().addFragment(this, fragment);
    }
    public void finish() {
        getMainActivity().removeFragment(this);
    }
    public void startService(@NonNull Intent service) {
        getMainActivity().startService(service);
    }
    public void stopService(@NonNull Intent name) {
        getMainActivity().stopService(name);
    }
    public boolean checkPermissions(int callbackId, String... permissions) {
        return PermissionsUtil.checkPermissions(callbackId, getMainActivity(), permissions);
    }
}
