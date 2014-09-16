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
 * Created by walter on 29.08.14.
 */
public class LostDialog extends Dialog implements
        android.view.View.OnClickListener {

    public Activity c;
    public Button ok;

    public LostDialog(Activity activity) {
        super(activity);
        this.c = activity;
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_lost);
        ok = (Button) findViewById(R.id.ok_button);
        ok.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok_button:
                dismiss();
                c.finish();
                break;
            default:
                break;
        }
    }
}
