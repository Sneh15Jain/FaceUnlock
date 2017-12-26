package com.microsoft.projectoxford.face.samples.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.samples.vision.face.facetracker.FaceTrackerActivity;
import com.microsoft.projectoxford.face.samples.R;
import com.microsoft.projectoxford.face.samples.facereciever.lockservice;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getString(R.string.subscription_key).startsWith("Please")) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.add_subscription_key_tip_title))
                    .setMessage(getString(R.string.add_subscription_key_tip))
                    .setCancelable(false)
                    .show();
        }

        startService(new Intent(this, lockservice.class));

    public void detection(View view) {
        Intent intent = new Intent(this, FaceTrackerActivity .class);
        startActivity(intent);
    }
    public void facePersonVerification(View view)
    {
        Log.i("TAG","going into facePersonverifiation");
        Intent intent = new Intent(this, PersonVerificationActivity.class);
        startActivity(intent);
    }
}
