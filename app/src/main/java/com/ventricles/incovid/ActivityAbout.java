package com.ventricles.incovid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ActivityAbout extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        TextView txtStopCovid = findViewById(R.id.txtBtnstopcovid);
        txtStopCovid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });
    }

    private void sendEmail(){
        Intent mailIntent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.parse("mailto:?subject=" + "I want to help you stop COVID 19" + "&body=" + "here should be the message" + "&to=" + "raysummee@gmail.com");
        mailIntent.setData(data);
        startActivity(Intent.createChooser(mailIntent,"send mail"));
    }
}
