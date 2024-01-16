package perso.edt1;

import static java.lang.Math.round;
import static perso.edt1.CalendarUtils.selectedDate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.view.GestureDetectorCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

public class DailyCalendarActivity extends AppCompatActivity
{

    private TextView monthDayText;
    private TextView dayOfWeekTV;

    private ScrollView scrollView;
    private LinearLayout hoursLinearLayout;
    private LinearLayout eventsLinearLayout;
    private View redLine;

    private GestureDetector gestureDetector;

    ArrayList<HourEvent> hourEvents;
    ArrayList<HourEvent> events;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_calendar);
        initWidgets();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initWidgets()
    {
        monthDayText = findViewById(R.id.monthDayText);
        dayOfWeekTV = findViewById(R.id.dayOfWeekTV);
        hoursLinearLayout = findViewById(R.id.hoursLinearLayout);
        eventsLinearLayout = findViewById(R.id.eventsLinearLayout);
        scrollView = findViewById(R.id.hoursEventsScrollView);
        redLine = findViewById(R.id.redLine);


        // Create a GestureDetector
        gestureDetector = new GestureDetector(this, new MyGestureListener());

        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        setDayView();
    }


    // Custom GestureDetector.SimpleOnGestureListener to handle swipe events
    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 80;
        private static final int SWIPE_VELOCITY_THRESHOLD = 80;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float diffX = 0;
            float diffY = 0;
            if(e1 != null && e2 != null) {
                diffX = e2.getX() - e1.getX();
                diffY = e2.getY() - e1.getY();
            }

            Log.d("diffX", "diffX: " + diffX);
            Log.d("diffY", "diffY: " + diffY);

            if (Math.abs(diffX) > Math.abs(diffY) &&
                    Math.abs(diffX) > SWIPE_THRESHOLD &&
                    Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                // Right swipe
                if (diffX > 0) {
                    // Update content to the right in verticalLinearLayout
                    CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusDays(1);
                    setDayView();
                    return true;
                }
                // Left swipe
                else {
                    // Update content to the left in verticalLinearLayout
                    CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusDays(1);
                    setDayView();
                    return true;
                }
            }

            return false;
        }
    }



    @SuppressLint("ClickableViewAccessibility")
    private void setDayView() {
        eventsLinearLayout.removeAllViews();
        hoursLinearLayout.removeAllViews();

        monthDayText.setText(CalendarUtils.monthDayFromDate(selectedDate));
        String dayOfWeek = selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        dayOfWeekTV.setText(dayOfWeek);

        setHours();
        setEvents();

        //Scroll to current time or to 8:00 if current time is before 8:00 and if the day selected is today or scroll to 8:00 if the day selected is not today
        LocalTime currentTime = LocalTime.now();
        LocalDate currentDate = LocalDate.now();
        if (selectedDate.equals(currentDate)) {
            if (currentTime.isBefore(LocalTime.of(8, 0))) {
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.scrollTo(0, 8*60*5);
                    }
                });
            } else {
                int scrollY = (int) currentTime.toSecondOfDay() / 60 * 5;
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.scrollTo(0, scrollY);
                    }
                });
            }
        } else {
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.scrollTo(0, 8*60*5);
                }
            });
        }

        //Adding a red line to show current time if the day is today
        if (selectedDate.equals(currentDate)){
            redLine.setVisibility(View.VISIBLE);
            int scrollY = (int) currentTime.toSecondOfDay() / 60 * 5;
            redLine.setY(scrollY);
        } else {
            redLine.setVisibility(View.GONE);
        }




    }

    private void setEvents(){
        events = hourEventList();
        for(int i = 0; i < events.size(); i++){
            if(events.get(i).events.size() == 0){
                events.remove(i);
                i--;
            }
        }

        for (int i = 0; i < events.size(); i++) {
            View view = getLayoutInflater().inflate(R.layout.hour_cell, null);
            setHourEvents(view, events.get(i).events);

            eventsLinearLayout.addView(view);
        }
    }

    private void setHourEvents(View convertView, ArrayList<Event> events) {
        convertView.findViewById(R.id.timeTV).setVisibility(View.GONE);
        LinearLayout event1 = convertView.findViewById(R.id.event1);
        LinearLayout event2 = convertView.findViewById(R.id.event2);
        LinearLayout event3 = convertView.findViewById(R.id.event3);
        LinearLayout event4 = convertView.findViewById(R.id.event4);
        int relativeLayoutWidth = convertView.getWidth();

        event1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!events.get(0).getCategory().equals("Fill")){
                    Intent intent = new Intent(getApplicationContext(), FullScreenEventActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("event", events.get(0));
                    startActivity(intent, null);
                }
            }
        });

        event2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!events.get(1).getCategory().equals("Fill")){
                    Intent intent = new Intent(getApplicationContext(), FullScreenEventActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("event", events.get(1));
                    startActivity(intent, null);
                }
            }
        });

        event3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!events.get(2).getCategory().equals("Fill")){
                    Intent intent = new Intent(getApplicationContext(), FullScreenEventActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("event", events.get(2));
                    startActivity(intent, null);
                }
            }
        });

        event4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!events.get(3).getCategory().equals("Fill")){
                    Intent intent = new Intent(getApplicationContext(), FullScreenEventActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("event", events.get(3));
                    startActivity(intent, null);
                }
            }
        });

        if(events.size() == 0)
        {
            hideEvent(event1);
            hideEvent(event2);
            hideEvent(event3);
            hideEvent(event4);
        }
        else if(events.size() == 1)
        {
            int perEventWidth = relativeLayoutWidth - (int) round(relativeLayoutWidth * 0.2);
            _setEvent(convertView, event1, events.get(0),1, perEventWidth);
            hideEvent(event2);
            hideEvent(event3);
            hideEvent(event4);
        }
        else if(events.size() == 2)
        {
            int perEventWidth = (relativeLayoutWidth - (int) round(relativeLayoutWidth * 0.2)) / 2;
            _setEvent(convertView, event1, events.get(0),1, perEventWidth);
            _setEvent(convertView, event2, events.get(1),2, perEventWidth);
            hideEvent(event3);
            hideEvent(event4);
        }
        else if(events.size() == 3)
        {
            int perEventWidth = (relativeLayoutWidth - (int) round(relativeLayoutWidth * 0.2)) / 3;
            _setEvent(convertView, event1, events.get(0),1, perEventWidth);
            _setEvent(convertView, event2, events.get(1),2, perEventWidth);
            _setEvent(convertView, event3, events.get(2),3, perEventWidth);
            hideEvent(event4);
        }
        else        //TODO: Limiter à 4 events max
        {
            int perEventWidth = (relativeLayoutWidth - (int) round(relativeLayoutWidth * 0.2)) / 4;
            _setEvent(convertView, event1, events.get(0),1, perEventWidth);
            _setEvent(convertView, event2, events.get(1),2, perEventWidth);
            _setEvent(convertView, event3, events.get(2),3, perEventWidth);
            _setEvent(convertView, event4, events.get(3),4, perEventWidth);
        }
    }

    private void _setEvent(View convertView, LinearLayout eventCell, Event event, int eventNb, int perEventWidth) {
        int moduleId = getApplicationContext().getResources().getIdentifier("event" + eventNb + "Module", "id", getApplicationContext().getPackageName());
        TextView eventModule = convertView.findViewById(moduleId);
        int roomId = getApplicationContext().getResources().getIdentifier("event" + eventNb + "Room", "id", getApplicationContext().getPackageName());
        TextView eventRoom = convertView.findViewById(roomId);
        int teacherId = getApplicationContext().getResources().getIdentifier("event" + eventNb + "Prof", "id", getApplicationContext().getPackageName());
        TextView eventTeacher = convertView.findViewById(teacherId);
        int groupId = getApplicationContext().getResources().getIdentifier("event" + eventNb + "Group", "id", getApplicationContext().getPackageName());
        TextView eventGroup = convertView.findViewById(groupId);
        int noteId = getApplicationContext().getResources().getIdentifier("event" + eventNb + "Note", "id", getApplicationContext().getPackageName());
        TextView eventNote = convertView.findViewById(noteId);

        String eventCategory = event.getCategory().replaceAll("\\s+", "_");
        Log.d("EventCategory", "EventCategory: " + eventCategory);

        int color = getApplicationContext().getResources().getIdentifier(eventCategory, "color", getApplicationContext().getPackageName());
        Drawable background = AppCompatResources.getDrawable(this,R.drawable.rounded_corners);
        assert background != null;
        if(color != 0) {
            background.setColorFilter(getApplicationContext().getColor(color), android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            background.setColorFilter(getApplicationContext().getColor(R.color.defaultEvent), android.graphics.PorterDuff.Mode.SRC_IN);
        }
        eventCell.setBackground(background);

        ViewGroup.LayoutParams layoutParameters = eventCell.getLayoutParams();
        layoutParameters.width = perEventWidth;

        Duration elapsedTime = Duration.between(event.getStartTime(), event.getEndTime());
        int minutes = (int) elapsedTime.toMinutes();

        layoutParameters.height = (int)round(minutes*5);
        eventCell.post(new Runnable() { @Override public void run() { eventCell.setLayoutParams(layoutParameters); }});

        eventCell.setLayoutParams(layoutParameters);


        eventModule.setText(event.getModule());

        ArrayList<String> eventRooms = event.getRoom();
        String StringEventRoom = "";
        for (int i = 0; i < eventRooms.size(); i++) {
            StringEventRoom += eventRooms.get(i);
            if (i != eventRooms.size() - 1)
                StringEventRoom += ", ";
        }
        eventRoom.setText(StringEventRoom);

        ArrayList<String> eventProfs = event.getTeacher();
        String StringEventProf = "";
        for (int i = 0; i < eventProfs.size(); i++) {
            StringEventProf += eventProfs.get(i);
            if (i != eventProfs.size() - 1)
                StringEventProf += ", ";
        }
        eventTeacher.setText(StringEventProf);

        ArrayList<String> eventGroups = event.getGroup();
        String StringEventGroup = "";
        for (int i = 0; i < eventGroups.size(); i++) {
            StringEventGroup += eventGroups.get(i);
            if (i != eventGroups.size() - 1)
                StringEventGroup += ", ";
        }
        eventGroup.setText(StringEventGroup);


        String noteText = event.getNotes();
        if (eventNote != null) {
            if (noteText == null) {
                eventNote.setVisibility(View.GONE);
            } else {
                eventNote.setText(noteText);
            }
        }

        eventCell.setVisibility(View.VISIBLE);
    }

    private void setHours() {
        hourEvents = hourEventList();
        for (int i = 0; i < hourEvents.size() - 1; i++) {
            if(hourEvents.get(i).getTime() == hourEvents.get(i+1).getTime()){
                hourEvents.remove(i);
                i--;
            }
        }

        for (int i = 0; i < hourEvents.size(); i++) {
            View view = getLayoutInflater().inflate(R.layout.hour_cell, null);
            TextView timeTV = view.findViewById(R.id.timeTV);
            timeTV.setText(hourEvents.get(i).getTime().toString());

            hideEvent(view.findViewById(R.id.event1));
            hideEvent(view.findViewById(R.id.event2));
            hideEvent(view.findViewById(R.id.event3));
            hideEvent(view.findViewById(R.id.event4));

            hoursLinearLayout.addView(view);
        }
    }

    private ArrayList<HourEvent> hourEventList() {
        ArrayList<HourEvent> list = new ArrayList<>();

        for(int hour = 0; hour < 24; hour++) {
                LocalTime time = LocalTime.of(hour, 0);
                ArrayList<Event> events = Event.eventsForDateAndTime(selectedDate, time);
                for(int i=0; i < events.size(); i++){
                    if(events.get(i).getCategory().equals("Fill")){
                        ArrayList<Event> fillEvent = new ArrayList<Event>();
                        fillEvent.add(events.get(i));
                        events.remove(i);
                        HourEvent hourEvent = new HourEvent(time, fillEvent);
                        list.add(hourEvent);
                    }
                }
                HourEvent hourEvent = new HourEvent(time, events);
                list.add(hourEvent);
        }

        return list;
    }

    public void previousDayAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusDays(1);
        setDayView();
    }

    public void nextDayAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusDays(1);
        setDayView();
    }

    public void MonthlyAction(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void fullScreenEventAction(Event event) {
        Intent intent = new Intent(this, FullScreenEventActivity.class);
        intent.putExtra("event", event);
        startActivity(intent);
    }

    private void hideEvent(LinearLayout eventCell) {
        eventCell.setVisibility(View.GONE);
    }
}