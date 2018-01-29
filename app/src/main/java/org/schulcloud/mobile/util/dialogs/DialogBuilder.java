package org.schulcloud.mobile.util.dialogs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.util.Action;
import org.schulcloud.mobile.util.Action0;

import rx.Single;
import rx.SingleSubscriber;

@SuppressWarnings("unchecked")
public abstract class DialogBuilder<T, D extends DialogBuilder<T, D>> {
    private Context mContext;

    private CharSequence mTitle;

    private CharSequence mMessage;

    private CharSequence mPositiveTitle;

    private CharSequence mNegativeTitle;

    protected DialogBuilder(@NonNull Context context) {
        mContext = context;

        mTitle = null;

        mMessage = null;

        mPositiveTitle = mContext.getString(R.string.dialog_action_ok);

        mNegativeTitle = mContext.getString(R.string.dialog_action_cancel);
    }
    @NonNull
    protected Context getContext() {
        return mContext;
    }

    @NonNull
    public D title(@StringRes int titleRes) {
        mTitle = mContext.getString(titleRes);
        return (D) this;
    }
    @NonNull
    public D title(@Nullable CharSequence title) {
        mTitle = title;
        return (D) this;
    }

    @NonNull
    public D message(@StringRes int messageRes) {
        mMessage = mContext.getString(messageRes);
        return (D) this;
    }
    @NonNull
    public D message(@Nullable CharSequence message) {
        mMessage = message;
        return (D) this;
    }

    @NonNull
    public D positiveTitle(@StringRes int positiveTitleRes) {
        mPositiveTitle = mContext.getString(positiveTitleRes);
        return (D) this;
    }
    @NonNull
    public D positiveTitle(@Nullable CharSequence positiveTitle) {
        mPositiveTitle = positiveTitle;
        return (D) this;
    }

    @NonNull
    public D negativeTitle(@StringRes int negativeTitleRes) {
        mNegativeTitle = mContext.getString(negativeTitleRes);
        return (D) this;
    }
    @NonNull
    public D negativeTitle(@Nullable CharSequence negativeTitle) {
        mNegativeTitle = negativeTitle;
        return (D) this;
    }

    @NonNull
    private AlertDialog.Builder prepareBuild(@NonNull SingleSubscriber<? super T> e) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                .setTitle(mTitle)
                .setMessage(mMessage);

        builder.setPositiveButton(mPositiveTitle, (dialog, which) ->
                e.onSuccess(getSuccessValue()));
        builder.setNegativeButton(mNegativeTitle, (dialog, which) ->
                e.onError(new DialogCancelledException()));
        // If any button is selected this will be fired too, but after the respective button event
        builder.setOnDismissListener(dialog ->
                e.onError(new DialogDismissedException()));

        return builder;
    }
    @NonNull
    public final Single<T> buildAsSingle() {
        return Single.create(e -> onBuild(prepareBuild(e)).show());
    }
    @NonNull
    protected AlertDialog.Builder onBuild(@NonNull AlertDialog.Builder builder) {
        return builder;
    }

    @NonNull
    protected abstract T getSuccessValue();
}
