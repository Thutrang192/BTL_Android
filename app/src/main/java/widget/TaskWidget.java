package widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;

import com.example.myapplication.LayoutCongViec;
import com.example.myapplication.R;

public class TaskWidget extends AppWidgetProvider {

    static void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetID) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.layout_widget_task);

        setRemoteAdapter(context, views);

        Intent intent = new Intent(context, LayoutCongViec.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE);

        //views.setPendingIntentTemplate(R.id.tv_date, pendingIntent);
        views.setOnClickPendingIntent(R.id.tv_date, pendingIntent);


        // Tạo Intent và PendingIntent để xử lý sự kiện click vao item trong listView
        Intent clickIntent = new Intent(context, TaskWidget.class);
        clickIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        PendingIntent clickPendingIntent = PendingIntent.getBroadcast(context, 0, clickIntent, PendingIntent.FLAG_MUTABLE);
        views.setPendingIntentTemplate(R.id.lv_task_widget, clickPendingIntent);


        appWidgetManager.updateAppWidget(appWidgetID, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

//        AppWidgetManager manager = AppWidgetManager.getInstance(context);
//        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.layout_widget_task);
//        ComponentName widget = new ComponentName(context, WidgetProvider.class);


    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        for (int appWidgetId : appWidgetIds) {

            // Tạo PendingIntent để mở màn hình khi nhấn vào widget
            Intent intent = new Intent(context, LayoutCongViec.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.putExtra("open_layout_congviec", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.layout_widget_task);
            views.setOnClickPendingIntent(R.id.ic_add_widget, pendingIntent);

            // Đặt remote adapter cho widget
            Intent intent1 = new Intent(context, WidgetTaskService.class);
            views.setRemoteAdapter(R.id.lv_task_widget, intent1);

            // Cập nhật widget
            appWidgetManager.updateAppWidget(appWidgetId, views);

        }

        // yêu cầu widget cập nhật dữ liệu sau khi RemoteViews Adapter được thiết lập
        //appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.lv_task_widget);

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }


    private static void setRemoteAdapter(Context context, @NonNull final  RemoteViews views) {
        //views.setRemoteAdapter(R.id.lv_task_widget, new Intent(context, WidgetService.class));

        Intent intent = new Intent(context, WidgetTaskService.class);
        views.setRemoteAdapter(R.id.lv_task_widget, intent);
    }

}
