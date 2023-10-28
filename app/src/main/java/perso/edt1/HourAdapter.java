package perso.edt1;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class HourAdapter extends ArrayAdapter<HourEvent>
{
    public HourAdapter(@NonNull Context context, List<HourEvent> hourEvents)
    {
        super(context, 0, hourEvents);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        HourEvent event = getItem(position);

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.hour_cell, parent, false);

        setHour(convertView, event.time);
        setEvents(convertView, event.events);

        return convertView;
    }

    private void setHour(View convertView, LocalTime time)
    {
        TextView timeTV = convertView.findViewById(R.id.timeTV);
        timeTV.setText(CalendarUtils.formattedShortTime(time));
    }

    // Doit adapter pour convenir avec les events

    private void setEvents(View convertView, ArrayList<Event> events)
    {
        for (int i = 0; i < events.size(); i++)
            Log.d("HourAdapter", events.get(i).getModule());

        LinearLayout event1 = convertView.findViewById(R.id.event1);
        LinearLayout event2 = convertView.findViewById(R.id.event2);
        LinearLayout event3 = convertView.findViewById(R.id.event3);

//        TextView event1 = convertView.findViewById(R.id.event1);
//        TextView event2 = convertView.findViewById(R.id.event2);
//        TextView event3 = convertView.findViewById(R.id.event3);

        if(events.size() == 0)
        {
            hideEvent(event1);
            hideEvent(event2);
            hideEvent(event3);
        }
        else if(events.size() == 1)
        {
            setEvent(convertView, event1, events.get(0));
            hideEvent(event2);
            hideEvent(event3);
        }
        else if(events.size() == 2)
        {
            setEvent(convertView, event1, events.get(0));
            setEvent(convertView, event2, events.get(1));
            hideEvent(event3);
        }
        else        //TODO: Limiter à 3 events max
        {
            setEvent(convertView, event1, events.get(0));
            setEvent(convertView, event2, events.get(1));
            setEvent(convertView, event3, events.get(2));
        }
    }

    private void setEvent(View convertView, LinearLayout eventCell, Event event)
    {
        TextView event1Module = convertView.findViewById(R.id.event1Module);
        TextView event1Room = convertView.findViewById(R.id.event1Room);
        TextView event1Teacher = convertView.findViewById(R.id.event1Prof);
        TextView event1Group = convertView.findViewById(R.id.event1Group);
        TextView event1Note = convertView.findViewById(R.id.event1Note);

        event1Module.setText(event.getModule());
        Log.d("HourAdapter", event.getModule());

        ArrayList<String> eventRooms = event.getRoom();
        String eventRoom = "";
        for (int i = 0; i < eventRooms.size(); i++) {
            eventRoom += eventRooms.get(i);
            if (i != eventRooms.size() - 1)
                eventRoom += ", ";
        }
        event1Room.setText(eventRoom);

        ArrayList<String> eventProfs = event.getTeacher();
        String eventProf = "";
        for (int i = 0; i < eventProfs.size(); i++) {
            eventProf += eventProfs.get(i);
            if (i != eventProfs.size() - 1)
                eventProf += ", ";
        }
        event1Teacher.setText(eventProf);

        ArrayList<String> eventGroups = event.getGroup();
        String eventGroup = "";
        for (int i = 0; i < eventGroups.size(); i++) {
            eventGroup += eventGroups.get(i);
            if (i != eventGroups.size() - 1)
                eventGroup += ", ";
        }
        event1Group.setText(eventGroup);

        event1Note.setText(event.getNotes());

        eventCell.setVisibility(View.VISIBLE);
    }

    private void hideEvent(LinearLayout eventCell)
    {
        eventCell.setVisibility(View.GONE);
    }

}













