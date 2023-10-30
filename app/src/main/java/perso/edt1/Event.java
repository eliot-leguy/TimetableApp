package perso.edt1;

import android.util.Log;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;


public class Event
{
    public static ArrayList<Event> eventsList = new ArrayList<>();
    private String module;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private ArrayList<String> room = new ArrayList<>();
    private ArrayList<String> teacher = new ArrayList<>();
    private ArrayList<String> group = new ArrayList<>();
    private String category;
    private String notes;

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

    public static ArrayList<Event> eventsForDate(LocalDate date)
    {
        ArrayList<Event> events = new ArrayList<>();

        for(Event event : eventsList)
        {
            if(event.getDate().equals(date))
                events.add(event);
        }

        return events;
    }

    public static ArrayList<Event> eventsForDateAndTime(LocalDate date, LocalTime time)
    {
        ArrayList<Event> events = new ArrayList<>();

        for(Event event : eventsList)
        {
            int eventHour = event.startTime.getHour();
            int cellHour = time.getHour();
            if(event.getDate().equals(date) && eventHour == cellHour)
                events.add(event);
        }

        Log.d("HourAdapter", "events.size()2 = " + events.size());

        return events;
    }

    public String getModule()
    {
        return module;
    }

    public void setModule(String module)
    {
        this.module = module;
    }

    public LocalDate getDate()
    {
        return date;
    }

    public void setDate(LocalDate date)
    {
        this.date = date;
    }

    public LocalTime getStartTime()
    {
        return startTime;
    }

    public void setStartTime(LocalTime startTime)
    {
        this.startTime = startTime;
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public LocalTime getEndTime()
    {
        return endTime;
    }

    public void setEndTime(LocalTime endTime)
    {
        this.endTime = endTime;
    }

    public ArrayList<String> getRoom()
    {
        return room;
    }

    public void setRoom(ArrayList<String> room) {
            this.room = room;
        }

    public ArrayList<String> getTeacher()
        {
            return teacher;
        }

    public void setTeacher(ArrayList<String> teacher)
        {
            this.teacher = teacher;
        }

    public ArrayList<String> getGroup()
        {
            return group;
        }

    public void setGroup(ArrayList<String> group)
        {
            this.group = group;
        }

    public String getNotes()
        {
            return notes;
        }

    public void setNotes(String notes)
        {
            this.notes = notes;
        }
}
