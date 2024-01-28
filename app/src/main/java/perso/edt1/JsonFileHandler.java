package perso.edt1;

import static java.lang.Character.isUpperCase;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;

public class JsonFileHandler extends Application {

    /**
     * Read the json files inside of the DirectoryPath and parse them into the Event class to use them.
     * It loads three weeks of calendar around the date given in parameter.
     *
     * @param DirectoryPath path of the directory containing the JSON files of the calendar.
     * @param context context of the activity.
     * @param date date of the calendar to load.
     */
    public static void loadEdtJson(File DirectoryPath, Context context, LocalDate date){

        ArrayList<File> files = getFiles(DirectoryPath);
        if(files.size() == 0){
            Toast.makeText(context, "No Local Edt", Toast.LENGTH_SHORT).show();
        } else {
            for (File file : files) {
                String fileName = file.getName();
                if (fileName.endsWith(".json")) {
                    String fileNameWithoutExtension = fileName.substring(0, fileName.length() - 5);
                    if((Event.selectedEdt.size() > 0 && Event.selectedEdt.contains(fileNameWithoutExtension)) || Event.selectedEdt.size() == 0){
                        Log.d("JsonFileHandler", "loadEdtJson: " + fileName);
                        Event.localEdt.add(fileNameWithoutExtension);
                        readEventsFromJsonFile(DirectoryPath + "/" + fileName, date);
                    }
                }
            }
        }
    }

    protected static ArrayList<File> getFiles(File directory){
        ArrayList<File> files = new ArrayList<>();
        File[] filesArray = directory.listFiles();

        assert filesArray != null;
        for(File file : filesArray){
            if(file.isDirectory()){
                files.addAll(getFiles(file));
            }else{
                files.add(file);
            }
        }

        return files;
    }

    /**
     * Read the JSON file at filePath and put his content into a JSONArray.
     * Then calls jsonToEvents(JsonEventArray, date) to transform the JSONArray into a map of events around the specified date.
     *
     * @param filePath path of the .json file to read.
     * @param date date of the calendar to load.
     */
    public static void readEventsFromJsonFile(String filePath, LocalDate date) {
        //We still have to load the entire file before being able to select which days we want to display
        StringBuilder stringBuilder = new StringBuilder();
        JSONArray JsonEventArray = new JSONArray();

        try {
            // Open FileInputStream for reading
            FileInputStream inputStream = new FileInputStream(filePath);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            // Read file content into StringBuilder
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            // Close the streams
            bufferedReader.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            // Parse the JSON string into a JSONArray
            JsonEventArray = new JSONArray(stringBuilder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("JsonFileHandler", "readEventsFromJsonFile: " + JsonEventArray.length());
        jsonToEvents(JsonEventArray, date);
    }

    /**
     * Transforms the content of a JSONArray into a map of events, loading three weeks around the specified date.
     * Then pass the map of events to the Event class.
     *
     * @param eventsArray JSONArray of the events read from a JSON file.
     * @param dateOfCalendar date of the calendar to load.
     */
    public static void jsonToEvents(JSONArray eventsArray, LocalDate dateOfCalendar) {
        Map<LocalDate, ArrayList<Event>> EventsByDay = new Hashtable<LocalDate, ArrayList<Event>>();

        //Getting the first day of the week before the dateOfCalendar
        LocalDate firstDayOfCalendar = dateOfCalendar.minusDays(dateOfCalendar.getDayOfWeek().getValue() + 7);

        //Getting the last day of the week after the dateOfCalendar
        LocalDate lastDayOfCalendar = dateOfCalendar.plusDays(14 - dateOfCalendar.getDayOfWeek().getValue());

        Log.d("JsonFileHandler", "jsonToEvents: " + firstDayOfCalendar + " " + lastDayOfCalendar);


        for (int i = 0; i < eventsArray.length(); i++) {
            try {
                JSONObject JsonEvent = eventsArray.getJSONObject(i);
                LocalDate date = LocalDate.parse(JsonEvent.getString("date"));

                // We only want to load three weeks of calendar around the dateOfCalendar
                //Maybe we can use a dichotomy to find the first and last day of the calendar
                if(date.isBefore(lastDayOfCalendar) || date.isAfter(firstDayOfCalendar)) {

                    ArrayList<String> roomList = new ArrayList<>();
                    if (JsonEvent.has("room")) {
                        String roomStr = JsonEvent.getString("room");
                        StringBuilder roomStrBuilder = new StringBuilder();
                        char currentChar;
                        for (int j = 0; j < roomStr.length(); j++) {
                            currentChar = roomStr.charAt(j);
                            if (currentChar == ',' || currentChar == ']') {
                                roomList.add(roomStrBuilder.toString());
                                roomStrBuilder = new StringBuilder();
                            } else if (currentChar != '[') {
                                roomStrBuilder.append(currentChar);
                            }
                        }
                    }

                    ArrayList<String> teacherList = new ArrayList<>();
                    if (JsonEvent.has("teacher")) {
                        String teacherStr = JsonEvent.getString("teacher");
                        StringBuilder teacherStrBuilder = new StringBuilder();
                        char currentChar;
                        for (int j = 0; j < teacherStr.length(); j++) {
                            currentChar = teacherStr.charAt(j);
                            if (currentChar == ',' && !isUpperCase(teacherStr.charAt(j - 1)) || currentChar == ']') {
                                teacherList.add(teacherStrBuilder.toString());
                                teacherStrBuilder = new StringBuilder();
                            } else if (currentChar != '[') {
                                teacherStrBuilder.append(currentChar);
                            }
                        }
                    }

                    ArrayList<String> groupList = new ArrayList<>();
                    if (JsonEvent.has("group")) {
                        String groupStr = JsonEvent.getString("group");
                        StringBuilder groupStrBuilder = new StringBuilder();
                        char currentChar;
                        for (int j = 0; j < groupStr.length(); j++) {
                            currentChar = groupStr.charAt(j);
                            if (currentChar == ',' || currentChar == ']') {
                                groupList.add(groupStrBuilder.toString());
                                groupStrBuilder = new StringBuilder();
                            } else if (currentChar != '[') {
                                groupStrBuilder.append(currentChar);
                            }
                        }
                    }

                    LocalTime startTime = null;
                    if (JsonEvent.has("startTime")) {
                        startTime = LocalTime.parse(JsonEvent.getString("startTime"));
                    }
                    LocalTime endTime = null;
                    if (JsonEvent.has("endTime")) {
                        endTime = LocalTime.parse(JsonEvent.getString("endTime"));
                    }
                    String category = null;
                    if (JsonEvent.has("category")) {
                        category = JsonEvent.getString("category");
                    }

                    String module = null;
                    if (JsonEvent.has("module")) {
                        module = JsonEvent.getString("module");
                    }
                    String notes = null;
                    if (JsonEvent.has("notes")) {
                        notes = JsonEvent.getString("notes");
                    }

                    Event event = new Event(0,
                            startTime,
                            endTime,
                            category,
                            date,
                            module,
                            roomList,
                            teacherList,
                            groupList,
                            notes);

                    date = event.getDate();
                    if (EventsByDay.containsKey(date)) {
                        EventsByDay.get(date).add(event);
                    } else {
                        ArrayList<Event> dailyEvent = new ArrayList<>();
                        dailyEvent.add(event);
                        EventsByDay.put(date, dailyEvent);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.d("JsonFileHandler", "jsonToEvents: " + EventsByDay);

        if(Event.EventsByDay != null && Event.EventsByDay.size() > 0){
            Event.EventsByDay.putAll(EventsByDay);
            Event.removeRedundantEvents();
        } else {
            Event.EventsByDay = EventsByDay;
        }
    }


    /**
     * Write a JSONArray into fileName.json .
     *
     * @param fileName name to use for the JSON file.
     * @param eventsArray JSONArray of the events.
     */
    public static void writeEventsToJsonFile(Context context, String fileName, JSONArray eventsArray) {
        try {
            if(context != null) {
                Log.d("JsonFileHandler", "writeEventsToJsonFile: " + context);
                // Open FileOutputStream for writing
                FileOutputStream outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);

                // Write the JSON string to the file
                outputStream.write(eventsArray.toString().getBytes());

                // Close the stream
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Transform the map of events into a JSONArray then calls writeEventsToJsonFile() to write the array to a JSON file.
     *
     * @param EventsByDay map of the events.
     */
    public static void main(Context context, Map<LocalDate, ArrayList<Event>> EventsByDay, String fileName) {
        JSONArray eventsArray = new JSONArray();

        ArrayList<LocalDate> dates = new ArrayList<>(EventsByDay.keySet());
        Collections.sort(dates);

        for(LocalDate key : dates) {
            ArrayList<Event> dailyEvent = EventsByDay.get(key);
            if (dailyEvent != null) {
                for (int i = 0; i < dailyEvent.size(); i++) {
                    JSONObject JsonEvent = new JSONObject();
                    Event event = dailyEvent.get(i);
                    try {
                        JsonEvent.put("category", event.getCategory());
                        JsonEvent.put("date", event.getDate());
                        JsonEvent.put("startTime", event.getStartTime());
                        JsonEvent.put("endTime", event.getEndTime());
                        JsonEvent.put("room", event.getRoom());
                        JsonEvent.put("teacher", event.getTeacher());
                        JsonEvent.put("group", event.getGroup());
                        JsonEvent.put("module", event.getModule());
                        JsonEvent.put("notes", event.getNotes());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    eventsArray.put(JsonEvent);
                }
            }
        }

        // Save to JSON file
        writeEventsToJsonFile(context, fileName+".json", eventsArray);
    }
}
