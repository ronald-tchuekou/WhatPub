package com.whatpub;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.whatpub.Services.WhatPubService;

public class StartServiceOnBootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: Les service est relancé.");
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, WhatPubService.class);
            context.startService(serviceIntent);
        }
        Toast.makeText(context, "WhatPub à relancé les services !!!", Toast.LENGTH_LONG).show();
    }
}
