package widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class WidgetNoteService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetNoteProvider(this.getApplicationContext(), intent);
    }
}
