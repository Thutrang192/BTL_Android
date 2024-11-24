package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LayoutDangNhap extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText edtEmail, edtPass;
    private ImageView ivEye;
    private Button btnDangNhap;
    private TextView tvDangKy;

    // bien luu trang thai hien thi mat khau
    private boolean eye = false;
    private static final String TAG = "LayoutDangNhap";


    @Override
    protected void onStart() {
        super.onStart();

        mAuth = FirebaseAuth.getInstance();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            // neu user da dang nhap vao tu phien truoc thi su dung user luon
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_layout_dang_nhap);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        edtEmail = findViewById(R.id.edtTaiKhoan);
        edtPass = findViewById(R.id.edtMatKhau);
        btnDangNhap = findViewById(R.id.btnDangNhap);
        tvDangKy = findViewById(R.id.tvDangNhap);
        ivEye = findViewById(R.id.ivEye);


        ivEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eye) {
                    // an mat khau
                    edtPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    ivEye.setImageResource(R.drawable.ic_eye_closed);
                } else {
                    edtPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    ivEye.setImageResource(R.drawable.ic_eye_open);
                }
                // dao nguoc gia tri cua bien eye. Chuyen doi trang thai cua bien tu true -> false va nguoc lai
                eye = !eye;

                // di chuyen con tro ve cuoi
                edtPass.setSelection(edtPass.getText().length());

            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            Bundle ex = intent.getExtras();
            if (ex != null) {
                edtEmail.setText(ex.getString("email"));
                edtPass.setText(ex.getString("pass"));
            }
        }

        tvDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(LayoutDangNhap.this, LayoutDangKy.class);
                startActivity(intent1);

            }
        });

        btnDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString();
                String password = edtPass.getText().toString();
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LayoutDangNhap.this, "Vui lòng nhập email và mật khẩu", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!isValidEmail(email)) {
                    Toast.makeText(LayoutDangNhap.this, "Dia chi email khong hop le", Toast.LENGTH_LONG).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LayoutDangNhap.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(LayoutDangNhap.this, "Dang nhap thanh cong", Toast.LENGTH_LONG).show();
                            Intent intent1 = new Intent(LayoutDangNhap.this, LayoutGhiChu.class);
                            saveUserID();
                            startActivity(intent1);
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LayoutDangNhap.this, "Tai khoan hoac mat khau khong chinh xac", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    private void saveUserID() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userID = user.getUid();

            // luu userID vao SharePreferene
            SharedPreferences sharedPreferences = getSharedPreferences("MyAppGhiChu", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("userID", userID);
            editor.apply();
        }
    }

    private boolean isValidEmail(String email) {
        String emailMau = "[A-Za-z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";
        if (email.matches(emailMau) && email != null) {
            return true;
        } else return false;
    }
}