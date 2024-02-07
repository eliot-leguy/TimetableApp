package perso.edt1;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;

/**
 * Implementation of App Widget functionality.
 */
public class AppWidget extends AppWidgetProvider {

    static DBManager dbHelper;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        Log.d("AppWidget", "updateAppWidget: " + appWidgetId);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);

        //Getting the events
        ArrayList<Event> currentEvents = getCurrentEvent();
        if(currentEvents!=null) {
            //Setting the current event
            setCurrentEvent(views, currentEvents);
        }



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

        // Create an Intent to be triggered periodically
        Intent intent = new Intent(context, AppWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //long updateIntervalMillis = 1000 * 60 * 15; // 15 minutes

        // Schedule the periodic update using AlarmManager
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), updateIntervalMillis, pendingIntent);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        dbHelper = new DBManager(context);
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private static ArrayList<Event> getCurrentEvent(){
        Map<LocalDate, ArrayList<Event>> eventsMap = new Hashtable<LocalDate, ArrayList<Event>>();
        ArrayList<Event> dayEvents = new ArrayList<Event>();
        ArrayList<Event> currentEvent = new ArrayList<Event>();

        ArrayList<String> groups = dbHelper.getGroups(true);
        //Loading the events from the db, with 15 days of events to be sure to find an event even after an empty period.
        eventsMap = dbHelper.getEvents(LocalDate.now(), LocalDate.now().plusDays(15), groups);

        //Getting the current event or if there is no event now, the upcoming one
        for(int i = 0; i < 15; i++){
            dayEvents = eventsMap.get(LocalDate.now().plusDays(i));

            if(dayEvents != null){
                for(int j = 0; j < dayEvents.size(); j++){
                    //If the event is currently happening and is not a "Fill" event
                    if(dayEvents.get(j).getStartTime().isBefore(LocalTime.now()) && dayEvents.get(j).getEndTime().isAfter(LocalTime.now()) && !Objects.equals(dayEvents.get(j).getCategory(), "Fill")){
                        currentEvent.add(dayEvents.get(j));
                    }
                }
                //If no events are currently happening, getting the next one that is not a "Fill" event
                if(currentEvent.isEmpty()){
                    Boolean nextEventFound = false;
                    for(int j = 0; j < dayEvents.size(); j++){
                        if(!nextEventFound && dayEvents.get(j).getStartTime().isAfter(LocalTime.now()) && !Objects.equals(dayEvents.get(j).getCategory(), "Fill")){
                            currentEvent.add(dayEvents.get(j));
                            nextEventFound = true;
                        }
                        //When one event is found, getting the others event happening in the same time
                        if(nextEventFound){
                            if(dayEvents.get(j).getStartTime().equals(currentEvent.get(0).getStartTime()) && !Objects.equals(dayEvents.get(j).getCategory(), "Fill")){
                                currentEvent.add(dayEvents.get(j));
                            }
                        }
                    }
                }
                if(currentEvent.size() > 0){
                    return currentEvent;
                }
            }
        }
        return null;
    }
}