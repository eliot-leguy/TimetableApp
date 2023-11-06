package perso.edt1;

import static perso.edt1.CalendarUtils.selectedDate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

public class DailyCalendarActivityGrid extends AppCompatActivity {

    private TextView monthDayText;
    private TextView dayOfWeekTV;
    private RecyclerView recyclerView;
    ArrayList<HourEvent> hourEvents;
    ArrayList<HourEvent> events;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_calandar_grid);
        monthDayText = findViewById(R.id.monthDayText);
        dayOfWeekTV = findViewById(R.id.dayOfWeekTV);

        recyclerView = findViewById(R.id.DailyRecyclerView);

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        setDayView();
    }

    private void setDayView() {
        events = hourEventList();
        monthDayText.setText(CalendarUtils.monthDayFromDate(selectedDate));
        String dayOfWeek = selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        dayOfWeekTV.setText(dayOfWeek);

        RecyclerView.Adapter<DailyEventAdapterGrid.ViewHolder> adapter = new DailyEventAdapterGrid(this, hourEventList());

        recyclerView.setAdapter(adapter);
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