package perso.edt1;

import static java.lang.Math.round;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
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
import static perso.edt1.CalendarUtils.sundayForDate;
import static perso.edt1.JsonFileHandler.loadEdtJson;

public class MainActivity extends AppCompatActivity
{
    private TextView monthYearText;
    private ScrollView weekScrollView;
    private LinearLayout hoursLinearLayout;
    private LinearLayout mondayLinearLayout;
    private LinearLayout tuesdayLinearLayout;
    private LinearLayout wednesdayLinearLayout;
    private LinearLayout thursdayLinearLayout;
    private LinearLayout fridayLinearLayout;
    private LinearLayout saturdayLinearLayout;
    private View redLine;
    private TextView sundayDateTV;
    private TextView mondayDateTV;
    private TextView tuesdayDateTV;
    private TextView wednesdayDateTV;
    private TextView thursdayDateTV;
    private TextView fridayDateTV;
    private TextView saturdayDateTV;

    ArrayList<LocalDate> days;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_view);
        initWidgets();
//        loadEDT_action(null);
//        setCalendarAdapter();
//        setWeekView();
    }

    private void initWidgets() {
        monthYearText = findViewById(R.id.monthYearTV);
        weekScrollView = findViewById(R.id.weekScrollView);
        hoursLinearLayout = findViewById(R.id.hoursLinearLayout);
        mondayLinearLayout = findViewById(R.id.mondayLinearLayout);
        tuesdayLinearLayout = findViewById(R.id.tuesdayLinearLayout);
        wednesdayLinearLayout = findViewById(R.id.wednesdayLinearLayout);
        thursdayLinearLayout = findViewById(R.id.thursdayLinearLayout);
        fridayLinearLayout = findViewById(R.id.fridayLinearLayout);
        saturdayLinearLayout = findViewById(R.id.saturdayLinearLayout);
        redLine = findViewById(R.id.redLine);
        sundayDateTV = findViewById(R.id.sundayDateTV);
        mondayDateTV = findViewById(R.id.mondayDateTV);
        tuesdayDateTV = findViewById(R.id.tuesdayDateTV);
        wednesdayDateTV = findViewById(R.id.wednesdayDateTV);
        thursdayDateTV = findViewById(R.id.thursdayDateTV);
        fridayDateTV = findViewById(R.id.fridayDateTV);
        saturdayDateTV = findViewById(R.id.saturdayDateTV);

        CalendarUtils.selectedDate = LocalDate.now();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        setCalendarAdapter();
        loadEDT_action(null);
//        setWeekView();
    }

    private void setCalendarAdapter() {
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        days = daysInWeekArray(CalendarUtils.selectedDate);

        sundayDateTV.setText(String.valueOf(days.get(0).getDayOfMonth()));
        mondayDateTV.setText(String.valueOf(days.get(1).getDayOfMonth()));
        tuesdayDateTV.setText(String.valueOf(days.get(2).getDayOfMonth()));
        wednesdayDateTV.setText(String.valueOf(days.get(3).getDayOfMonth()));
        thursdayDateTV.setText(String.valueOf(days.get(4).getDayOfMonth()));
        fridayDateTV.setText(String.valueOf(days.get(5).getDayOfMonth()));
        saturdayDateTV.setText(String.valueOf(days.get(6).getDayOfMonth()));


        switch(CalendarUtils.selectedDate.getDayOfWeek().getValue()){
            case 7:
                sundayDateTV.setBackground(AppCompatResources.getDrawable(this,R.drawable.rounded_corners_date));
                break;
            case 1:
                mondayLinearLayout.setBackground(AppCompatResources.getDrawable(this,R.drawable.grid_background_selected_day));
                mondayDateTV.setBackground(AppCompatResources.getDrawable(this,R.drawable.rounded_corners_date));
                break;
            case 2:
                tuesdayLinearLayout.setBackground(AppCompatResources.getDrawable(this,R.drawable.grid_background_selected_day));
                tuesdayDateTV.setBackground(AppCompatResources.getDrawable(this,R.drawable.rounded_corners_date));
                break;
            case 3:
                wednesdayLinearLayout.setBackground(AppCompatResources.getDrawable(this,R.drawable.grid_background_selected_day));
                wednesdayDateTV.setBackground(AppCompatResources.getDrawable(this,R.drawable.rounded_corners_date));
                break;
            case 4:
                thursdayLinearLayout.setBackground(AppCompatResources.getDrawable(this,R.drawable.grid_background_selected_day));
                thursdayDateTV.setBackground(AppCompatResources.getDrawable(this,R.drawable.rounded_corners_date));
                break;
            case 5:
                fridayLinearLayout.setBackground(AppCompatResources.getDrawable(this,R.drawable.grid_background_selected_day));
                fridayDateTV.setBackground(AppCompatResources.getDrawable(this,R.drawable.rounded_corners_date));
                break;
            case 6:
                saturdayLinearLayout.setBackground(AppCompatResources.getDrawable(this,R.drawable.grid_background_selected_day));
                saturdayDateTV.setBackground(AppCompatResources.getDrawable(this,R.drawable.rounded_corners_date));
                break;
        }


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
        TextView eventModule = null;
        TextView eventRoom = null;
        TextView eventTeacher = null;
        TextView eventGroup = null;
        TextView eventNote = null;

        switch (eventNb){
            case 1:
                eventModule = convertView.findViewById(R.id.event1Module);
                eventRoom = convertView.findViewById(R.id.event1Room);
                eventTeacher = convertView.findViewById(R.id.event1Prof);
                eventGroup = convertView.findViewById(R.id.event1Group);
                eventNote = convertView.findViewById(R.id.event1Note);
                break;
            case 2:
                eventModule = convertView.findViewById(R.id.event2Module);
                eventRoom = convertView.findViewById(R.id.event2Room);
                eventTeacher = convertView.findViewById(R.id.event2Prof);
                eventGroup = convertView.findViewById(R.id.event2Group);
                eventNote = convertView.findViewById(R.id.event2Note);
                break;
            case 3:
                eventModule = convertView.findViewById(R.id.event3Module);
                eventRoom = convertView.findViewById(R.id.event3Room);
                eventTeacher = convertView.findViewById(R.id.event3Prof);
                eventGroup = convertView.findViewById(R.id.event3Group);
                eventNote = convertView.findViewById(R.id.event3Note);
                break;
            case 4:
                eventModule = convertView.findViewById(R.id.event4Module);
                eventRoom = convertView.findViewById(R.id.event4Room);
                eventTeacher = convertView.findViewById(R.id.event4Prof);
                eventGroup = convertView.findViewById(R.id.event4Group);
                eventNote = convertView.findViewById(R.id.event4Note);
                break;
        }






        String eventCategory = event.getCategory().replaceAll("\\s+", "_");

        int color = getApplicationContext().getResources().getIdentifier(eventCategory, "color", getApplicationContext().getPackageName());
        Drawable background = AppCompatResources.getDrawable(this,R.drawable.rounded_corners_default_event_color);
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
        eventModule.setPadding(5,5,5,1);

        ArrayList<String> eventRooms = event.getRoom();
        String StringEventRoom = "";
        for (int i = 0; i < eventRooms.size(); i++) {
            StringEventRoom += eventRooms.get(i);
            if (i != eventRooms.size() - 1)
                StringEventRoom += ", ";
        }
        eventRoom.setSingleLine(false);
        eventRoom.setTextSize(8);
        eventRoom.setText(StringEventRoom);
        eventRoom.setPadding(5,1,5,5);

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

    public void previousWeekAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1);
        loadEDT loadEDT = new loadEDT();
        loadEDT.execute();
        setCalendarAdapter();
        setWeekView();
    }

    public void nextWeekAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1);
        loadEDT loadEDT = new loadEDT();
        loadEDT.execute();
        setCalendarAdapter();
        setWeekView();
    }

    public void selectDateSunday(View view) {
        CalendarUtils.selectedDate = sundayForDate(CalendarUtils.selectedDate);
        sundayDateTV.setBackground(AppCompatResources.getDrawable(this,R.drawable.rounded_corners_date));
        mondayLinearLayout.setBackground(AppCompatResources.getDrawable(this,R.drawable.grid_background_transparent));
        mondayDateTV.setBackground(null);
        tuesdayLinearLayout.setBackground(AppCompatResources.getDrawable(this,R.drawable.grid_background_transparent));
        tuesdayDateTV.setBackground(null);
        wednesdayLinearLayout.setBackground(AppCompatResources.getDrawable(this,R.drawable.grid_background_transparent));
        wednesdayDateTV.setBackground(null);
        thursdayLinearLayout.setBackground(AppCompatResources.getDrawable(this,R.drawable.grid_background_transparent));
        thursdayDateTV.setBackground(null);
        fridayLinearLayout.setBackground(AppCompatResources.getDrawable(this,R.drawable.grid_background_transparent));
        fridayDateTV.setBackground(null);
        saturdayLinearLayout.setBackground(AppCompatResources.getDrawable(this,R.drawable.grid_background_transparent));
        saturdayDateTV.setBackground(null);
    }

    public void selectDateMonday(View view) {
        CalendarUtils.selectedDate = sundayForDate(CalendarUtils.selectedDate).plusDays(1);
        mondayLinearLayout.setBackground(AppCompatResources.getDrawable(this,R.drawable.grid_background_selected_day));
        sundayDateTV.setBackground(null);
        mondayDateTV.setBackground(AppCompatResources.getDrawable(this,R.drawable.rounded_corners_date));
        tuesdayLinearLayout.setBackground(AppCompatResources.getDrawable(this,R.drawable.grid_background_transparent));
        tuesdayDateTV.setBackground(null);
        wednesdayLinearLayout.setBackground(AppCompatResources.getDrawable(this,R.drawable.grid_background_transparent));
        wednesdayDateTV.setBackground(null);
        thursdayLinearLayout.setBackground(AppCompatResources.getDrawable(this,R.drawable.grid_background_transparent));
        thursdayDateTV.setBackground(null);
        fridayLinearLayout.setBackground(AppCompatResources.getDrawable(this,R.drawable.grid_background_transparent));
        fridayDateTV.setBackground(null);
        saturdayLinearLayout.setBackground(AppCompatResources.getDrawable(this,R.drawable.grid_background_transparent));
        saturdayDateTV.setBackground(null);
    }

    public void selectDateTuesday(View view) {
        CalendarUtils.selectedDate = sundayForDate(CalendarUtils.selectedDate).plusDays(2);
        tuesdayLinearLayout.setBackground(AppCompatResources.getDrawable(this,R.drawable.grid_background_selected_day));
        sundayDateTV.setBackground(null);
        mondayLinearLayout.setBackground(AppCompatResources.getDrawable(this,R.drawable.grid_background_transparent));
        mondayDateTV.setBackground(null);
        tuesdayDateTV.setBackground(AppCompatResources.getDrawable(this,R.drawable.rounded_corners_date));
        wednesdayLinearLayout.setBackground(AppCompatResources.getDrawable(this,R.drawable.grid_background_transparent));
        wednesdayDateTV.setBackground(null);
        thursdayLinearLayout.setBackground(AppCompatResources.getDrawable(this,R.drawable.grid_background_transparent));
        thursdayDateTV.setBackground(null);
        fridayLinearLayout.setBackground(AppCompatResources.getDrawable(this,R.drawable.grid_background_transparent));
        fridayDateTV.setBackground(null);
        saturdayLinearLayout.setBackground(AppCompatResources.getDrawable(this,R.drawable.grid_background_transparent));
        saturdayDateTV.setBackground(null);
    }

    public void selectDateWednesday(View view) {
        CalendarUtils.selectedDate = sundayForDate(CalendarUtils.selectedDate).plusDays(3);
        wednesdayLinearLayout.setBackground(AppCompatResources.getDrawable(this,R.drawable.grid_background_selected_day));;
        sundayDateTV.setBackground(null);
        mondayLinearLayout.setBackground(AppCompatResources.getDrawable(this,R.drawable.grid_background_transparent));
        mondayDateTV.setBackground(null);
        tuesdayLinearLayout.setBackground(AppCompatResources.getDrawable(this,R.drawable.grid_background_transparent));
        tuesdayDateTV.setBackground(null);
        wednesdayDateTV.setBackground(AppCompatResources.getDrawable(this,R.drawable.rounded_corners_date));
        thursdayLinearLayout.setBackground(AppCompatResources.getDrawable(this,R.drawable.grid_background_transparent));
        thursdayDateTV.setBackground(null);
        fridayLinearLayout.setBackground(AppCompatResources.getDrawable(this,R.drawable.grid_background_transparent));
        fridayDateTV.setBackground(null);
        saturdayLinearLayout.setBackground(AppCompatResources.getDrawable(this,R.drawable.grid_background_transparent));
        saturdayDateTV.setBackground(null);
    }

    public void selectDateThursday(View view) {
        CalendarUtils.selectedDate = sundayForDate(CalendarUtils.selectedDate).plusDays(4);
        thursdayLinearLayout.setBackground(AppCompatResources.getDrawable(this,R.drawable.grid_background_selected_day));
        sundayDateTV.setBackground(null);
        mondayLinearLayout.setBackground(AppCompatResources.getDrawable(this,R.drawable.grid_background_transparent));
        mondayDateTV.setBackground(null);
        tuesdayLinearLayout.setBackground(AppCompatResources.getDrawable(this,R.drawable.grid_background_transparent));
        tuesdayDateTV.setBackground(null);
        wednesdayLinearLayout.setBackground(AppCompatResources.getDrawable(this,R.drawable.grid_background_transparent));
        wednesdayDateTV.setBackground(null);
        thursdayDateTV.setBackground(AppCompatResources.getDrawable(this,R.drawable.rounded_corners_date));
        fridayLinearLayout.setBackground(AppCompatResources.getDrawable(this,R.drawable.grid_background_transparent));
        fridayDateTV.setBackground(null);
        saturdayLinearLayout.setBackground(AppCompatResources.getDrawable(this,R.drawable.grid_background_transparent));
        saturdayDateTV.setBackground(null);
    }

    public void selectDateFriday(View view) {
        CalendarUtils.selectedDate = sundayForDate(CalendarUtils.selectedDate).plusDays(5);
        fridayLinearLayout.setBackground(AppCompatResources.getDrawable(this,R.drawable.grid_background_selected_day));
        sundayDateTV.setBackground(null);
        mondayLinearLayout.setBackground(AppCompatResources.getDrawable(this,R.drawable.grid_background_transparent));
        mondayDateTV.setBackground(null);
        tuesdayLinearLayout.setBackground(AppCompatResources.getDrawable(this,R.drawable.grid_background_transparent));
        tuesdayDateTV.setBackground(null);
        wednesdayLinearLayout.setBackground(AppCompatResources.getDrawable(this,R.drawable.grid_background_transparent));
        wednesdayDateTV.setBackground(null);
        thursdayLinearLayout.setBackground(AppCompatResources.getDrawable(this,R.drawable.grid_background_transparent));
        thursdayDateTV.setBackground(null);
        fridayDateTV.setBackground(AppCompatResources.getDrawable(this,R.drawable.rounded_corners_date));
        saturdayLinearLayout.setBackground(AppCompatResources.getDrawable(this,R.drawable.grid_background_transparent));
        saturdayDateTV.setBackground(null);
    }

    public void selectDateSaturday(View view) {
        CalendarUtils.selectedDate = sundayForDate(CalendarUtils.selectedDate).plusDays(6);
        saturdayLinearLayout.setBackground(AppCompatResources.getDrawable(this,R.drawable.grid_background_selected_day));;
        sundayDateTV.setBackground(null);
        mondayLinearLayout.setBackground(AppCompatResources.getDrawable(this,R.drawable.grid_background_transparent));
        mondayDateTV.setBackground(null);
        tuesdayLinearLayout.setBackground(AppCompatResources.getDrawable(this,R.drawable.grid_background_transparent));
        tuesdayDateTV.setBackground(null);
        wednesdayLinearLayout.setBackground(AppCompatResources.getDrawable(this,R.drawable.grid_background_transparent));
        wednesdayDateTV.setBackground(null);
        thursdayLinearLayout.setBackground(AppCompatResources.getDrawable(this,R.drawable.grid_background_transparent));
        thursdayDateTV.setBackground(null);
        fridayLinearLayout.setBackground(AppCompatResources.getDrawable(this,R.drawable.grid_background_transparent));
        fridayDateTV.setBackground(null);
        saturdayDateTV.setBackground(AppCompatResources.getDrawable(this,R.drawable.rounded_corners_date));
    }

    public void dailyAction(View view) {
        startActivity(new Intent(this, DailyCalendarActivity.class));
    }

    private void hideEvent(LinearLayout eventCell) {
        eventCell.setVisibility(View.GONE);
    }

    public void downloadEDT_action(View view) {
//        Toast.makeText(this, "Downloading Edt", Toast.LENGTH_SHORT).show();
//        String url = "https://edt.univ-nantes.fr/chantrerie-gavy/g914391.xml";
//        new UrlRequests(this, "EDT").execute(url);
//        loadEDT_action(null);

        startActivity(new Intent(this, EdtSelectionActivity.class));
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
            loadEdtJson(getFilesDir(), getApplicationContext(), CalendarUtils.selectedDate);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setWeekView();
        }
    }
}