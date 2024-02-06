package perso.edt1;

import static java.lang.Math.round;
import static perso.edt1.Event.eventsForDateAndTime;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 * Implementation of App Widget functionality.
 */
public class AppWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        Log.d("AppWidget", "updateAppWidget: " + appWidgetId);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);

        //Getting the events
        ArrayList<Event> currentEvents = eventsForDateAndTime(LocalDate.now(), LocalTime.now());
        if(currentEvents.size() == 0)
        {
            currentEvents = eventsForDateAndTime(LocalDate.now().plusDays(1), LocalTime.of(8,0,0));
        }
//        LocalTime nextEventStartTime = CalendarUtils.getNextEvent(currentEvents.get(currentEvents.size()-1), false).getStartTime();
//        ArrayList<Event> nextEvents = null;
//
//        for(int i = 0; i < 4; i++) {
//            nextEvents = eventsForDateAndTime(LocalDate.now(), nextEventStartTime.plusHours(i));
//            if(nextEvents.size() > 0) {
//                break;
//            }
//        }


        //Setting the current event
        setCurrentEvent(views, currentEvents);






        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    static void setCurrentEvent(RemoteViews views, ArrayList<Event> events) {

        if(events.size() == 1)
        {
            _setEvent(views, events.get(0),1);
        }
        else if(events.size() == 2)
        {
            _setEvent(views, events.get(0),1);
            _setEvent(views, events.get(1),2);
        }
        else if(events.size() == 3)
        {
            _setEvent(views, events.get(0),1);
            _setEvent(views, events.get(1),2);
            _setEvent(views, events.get(2),3);
        }
        else        //TODO: Limiter à 4 events max
        {
            _setEvent(views, events.get(0),1);
            _setEvent(views, events.get(1),2);
            _setEvent(views, events.get(2),3);
            _setEvent(views, events.get(3),4);
        }
    }

    private static void _setEvent(RemoteViews view, Event event, int eventNb) {

        ArrayList<String> eventRooms = event.getRoom();
        String StringEventRoom = "";
        for (int i = 0; i < eventRooms.size(); i++) {
            StringEventRoom += eventRooms.get(i);
            if (i != eventRooms.size() - 1)
                StringEventRoom += ", ";
        }

//        String noteText = event.getNotes();

        switch (eventNb){
            case 1:
                view.setTextViewText(R.id.currentEvent1Module, event.getModule());
                view.setTextViewText(R.id.currentEvent1Hours, timeToText(event.getStartTime(), event.getEndTime()));
                view.setTextViewText(R.id.currentEvent1Room, StringEventRoom);
//                view.setTextViewText(R.id.currentEvent1Note, event.getNotes());
                break;
            case 2:
                view.setTextViewText(R.id.currentEvent2Module, event.getModule());
                view.setTextViewText(R.id.currentEvent2Hours, timeToText(event.getStartTime(), event.getEndTime()));
                view.setTextViewText(R.id.currentEvent2Room, StringEventRoom);
//                view.setTextViewText(R.id.currentEvent2Note, event.getNotes());
                break;
            case 3:
                view.setTextViewText(R.id.currentEvent3Module, event.getModule());
                view.setTextViewText(R.id.currentEvent3Hours, timeToText(event.getStartTime(), event.getEndTime()));
                view.setTextViewText(R.id.currentEvent3Room, StringEventRoom);
//                view.setTextViewText(R.id.currentEvent3Note, event.getNotes());
                break;
            case 4:
                view.setTextViewText(R.id.currentEvent4Module, event.getModule());
                view.setTextViewText(R.id.currentEvent4Hours, timeToText(event.getStartTime(), event.getEndTime()));
                view.setTextViewText(R.id.currentEvent4Room, StringEventRoom);
//                view.setTextViewText(R.id.currentEvent4Note, event.getNotes());
                break;
        }

        String eventCategory = event.getCategory().replaceAll("\\s+", "_");

        switch (eventCategory){
            case "CM":
                view.setInt(R.id.currentEvent1, "setBackgroundColor", R.color.CM);
                break;
            case "TD":
                view.setInt(R.id.currentEvent1, "setBackgroundColor", R.color.TD);
                break;
            case "TP":
                view.setInt(R.id.currentEvent1, "setBackgroundColor", R.color.TP);
                break;
            case "Examens":
                view.setInt(R.id.currentEvent1, "setBackgroundColor", R.color.Examens);
                break;
            case "TD_anglais":
                view.setInt(R.id.currentEvent1, "setBackgroundColor", R.color.TD_anglais);
                break;
            case "Vacances":
                view.setInt(R.id.currentEvent1, "setBackgroundColor", R.color.Vacances);
                break;
            default:
                view.setInt(R.id.currentEvent1, "setBackgroundColor", R.color.defaultEvent);
                break;
        }

        view.setInt(R.id.currentEvent1, "setVisibility", View.VISIBLE);
    }

    private static String timeToText(LocalTime startTime, LocalTime endTime){
        String startHour = startTime.getHour() < 10 ? "0" + startTime.getHour() : "" + startTime.getHour();
        String startMinute = startTime.getMinute() < 10 ? "0" + startTime.getMinute() : "" + startTime.getMinute();
        String endHour = endTime.getHour() < 10 ? "0" + endTime.getHour() : "" + endTime.getHour();
        String endMinute = endTime.getMinute() < 10 ? "0" + endTime.getMinute() : "" + endTime.getMinute();
        return startHour + ":" + startMinute + " - " + endHour + ":" + endMinute;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}