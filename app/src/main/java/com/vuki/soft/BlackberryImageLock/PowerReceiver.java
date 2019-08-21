package com.vuki.soft.BlackberryImageLock;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.List;

public class PowerReceiver extends BroadcastReceiver {
    Intent mIntent;
    Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (mIntent == null) {
            mIntent = new Intent(context, UnlockActivity.class);
            mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        if (mContext == null) {
            mContext = context;
        }
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
//            start_lockscreen();
            Log.d("vukihai", "screen off");
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            Log.d("vukihai", "screen on");
//            if(!isActivityRunning(UnlockActivity.class)){
//                Log.d("vukihai", "activity not running yet!");
            start_lockscreen();
//            }
        } else {
            start_lockscreen();
        }
    }

    // Display lock screen
    private void start_lockscreen() {
        mContext.startActivity(mIntent);
    }

}

