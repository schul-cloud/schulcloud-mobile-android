package org.schulcloud.mobile.util.dialogs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import org.schulcloud.mobile.R;

import butterknife.ButterKnife;

public class EditTextDialogBuilder extends DialogBuilder<String, EditTextDialogBuilder> {

    private CharSequence mTextDefault;
    private boolean mTextPreselected;

    private EditText vEt_text;

    public EditTextDialogBuilder(@NonNull Context context) {
        super(context);

        mTextDefault = null;
        mTextPreselected = true;
    }

    @NonNull
    public EditTextDialogBuilder textDefault(@StringRes int textDefaultRes) {
        mTextDefault = getContext().getString(textDefaultRes);
        return this;
    }
    @NonNull
    public EditTextDialogBuilder textDefault(@Nullable CharSequence textDefault) {
        mTextDefault = textDefault;
        return this;
    }

    @NonNull
    public EditTextDialogBuilder textPreselected(boolean textPreselected) {
        mTextPreselected = textPreselected;
        return this;
    }

    @NonNull
    @Override
    protected AlertDialog.Builder onBuild(@NonNull AlertDialog.Builder builder) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_text, null);
        vEt_text = ButterKnife.findById(view, R.id.text_et_text);
        vEt_text.setText(mTextDefault);

        if (mTextPreselected)
            vEt_text.selectAll();

        builder.setView(vEt_text);

        return builder;
    }
    @NonNull
    @Override
    protected String getSuccessValue() {
        return vEt_text.getText().toString();
    }
}
