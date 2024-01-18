package perso.edt1;

import static java.lang.Math.round;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import static perso.edt1.CalendarUtils.daysInWeekArray;
import static perso.edt1.CalendarUtils.monthYearFromDate;
import static perso.edt1.JsonFileHandler.loadEdtJson;
import static perso.edt1.LoadXMLedt.loadEdt;

public class MainActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener
{
    private TextView monthYearText;
    private RecyclerView weekRecyclerView;
    private ScrollView weekScrollView;
    private LinearLayout hoursLinearLayout;
    private LinearLayout mondayLinearLayout;
    private LinearLayout tuesdayLinearLayout;
    private LinearLayout wednesdayLinearLayout;
    private LinearLayout thursdayLinearLayout;
    private LinearLayout fridayLinearLayout;
    private LinearLayout saturdayLinearLayout;

    private View redLine;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_view);
        initWidgets();
        loadEDT_action(null);
        setCalendarAdapter();
        setWeekView();
    }

    private void initWidgets() {
        monthYearText = findViewById(R.id.monthYearTV);
        weekRecyclerView = findViewById(R.id.weekRecyclerView);
        weekScrollView = findViewById(R.id.weekScrollView);
        hoursLinearLayout = findViewById(R.id.hoursLinearLayout);
        mondayLinearLayout = findViewById(R.id.mondayLinearLayout);
        tuesdayLinearLayout = findViewById(R.id.tuesdayLinearLayout);
        wednesdayLinearLayout = findViewById(R.id.wednesdayLinearLayout);
        thursdayLinearLayout = findViewById(R.id.thursdayLinearLayout);
        fridayLinearLayout = findViewById(R.id.fridayLinearLayout);
        saturdayLinearLayout = findViewById(R.id.saturdayLinearLayout);
        redLine = findViewById(R.id.redLine);

        CalendarUtils.selectedDate = LocalDate.now();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        setCalendarAdapter();
        setWeekView();
    }

    private void setCalendarAdapter()
    {
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> days = daysInWeekArray(CalendarUtils.selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(days, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        weekRecyclerView.setLayoutManager(layoutManager);
        weekRecyclerView.setAdapter(calendarAdapter);

    }

    private void setWeekView(){
        hoursLinearLayout.removeAllViews();
        mondayLinearLayout.removeAllViews();
        mondayLinearLayout.setPadding(0,1,1,1);
        tuesdayLinearLayout.removeAllViews();
        tuesdayLinearLayout.setPadding(1,1,1,1);
        wednesdayLinearLayout.removeAllViews();
        wednesdayLinearLayout.setPadding(1,1,1,1);
        thursdayLinearLayout.removeAllViews();
        thursdayLinearLayout.setPadding(1,1,1,1);
        fridayLinearLayout.removeAllViews();
        fridayLinearLayout.setPadding(1,1,1,1);
        saturdayLinearLayout.removeAllViews();
        saturdayLinearLayout.setPadding(1,1,2,1);

        weekScrollView.post(new Runnable() {
            @Override
            public void run() {
                weekScrollView.scrollTo(0, (int)round(8*60*2.5));
            }
        });

        setHours();

        ArrayList<LocalDate> days = daysInWeekArray(CalendarUtils.selectedDate);
        boolean displayRedLine = false;
        for(int i=1; i < days.size(); i++){
            setEvents(days.get(i), i);
            if (days.get(i).equals(LocalDate.now())) {
                displayRedLine = true;
                int finalI = i;
                redLine.post(new Runnable() {
                    @Override
                    public void run() {
                        redLine.setVisibility(View.VISIBLE);
                        int minutes = LocalTime.now().getHour() * 60 + LocalTime.now().getMinute();
                        redLine.setY((int)round(minutes*2.5));
                        redLine.setX(finalI * hoursLinearLayout.getWidth());
                    }
                });
            }
        }
        if(!displayRedLine){
            redLine.setVisibility(View.GONE);
        }

    }

    private void setHours() {

        for (int i = 0; i < 24; i++) {
            View view = getLayoutInflater().inflate(R.layout.hour_cell, null);
            TextView timeTV = view.findViewById(R.id.timeTV);
            timeTV.setText(LocalTime.of(i, 0).toString());
            timeTV.setTextSize(17);
            timeTV.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

            hideEvent(view.findViewById(R.id.event1));
            hideEvent(view.findViewById(R.id.event2));
            hideEvent(view.findViewById(R.id.event3));
            hideEvent(view.findViewById(R.id.event4));


            //Change the height of the view
            view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)round(60*2.5)));

            hoursLinearLayout.addView(view);


        }
    }

    private void setEvents(LocalDate day, int dayOfWeek){
        ArrayList<HourEvent> events = hourEventList(day);
        for(int i = 0; i < events.size(); i++){
            if(events.get(i).events.size() == 0){
                events.remove(i);
                i--;
            }
        }

        for (int i = 0; i < events.size(); i++) {
            View view = getLayoutInflater().inflate(R.layout.hour_cell, null);
            view.setPadding(0,0,0,0);
            setHourEvents(view, events.get(i).events);

            switch(dayOfWeek){
                case 1:
                    mondayLinearLayout.addView(view);
                    break;
                case 2:
                    tuesdayLinearLayout.addView(view);
                    break;
                case 3:
                    wednesdayLinearLayout.addView(view);
                    break;
                case 4:
                    thursdayLinearLayout.addView(view);
                    break;
                case 5:
                    fridayLinearLayout.addView(view);
                    break;
                case 6:
                    saturdayLinearLayout.addView(view);
                    break;
            }
        }
    }

    private void setHourEvents(View convertView, ArrayList<Event> events) {
        convertView.findViewById(R.id.timeTV).setVisibility(View.GONE);
        LinearLayout event1 = convertView.findViewById(R.id.event1);
        LinearLayout event2 = convertView.findViewById(R.id.event2);
        LinearLayout event3 = convertView.findViewById(R.id.event3);
        LinearLayout event4 = convertView.findViewById(R.id.event4);
        int relativeLayoutWidth = convertView.getWidth();

        event1.setPadding(1,1,1,1);
        event2.setPadding(1,1,1,1);
        event3.setPadding(1,1,1,1);
        event4.setPadding(1,1,1,1);

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

        layoutParameters.height = (int)round(minutes*2.5);
        eventCell.post(new Runnable() { @Override public void run() { eventCell.setLayoutParams(layoutParameters); }});

        eventCell.setLayoutParams(layoutParameters);

        eventModule.setSingleLine(false);
        eventModule.setTextSize(12);
        eventModule.setText(event.getModule());
        eventRoom.setVisibility(View.GONE);
        eventTeacher.setVisibility(View.GONE);
        eventGroup.setVisibility(View.GONE);


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

    private ArrayList<HourEvent> hourEventList(LocalDate day) {
        ArrayList<HourEvent> list = new ArrayList<>();

        for(int hour = 0; hour < 24; hour++) {
            LocalTime time = LocalTime.of(hour, 0);
            ArrayList<Event> events = Event.eventsForDateAndTime(day, time);
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


    public void previousWeekAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1);
        setCalendarAdapter();
        setWeekView();
    }

    public void nextWeekAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1);
        setCalendarAdapter();
        setWeekView();
    }

    @Override
    public void onItemClick(int position, LocalDate date)
    {
        CalendarUtils.selectedDate = date;
        setCalendarAdapter();
    }

    public void dailyAction(View view)
    {
        startActivity(new Intent(this, DailyCalendarActivity.class));
    }

    private void hideEvent(LinearLayout eventCell) {
        eventCell.setVisibility(View.GONE);
    }

    public void downloadEDT_action(View view) {
        Toast.makeText(this, "Downloading Edt", Toast.LENGTH_SHORT).show();
        String url = "https://edt.univ-nantes.fr/chantrerie-gavy/g914391.xml";
        new UrlRequests(this).execute(url);
        loadEDT_action(null);
        //JsonFileHandler.main((Context) this, Event.EventsByDay);
    }

    public void loadEDT_action(View view) {
        Toast.makeText(this, "Loading Edt", Toast.LENGTH_SHORT).show();
        if(Event.EventsByDay != null){
            Event.EventsByDay.clear();
        }
        loadEDT loadEDT = new loadEDT();
        loadEDT.execute();
    }

    private class loadEDT extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            loadEdtJson(getFilesDir(), getApplicationContext());
            return null;
        }
    }
}