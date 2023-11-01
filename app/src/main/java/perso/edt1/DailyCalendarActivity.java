package perso.edt1;

import static perso.edt1.CalendarUtils.formattedHours;
import static perso.edt1.CalendarUtils.selectedDate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DailyCalendarActivity extends AppCompatActivity
{

    private TextView monthDayText;
    private TextView dayOfWeekTV;
    private ListView hourListView;
    private ListView eventListView;

    private View touchSource;
    private View clickSource;
    private int HourOffset = 0;

    private int eventPosition = 0;
    private int hourPosition = 0;

    ArrayList<HourEvent> hourEvents;

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
        hourListView = findViewById(R.id.hourListView);
        eventListView = findViewById(R.id.eventListView);



        //When we scroll one list, we want both to scroll so we have to link them :

        eventListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(touchSource == null)
                    touchSource = v;

                if(v == touchSource) {
                    hourListView.dispatchTouchEvent(event);
                    if(event.getAction() == MotionEvent.ACTION_UP) {
                        clickSource = v;
                        touchSource = null;
                    }
                    if(event.getAction() == MotionEvent.ACTION_CANCEL){
                        clickSource = null;
                        touchSource = null;
                    }
                }

                return false;
            }
        });

        hourListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(touchSource == null)
                    touchSource = v;

                if(v == touchSource) {
                    eventListView.dispatchTouchEvent(event);
                    if(event.getAction() == MotionEvent.ACTION_UP) {
                        clickSource = v;
                        touchSource = null;
                    }
                    if(event.getAction() == MotionEvent.ACTION_CANCEL){
                        clickSource = null;
                        touchSource = null;
                    }
                }

                return false;
            }
        });

        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(parent == clickSource) {
                    // Do something with the ListView was clicked
                }
            }
        });

        hourListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(parent == clickSource) {
                    // Do something with the ListView was clicked
                }
            }
        });

        hourListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(view == clickSource) {
                    //Log.d("Scroll", "2: " + firstVisibleItem + " " + view.getChildAt(0).getTop() + " " + totalItemCount);
                    //eventListView.setSelectionFromTop(firstVisibleItem, view.getChildAt(0).getTop());
                    //hourListView.setSelectionFromTop(firstVisibleItem, view.getChildAt(0).getTop());

                    hourPosition = firstVisibleItem;
                    HourOffset = view.getChildAt(0).getTop();
                } else {
                    HourOffset = 0;
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}
        });

        eventListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(view == clickSource) {
                    int position = view.getChildAt(0).getTop();
                    //hourListView.setSelectionFromTop(firstVisibleItem, view.getChildAt(0).getTop());
                    int offset = Integer.parseInt(formattedHours(hourEvents.get(firstVisibleItem).time));
                    hourListView.smoothScrollToPosition(firstVisibleItem);
                    eventPosition = firstVisibleItem;
                }
            }
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
        });

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        setDayView();
        eventListView.setSelection(eventPosition);
        //eventListView.setSelectionFromTop(eventPosition, eventListView.getChildAt(0).getTop());
        hourListView.setSelection(hourPosition);
    }

    private void setDayView()
    {
        monthDayText.setText(CalendarUtils.monthDayFromDate(selectedDate));
        String dayOfWeek = selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        dayOfWeekTV.setText(dayOfWeek);
        setHourAdapter();
        setEventAdapter();
    }

    private void setEventAdapter() {
        DailyEventAdapter dailyEventAdapter = new DailyEventAdapter(getApplicationContext(), hourEventList());
        eventListView.setAdapter(dailyEventAdapter);
    }

    private void setHourAdapter()
    {
        hourEvents = hourEventList();
        for (int i = 0; i < hourEvents.size() - 1; i++) {
            if(hourEvents.get(i).getTime() == hourEvents.get(i+1).getTime()){
                hourEvents.remove(i);
                i--;
            }
        }
        HourAdapter hourAdapter = new HourAdapter(getApplicationContext(), hourEvents);
        //HourAdapter hourAdapter = new HourAdapter(getApplicationContext(), hourEventList());
        hourListView.setAdapter(hourAdapter);
    }

    private ArrayList<HourEvent> hourEventList()
    {
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

    public void newEventAction(View view)
    {
        startActivity(new Intent(this, EventEditActivity.class));
    }

    public void MonthlyAction(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void fullScreenEventAction(Event event) {
        Intent intent = new Intent(this, FullScreenEventActivity.class);
        intent.putExtra("event", event);
        startActivity(intent);
    }
}