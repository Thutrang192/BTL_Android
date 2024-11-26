package widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class WidgetTaskService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetTaskProvider(this.getApplicationContext(), intent);
    }
}
