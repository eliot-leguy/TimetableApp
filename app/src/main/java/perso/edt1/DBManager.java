package perso.edt1;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DBManager extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "events.db";
    private static final int DATABASE_VERSION = 1;

    //First table arguments
    private static final String TABLE_EVENTS = "events";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_MODULE = "module";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_START_TIME = "startTtime";
    private static final String COLUMN_END_TIME = "endTime";
    private static final String COLUMN_ROOM = "room";
    private static final String COLUMN_TEACHER = "teacher";
    private static final String COLUMN_GROUP = "EDTgroup";
    private static final String COLUMN_DISPLAYED_GROUP = "displayedGroup";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_NOTES = "notes";

    //Second table arguments
    private static final String TABLE_LOCAL_GROUPS = "localGroups";
    private static final String COLUMN_LOCAL_GROUP = "localGroup";
    private static final String COLUMN_SELECTED = "selected";

    public DBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createEventsTable = "CREATE TABLE IF NOT EXISTS " + TABLE_EVENTS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_DATE + " TEXT, "
                + COLUMN_START_TIME + " TEXT, "
                + COLUMN_END_TIME + " TEXT, "
                + COLUMN_MODULE + " TEXT,"
                + COLUMN_ROOM + " TEXT,"
                + COLUMN_TEACHER + " TEXT,"
                + COLUMN_GROUP + " TEXT,"
                + COLUMN_DISPLAYED_GROUP + " TEXT,"
                + COLUMN_CATEGORY + " TEXT,"
                + COLUMN_NOTES + " TEXT)";

        db.execSQL(createEventsTable);

        String createLocalGroupsTable = "CREATE TABLE IF NOT EXISTS " + TABLE_LOCAL_GROUPS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_LOCAL_GROUP + " TEXT, "
                + COLUMN_SELECTED + " INTEGER)";

        db.execSQL(createLocalGroupsTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        onCreate(db);
    }

    public void addEvent(Event event, String group) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_DATE, event.getDate().toString());
        values.put(COLUMN_START_TIME, event.getStartTime().toString());
        values.put(COLUMN_END_TIME, event.getEndTime().toString());
        values.put(COLUMN_MODULE, event.getModule());
        values.put(COLUMN_ROOM, event.getRoomString());
        values.put(COLUMN_TEACHER, event.getTeacherString());
        values.put(COLUMN_GROUP, group);
        values.put(COLUMN_DISPLAYED_GROUP, event.getGroupString());
        values.put(COLUMN_CATEGORY, event.getCategory());
        values.put(COLUMN_NOTES, event.getNotes());

        db.insert(TABLE_EVENTS, null, values);
        db.close();
    }

    @SuppressLint("Range")
    public ArrayList<Event> getEventsByDate(String date, String group) {
        ArrayList<Event> eventList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {COLUMN_ID, COLUMN_DATE, COLUMN_START_TIME, COLUMN_END_TIME, COLUMN_MODULE, COLUMN_ROOM, COLUMN_TEACHER, COLUMN_GROUP, COLUMN_DISPLAYED_GROUP, COLUMN_CATEGORY, COLUMN_NOTES};
        String selection = COLUMN_DATE + "=? AND " + COLUMN_GROUP + "=?";
        String[] selectionArgs = {date, group};

        Cursor cursor = db.query(TABLE_EVENTS, columns, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                LocalTime startTime = LocalTime.parse(cursor.getString(cursor.getColumnIndex(COLUMN_START_TIME)));
                LocalTime endTime = LocalTime.parse(cursor.getString(cursor.getColumnIndex(COLUMN_END_TIME)));
                String module = cursor.getString(cursor.getColumnIndex(COLUMN_MODULE));
                String roomString = cursor.getString(cursor.getColumnIndex(COLUMN_ROOM));
                String teacherString = cursor.getString(cursor.getColumnIndex(COLUMN_TEACHER));
                String displayedGroup = cursor.getString(cursor.getColumnIndex(COLUMN_DISPLAYED_GROUP));
                String category = cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY));
                String notes = cursor.getString(cursor.getColumnIndex(COLUMN_NOTES));


                ArrayList<String> roomList = new ArrayList<>();
                ArrayList<String> teacherList = new ArrayList<>();
                ArrayList<String> groupList = new ArrayList<>();

                if (roomString != null) {
                    String[] rooms = roomString.split(";");
                    roomList.addAll(Arrays.asList(rooms));
                }
                if (teacherString != null) {
                    String[] teachers = teacherString.split(";");
                    teacherList.addAll(Arrays.asList(teachers));
                }
                if (displayedGroup != null) {
                    String[] groups = displayedGroup.split(";");
                    groupList.addAll(Arrays.asList(groups));
                }

                Event event = new Event(0, startTime, endTime, category, LocalDate.parse(date), module, roomList, teacherList, groupList, notes);

                eventList.add(event);
            } while (cursor.moveToNext());
        } else {
            Log.d("DBManager", "getEventsByDate: No events found");
        }

        cursor.close();
        db.close();

        return eventList;
    }

    public void addAllEvents(Map<LocalDate, ArrayList<Event>> events, String group){
        for (Map.Entry<LocalDate, ArrayList<Event>> entry : events.entrySet()) {
            for (Event event : entry.getValue()) {
//                Log.d("DBManager", "addAllEvents: " + event.getModule() + " " + event.getGroupString() + " " + event.getDate().toString());
                addEvent(event, group);
            }
        }

    }

    public void addGroup(String group) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        Log.d("DBManager", "addGroup: " + group);

        values.put(COLUMN_LOCAL_GROUP, group);
        values.put(COLUMN_SELECTED, true);

        db.insert(TABLE_LOCAL_GROUPS, null, values);
        db.close();
    }

    public void deleteGroup(String group) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LOCAL_GROUPS, COLUMN_LOCAL_GROUP + " = ?", new String[]{group});
        db.close();
    }

    public void updateGroup(String group, boolean selected) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_SELECTED, String.valueOf(selected ? 1 : 0));

        db.update(TABLE_LOCAL_GROUPS, values, COLUMN_LOCAL_GROUP + " = ?", new String[]{group});
        db.close();
    }
    @SuppressLint("Range")
    public ArrayList<String> getGroups(Boolean selected) {
        ArrayList<String> groups = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {COLUMN_ID, COLUMN_LOCAL_GROUP};
        Cursor cursor;
        if(selected) {
            String selection = COLUMN_SELECTED + "=?";
            String[] selectionArgs = {"1"};

            cursor = db.query(TABLE_LOCAL_GROUPS, columns, selection, selectionArgs, null, null, null);
        } else {
            cursor = db.query(TABLE_LOCAL_GROUPS, columns, null, null, null, null, null);
        }

        if (cursor.moveToFirst()) {
            do {
                String group = cursor.getString(cursor.getColumnIndex(COLUMN_LOCAL_GROUP));
                groups.add(group);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return groups;
    }
    @SuppressLint("Range")
    public void getEvents(LocalDate firstDay, LocalDate lastDay, List<String> groups) {
        for(String group : groups){
            LocalDate currentDay = firstDay;
            while(currentDay.isBefore(lastDay)){
                ArrayList<Event> events = getEventsByDate(currentDay.toString(), group);
                ArrayList<Event> eventsByDay = Event.EventsByDay.get(currentDay);
                if(eventsByDay != null){
                    Event.EventsByDay.put(currentDay, mergeArrays(eventsByDay, events));
                } else {
                    Event.EventsByDay.put(currentDay, events);
                }



                currentDay = currentDay.plusDays(1);
            }
        }
    }


    public ArrayList<Event> mergeArrays(ArrayList<Event> eventsA, ArrayList<Event> eventsB){
        ArrayList<Event> mergedEvents = new ArrayList<>();
        for(Event eventA : eventsA){
            boolean found = false;
            for(Event eventB : eventsB){
                if(equalEvents(eventA, eventB)){
                    found = true;
                    break;
                }
            }
            if(!found){
                mergedEvents.add(eventA);
            }
        }
        mergedEvents.addAll(eventsB);
        return mergedEvents;
    }

    private static boolean equalEvents(Event eventA, Event eventB){
        boolean startTime = eventA.getStartTime().equals(eventB.getStartTime());
        boolean endTime = eventA.getEndTime().equals(eventB.getEndTime());
        boolean category;
        boolean room;
        boolean teacher;
        boolean module;

        if(eventA.getCategory() == null) {
            category = true;
        } if (eventB.getCategory() == null){
            category = true;
        } else category = eventA.getCategory().equals(eventB.getCategory());

        if(eventA.getRoom() == null) {
            room = true;
        } else if (eventB.getRoom() == null){
            room = true;
        } else room = eventA.getRoom().equals(eventB.getRoom());

        if(eventA.getTeacher() == null) {
            teacher = true;
        } else if (eventB.getTeacher() == null){
            teacher = true;
        } else teacher = eventA.getTeacher().equals(eventB.getTeacher());

        if(eventA.getModule() == null) {
            return eventB.getModule() == null;
        } else if (eventB.getModule() == null){
            return false;
        } else module = eventA.getModule().equals(eventB.getModule());

        return startTime && endTime && category && room && teacher && module;
    }
}
