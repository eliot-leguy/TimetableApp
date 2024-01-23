package perso.edt1;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;


public class Event implements Parcelable {
    public static ArrayList<Event> eventsList = new ArrayList<>();
    static Map<LocalDate, ArrayList<Event>> EventsByDay;
    private String module;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private ArrayList<String> room = new ArrayList<>();
    private ArrayList<String> teacher = new ArrayList<>();
    private ArrayList<String> group = new ArrayList<>();
    private String category;
    private String notes;
    public static ArrayList<String> localEdt = new ArrayList<>();

    public Event(int dayShift, LocalTime startTime, LocalTime endTime, String category, LocalDate weekStartDate, String module, ArrayList<String> room, ArrayList<String> teacher, ArrayList<String> group, String notes) {
        this.module = module;
        this.date = weekStartDate.plusDays(dayShift);
        this.startTime = startTime;
        this.endTime = endTime;
        this.room = room;
        this.teacher = teacher;
        this.group = group;
        this.category = category;
        this.notes = notes;
    }

    public Event(Parcel parcel) {
        this.module = parcel.readString();
        this.date = LocalDate.parse(parcel.readString());
        this.startTime = LocalTime.parse(parcel.readString());
        this.endTime = LocalTime.parse(parcel.readString());
        this.room = parcel.createStringArrayList();
        this.teacher = parcel.createStringArrayList();
        this.group = parcel.createStringArrayList();
        this.category = parcel.readString();
        this.notes = parcel.readString();
    }

    /**
     * Returns the events for a given date and time.
     *
     * @param date date of the events requested.
     * @param time time in hour of the events requested.
     * @return An ArrayList<Event> of the events for the given date and time.
     */
    public static ArrayList<Event> eventsForDateAndTime(LocalDate date, LocalTime time) {
        if (EventsByDay == null)
            return new ArrayList<Event>();
        ArrayList<Event> events = EventsByDay.get(date);
        ArrayList<Event> eventsForDateAndTime = new ArrayList<>();

        LocalTime eventHour;
        Event event;
        if(events == null)
            return eventsForDateAndTime;
        for (int i = 0; i < events.size(); i++) {
            event = events.get(i);
            eventHour = event.startTime;
            if (eventHour.equals(time) || eventHour.isBefore(time.plusHours(1)) && eventHour.isAfter(time))
                eventsForDateAndTime.add(event);
        }

        return eventsForDateAndTime;
    }

    /**
     * Convert the eventsList to a map EventsByDay, adding 'filling' events for the layouts and by sorting the events by day and hour.
     */
    public static void prepareEventListForView() {
        EventsByDay = sortedEventsByDay(addEmptyEvents(sortedEventsByDay(eventsByDay(eventsList))));
    }

    /**
     * Add 'fill' events between the events to space the layout.
     *
     * @param eventsByDaySorted the map of the events by day, they have to be sorted.
     * @return the map, with the empty events spaced between the events.
     */
    private static Map<LocalDate, ArrayList<Event>> addEmptyEvents(Map<LocalDate, ArrayList<Event>> eventsByDaySorted) {        //TODO : check why some events are not added

        LocalTime midnight = LocalTime.of(0, 0);
        LocalTime nextDayMidnight = LocalTime.of(23, 59);

        eventsByDaySorted.forEach((key, value) -> {
            ArrayList<Event> newEvents = new ArrayList<>();

            if (value.size() == 0) {
                Event emptyEvent = new Event(0, midnight, nextDayMidnight, "Fill", key, null, new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), "");
                newEvents.add(emptyEvent);
            } else {

                Event event = value.get(0);

                Event emptyEvent = new Event(0, midnight, event.getStartTime(), "Fill", event.getDate(), null, new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), "");
                newEvents.add(emptyEvent);

                for (int i = 0; i < value.size() - 1; i++) {
                    event = value.get(i);
                    newEvents.add(event);
                    Event nextEvent = value.get(i + 1);
                    if (event.getEndTime().isBefore(nextEvent.getStartTime())) {
//                        Log.d("Event", "addEmptyEvents: " + event.getEndTime() + " " + nextEvent.getStartTime());
                        emptyEvent = new Event(0, event.getEndTime(), nextEvent.getStartTime(), "Fill", event.getDate(), null, new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), "");
                        newEvents.add(emptyEvent);
                    } else {
//                        Log.d("Event", "Can't addEmptyEvents: " + event.getEndTime() + " " + nextEvent.getStartTime());
                    }
                }
                newEvents.add(value.get(value.size() - 1));
                newEvents.add(new Event(0, value.get(value.size() - 1).getEndTime(), nextDayMidnight, "Fill", key, null, new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), ""));

                eventsByDaySorted.put(key, newEvents);
            }
        });

        return eventsByDaySorted;
    }

    /**
     * Sort the events by day and put them into a map.
     * Also add an empty event for every empty day.
     *
     * @param events array list of all the events.
     * @return a map of the events sorted by day.
     */
    private static Map<LocalDate, ArrayList<Event>> eventsByDay(ArrayList<Event> events) {
        LocalDate minDate = events.get(0).getDate();
        LocalDate maxDate = events.get(0).getDate();
        LocalDate currentDate = maxDate;

        for (int i = 0; i < events.size(); i++) {
            currentDate = events.get(i).getDate();
            if (currentDate.isBefore(minDate))
                minDate = currentDate;
            if (currentDate.isAfter(maxDate))
                maxDate = currentDate;
        }

        // Create a map with all days between minDate and maxDate
       Map<LocalDate, ArrayList<Event>> EventsByDay = new Hashtable<>();
        currentDate = minDate;
        while (currentDate.isBefore(maxDate) || currentDate.isEqual(maxDate)) {
            EventsByDay.put(currentDate, new ArrayList<Event>());
            currentDate = currentDate.plusDays(1);
        }

        // Add events to the map
        for (int i = 0; i < events.size(); i++) {
            EventsByDay.get(events.get(i).getDate()).add(events.get(i));
        }

        currentDate = minDate;
        while (currentDate.isBefore(maxDate) || currentDate.isEqual(maxDate)) {
            if (EventsByDay.get(currentDate) == null) {
                ArrayList<Event> event = new ArrayList<Event>();
                event.add(new Event(0, LocalTime.of(0, 0), LocalTime.of(23, 59), "Fill", currentDate, null, new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), ""));
                EventsByDay.put(currentDate, event);
            }
            currentDate = currentDate.plusDays(1);
        }

        return EventsByDay;
    }

    /**
     * Sort the events by hour for each day.
     *
     * @param eventsByDay map of the events.
     * @return the map of the events.
     */
    private static Map<LocalDate, ArrayList<Event>> sortedEventsByDay(Map<LocalDate, ArrayList<Event>> eventsByDay) {
        Map<LocalDate, ArrayList<Event>> sortedEventsByDay = new Hashtable<LocalDate, ArrayList<Event>>();

        eventsByDay.forEach((key, value) -> {
            if(value.size() > 0) {
                ArrayList<Event> sortedEvents = new ArrayList<>();
                for (int i = 0; i < value.size(); i++) {
                    Event event = value.get(i);
                    if (sortedEvents.size() == 0)
                        sortedEvents.add(event);
                    else {
                        for (int j = 0; j < sortedEvents.size(); j++) {
                            if (event.getStartTime().isBefore(sortedEvents.get(j).getStartTime())) {
                                sortedEvents.add(j, event);
                                break;
                            }
                            if (j == sortedEvents.size() - 1) {
                                sortedEvents.add(event);
                                break;
                            }
                        }
                    }

                }
                sortedEventsByDay.put(key, sortedEvents);
            }
        });

        return sortedEventsByDay;
    }

    public String getModule() {
        return module;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public String getCategory() {
        return category;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public ArrayList<String> getRoom() {
        return room;
    }

    public ArrayList<String> getTeacher() {
        return teacher;
    }

    public ArrayList<String> getGroup() {
        return group;
    }

    public String getNotes() {
        return notes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(module);
        parcel.writeString(date.toString());
        parcel.writeString(startTime.toString());
        parcel.writeString(endTime.toString());
        parcel.writeStringList(room);
        parcel.writeStringList(teacher);
        parcel.writeStringList(group);
        parcel.writeString(category);
        parcel.writeString(notes);
    }

    public static final Creator<Event> CREATOR = new Parcelable.Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel parcel) {
            return new Event(parcel);
        }

        @Override
        public Event[] newArray(int i) {
            return new Event[0];
        }
    };
}
