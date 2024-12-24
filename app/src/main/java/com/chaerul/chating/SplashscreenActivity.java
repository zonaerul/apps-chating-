package com.chaerul.chating;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashscreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.splashscreen);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashscreenActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }, 3000);
    }
}
