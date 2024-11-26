package widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;

import com.example.myapplication.LayoutChiTietGhiChu;
import com.example.myapplication.LayoutCongViec;
import com.example.myapplication.LayoutGhiChu;
import com.example.myapplication.R;

public class NoteWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        //CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.layout_widget_note);
        //views.setTextViewText(R.id.appwidget_text, widgetText);

        setRemoteAdapter(context, views);

        Intent intent = new Intent(context, LayoutGhiChu.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,PendingIntent.FLAG_MUTABLE );

        views.setOnClickPendingIntent(R.id.tv_title_widget, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            //updateAppWidget(context, appWidgetManager, appWidgetId);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.layout_widget_note);


            // Tạo PendingIntent để mở màn hình khi nhấn vào widget
            Intent intent = new Intent(context, LayoutGhiChu.class);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE);
            views.setOnClickPendingIntent(R.id.ic_addNote_widget, pendingIntent);

            // Liên kết Adapter của ListView trong Widget
            Intent intent2 = new Intent(context, WidgetNoteService.class);
            views.setRemoteAdapter(R.id.lv_note_widget, intent2);

            //  PendingIntent để xử lý khi click vào item
            Intent clickIntent = new Intent(context, LayoutChiTietGhiChu.class);
            PendingIntent clickPendingIntent = PendingIntent.getActivity(context, appWidgetId, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            views.setPendingIntentTemplate(R.id.lv_note_widget, clickPendingIntent);

            // Update widget
            appWidgetManager.updateAppWidget(appWidgetId, views);

        }

        //appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.lv_note_widget);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private static void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(R.id.lv_note_widget, new Intent(context, WidgetNoteService.class));
    }

}