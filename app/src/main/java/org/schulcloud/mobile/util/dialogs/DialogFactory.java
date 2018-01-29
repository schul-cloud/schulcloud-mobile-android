package org.schulcloud.mobile.util.dialogs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.github.johnpersano.supertoasts.library.SuperToast;

import org.schulcloud.mobile.R;

import butterknife.ButterKnife;
import rx.Single;

public final class DialogFactory {

    public static Dialog createSimpleOkErrorDialogMultiLine(Context context, String title, String message) {
        TextView textView = new TextView(context);
        textView.setTextColor(Color.parseColor("#000000"));
        textView.setTextSize(16);
        textView.setPadding(10, 5, 0, 0);
        textView.setText(title);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                .setCustomTitle(textView)
                .setMessage(message)
                .setNeutralButton(R.string.dialog_action_ok, null);
        return  alertDialog.create();
    }

    public static Dialog createSimpleOkErrorDialog(Context context, String title, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton(R.string.dialog_action_ok, null);
        return alertDialog.create();
    }

    public static AlertDialog.Builder createSimpleOkCancelDialog(Context context, String title, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(R.string.dialog_action_cancel, null)
                .setPositiveButton(R.string.dialog_action_ok, null);
        return alertDialog;
    }

    public static Dialog createSimpleOkErrorDialog(Context context,
                                                   @StringRes int titleResource,
                                                   @StringRes int messageResource) {

        return createSimpleOkErrorDialog(context,
                context.getString(titleResource),
                context.getString(messageResource));
    }

    public static Dialog createGenericErrorDialog(Context context, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.dialog_error_title))
                .setMessage(message)
                .setNeutralButton(R.string.dialog_action_ok, null);
        return alertDialog.create();
    }

    public static Dialog createGenericErrorDialog(Context context, @StringRes int messageResource) {
        return createGenericErrorDialog(context, context.getString(messageResource));
    }

    @CheckResult
    @NonNull
    public static Single<String> showSimpleTextInputDialog(@NonNull Context context,
            @NonNull String title, @NonNull String positiveTitle, @NonNull String negativeTitle) {
        return Single.create(e -> {
            View view = LayoutInflater.from(context).inflate(R.layout.dialog_text, null);
            EditText input = ButterKnife.findById(view, R.id.text_et_text);

            new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setView(view)
                    .setPositiveButton(positiveTitle, (dialog, which) ->
                            e.onSuccess(input.getText().toString()))
                    .setNegativeButton(negativeTitle, (dialog, which) ->
                            e.onError(new DialogCancelledException()))
                    .setOnDismissListener(dialog -> e.onError(new DialogDismissedException()))
                    .show();
        });
    }

    public static AlertDialog.Builder createSingleSelectDialog(@NonNull Context context,
            @NonNull CharSequence[] singleChoiceItems, int checkedItem,
            @Nullable DialogInterface.OnClickListener selectionChangedListener,
            @StringRes int messageResource) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setSingleChoiceItems(singleChoiceItems, checkedItem, selectionChangedListener);
        builder.setNegativeButton(R.string.dialog_action_cancel, null);
        builder.setPositiveButton(R.string.dialog_action_ok, null);
        builder.setTitle(context.getString(messageResource));
        return builder;
    }

    public static ProgressDialog createProgressDialog(Context context, String message) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message);
        return progressDialog;
    }

    public static ProgressDialog createProgressDialog(Context context,
                                                      @StringRes int messageResource) {
        return createProgressDialog(context, context.getString(messageResource));
    }


    public static SuperToast createSuperToast(Context context, String message, Integer color) {
        return SuperActivityToast.create(context, new Style(), Style.TYPE_BUTTON)
                .setButtonIconResource(R.mipmap.ic_launcher)
                .setProgressBarColor(Color.WHITE)
                .setText(message)
                .setDuration(Style.DURATION_SHORT)
                .setFrame(Style.FRAME_LOLLIPOP)
                .setColor(color)
                .setAnimations(Style.ANIMATIONS_POP);
    }

}
