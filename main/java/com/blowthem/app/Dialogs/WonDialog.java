package com.blowthem.app.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.blowthem.app.R;

/**
 * Created by walter on 29.08.14.
 */
public class WonDialog extends Dialog implements
        android.view.View.OnClickListener {

    public Activity c;
    public Button btn_super;

    public WonDialog(Activity activity) {
        super(activity);
        this.c = activity;
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_won);
        btn_super = (Button) findViewById(R.id.super_button);
        btn_super.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.super_button:
                dismiss();
                c.finish();
                break;
            default:
                break;
        }
    }
}
