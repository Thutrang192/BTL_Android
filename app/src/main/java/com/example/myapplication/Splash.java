package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Kiểm tra xem ứng dụng có được mở từ widget không
        if (getIntent().hasExtra("open_layout_congviec")) {
            Intent intent = new Intent(this, LayoutCongViec.class);
            startActivity(intent);
            finish(); // Đóng SplashActivity ngay sau khi chuyển
            return;
        }

        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                nextActiviry();
            }

        }, 2000);
    }

    private void nextActiviry() {

       FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
       if (user == null) {
           // chua dang nhap
           Intent dn = new Intent(this, LayoutDangNhap.class);
           startActivity(dn);
       } else {
           Intent intent = new Intent(this, LayoutGhiChu.class);
           startActivity(intent);
       }

        finish();
    }
}