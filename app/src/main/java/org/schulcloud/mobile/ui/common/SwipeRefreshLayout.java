package org.schulcloud.mobile.ui.common;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;

/**
 * A {@link android.support.v4.widget.SwipeRefreshLayout} which does not lead to frozen artifacts
 * when active while the containing fragment is being removed.
 *
 * @see <a href="https://issuetracker.google.com/issues/37008170">Original issue in issue
 * tracker</a>
 */
public class SwipeRefreshLayout extends android.support.v4.widget.SwipeRefreshLayout {
    private boolean mSelfCancelled = false;

    public SwipeRefreshLayout(Context context) {
        super(context);
    }

    public SwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        if (isRefreshing()) {
            clearAnimation();
            setRefreshing(false);
            mSelfCancelled = true;
        }
        return super.onSaveInstanceState();
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        super.setRefreshing(refreshing);
        mSelfCancelled = false;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus && mSelfCancelled)
            setRefreshing(true);
    }
}
