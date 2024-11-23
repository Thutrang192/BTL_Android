package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import Interface.iClickItemTask;
import model.Task;
import model.TaskAdapter;

public class LayoutCongViec extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private FirebaseDatabase database;
    private DatabaseReference taskRef;

    private CalendarView calendarView;
    private RecyclerView rvTask;
    private List<Task> lstTask;
    private TaskAdapter taskAdapter;
    private FloatingActionButton btn_add;
    private String selectedDate;
    private int hour = -1;
    private int minute = -1;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_overflow, menu);
        inflater.inflate(R.menu.menu_congviec, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void bottomNav() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setSelectedItemId(R.id.im_todo);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.im_note) {
                    startActivity(new Intent(getApplicationContext(), LayoutGhiChu.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                if (id == R.id.im_todo ) {
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
        setContentView(R.layout.activity_layout_cong_viec);

        Toolbar toolbar = findViewById(R.id.toolbar_todolist);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bottomNav();

        database = FirebaseDatabase.getInstance();
        firestore = FirebaseFirestore.getInstance();
        taskRef = database.getReference("tasks");

        calendarView = findViewById(R.id.calendarView);
        lstTask = new ArrayList<>();
        btn_add = findViewById(R.id.btn_add);
        rvTask = findViewById(R.id.rv_task);
        rvTask.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        taskAdapter = new TaskAdapter(lstTask, new iClickItemTask() {
            @Override
            public void onClickItemTask(Task task) {

            }

            @Override
            public void deleteData(String taskID) {
                taskRef.child(taskID).removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        if (error == null) {
                            Toast.makeText(LayoutCongViec.this, "Xóa thành công", Toast.LENGTH_LONG).show();
                        } else {
                            Log.d("DEBUG", "Xóa thất bại", error.toException());
                        }
                    }
                });
            }
        });

        rvTask.setAdapter(taskAdapter);

        readTaskFromRealtime();

        getDate();
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTaskDialog();
            }
        });


    }

    private void getDate() {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);  // Tháng bắt đầu từ 0
        int currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // lay ngay hien tai
        selectedDate = currentDayOfMonth + "/" + (currentMonth + 1) + "/" + currentYear;

        // lay ngay khi thay doi
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
        });
    }




    private void openTaskDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_dialog_add_task, null);
        EditText etTaskName = dialogView.findViewById(R.id.etTaskName);
        Button btnHuy = dialogView.findViewById(R.id.btn_huy);
        Button btnLuu = dialogView.findViewById(R.id.btn_luu);
        ImageView ivRemind = dialogView.findViewById(R.id.iv_remind);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ivRemind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.layout_dialog_time, null);
                TimePicker timePicker = dialogView.findViewById(R.id.timePicker);
                Button btnHuy = dialogView.findViewById(R.id.btn_huy);
                Button btnLuu = dialogView.findViewById(R.id.btn_luu);

                AlertDialog.Builder builder = new AlertDialog.Builder(LayoutCongViec.this);
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();

                btnHuy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                btnLuu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Lấy thời gian từ TimePicker
                        hour = timePicker.getHour();
                        minute = timePicker.getMinute();
                        Log.d("DEBUG", "hour" + hour + "minute" + minute);
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        btnLuu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String taskName = etTaskName.getText().toString().trim();
                if (taskName.isEmpty()) {
                    Toast.makeText(LayoutCongViec.this, "Vui lòng nhập tên công việc", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Log.d("DEBUG", "hour: " + hour + " minute: " + minute);


                // Tạo công việc mới
                String taskId = UUID.randomUUID().toString();
                Task newTask = new Task(taskId, taskName, selectedDate, false, hour, minute);
                // Thêm công việc vào danh sách và thông báo cho adapter
                lstTask.add(newTask);
                taskAdapter.notifyItemInserted(lstTask.size() - 1);  // Thông báo cho adapter rằng có phần tử mới ở cuối danh sách

                // Lưu công việc vào Firebase
                taskRef.child(taskId).setValue(newTask);

                // Đặt thông báo
                //setNotification(newTask);
                rvTask.scrollToPosition(lstTask.size() - 1);
                // Đóng hộp thoại
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void readTaskFromRealtime() {
        taskRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lstTask.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Task task = dataSnapshot.getValue(Task.class);
                    lstTask.add(task);
                }
                taskAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}