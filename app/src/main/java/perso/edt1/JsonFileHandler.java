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
import java.util.Hashtable;
import java.util.Map;

public class JsonFileHandler extends Application {

    public static void loadEdtJson(File DirectoryPath, Context context){

        ArrayList<File> files = getFiles(DirectoryPath);
        if(files.size() == 0){
            Toast.makeText(context, "No Local Edt" + DirectoryPath, Toast.LENGTH_SHORT).show();
        } else {
            for (File file : files) {
                if (file.getName().endsWith(".json")) {
                    Log.d("JsonFileHandler", "loadEdtJson: " + file.getName());
                    readEventsFromJsonFile(context, DirectoryPath + "/" + file.getName());
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

    public static void jsonToEvents(JSONArray eventsArray) {
        Map<LocalDate, ArrayList<Event>> EventsByDay = new Hashtable<LocalDate, ArrayList<Event>>();
        for (int i = 0; i < eventsArray.length(); i++) {
            try {
                JSONObject JsonEvent = eventsArray.getJSONObject(i);

                String roomStr = JsonEvent.getString("room");
                ArrayList<String> roomList = new ArrayList<>();
                for(int j = 0; j < roomStr.length(); j++){
                    if(roomStr.charAt(j) == ','){
                        roomList.add(roomStr.substring(0, j));
                        roomStr = roomStr.substring(j+1);
                        j = 0;
                    }
                }

                String teacherStr = JsonEvent.getString("teacher");
                ArrayList<String> teacherList = new ArrayList<>();
                for(int j = 0; j < teacherStr.length(); j++){
                    if(teacherStr.charAt(j) == ',' && !isUpperCase(teacherStr.charAt(j-1))){
                        teacherList.add(teacherStr.substring(0, j));
                        teacherStr = teacherStr.substring(j+1);
                        j = 0;
                    }
                }

                String groupStr = JsonEvent.getString("group");
                ArrayList<String> groupList = new ArrayList<>();
                for(int j = 0; j < groupStr.length(); j++){
                    if(groupStr.charAt(j) == ','){
                        groupList.add(groupStr.substring(0, j));
                        groupStr = groupStr.substring(j+1);
                        j = 0;
                    }
                }


                Event event = new Event(0,
                        LocalTime.parse(JsonEvent.getString("startTime")),
                        LocalTime.parse(JsonEvent.getString("endTime")),
                        JsonEvent.getString("category"),
                        LocalDate.parse(JsonEvent.getString("date")),
                        JsonEvent.getString("module"),
                        roomList,
                        teacherList,
                        groupList,
                        JsonEvent.getString("notes"));

                LocalDate date = event.getDate();
                if (EventsByDay.containsKey(date)) {
                    EventsByDay.get(date).add(event);
                } else {
                    ArrayList<Event> dailyEvent = new ArrayList<>();
                    dailyEvent.add(event);
                    EventsByDay.put(date, dailyEvent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Event.EventsByDay = EventsByDay;
    }

    public static void readEventsFromJsonFile(Context context, String filePath) {
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
        jsonToEvents(JsonEventArray);
    }

    public static void writeEventsToJsonFile(Context context, String fileName, JSONArray eventsArray) {
        try {
            // Open FileOutputStream for writing
            FileOutputStream outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);

            // Write the JSON string to the file
            outputStream.write(eventsArray.toString().getBytes());

            // Close the stream
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(Context context, Map<LocalDate, ArrayList<Event>> EventsByDay) {
        JSONArray eventsArray = new JSONArray();

        for(LocalDate key : EventsByDay.keySet()) {
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
        writeEventsToJsonFile(context, "events.json", eventsArray);
    }
}
