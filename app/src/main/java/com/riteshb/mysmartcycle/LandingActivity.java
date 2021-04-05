package com.riteshb.mysmartcycle;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LandingActivity extends AppCompatActivity {
    Handler mHandler = new Handler();

    Runnable mHandlerTask = new Runnable()
    {
        @Override
        public void run() {
            Intent intent = new Intent(LandingActivity.this, DrawerActivity.class);
            startActivity(intent);
            finish();
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_landing);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        mHandlerTask.run();
        mHandler.postDelayed(mHandlerTask, 5000);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        hideSystemUi();
    }


}
