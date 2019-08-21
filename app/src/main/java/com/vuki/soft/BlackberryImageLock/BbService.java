package com.vuki.soft.BlackberryImageLock;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class BbService extends Service {
    BroadcastReceiver powerBroadcastReceiver;
    IntentFilter powerFilter;
    public BbService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        powerFilter = new IntentFilter();
        powerBroadcastReceiver = new PowerReceiver();
        powerFilter.addAction("android.intent.action.SCREEN_ON");
        powerFilter.addAction("android.intent.action.SCREEN_OFF");
        registerReceiver(powerBroadcastReceiver, powerFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
