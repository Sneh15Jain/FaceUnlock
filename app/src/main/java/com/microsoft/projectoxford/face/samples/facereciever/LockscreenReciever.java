package com.microsoft.projectoxford.face.samples.facereciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Sneh Jain on 04-11-2017.
 */


public class LockscreenReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Recieved",Toast.LENGTH_LONG).show();
        Log.w("Lock Screen", "Recieved");
        context.startService(new Intent(context, lockservice.class));

    }
}
