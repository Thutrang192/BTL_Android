package widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import model.Note;

public class WidgetNoteProvider implements RemoteViewsService.RemoteViewsFactory{

    List<Note> notes = new ArrayList<>();
    Context context;
    Intent intent;

    private FirebaseAuth auth;
    private DatabaseReference noteRef;
    private String userID;
    private FirebaseUser user;


    private boolean isDataLoaded = false;

    public WidgetNoteProvider(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user != null) {
            userID = user.getUid();
        }
    }

    private void init() throws NullPointerException {
        try {
            //notes.clear();

            if (userID != null) {

                noteRef = FirebaseDatabase.getInstance().getReference().child(userID).child("notes");
//            noteRef.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                        Note note = snapshot.getValue(Note.class);
//                        lstNote.add(note);
//                    }
//
//                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
//                    ComponentName widget = new ComponentName(context, WidgetNoteProvider.class);
//                    int[] appWidgetIds = appWidgetManager.getAppWidgetIds(widget);
//                    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.lv_note_widget);
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });

                noteRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Note> newNotes = new ArrayList<>();
                        if (snapshot.exists()) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                Note note = snapshot1.getValue(Note.class);
                                if (note != null && !containsNoteWitdID(note.getId())) {
                                    newNotes.add(note);
                                }
                            }
                        }

                        if (!isDataLoaded) {
                            notes.clear();
                            notes.addAll(newNotes);
                            isDataLoaded = true;
                            updateWidget();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                // lang nghe su thay doi theo thoi gian thuc
                noteRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Note newNote = snapshot.getValue(Note.class);
                        if (newNote != null && !containsNoteWitdID(newNote.getId())) {
                            notes.add(newNote);
                            updateWidget();

                        }


                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Note updateNote = snapshot.getValue(Note.class);
                        if (updateNote != null) {
                            for (int i = 0; i < notes.size(); i++) {
                                if (notes.get(i).getId().equals(updateNote.getId())) {
                                    notes.set(i, updateNote);
                                    updateWidget();
                                    break;
                                }
                            }
                        }
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                        Note removedNote = snapshot.getValue(Note.class);
                        if (removedNote != null) {
                            notes.removeIf(note -> note.getId().equals(removedNote.getId()));
                            updateWidget();
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
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void updateWidget() {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName componentName = new ComponentName(context, NoteWidget.class);
        appWidgetManager.notifyAppWidgetViewDataChanged(
                appWidgetManager.getAppWidgetIds(componentName),
                R.id.lv_note_widget
        );
    }

    private  boolean containsNoteWitdID(String noteId) {
        for (Note note : notes) {
            if (note.getId().equals(noteId)) {
                return  true;
            }
        }
        return  false;
    }



    @Override
    public void onCreate() {
        init();
    }

    @Override
    public void onDataSetChanged() {
        //init();
    }

    @Override
    public void onDestroy() {
        notes.clear();
    }

    @Override
    public int getCount() {
        return notes.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (notes == null || position < 0 || position >= notes.size()) {
            return null;
        }

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_note_item);

        Note note = notes.get(position);
        views.setTextViewText(R.id.tv_title_widget, note.getTitle());
        views.setTextViewText(R.id.tv_content_widget, note.getContent());
        views.setTextViewText(R.id.tv_date_widget, note.getDate());

        Intent intent1 = new Intent();
        Bundle bundle = new Bundle();
        intent1.putExtra("notedID", notes.get(position).getId());
        views.setOnClickFillInIntent(R.id.widget_note_item, intent1);

        // PendingIntent mở Activity chi tiết
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, notes.get(position).getId().hashCode(), intent, PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_note_item, pendingIntent);

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
