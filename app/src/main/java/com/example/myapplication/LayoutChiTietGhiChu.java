package com.example.myapplication;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;


import org.checkerframework.checker.units.qual.C;
import org.checkerframework.checker.units.qual.N;
import org.checkerframework.common.subtyping.qual.Bottom;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import model.Note;
import model.NoteAdapter;

public class LayoutChiTietGhiChu extends AppCompatActivity {

    private EditText edtTitle, edtContent;
    private TextView tvDate;
    private LinearLayout layout_ctghc;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private DatabaseReference myRref;
    private FirebaseDatabase database;
    private NoteAdapter noteAdapter;
    private List<Note> lstNote;

    private String noteID;
    private String passwordNote = "";
    private String newPass="";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_chitietghichu, menu);
        inflater.inflate(R.menu.menu_overflow, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void getView() {
        edtTitle = findViewById(R.id.edt_title);
        edtContent = findViewById(R.id.edt_content);
        tvDate = findViewById(R.id.tv_date);
        layout_ctghc = findViewById(R.id.layout_ctgc);
    }

    private void toolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_CTGC);
        setSupportActionBar(toolbar);
        // an hien thi ten ung dung
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // Thêm nút quay lại (back button) trong Toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_layout_chi_tiet_ghi_chu);

        getView();
        toolbar();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.layout_ctgc), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getDate();
        getDataFromItemNote();

        // khoi tai Firestore
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        database = FirebaseDatabase.getInstance();

        SharedPreferences sharedPreferences = getSharedPreferences("MyAppGhiChu", MODE_PRIVATE);
        String userID = sharedPreferences.getString("userID", null);
        myRref = database.getReference(userID).child("notes");

        lstNote = new ArrayList<>();

    }

    private boolean getDataFromItemNote() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            theme("#F2A7AD");
            Log.d("Activity", "Khong co bundle");
            return false;
        }

        Note note = (Note) bundle.get("itemNote");
//        Note note = (Note) bundle.get("widget");
//        if (note == null) {
//            Log.d("Activity", "Khong nhan dc Note");
//            return false;
//        }

        if (!note.getPassword().isEmpty()) {
            passwordNote = note.getPassword().toString();
        }

        noteID = note.getId().toString();
        edtTitle.setText(note.getTitle());
        edtContent.setText(note.getContent());
        layout_ctghc.setBackgroundColor(Color.parseColor(note.getTheme()));
        return true;

    }

    private void getDate() {
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        // dinh dang ngay gio
        SimpleDateFormat date = new SimpleDateFormat("HH:mm dd/MM/yyyy");
        String formatDate = date.format(currentDate);

        tvDate.setText(formatDate);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // android.R.id.home: ID cua nut quay lai
        if (item.getItemId() == android.R.id.home) {
            addNote();
            // quay lai activity truoc
            onBackPressed();
        }

        if (item.getItemId() == R.id.ic_lock) {

                Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.layout_dialog_pass);

                Window window = dialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                Button btn_huy = dialog.findViewById(R.id.btn_huy);
                Button btn_luu = dialog.findViewById(R.id.brn_luu);
                EditText edtPass = dialog.findViewById(R.id.edt_passNote);

                if (!passwordNote.isEmpty()) {
                    edtPass.setText(passwordNote);
                }

            Log.d("DEBUG", "passwordNote: " + passwordNote);


            btn_huy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                btn_luu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        newPass  = edtPass.getText().toString();
                        Log.d("DEBUG", "Mật khẩu mới: " + newPass);
                        dialog.dismiss();
                    }
                });

                dialog.show();
        }

        if (item.getItemId() == R.id.ic_share) {
            String content = edtContent.getText().toString();
            if (!content.isEmpty()) {
                shareNote(content);
            } else {
                Toast.makeText(LayoutChiTietGhiChu.this, "Ghi chú trống", Toast.LENGTH_LONG).show();
            }
        }
        if (item.getItemId() == R.id.ic_theme) {
            // tao bottomsheetdialog
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(LayoutChiTietGhiChu.this);
            
            View bottomsheet_choose_color = getLayoutInflater().inflate(R.layout.layout_bottomsheet_choose_color, null);
            bottomSheetDialog.setContentView(bottomsheet_choose_color);

            bottomSheetDialog.show();

            bottomsheet_choose_color.findViewById(R.id.iv_red).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   theme("#F2A7AD");
                }
            });

            bottomsheet_choose_color.findViewById(R.id.iv_orange).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    theme("#F2B9AC");
                }
            });

            bottomsheet_choose_color.findViewById(R.id.iv_blue).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    theme("#B2D9EA");
                }
            });

            bottomsheet_choose_color.findViewById(R.id.iv_green).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    theme("#9EBF99");
                }
            });

            bottomsheet_choose_color.findViewById(R.id.iv_violet).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    theme("#BEB6F2");
                }
            });

        }


        int id = item.getItemId();

        if (id == R.id.im_logout) {
            // Xóa thông tin userID khỏi SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("MyAppGhiChu", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("userID");
            editor.apply();

            auth.signOut();

            Intent intent = new Intent(LayoutChiTietGhiChu.this, LayoutDangNhap.class);
            startActivity(intent);
        }

        if (id == R.id.im_deleteAccount) {
            String userID = auth.getCurrentUser().getUid();

            if (userID != null) {
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(userID);

                Dialog dialog = new Dialog(LayoutChiTietGhiChu.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.layout_dialog_delete_account);

                Window window = dialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                Button btn_huy = dialog.findViewById(R.id.btn_huy_delete_account);
                Button btn_ok = dialog.findViewById(R.id.btn_ok_delete_account);

                btn_huy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        userRef.removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                if (error == null) {
                                    Toast.makeText(LayoutChiTietGhiChu.this, "Xóa tài khoản thành công", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(LayoutChiTietGhiChu.this, LayoutDangNhap.class);
                                    startActivity(intent);
                                    dialog.dismiss();
                                } else {
                                    Log.d("DEBUG", "Xóa tài khoản thất bại", error.toException());
                                }
                            }
                        });
                    }
                });
                dialog.show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareNote(String content) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, content);
        startActivity(Intent.createChooser(share, "Chia sẻ ghi chú qua "));
    }

    private void theme(String selectedColor) {
        ColorDrawable colorDrawable = (ColorDrawable) layout_ctghc.getBackground();
        colorDrawable.setColor(Color.parseColor(selectedColor));
    }

    private void addNote() {
        String title = edtTitle.getText().toString();
        String content = edtContent.getText().toString();
        String date = tvDate.getText().toString();
        ColorDrawable colorDrawable = (ColorDrawable) layout_ctghc.getBackground();
        String theme = String.format("#%06X", (0xFFFFFF & colorDrawable.getColor())); // Lấy mã màu HEX

        Log.d("DEBUG", theme);


        Log.d("DEBUG", "Mật khẩu trước khi lưu: " + newPass);


        if (getDataFromItemNote()) {
            if (!newPass.isEmpty()) {
                passwordNote = newPass;
            } else {
                passwordNote = "";
            }
            Log.d("DEBUG", "passwordNote: " + passwordNote);
            Log.d("DEBUG", "newPass 2: " + newPass);

            Note updateNote = new Note(noteID, title,content, date, theme, passwordNote);
            myRref.child(noteID).setValue(updateNote).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(LayoutChiTietGhiChu.this, "Sua ghi chu thanh cong", Toast.LENGTH_LONG).show();
                    } else {
                        Log.d("DEBUG", "Sua ghi chu that bai", task.getException());
                    }
                }
            });

        } else {
            if (newPass != null || !newPass.isEmpty()) {
                passwordNote = newPass;
            } else {
                passwordNote = "";
            }
            Log.d("DEBUG", "passwordNote: " + passwordNote);
            Log.d("DEBUG", "newPass: " + newPass);

            String idNewNote = myRref.push().getKey();
            if (!title.isEmpty() && !content.isEmpty()) {
                Log.d("DEBUG", "Mật khẩu duoc lưu: " + passwordNote);
                myRref.child(idNewNote).setValue(new Note(idNewNote, title, content, date, theme, passwordNote)).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LayoutChiTietGhiChu.this, "Them ghi chu thanh cong", Toast.LENGTH_LONG).show();
                        } else {
                            Log.d("DEBUG", "Them ghi chu that bai", task.getException());
                        }
                    }
                });
            }
        }

        layout_ctghc.setBackgroundColor(Color.parseColor(theme));
    }

    private void addNoteToFirestore() {
        String id = String.valueOf(lstNote.size() + 1);
        String title = edtTitle.getText().toString();
        String content = edtContent.getText().toString();
        String date = tvDate.getText().toString();

        if (!title.isEmpty() && !content.isEmpty()) {
            // them note vao Firestore
            CollectionReference notes = firestore.collection("notes");
            Note newNote = new Note(id, title, date, content);
            notes.add(newNote).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(LayoutChiTietGhiChu.this, "Them ghi chu thanh cong", Toast.LENGTH_LONG).show();

                    } else {
                        Log.d("DEBUG", "Them ghi chu that bai", task.getException());
                    }
                }
            });
        }
    }

}