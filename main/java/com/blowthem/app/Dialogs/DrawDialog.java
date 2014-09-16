package com.blowthem.app.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.blowthem.app.R;

/**
 * Created by walter on 10.09.14.
 */
public class DrawDialog extends Dialog implements android.view.View.OnClickListener {

    public Activity c;
    public Button btn_super;

    public DrawDialog(Activity activity) {
        super(activity);
        this.c = activity;
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_draw);
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


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                //show();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
