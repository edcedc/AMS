package com.yyc.ams.keyctrl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class KeyRecerver extends BroadcastReceiver {

    private static IKeyRecv mKeyRecvCallback;
    public static void setKeyRecvCallback(IKeyRecv kr) { mKeyRecvCallback = kr; }

    @Override
    public void onReceive(Context context, Intent intent) {
        int keyCode = intent.getIntExtra("keyCode", 0);
        if (keyCode == 0) {
            keyCode = intent.getIntExtra("keycode", 0);
        }

        boolean keyDown = intent.getBooleanExtra("keydown", false);
        if (keyDown) {
            if (mKeyRecvCallback != null) {
                mKeyRecvCallback.OnKeyDown(keyCode);
            }

        }else {//up
            if (mKeyRecvCallback != null) {
                mKeyRecvCallback.OnKeyUp(keyCode);
            }
        }

        Toast.makeText(context, "mKeyRecvCallback:"+((mKeyRecvCallback == null)?"null":"OK")+"keyCode = " + keyCode, Toast.LENGTH_SHORT);
    }
}
