package com.example.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationReceiver  extends BroadcastReceiver {

    private static final String CHANNEL_ID = "TaskReminderChannel";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Lấy tên công việc từ Intent
        String taskName = intent.getStringExtra("taskName");
        // Tạo kênh thông báo (chỉ cần thực hiện nếu chưa có)
        createNotificationChannel(context);

        // Tạo Intent để mở Activity LayoutCongViec khi người dùng nhấn vào thông báo
        Intent openActivityIntent = new Intent(context, LayoutCongViec.class);
        openActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Tạo PendingIntent để mở Activity khi người dùng nhấn vào thông báo
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                openActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE // Thêm FLAG_IMMUTABLE nếu đang sử dụng Android 12 trở lên
        );

        // Tạo thông báo
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // Đảm bảo biểu tượng tồn tại
                .setContentTitle("Nhắc nhở công việc")
                .setContentText("Đã đến lúc hoàn thành: " + taskName)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent); // Gán PendingIntent vào thông báo


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // Kiểm tra quyền
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Log.d("DEBUG", "Chua cap quyen");
            return; // Thoát nếu quyền chưa được cấp
        }

        // Sử dụng hashCode của taskName làm ID thông báo để tránh trùng lặp
        int notificationId = taskName.hashCode();
        notificationManager.notify(notificationId, builder.build());
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = "Nhắc nhở công việc";
            String channelDescription = "Kênh thông báo nhắc nhở công việc";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
            channel.setDescription(channelDescription);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
