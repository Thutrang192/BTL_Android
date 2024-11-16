package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import org.w3c.dom.Text;

public class LayoutDangKy extends AppCompatActivity {

    private EditText edtDKEmail, edtDKPass, edtDKPass2;
    private Button btnDangKy;
    private TextView tvDangNhap;
    private FirebaseAuth mAuth;
    private static final String TAG = "LayoutDangKy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_layout_dang_ky);

        edtDKEmail = findViewById(R.id.edtDKTaiKhoan);
        edtDKPass = findViewById(R.id.edtDKMatKhau);
        edtDKPass2 = findViewById(R.id.edtDKMatKhau2);
        btnDangKy = findViewById(R.id.btnDangKy);
        mAuth = FirebaseAuth.getInstance();
        tvDangNhap = findViewById(R.id.tvDangNhap);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LayoutDangKy.this, LayoutDangNhap.class);
                startActivity(intent);
            }
        });

        btnDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtDKEmail.getText().toString();
                String pass = edtDKPass.getText().toString();
                String rppass = edtDKPass2.getText().toString();

                if (email.equals("")||pass.equals("")||rppass.equals("")) {
                    Toast.makeText(LayoutDangKy.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!isValidEmail(email)) {
                    Toast.makeText(LayoutDangKy.this, "Email không hợp lệ", Toast.LENGTH_LONG).show();

                }

                if (pass.length() < 6 || !hasUpperCase(pass) || !hasLowerCase(pass) || !hasDigit(pass)) {
                    Toast.makeText(LayoutDangKy.this, "Mật khẩu có ít nhất 6 ký tự, gồm chữ hoa, chữ thường, chữ sô", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!pass.equals(rppass)) {
                    Toast.makeText(LayoutDangKy.this, "Mật khẩu không chính xác", Toast.LENGTH_LONG).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(
                        LayoutDangKy.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Intent intent = new Intent(LayoutDangKy.this, LayoutDangNhap.class);
                                    intent.putExtra("email", email);
                                    intent.putExtra("pass", pass);
                                    startActivity(intent);
                                    Toast.makeText(LayoutDangKy.this, "Đăng ký tài khoản thành công", Toast.LENGTH_LONG).show();

                                }
                            }
                        }
                );
            }
        });

    }

    private boolean isValidEmail(String email) {
        String emailMau = "[A-Za-z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";
        if (email.matches(emailMau) && email != null) {
            return true;
        } else return false;
    }

    private boolean hasUpperCase(String pass) {
        for (int i = 0; i < pass.length(); i++) {
            if (Character.isUpperCase(pass.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private boolean hasLowerCase(String pass) {
        for (int i = 0; i < pass.length(); i++) {
            if (Character.isLowerCase(pass.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private boolean hasDigit(String pass) {
        for (int i = 0; i < pass.length(); i++) {
            if (Character.isDigit(pass.charAt(i))) {
                return true;
            }
        }
        return false;
    }

}