package perso.edt1;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

public class EdtSelectionActivity extends AppCompatActivity {

    private static Map<String,String> groups;
    public static String groupsString;
    private static Map<String,String> selectedGroups;

    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.edt_selection_activity);
        scrollView = findViewById(R.id.groupsScrollView);

        Toast.makeText(this, "Fetching groups", Toast.LENGTH_SHORT).show();
        String url = "https://edt.univ-nantes.fr/chantrerie-gavy/gindex.html";
        UrlRequests urlRequests = new UrlRequests(this, "GROUPS");
        urlRequests.execute(url);
    }

    //on resume
    @Override
    protected void onResume(){
        super.onResume();

        getGroups(groupsString);
        displayGroups();

    }

    private class getGroups extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }


    /**
     * Returns a map containing the groups names and id from the inputString.
     *
     * @param inputString Context of the application.
     */
    void getGroups(String inputString) {
        String[] lines = inputString.split("\n");
        groups = new Hashtable<>();

        for (String line : lines) {
            if(line.contains("<option value")){
                boolean endID = false;
                boolean endName = false;
                StringBuilder id = new StringBuilder();
                StringBuilder name = new StringBuilder();
                for (int i = 0; i < line.length(); i++) {
                    if (endID) {
                        id.append(line.charAt(i));
                    }
                    if (line.charAt(i) == '"') {
                        endID = !endID;
                    }
                    if (line.charAt(i) == '<') {
                        endName = false;
                    }
                    if (endName) {
                        name.append(line.charAt(i));
                    }
                    if (line.charAt(i) == '>') {
                        endName = true;
                    }
                    if (line.charAt(i) == '/') {
                        //Cut id at the dot
                        id = new StringBuilder(id.substring(0, id.length() - 6));
                        groups.put(name.toString(), id.toString());
                        break;
                    }
                }
            }
        }
    }

    private void displayGroups() {

        ArrayList<String> groupsNames = new ArrayList<>(groups.keySet());
        selectedGroups = new Hashtable<>();

        for (String groupName : groupsNames) {

            View view = getLayoutInflater().inflate(R.layout.group_selector, null);
            TextView groupNameTextView = view.findViewById(R.id.groupNameTextView);
            CheckBox groupCheckBox = view.findViewById(R.id.groupCheckBox);

            groupNameTextView.setText(groupName);

            if(Event.localEdt.contains(groupName)){
                groupCheckBox.setChecked(true);
            }
            groupCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedGroups.put(groupName, groups.get(groupName));
                } else {
                    selectedGroups.remove(groupName);
                }
            });

            scrollView.addView(view);
        }
    }
}