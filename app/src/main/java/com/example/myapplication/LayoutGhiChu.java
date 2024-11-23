package com.example.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import org.checkerframework.checker.units.qual.N;

import java.util.ArrayList;
import java.util.List;

import Interface.iClickItemNote;
import model.Note;
import model.NoteAdapter;

public class LayoutGhiChu extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private RecyclerView rvNotes;
    private List<Note> lstNote;
    private NoteAdapter noteAdapter;
    private FloatingActionButton btnAdd;
    private ArrayAdapter<String> arrayAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_ghichu, menu);
        inflater.inflate(R.menu.menu_overflow, menu);

        MenuItem menuItem = menu.findItem(R.id.ic_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search");
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                noteAdapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                noteAdapter.filter(newText);
                return false;
            }
        });

        // lang nghe su kien thay doi focus, tu dong SearchView khi click ra ngoai
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    searchView.clearFocus();
                    searchView.setIconified(true);

                    // hien thi lai danh sach origin
                    noteAdapter.filter("");
                } else {

                }
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void bottomNav() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setSelectedItemId(R.id.im_note);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.im_note) {
                    return true;
                }
                if (id == R.id.im_todo ) {
                    startActivity(new Intent(getApplicationContext(), LayoutCongViec.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_layout_ghi_chu);

        Toolbar toolbar = findViewById(R.id.toolbar_note);
        setSupportActionBar(toolbar);
        // an hien thi ten ung dung
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bottomNav();

        database = FirebaseDatabase.getInstance();
        firestore = FirebaseFirestore.getInstance();
        myRef = database.getReference("notes");

        rvNotes = findViewById(R.id.rv_note);

        btnAdd = findViewById(R.id.btn_add);

        rvNotes.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        lstNote = new ArrayList<>();

        // khoi tao Adapter va gan vao RecyclerView
        noteAdapter = new NoteAdapter(lstNote, new iClickItemNote() {
            @Override
            public void onClickItemNote(Note note) {
                if (note.getPassword() == null || note.getPassword().isEmpty()) {
                    chitietghichu(note);
                } else {
                    Dialog dialog = new Dialog(LayoutGhiChu.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.layout_open_pass);

                    Window window = dialog.getWindow();
                    window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    Button btn_huy = dialog.findViewById(R.id.btn_huy);
                    Button btn_ok = dialog.findViewById(R.id.btn_ok);
                    EditText edtPass = dialog.findViewById(R.id.edt_passNote);

                    btn_huy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    btn_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (edtPass.getText().toString().equals(note.getPassword())) {
                                chitietghichu(note);
                                dialog.dismiss();
                            } else {
                                Toast.makeText(LayoutGhiChu.this, "Mật khẩu không chính xác", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    dialog.show();
                }
            }

            @Override
            public void deleteData(String noteID) {
                deleteDataFromRealtime(noteID);
            }
        });
        rvNotes.setAdapter(noteAdapter);

        readDataFromRealtime();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LayoutGhiChu.this, LayoutChiTietGhiChu.class);
                startActivity(intent);
            }
        });
    }

    private  void readDataFromRealtime() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lstNote.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    // lay doi tuong Note tu Firebase
                    Note note = dataSnapshot.getValue(Note.class);
                    lstNote.add(note);
                }

                // cap nhat danh sach goc
                noteAdapter.updateListSearch(new ArrayList<>(lstNote));

                // cap nhat RecyclerView
                noteAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("DEBUG", "Failes to read data from realtime", error.toException());
            }
        });
    }

    private void readDataFromoFirestore() {
        firestore.collection("notes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            lstNote.clear(); // xoa du lieu cu
                            for (QueryDocumentSnapshot document :task.getResult()) {
                                Note note = document.toObject(Note.class);
                                lstNote.add(note);
                            }

                            noteAdapter.notifyDataSetChanged(); // cap nhat du lieu moi
                        } else {
                            Log.d("DEBUG", "Loi doc du lieu", task.getException());
                        }
                    }
                });
    }

    public void deleteDataFromRealtime(String noteID) {
        myRef.child(noteID).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if (error == null) {
                    Toast.makeText(LayoutGhiChu.this, "Xóa ghi chú thành công", Toast.LENGTH_LONG).show();
                } else {
                    Log.d("DEBUG", "Xóa ghi chú thất bại", error.toException());
                }
            }
        });
    }

    public void deleteData(String idNote, int position) {
        firestore.collection("notes")
                .document(idNote)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // xoa item note ra khoi danh sach
                            lstNote.remove(position);
                            //originNote.remove(position);

                            // cap nhat RecyclerView
                            noteAdapter.notifyItemRemoved(position);
                            Toast.makeText(LayoutGhiChu.this, "Xoa ghi chu thanh cong", Toast.LENGTH_LONG).show();
                        } else {
                            Log.d("DEBUG", "Xoa ghi chu that bai", task.getException());
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        readDataFromRealtime();
    }

    private void chitietghichu(Note note) {
        Intent intent = new Intent(this, LayoutChiTietGhiChu.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("ItemNote", note);
         intent.putExtras(bundle);
        startActivity(intent);
    }
}