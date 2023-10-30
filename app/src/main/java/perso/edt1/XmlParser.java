package perso.edt1;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import perso.edt1.Event;

public class XmlParser {

    public static void parseXml(String xmlString) throws XmlPullParserException, IOException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser parser = factory.newPullParser();

        StringReader reader = new StringReader(xmlString);
        int character=0;
        try {
            reader.reset();
            character = reader.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate date = null;
        String rawWeeks = null;

        Map<String, LocalDate> weekInfoMap = new Hashtable<String, LocalDate>();

        String module = null;
        int dayShift = 0;
        LocalTime startTime = null;
        LocalTime endTime = null;
        ArrayList<String> room = new ArrayList<>();
        ArrayList<String> teacher = new ArrayList<>();
        ArrayList<String> group = new ArrayList<>();
        String category = null;
        String notes = null;

        String dayString = null;

        parser.setInput(reader);
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if(eventType == XmlPullParser.START_TAG) {

                // On veut récupérer les numéros de semaines avec les alleventsweeks de la forme YNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN
                if(parser.getName() != null && parser.getName().equals("description")) {
                    try {
                        eventType = parser.next();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    String weekStartDate = parser.getText();
                    weekStartDate = weekStartDate.substring(weekStartDate.length()-10, weekStartDate.length()); // On garde que la fin de la string qui est une date

                    try {
                        date = LocalDate.parse(weekStartDate,formatter);
                        eventType = parser.next();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if(parser.getName() != null && parser.getName().equals("alleventweeks")) {
                    try {
                        eventType = parser.next();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    rawWeeks = parser.getText();

                    try {
                        eventType = parser.next();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // On veut récupérer les évènements
                if(parser.getName() != null && parser.getName().equals("day")){
                    try {
                        eventType = parser.next();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    dayString = parser.getText();
                    if(isInteger(dayString,10)) {
                        dayShift = Integer.parseInt(parser.getText());
                    }
                    try {
                        eventType = parser.next();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
               }

                if(parser.getName() != null && parser.getName().equals("starttime")){
                    try {
                        eventType = parser.next();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    startTime = LocalTime.parse(parser.getText());
                    try {
                        eventType = parser.next();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if(parser.getName() != null && parser.getName().equals("endtime")){
                    try {
                        eventType = parser.next();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    endTime = LocalTime.parse(parser.getText());
                    try {
                        eventType = parser.next();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if(parser.getName() != null && parser.getName().equals("category")){
                    try {
                        eventType = parser.next();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    category = parser.getText();
                    try {
                        eventType = parser.next();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if(parser.getName() != null && parser.getName().equals("rawweeks")){
                    try {
                        eventType = parser.next();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    rawWeeks = parser.getText();
                    try {
                        eventType = parser.next();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if(parser.getName() != null && parser.getName().equals("module")) {

                    try {
                        eventType = parser.nextTag();
                        eventType = parser.next();
                        if(eventType == XmlPullParser.START_TAG){
                            module = parser.nextText();
                        } else {
                            module = parser.getText();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


                if(parser.getName() != null && parser.getName().equals("notes")){
                    try {
                        eventType = parser.next();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    notes = parser.getText();
                    try {
                        eventType = parser.next();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if(parser.getName() != null && parser.getName().equals("room")){
                    boolean stillData = true;
                    while(stillData){
                        try {
                            eventType = parser.nextTag();
                            if(eventType != XmlPullParser.END_TAG) {
                                if (parser.next() == XmlPullParser.TEXT) {
                                    room.add(parser.getText());
                                } else {
                                    room.add(parser.nextText());

                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        stillData = !(eventType == XmlPullParser.END_TAG && parser.getName() != null && parser.getName().equals("room"));
                    }
                }

                if(parser.getName() != null && parser.getName().equals("staff")){
                    boolean stillData = true;
                    while(stillData){
                        try {
                            eventType = parser.nextTag();
                            if(eventType != XmlPullParser.END_TAG) {
                                if (parser.next() == XmlPullParser.TEXT) {
                                    teacher.add(parser.getText());
                                } else {
                                    teacher.add(parser.nextText());

                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        stillData = !(eventType == XmlPullParser.END_TAG && parser.getName() != null && parser.getName().equals("staff"));
                    }
                }

                if(parser.getName() != null && parser.getName().equals("group")){
                    boolean stillData = true;
                    while(stillData){
                        try {
                            eventType = parser.nextTag();
                            if(eventType != XmlPullParser.END_TAG) {
                                if (parser.next() == XmlPullParser.TEXT) {
                                    group.add(parser.getText());
                                } else {
                                    group.add(parser.nextText());

                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        stillData = !(eventType == XmlPullParser.END_TAG && parser.getName() != null && parser.getName().equals("group"));
                    }
                }

            } else if(eventType == XmlPullParser.END_TAG) {

                // Si on a toutes les infos sur la semaines on les ajoute dans un dictionnaire
                if(parser.getName().equals("span")){
                    weekInfoMap.put(rawWeeks, date);
                }

                // Si on a toutes les infos sur l'évènement on le créer et on l'ajoute dans la liste
                if(parser.getName().equals("event")){
                    LocalDate weekStartDate = weekInfoMap.get(rawWeeks);
                    assert weekStartDate != null;           //TODO : Pas sur ce celui la
                    Event event = new Event(dayShift, startTime, endTime, category, weekStartDate, module, room, teacher, group, notes);
                    Event.eventsList.add(event);


//                    String toPrint = "\n";
//                    toPrint += "StartTime : " + event.getStartTime() + "\n";
//                    toPrint += "EndTime : " + event.getEndTime() + "\n";
//                    toPrint += "Category : " + event.getCategory() + "\n";
//                    toPrint += "Date : " + event.getDate() + "\n";
//                    toPrint += "Module : " + event.getModule() + "\n";
//                    toPrint += "Room : " + event.getRoom() + "\n";
//                    toPrint += "Teacher : " + event.getTeacher() + "\n";
//                    toPrint += "Group : " + event.getGroup() + "\n";
//                    toPrint += "Notes : " + event.getNotes() + "\n";
//                    Log.d("truc", toPrint);



                    dayShift = 0;
                    startTime = null;
                    endTime = null;
                    category = null;
                    rawWeeks = null;
                    module = null;
                    room = new ArrayList<>();
                    teacher = new ArrayList<>();
                    group = new ArrayList<>();
                    notes = null;
                }
            }
            // Move to the next event
            try {
                eventType = parser.next();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isInteger(String s, int radix) {
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;
    }

}

