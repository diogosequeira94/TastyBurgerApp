package com.example.ifood.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.ifood.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                openAutentication();

            }
        }, 3000);
    }

    private void openAutentication(){
        Intent intent = new Intent(this,AutenticationActivity.class);
        startActivity(intent);
        finish();
    }
}
