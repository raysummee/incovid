package com.ventricles.incovid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class ActivityStartup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        if (FirebaseAuth.getInstance().getCurrentUser()==null)
            startActivity(new Intent(this,LoginActivity.class));
        else {
            startActivity(new Intent(this, MainActivity.class));
        }
        finish();
    }
}
