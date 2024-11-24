package model;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import android.app.AlertDialog;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import Interface.iClickItemTask;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> lstTask;
    private iClickItemTask iClickItemTask;
    private DatabaseReference taskRef;

    // Constructor to initialize the task list
    public TaskAdapter(List<Task> taskList, iClickItemTask listener) {
        this.lstTask = taskList;
        this.iClickItemTask = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = lstTask.get(position);

        // Set task name, date, and time
        holder.tvTaskName.setText(task.getName());
        Context context = holder.itemView.getContext();



        if (task.getHour() != -1 && task.getMinute() != -1) {
            holder.tvTaskTime.setText(String.format("%02d:%02d", task.getHour(), task.getMinute()));
        } else {
            holder.tvTaskTime.setText("");
        }

        // Loại bỏ listener cũ để tránh callback không mong muốn
        holder.checkBoxCompleted.setOnCheckedChangeListener(null);

        holder.checkBoxCompleted.setChecked(task.getCompleted());

        // gach ngang chu khi hoan thanh
        if (task.getCompleted()) {
            holder.tvTaskName.setAlpha(0.5f);
            holder.tvTaskName.setPaintFlags(holder.tvTaskName.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.tvTaskName.setAlpha(1f);
            holder.tvTaskName.setPaintFlags(holder.tvTaskName.getPaintFlags()&~Paint.STRIKE_THRU_TEXT_FLAG);
        }

        holder.checkBoxCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setCompleted(isChecked);

            // cap nhat trang thai tren firebase
            taskRef = FirebaseDatabase.getInstance().getReference("tasks");
            taskRef.child(task.getId()).child("completed").setValue(isChecked);

            if (isChecked) {
                moveTaskToEnd(position);
            } else {
                notifyItemChanged(position);
            }

        });

        holder.ivDele.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    new AlertDialog.Builder(context)
                            .setTitle("Xóa công việc")
                            .setMessage("Bạn có chắc chắn muốn xóa ghi chú này?")
                            .setPositiveButton("Xóa", ((dialog, which) -> {
                                Task deleTask = lstTask.get(currentPosition);

                                iClickItemTask.deleteData(task.getId());
                                lstTask.remove(currentPosition);

                                notifyItemRemoved(currentPosition);

                            }))
                            .setNegativeButton("Hủy", null)
                            .show();
                }
            }
        });

        holder.layout_item_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iClickItemTask.onClickItemTask(task);
            }
        });
    }

    private void moveTaskToEnd(int position) {
        Task comletedTask = lstTask.get(position);
        lstTask.remove(position); // xoa khoi vi tri cu
        lstTask.add(comletedTask); // them vao cuoi danh sach
        notifyItemMoved(position, lstTask.size() -1); // thong bao ve su thay doi vi tri
    }

    @Override
    public int getItemCount() {
        return lstTask.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView tvTaskName, tvTaskTime;
        ImageView ivDele;
        CheckBox checkBoxCompleted;
        CardView layout_item_task;

        public TaskViewHolder(View itemView) {
            super(itemView);
            layout_item_task = itemView.findViewById(R.id.layout_item_task);
            tvTaskName = itemView.findViewById(R.id.tvTaskName);
            tvTaskTime = itemView.findViewById(R.id.tvTaskTime);
            checkBoxCompleted = itemView.findViewById(R.id.checkBoxCompleted);
            ivDele = itemView.findViewById(R.id.ic_delete);

        }
    }
}
