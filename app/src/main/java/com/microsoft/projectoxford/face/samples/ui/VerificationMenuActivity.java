package com.microsoft.projectoxford.face.samples.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.microsoft.projectoxford.face.samples.R;
public class VerificationMenuActivity extends AppCompatActivity {

    // When the activity is created, set all the member variables to initial state.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_menu);
    }
    //face to face verification button click
    public void faceFaceVerification(View view)
    {
        Intent intent = new Intent(this, FaceVerificationActivity.class);
        startActivity(intent);
    }
}
