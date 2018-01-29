package org.schulcloud.mobile.util.dialogs;

import android.content.Context;
import android.support.annotation.NonNull;

public class SimpleDialogBuilder extends DialogBuilder<Object, SimpleDialogBuilder> {

    public SimpleDialogBuilder(@NonNull Context context) {
        super(context);
    }

    @NonNull
    @Override
    protected Object getSuccessValue() {
        return new Object();
    }
}
