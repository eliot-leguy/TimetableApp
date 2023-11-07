package perso.edt1;

import static java.lang.Math.round;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;

public class DailyEventAdapterGrid extends RecyclerView.Adapter<DailyEventAdapterGrid.ViewHolder>{

    ArrayList<HourEvent> dailyEvents;
    Context context;
    View ItemView;
    private int RelativeLayoutWidth;
    

    public DailyEventAdapterGrid(Context context, ArrayList<HourEvent> dailyEvents){
        this.context = context;
        this.dailyEvents = dailyEvents;
    }

    @NonNull
    @Override
    public DailyEventAdapterGrid.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_cell, parent, false);
        
        ViewGroup.LayoutParams MainRelativeLayoutParams = view.getLayoutParams();
        RelativeLayoutWidth = MainRelativeLayoutParams.width;

        return new DailyEventAdapterGrid.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyEventAdapterGrid.ViewHolder holder, int position) {
//      String module = hourEvents.get(position).getEvents().get(0).getModule();
        ArrayList<Event> events = dailyEvents.get(position).getEvents();
        LocalTime hour = dailyEvents.get(position).getTime();
        setEvents(events);
//        setHours(hour);
    }

    private void setHours(LocalTime hour){

        LinearLayout event1 = ItemView.findViewById(R.id.event1);
        LinearLayout event2 = ItemView.findViewById(R.id.event2);
        LinearLayout event3 = ItemView.findViewById(R.id.event3);
        LinearLayout event4 = ItemView.findViewById(R.id.event4);
        hideEvent(event1);
        hideEvent(event2);
        hideEvent(event3);
        hideEvent(event4);

        TextView timeTV = ItemView.findViewById(R.id.timeTV);
        timeTV.setText(CalendarUtils.formattedTime(hour));

        timeTV.setHeight(150);
        timeTV.setWidth(70);

    }
    
    private void setEvents(ArrayList<Event> events)
    {
        LinearLayout event1 = ItemView.findViewById(R.id.event1);
        LinearLayout event2 = ItemView.findViewById(R.id.event2);
        LinearLayout event3 = ItemView.findViewById(R.id.event3);
        LinearLayout event4 = ItemView.findViewById(R.id.event4);

        TextView timeTV = ItemView.findViewById(R.id.timeTV);
        timeTV.setVisibility(View.GONE);


        event1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!events.get(0).getCategory().equals("Fill")){
                    Intent intent = new Intent(context, FullScreenEventActivity.class);
                    intent.putExtra("event", events.get(0));
                    ContextCompat.startActivity(context, intent, null);
                }
            }
        });

        event2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!events.get(1).getCategory().equals("Fill")){
                    Intent intent = new Intent(context, FullScreenEventActivity.class);
                    intent.putExtra("event", events.get(1));
                    ContextCompat.startActivity(context, intent, null);
                }
            }
        });

        event3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!events.get(2).getCategory().equals("Fill")){
                    Intent intent = new Intent(context, FullScreenEventActivity.class);
                    intent.putExtra("event", events.get(2));
                    ContextCompat.startActivity(context, intent, null);
                }
            }
        });

        event4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!events.get(3).getCategory().equals("Fill")){
                    Intent intent = new Intent(context, FullScreenEventActivity.class);
                    intent.putExtra("event", events.get(3));
                    ContextCompat.startActivity(context, intent, null);
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
            int perEventWidth = RelativeLayoutWidth - (int) round(RelativeLayoutWidth * 0.2);
            setEvent(event1, events.get(0),1, perEventWidth);
            hideEvent(event2);
            hideEvent(event3);
            hideEvent(event4);
        }
        else if(events.size() == 2)
        {
            int perEventWidth = (RelativeLayoutWidth - (int) round(RelativeLayoutWidth * 0.2)) / 2;
            setEvent(event1, events.get(0),1, perEventWidth);
            setEvent(event2, events.get(1),2, perEventWidth);
            hideEvent(event3);
            hideEvent(event4);
        }
        else if(events.size() == 3)
        {
            int perEventWidth = (RelativeLayoutWidth - (int) round(RelativeLayoutWidth * 0.2)) / 3;
            setEvent(event1, events.get(0),1, perEventWidth);
            setEvent(event2, events.get(1),2, perEventWidth);
            setEvent(event3, events.get(2),3, perEventWidth);
            hideEvent(event4);
        }
        else        //TODO: Limiter à 4 events max
        {
            int perEventWidth = (RelativeLayoutWidth - (int) round(RelativeLayoutWidth * 0.2)) / 4;
            setEvent(event1, events.get(0),1, perEventWidth);
            setEvent(event2, events.get(1),2, perEventWidth);
            setEvent(event3, events.get(2),3, perEventWidth);
            setEvent(event4, events.get(3),4, perEventWidth);
        }
    }

    private void setEvent(LinearLayout eventCell, Event event, int eventNb, int perEventWidth)
    {
        int moduleId = context.getResources().getIdentifier("event" + eventNb + "Module", "id", context.getPackageName());
        TextView eventModule = ItemView.findViewById(moduleId);
        int roomId = context.getResources().getIdentifier("event" + eventNb + "Room", "id", context.getPackageName());
        TextView eventRoom = ItemView.findViewById(roomId);
        int teacherId = context.getResources().getIdentifier("event" + eventNb + "Prof", "id", context.getPackageName());
        TextView eventTeacher = ItemView.findViewById(teacherId);
        int groupId = context.getResources().getIdentifier("event" + eventNb + "Group", "id", context.getPackageName());
        TextView eventGroup = ItemView.findViewById(groupId);
        int noteId = context.getResources().getIdentifier("event" + eventNb + "Notes", "id", context.getPackageName());
        TextView eventNote = ItemView.findViewById(noteId);
        int timeId = context.getResources().getIdentifier("event" + eventNb + "Time", "id", context.getPackageName());
        TextView eventTime = ItemView.findViewById(timeId);

        String eventCategory = event.getCategory().replaceAll("\\s+", "_");

        int color = context.getResources().getIdentifier(eventCategory, "color", context.getPackageName());
        if(color != 0) {
            eventCell.setBackgroundColor(context.getColor(color));
        }

        ViewGroup.LayoutParams layoutParameters = eventCell.getLayoutParams();
        layoutParameters.width = perEventWidth;

        Duration elapsedTime = Duration.between(event.getStartTime(), event.getEndTime());
        int minutes = (int) elapsedTime.toMinutes();

        layoutParameters.height = (int)round(minutes*7);

        eventCell.post(new Runnable() { @Override public void run() { eventCell.setLayoutParams(layoutParameters); }});

        eventCell.setLayoutParams(layoutParameters);


        eventModule.setText(event.getModule());

        String eventTimeText = CalendarUtils.formattedTime(event.getStartTime()) + " - " + CalendarUtils.formattedTime(event.getEndTime());
        eventTime.setText(eventTimeText);

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

        eventNote.setText(event.getNotes());

        eventCell.setVisibility(View.VISIBLE);
    }

    private void hideEvent(LinearLayout eventCell) {
        eventCell.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return dailyEvents.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ItemView = itemView;
        }
    }
}
