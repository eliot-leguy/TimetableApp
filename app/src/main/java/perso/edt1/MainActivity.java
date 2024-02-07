package perso.edt1;

import static java.lang.Math.round;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static perso.edt1.CalendarUtils.daysInWeekArray;
import static perso.edt1.CalendarUtils.monthYearFromDate;
import static perso.edt1.CalendarUtils.selectedDate;
import static perso.edt1.CalendarUtils.sundayForDate;
import static perso.edt1.JsonFileHandler.loadEdtJson;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements EventsLoaderThread.DatabaseLoadListener
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
    private boolean reloadEdt;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWidgets();
//        loadEDT_action(null);
//        setCalendarAdapter();
//        setWeekView();
        reloadEdt = true;
        setMenu();
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

        drawerLayout = findViewById(R.id.drawerLayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CalendarUtils.selectedDate = LocalDate.now();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if(reloadEdt){
            setCalendarAdapter();
            loadEDT_action(null);
        }
        reloadEdt = false;
//        setWeekView();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setMenu(){
        NavigationView navigationView = findViewById(R.id.navigationView);
        Menu menu = navigationView.getMenu();

        //Getting the groups out of the db :
        DBManager dbHelper = new DBManager(this);
        List<String> allGroups = dbHelper.getGroups(false);
        List<String> selectedGroups = dbHelper.getGroups(true);

        for(String group : allGroups){
            MenuItem item = menu.add(group);
            View actionView = LayoutInflater.from(this).inflate(R.layout.edt_menu, null);

            CheckBox checkBox = actionView.findViewById(R.id.checkBox);
            checkBox.setChecked(selectedGroups.contains(group));
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(checkBox.isChecked()){
                        dbHelper.updateGroup(group, true);
                    } else {
                        dbHelper.updateGroup(group, false);
                    }
                    loadEDT_action(null);
                }
            });



            item.setActionView(actionView);
        }
//        navigationView.invalidate();


    }

    public void setCalendarAdapter() {
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

        hoursLinearLayout.removeAllViews();
        setHours();



    }

    public void setWeekView(){
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

        findViewById(R.id.loadingPanel).setVisibility(View.GONE);

    }

    private void setHours() {

        for (int i = 0; i < 24; i++) {
            TextView timeTV2 = new TextView(this);
            timeTV2.setText(LocalTime.of(i, 0).toString());
            timeTV2.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
//            timeTV2.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
            timeTV2.setTextSize(17);
            timeTV2.setHeight((int)round(60*2.5));
            timeTV2.setWidth(70);

            hoursLinearLayout.addView(timeTV2);
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

            if(events.get(i).events.get(0).getCategory().equals("Fill")){
                FrameLayout emptyView = new FrameLayout(this);
                Duration elapsedTime = Duration.between(events.get(i).events.get(0).getStartTime(), events.get(i).events.get(0).getEndTime());
                int minutes = (int) elapsedTime.toMinutes();
                emptyView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, (int)round(minutes*2.5)));
                switch (dayOfWeek) {
                    case 1:
                        mondayLinearLayout.addView(emptyView);
                        break;
                    case 2:
                        tuesdayLinearLayout.addView(emptyView);
                        break;
                    case 3:
                        wednesdayLinearLayout.addView(emptyView);
                        break;
                    case 4:
                        thursdayLinearLayout.addView(emptyView);
                        break;
                    case 5:
                        fridayLinearLayout.addView(emptyView);
                        break;
                    case 6:
                        saturdayLinearLayout.addView(emptyView);
                        break;
                }
            } else {
                View view = null;
                switch (dayOfWeek) {
                    case 1:
                        view = getLayoutInflater().inflate(R.layout.events_cells_week, null);
                        setHourEvents(view, events.get(i).events);
                        mondayLinearLayout.addView(view);
                        break;
                    case 2:
                        view = getLayoutInflater().inflate(R.layout.events_cells_week, null);
                        setHourEvents(view, events.get(i).events);
                        tuesdayLinearLayout.addView(view);
                        break;
                    case 3:
                        view = getLayoutInflater().inflate(R.layout.events_cells_week, null);
                        setHourEvents(view, events.get(i).events);
                        wednesdayLinearLayout.addView(view);
                        break;
                    case 4:
                        view = getLayoutInflater().inflate(R.layout.events_cells_week, null);
                        setHourEvents(view, events.get(i).events);
                        thursdayLinearLayout.addView(view);
                        break;
                    case 5:
                        view = getLayoutInflater().inflate(R.layout.events_cells_week, null);
                        setHourEvents(view, events.get(i).events);
                        fridayLinearLayout.addView(view);
                        break;
                    case 6:
                        view = getLayoutInflater().inflate(R.layout.events_cells_week, null);
                        setHourEvents(view, events.get(i).events);
                        saturdayLinearLayout.addView(view);
                        break;
                }
            }
        }
    }

    private void setHourEvents(View convertView, ArrayList<Event> events) {
        ConstraintLayout event1 = convertView.findViewById(R.id.event1);
        ConstraintLayout event2 = convertView.findViewById(R.id.event2);
        ConstraintLayout event3 = convertView.findViewById(R.id.event3);
        ConstraintLayout event4 = convertView.findViewById(R.id.event4);
        int relativeLayoutWidth = convertView.getWidth();

//        event1.setPadding(1,1,1,1);
//        event2.setPadding(1,1,1,1);
//        event3.setPadding(1,1,1,1);
//        event4.setPadding(1,1,1,1);

        event1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CalendarUtils.selectedDate = events.get(0).getDate();
                reloadEdt = false;

                Intent intent = new Intent(getApplicationContext(), FullScreenEventActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("event", events.get(0));
                startActivity(intent, null);
            }
        });

        event2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CalendarUtils.selectedDate = events.get(1).getDate();
                reloadEdt = false;

                Intent intent = new Intent(getApplicationContext(), FullScreenEventActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("event", events.get(1));
                startActivity(intent, null);
            }
        });

        event3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CalendarUtils.selectedDate = events.get(2).getDate();
                reloadEdt = false;

                Intent intent = new Intent(getApplicationContext(), FullScreenEventActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("event", events.get(2));
                startActivity(intent, null);
            }
        });

        event4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CalendarUtils.selectedDate = events.get(3).getDate();
                reloadEdt = false;

                Intent intent = new Intent(getApplicationContext(), FullScreenEventActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("event", events.get(3));
                startActivity(intent, null);
            }
        });

        if(events.size() == 0)
        {
            hideEvent(event1);
        }
        else if(events.size() == 1)
        {
            int perEventWidth = relativeLayoutWidth - (int) round(relativeLayoutWidth * 0.2);
            _setEvent(convertView, event1, events.get(0),1, perEventWidth);
        }
        else if(events.size() == 2)
        {
            int perEventWidth = (relativeLayoutWidth - (int) round(relativeLayoutWidth * 0.2)) / 2;
            _setEvent(convertView, event1, events.get(0),1, perEventWidth);
            _setEvent(convertView, event2, events.get(1),2, perEventWidth);
        }
        else if(events.size() == 3)
        {
            int perEventWidth = (relativeLayoutWidth - (int) round(relativeLayoutWidth * 0.2)) / 3;
            _setEvent(convertView, event1, events.get(0),1, perEventWidth);
            _setEvent(convertView, event2, events.get(1),2, perEventWidth);
            _setEvent(convertView, event3, events.get(2),3, perEventWidth);
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

    private void _setEvent(View convertView, ConstraintLayout eventCell, Event event, int eventNb, int perEventWidth) {
        TextView eventModule = null;
        TextView eventRoom = null;
        TextView eventNote = null;

        switch (eventNb){
            case 1:
                eventModule = convertView.findViewById(R.id.event1Module);
                eventRoom = convertView.findViewById(R.id.event1Room);
                eventNote = convertView.findViewById(R.id.event1Note);
                break;
            case 2:
                eventModule = convertView.findViewById(R.id.event2Module);
                eventRoom = convertView.findViewById(R.id.event2Room);
                eventNote = convertView.findViewById(R.id.event2Note);
                break;
            case 3:
                eventModule = convertView.findViewById(R.id.event3Module);
                eventRoom = convertView.findViewById(R.id.event3Room);
                eventNote = convertView.findViewById(R.id.event3Note);
                break;
            case 4:
                eventModule = convertView.findViewById(R.id.event4Module);
                eventRoom = convertView.findViewById(R.id.event4Room);
                eventNote = convertView.findViewById(R.id.event4Note);
                break;
        }

        String eventCategory = event.getCategory().replaceAll("\\s+", "_");

        Drawable background = AppCompatResources.getDrawable(this,R.drawable.rounded_corners_default_event_color);
        switch (eventCategory){
            case "CM":
                background.setColorFilter(getApplicationContext().getColor(R.color.CM), android.graphics.PorterDuff.Mode.SRC_IN);
                eventCell.setBackground(background);
                break;
            case "TD":
                background.setColorFilter(getApplicationContext().getColor(R.color.TD), android.graphics.PorterDuff.Mode.SRC_IN);
                eventCell.setBackground(background);
                break;
            case "TP":
                background.setColorFilter(getApplicationContext().getColor(R.color.TP), android.graphics.PorterDuff.Mode.SRC_IN);
                eventCell.setBackground(background);
                break;
            case "Examens":
                background.setColorFilter(getApplicationContext().getColor(R.color.Examens), android.graphics.PorterDuff.Mode.SRC_IN);
                eventCell.setBackground(background);
                break;
            case "TD_anglais":
                background.setColorFilter(getApplicationContext().getColor(R.color.TD_anglais), android.graphics.PorterDuff.Mode.SRC_IN);
                eventCell.setBackground(background);
                break;
            case "Vacances":
                background.setColorFilter(getApplicationContext().getColor(R.color.Vacances), android.graphics.PorterDuff.Mode.SRC_IN);
                eventCell.setBackground(background);
                break;
            default:
                eventCell.setBackground(background);
        }

        ViewGroup.LayoutParams layoutParameters = eventCell.getLayoutParams();
        layoutParameters.width = perEventWidth;

        Duration elapsedTime = Duration.between(event.getStartTime(), event.getEndTime());
        int minutes = (int) elapsedTime.toMinutes();

        layoutParameters.height = (int)round(minutes*2.5);
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
        setLoadingBarCenter();
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);

        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1);
        EventsLoaderThread eventsLoaderThread = new EventsLoaderThread(this, this, selectedDate);
        eventsLoaderThread.start();

    }

    public void nextWeekAction(View view) {
        setLoadingBarCenter();
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1);
        EventsLoaderThread eventsLoaderThread = new EventsLoaderThread(this, this, selectedDate);
        eventsLoaderThread.start();
    }

    public void setLoadingBarCenter(){
        ProgressBar loadingPanel = findViewById(R.id.loadingPanel);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 500);
        params.gravity = Gravity.CENTER;
        loadingPanel.setLayoutParams(params);
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
        reloadEdt = false;
    }

    private void hideEvent(ConstraintLayout eventCell) {
        eventCell.setVisibility(View.GONE);
    }

    public void downloadEDT_action(View view) {
//        Toast.makeText(this, "Downloading Edt", Toast.LENGTH_SHORT).show();
//        String url = "https://edt.univ-nantes.fr/chantrerie-gavy/g914391.xml";
//        new UrlRequests(this, "EDT").execute(url);
//        loadEDT_action(null);

        startActivity(new Intent(this, EdtSelectionActivity.class));
        reloadEdt = true;
    }

    public void loadEDT_action(View view) {
        Toast.makeText(this, "Loading Edt", Toast.LENGTH_SHORT).show();
        if(Event.EventsByDay != null){
            Event.EventsByDay.clear();
        }
//        loadEDT loadEDT = new loadEDT();
//        loadEDT.execute();

        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);


        EventsLoaderThread eventsLoaderThread = new EventsLoaderThread(this, this, selectedDate);
        eventsLoaderThread.start();
    }

    private class loadEDT extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            if(Event.EventsByDay != null){
                Event.EventsByDay.clear();
            }
            loadEdtJson(getFilesDir(), getApplicationContext(), CalendarUtils.selectedDate);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setCalendarAdapter();
            setWeekView();
        }
    }

    public void onEventsLoadComplete(){
        setCalendarAdapter();
        setWeekView();

    }
}