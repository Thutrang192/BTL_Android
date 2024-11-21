package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;


import org.checkerframework.checker.units.qual.C;
import org.checkerframework.checker.units.qual.N;

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

    private FirebaseFirestore firestore;
    private DatabaseReference myRref;
    private FirebaseDatabase database;
    private NoteAdapter noteAdapter;
    private List<Note> lstNote;

    private String noteID;
    private boolean editNote = false;

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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getDate();
        getDataFromItemNote();

        // khoi tai Firestore
        firestore = FirebaseFirestore.getInstance();
        database = FirebaseDatabase.getInstance();
        myRref = database.getReference("notes");

        lstNote = new ArrayList<>();

    }

    private boolean getDataFromItemNote() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return false;
        }

        Note note = (Note) bundle.get("ItemNote");
        noteID = note.getId().toString();
        edtTitle.setText(note.getTitle());
        edtContent.setText(note.getContent());

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
        return super.onOptionsItemSelected(item);
    }

    private void addNote() {

        String title = edtTitle.getText().toString();
        String content = edtContent.getText().toString();
        String date = tvDate.getText().toString();

        if (getDataFromItemNote()) {
            Note updateNote = new Note(noteID, title, date, content);
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
            String id = myRref.push().getKey();
            if (!title.isEmpty() && !content.isEmpty()) {
                myRref.child(id).setValue(new Note(id, title, date, content)).addOnCompleteListener(new OnCompleteListener<Void>() {
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