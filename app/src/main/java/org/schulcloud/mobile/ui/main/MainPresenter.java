package org.schulcloud.mobile.ui.main;

import android.content.Context;
import android.support.annotation.NonNull;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.ui.base.MvpView;
import org.schulcloud.mobile.util.NetworkUtil;
import org.schulcloud.mobile.util.RxUtil;

import java.util.Stack;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

@ConfigPersistent
public class MainPresenter extends BasePresenter<MainMvpView> {
    private static final int TAB_LEVEL_TOP = 0;
    private static final int TAB_LEVEL_LAST = -1;
    private static final int TAB_LEVEL_ONE_BACK = -2;

    private final DataManager mDataManager;
    private Subscription mSubscription;

    private Stack<MainFragment>[] mFragments;
    private MainFragment mCurrentFragment;
    private int mCurrentTabIndex;
    private int mCurrentLevel;

    @Inject
    public MainPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    protected void onViewAttached(@NonNull MainMvpView mvpView) {
        super.onViewAttached(mvpView);
        if (mFragments == null) {
            MainFragment[] topLevelFragments = getViewOrThrow().getInitialFragments();

            //noinspection unchecked
            mFragments = (Stack<MainFragment>[]) new Stack[topLevelFragments.length];
            for (int i = 0; i < topLevelFragments.length; i++) {
                Stack<MainFragment> stack = new Stack<>();
                stack.push(topLevelFragments[i]);
                mFragments[i] = stack;
            }

            showFragment(0, TAB_LEVEL_TOP, false);
        }
    }
    /**
     * Checks whether there is already a logged-in user, if not so go to sign-in screen
     */
    public void checkSignedIn(@NonNull Context context) {
        // 1. try to get currentUser from prefs
        String currentUserId = mDataManager.getCurrentUserId();

        // value is "null" as String if pref does not exist
        if (currentUserId.equals("null")) {
            getViewOrThrow().goToSignIn();
            return;
        }

        // 2. if there is a valid jwt in the storage (just online)
        if (NetworkUtil.isNetworkConnected(context)) {
            RxUtil.unsubscribe(mSubscription);
            mSubscription = mDataManager.syncCurrentUser(currentUserId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            // onNext
                            currentUser -> {},
                            // onError, check failed
                            error -> {
                                Timber.e(error, "There was an error while fetching currentUser.");
                                sendToView(MvpView::goToSignIn);
                            });
        }
    }

    public void onTabSelected(int tabIndex) {
        if (tabIndex != mCurrentTabIndex)
            showFragment(tabIndex, TAB_LEVEL_LAST, false);
        else // If the user selects the same tab again, navigate back to the first fragment of the stack
            showFragment(tabIndex, TAB_LEVEL_TOP, false);
    }
    /**
     * Adds the fragment as the child of parent, and shows it. If parent already has a child (and
     * possibly sub-children), those are removed.
     *
     * @param parent The parent of the added fragment
     * @param child  The fragment to add
     */
    public void addFragment(@NonNull MainFragment parent, @NonNull MainFragment child) {
        for (int tabIndex = 0; tabIndex < mFragments.length; tabIndex++) {
            Stack<MainFragment> tabStack = mFragments[tabIndex];
            for (int level = 0; level < tabStack.size(); level++) {
                MainFragment fragment = tabStack.get(level);
                if (parent.equals(fragment)) {
                    int diff = tabStack.size() - tabStack.indexOf(parent) - 1;
                    for (int k = 0; k < diff; k++)
                        tabStack.pop();
                    tabStack.push(child);
                    showFragment(tabIndex, TAB_LEVEL_LAST, false);
                    return;
                }
            }
        }
    }
    /**
     * Removes the specified fragment from the hierarchy. Any child fragments will be removed too.
     *
     * @param fragment The fragment to be removed.
     * @return True if the fragment was removed, false otherwise (e.g., the fragment is the top
     * level fragment).
     */
    public boolean removeFragment(@NonNull MainFragment fragment) {
        if (fragment.equals(mCurrentFragment))
            return showFragment(mCurrentTabIndex, TAB_LEVEL_ONE_BACK, false);

        int i = mFragments[mCurrentTabIndex].indexOf(fragment);
        if (i >= 0)
            return showFragment(mCurrentTabIndex, i, false);

        for (int tabIndex = 0; tabIndex < mFragments.length; tabIndex++) {
            Stack<MainFragment> tabStack = mFragments[tabIndex];
            for (int level = 0; level < tabStack.size(); level++) {
                MainFragment f = tabStack.get(level);
                if (fragment.equals(f))
                    while (tabStack.size() > level + 1)
                        tabStack.pop();
            }
        }
        return true;
    }
    /**
     * Displays the fragment identified by its tab index and level. Child fragments are removed
     * automatically.
     *
     * @param tabIndex     The index of the tab that the fragment belongs to.
     * @param level        The level of the fragment. {@link #TAB_LEVEL_TOP}, {@link
     *                     #TAB_LEVEL_LAST} and {@link #TAB_LEVEL_ONE_BACK} are allowed.
     * @param closeIfEmpty If set to true and the other parameters would lead to the top level
     *                     fragment being removed, the app will be closed. Handy for back
     *                     navigation, but not if a fragment tries to remove itself.
     * @return True if the fragment identified by {@code tabIndex} and {@code level} is now
     * displayed, false otherwise (e.g., if that would have closed the app and that isn't
     * permitted).
     */
    private boolean showFragment(int tabIndex, int level, boolean closeIfEmpty) {
        Stack<MainFragment> tabStack = mFragments[tabIndex];
        if (level == TAB_LEVEL_LAST)
            level = tabStack.size() - 1;
        else if (level == TAB_LEVEL_ONE_BACK)
            level = tabStack.size() - 2;

        if (level < 0) {
            if (!closeIfEmpty)
                return false;
            getViewOrThrow().finish();
        } else {
            while (tabStack.size() > level + 1)
                tabStack.pop();
            MainFragment fragment = tabStack.get(level);

            sendToView(view -> view.showFragment(fragment, mCurrentTabIndex, tabIndex));

            mCurrentFragment = fragment;
            mCurrentTabIndex = tabIndex;
            mCurrentLevel = level;
        }
        return false;
    }
    public void onBackPressed() {
        if (!mCurrentFragment.onBackPressed())
            showFragment(mCurrentTabIndex, TAB_LEVEL_ONE_BACK, true);
    }
}

