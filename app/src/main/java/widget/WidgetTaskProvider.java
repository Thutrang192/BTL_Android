package widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import model.Task;

public class WidgetTaskProvider implements RemoteViewsService.RemoteViewsFactory {

    Context context;
    List<Task> taskList = new ArrayList<>();
    Intent intent;

    private FirebaseAuth auth;
    private DatabaseReference taskRef;
    private String userId;
    private FirebaseUser user;

    private boolean isDataLoaded = false;

    public WidgetTaskProvider(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        }
    }

    private void initializeData() throws NullPointerException {

        try {

            if (userId != null) {

            taskRef = FirebaseDatabase.getInstance().getReference().child(userId).child("tasks");

                taskRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Task> newTaskList = new ArrayList<>();
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Task task = snapshot.getValue(Task.class);
                                if (task != null && !containsTaskWithId(task.getId())) {
                                    newTaskList.add(task);
                                }
                            }
                        }

                        if (!isDataLoaded) {
                            taskList.clear();
                            taskList.addAll(newTaskList);
                            sortTaskListByDate();
                            Log.d("TaskList", "Task list changed. Updated size: " + taskList.size());
                            isDataLoaded= true;
                            updateWidget();
                        } else {
                            Log.d("TaskList", "No changes in task list.");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                taskRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Task newTask = snapshot.getValue(Task.class);
                        if(newTask != null && !containsTaskWithId(newTask.getId())){
                            taskList.add(newTask);

                        }
                        sortTaskListByDate();
                        updateWidget();

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Task updateTask = snapshot.getValue(Task.class);
                        if(updateTask != null){
                            for(int i=0;i<taskList.size();i++){
                                if(taskList.get(i).getId().equals(updateTask.getId())){
                                    taskList.set(i, updateTask);
                                    sortTaskListByDate();
                                    updateWidget();
                                    break;
                                }
                            }
                        }
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                        Task removeTask= snapshot.getValue(Task.class);
                        if(removeTask != null){
                            taskList.removeIf(task -> task.getId().equals(removeTask.getId()));  // Xóa ghi chú
                            sortTaskListByDate();
                            updateWidget();  // Cập nhật widget
                        }
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean containsTaskWithId(String taskId) {
        for (Task task : taskList) {
            if (task.getId().equals(taskId)) {
                return true;
            }
        }
        return false;
    }

    private void updateWidget() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName componentName = new ComponentName(context, TaskWidget.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(componentName);

        Log.d("Widget", "Notifying widget update for " + appWidgetIds.length + " widget(s)");
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.lv_task_widget);
    }


    @Override
    public void onCreate() {
        initializeData();
    }

    @Override
    public void onDataSetChanged() {

    }


    @Override
    public void onDestroy() {
        taskList.clear();
    }

    @Override
    public int getCount() {
        return taskList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {

        if (position >= taskList.size() || taskList.get(position) == null) {
            return null;
        }

        Task task = taskList.get(position);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_task_item);

        views.setTextViewText(R.id.tv_TaskName_widget, task.getName());
        views.setTextViewText(R.id.tv_TaskDate_widget, task.getDate());

        if(task.getHour() != -1 && task.getMinute() != -1){
            views.setTextViewText(R.id.tv_TaskTime_widget, task.getHour() + ":" + task.getMinute());
        }
        else{
            views.setTextViewText(R.id.tv_TaskTime_widget, "");
        }
        return views;

    }

    private void sortTaskListByDate() {
        Log.d("TaskList", "Sorting task list by date...");

        // Định dạng ngày như lưu trữ trong Firebase
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        try {
            // Sắp xếp danh sách theo ngày
            Collections.sort(taskList, (t1, t2) -> {
                try {
                    // Kiểm tra nếu `date` bị null
                    if (t1.getDate() == null) return 1; // T1 bị null, đẩy xuống cuối
                    if (t2.getDate() == null) return -1; // T2 bị null, đẩy xuống cuối

                    // Chuyển chuỗi `date` thành đối tượng `Date` để so sánh
                    Date date1 = dateFormat.parse(t1.getDate());
                    Date date2 = dateFormat.parse(t2.getDate());
                    return date2.compareTo(date1);
                } catch (ParseException e) {
                    Log.e("TaskList", "Error parsing date: " + e.getMessage());
                    return 0; // Không thay đổi thứ tự nếu lỗi
                }
            });

            Log.d("TaskList", "Task list sorted successfully.");
        } catch (Exception e) {
            Log.e("TaskList", "Error while sorting task list: " + e.getMessage());
        }
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
