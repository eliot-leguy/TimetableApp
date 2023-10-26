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

    public static void parseXml(String xmlString) throws XmlPullParserException {
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
            if (eventType == XmlPullParser.START_DOCUMENT) {
                Log.d("xmlDocument", "Start Document");
            } else if(eventType == XmlPullParser.START_TAG) {
                //Log.d("xmlDocument", "Start Tag " + parser.getName());

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
                    try {
                        dayShift = Integer.parseInt(parser.getText());
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        Log.d("test", "dayShift is not a number");
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

                if(parser.getName() != null && parser.getName().equals("module")){
                    try {
                        eventType = parser.next();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    module = parser.getText();
                    try {
                        eventType = parser.next();
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
                    for(int i=0; i < parser.getAttributeCount(); i++){
                        try {
                            eventType = parser.next();
                            eventType = parser.next();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if(parser.getName().equals("item")) {
                            try {
                                eventType = parser.next();
                                eventType = parser.next();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if(parser.getName().equals("a")){
                                try {
                                    eventType = parser.next();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                room.add(parser.getText());
                            }
                        }
                    }
                    try {
                        eventType = parser.next();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if(parser.getName() != null && parser.getName().equals("staff")){
                    for(int i=0; i < parser.getAttributeCount(); i++){
                        try {
                            eventType = parser.next();
                            eventType = parser.next();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if(parser.getName().equals("item")) {
                            try {
                                eventType = parser.next();
                                eventType = parser.next();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if(parser.getName().equals("a")){
                                try {
                                    eventType = parser.next();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                teacher.add(parser.getText());
                            }
                        }
                    }
                    try {
                        eventType = parser.next();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if(parser.getName() != null && parser.getName().equals("group")){
                    for(int i=0; i < parser.getAttributeCount(); i++){
                        try {
                            eventType = parser.next();
                            eventType = parser.next();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if(parser.getName().equals("item")) {
                            try {
                                eventType = parser.next();
                                eventType = parser.next();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if(parser.getName().equals("a")){
                                try {
                                    eventType = parser.next();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                group.add(parser.getText());
                            }
                        }
                    }
                    try {
                        eventType = parser.next();
                    } catch (IOException e) {
                        e.printStackTrace();
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
                    Event event = new Event(dayShift, startTime, endTime, category, weekStartDate, module, room, teacher, group, notes);
                    Event.eventsList.add(event);
                }

            } else if(eventType == XmlPullParser.TEXT) {
                //Log.d("xmlDocument", "Text " + parser.getText());
            }
            // Move to the next event
            try {
                eventType = parser.next();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

