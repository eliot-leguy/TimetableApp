package perso.edt1;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class EventsLoaderThread extends Thread{
    private Context context;
    private DatabaseLoadListener listener;
    private Handler uiHandler;
    private LocalDate dateOfCalendar;

    public EventsLoaderThread(Context context, DatabaseLoadListener listener, LocalDate dateOfCalendar) {
        this.context = context;
        this.listener = listener;
        this.uiHandler = new Handler(Looper.getMainLooper());
        this.dateOfCalendar = dateOfCalendar;
    }

    @Override
    public void run() {
        DBManager dbHelper = new DBManager(context);

        //Getting the groups out of the db :
        List<String> groups = dbHelper.getGroups(true);
        Log.d("EventsLoaderThread", "Groups : " + Arrays.toString(groups.toArray()));

        //Getting the first day of the week before the dateOfCalendar
        LocalDate firstDayOfCalendar = dateOfCalendar.minusDays(dateOfCalendar.getDayOfWeek().getValue() + 7);

        //Getting the last day of the week after the dateOfCalendar
        LocalDate lastDayOfCalendar = dateOfCalendar.plusDays(14 - dateOfCalendar.getDayOfWeek().getValue());

        //Getting the events from the db :
        Event.EventsByDay = dbHelper.getEvents(firstDayOfCalendar, lastDayOfCalendar, groups);

        // Notify the UI thread with the loaded events
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    listener.onEventsLoadComplete();
                }
            }
        });
    }

    public interface DatabaseLoadListener {
        void onEventsLoadComplete();
    }
}
