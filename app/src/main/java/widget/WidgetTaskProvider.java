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

import java.util.ArrayList;
import java.util.List;

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

                        updateWidget();
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Task updateTask = snapshot.getValue(Task.class);
                        if(updateTask != null){
                            for(int i=0;i<taskList.size();i++){
                                if(taskList.get(i).getId().equals(updateTask.getId())){
                                    taskList.set(i, updateTask);
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

//        // Kiểm tra danh sách trước khi truy cập
//        if (taskList == null || taskList.isEmpty()) {
//            Log.e("TashList", "Danh sach trong!");
//            return null;
//        }
//
//        // tao item hien thi trong widget
//        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_task_item);
//
//        Task task = taskList.get(position);
//        views.setTextViewText(R.id.tv_TaskName_widget, task.getName());
//
//        if (task.getHour() != -1 && task.getMinute() != -1) {
//            String time = String.format("%02d:%02d", task.getHour(), task.getMinute());
//            views.setTextViewText(R.id.tv_TaskTime_widget, time);
//        } else {
//            views.setTextViewText(R.id.tv_TaskTime_widget, "");
//        }


        if (position >= taskList.size() || taskList.get(position) == null) {
            return null; // Trả về null nếu dữ liệu không hợp lệ
        }

        Task task = taskList.get(position);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_task_item);
        views.setTextViewText(R.id.tv_TaskName_widget, task.getName());
        if(task.getHour() != -1 && task.getMinute() != -1){
            views.setTextViewText(R.id.tv_TaskTime_widget, task.getHour() + ":" + task.getMinute());
        }
        else{
            views.setTextViewText(R.id.tv_TaskTime_widget, "");
        }

        return views;

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
