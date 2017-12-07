package org.schulcloud.mobile.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.view.inputmethod.InputMethodManager;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.ui.common.SwipeRefreshLayout;

public final class ViewUtil {

    public static float pxToDp(float px) {
        float densityDpi = Resources.getSystem().getDisplayMetrics().densityDpi;
        return px / (densityDpi / 160f);
    }

    public static int dpToPx(int dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm =
                (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
    }

    public static void initSwipeRefreshColors(@NonNull SwipeRefreshLayout swipeRefreshLayout) {
        Context c = swipeRefreshLayout.getContext();
        swipeRefreshLayout.setColorSchemeColors(
                ResourcesCompat.getColor(c.getResources(), R.color.hpiRed, c.getTheme()),
                ResourcesCompat.getColor(c.getResources(), R.color.hpiOrange, c.getTheme()),
                ResourcesCompat.getColor(c.getResources(), R.color.hpiYellow, c.getTheme()));
    }
}
