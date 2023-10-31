package perso.edt1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;


import java.util.ArrayList;

public class FullScreenEventActivity extends Activity {

    protected Event event;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        event = intent.getParcelableExtra("event");
        setContentView(R.layout.activity_full_screen_event);
        setInfo();
    }

    private void setInfo() {
        TextView categoryText = findViewById(R.id.eventCategory);
        TextView dateText = findViewById(R.id.eventDate);
        TextView startTimeText = findViewById(R.id.eventStartTime);
        TextView endTimeText = findViewById(R.id.eventEndTime);
        TextView moduleText = findViewById(R.id.eventModule);
        TextView roomText = findViewById(R.id.eventRoom);
        TextView profText = findViewById(R.id.eventProf);
        TextView groupText = findViewById(R.id.eventGroup);


        categoryText.setText(event.getCategory());
        dateText.setText(event.getDate().toString());
        startTimeText.setText(event.getStartTime().toString());
        endTimeText.setText(event.getEndTime().toString());
        moduleText.setText(event.getModule());

        ArrayList<String> eventRooms = event.getRoom();
        String StringEventRoom = "";
        for (int i = 0; i < eventRooms.size(); i++) {
            StringEventRoom += eventRooms.get(i);
            if (i != eventRooms.size() - 1)
                StringEventRoom += ", ";
        }
        roomText.setText(StringEventRoom);

        ArrayList<String> eventProfs = event.getTeacher();
        String StringEventProf = "";
        for (int i = 0; i < eventProfs.size(); i++) {
            StringEventProf += eventProfs.get(i);
            if (i != eventProfs.size() - 1)
                StringEventProf += ", ";
        }
        profText.setText(StringEventProf);

        ArrayList<String> eventGroups = event.getGroup();
        String StringEventGroup = "";
        for (int i = 0; i < eventGroups.size(); i++) {
            StringEventGroup += eventGroups.get(i);
            if (i != eventGroups.size() - 1)
                StringEventGroup += ", ";
        }
        groupText.setText(StringEventGroup);
    }

    public void closeActivityAction(View view) {
        finish();
    }
}
