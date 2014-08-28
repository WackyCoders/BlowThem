package com.blowthem.app.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.blowthem.app.R;

/**
 * Created by walter on 28.08.14.
 */
public class WaitDialog extends Dialog {

    public Activity c;
    public Dialog d;

    public WaitDialog(Activity a) {
        super(a);
        this.c = a;
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_wait_for_oponent);
    }
}
