//package widget;
//
//import android.content.Context;
//import android.widget.RemoteViews;
//import android.widget.RemoteViewsService;
//
//import androidx.annotation.NonNull;
//
//import com.example.myapplication.R;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import model.Task;
//
//public class TaskRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {
//
//    private final Context context;
//    private final List<Task> taskList = new ArrayList<>();
//    private final DatabaseReference taskRef = FirebaseDatabase.getInstance().getReference("tasks");
//
//    public TaskRemoteViewFactory(Context context) {
//        this.context = context;
//    }
//
//
//    @Override
//    public void onCreate() {
//
//        taskRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                taskList.clear();
//                for (DataSnapshot dataSnapshot : snapshot
//                        .getChildren()) {
//                    Task task = dataSnapshot.getValue(Task.class);
//                    if (task != null) {
//                        taskList.add(task);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//    }
//
//    @Override
//    public void onDataSetChanged() {
//        // du lieu thay doi lam moi widget
//    }
//
//    @Override
//    public void onDestroy() {
//        taskList.clear();
//    }
//
//    @Override
//    public int getCount() {
//        return taskList.size();
//    }
//
//    @Override
//    public RemoteViews getViewAt(int position) {
//
//        // tao item hien thi trong widget
//        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_task_item);
//        Task task = taskList.get(position);
//        views.setTextViewText(R.id.tv_TaskName_widget, task.getName());
//        return views;
//    }
//
//    @Override
//    public RemoteViews getLoadingView() {
//        return null;
//    }
//
//    @Override
//    public int getViewTypeCount() {
//        return 1;
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public boolean hasStableIds() {
//        return true;
//    }
//}
