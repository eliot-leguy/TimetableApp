package perso.edt1;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

public class EdtSelectionActivity extends AppCompatActivity {

    private static Map<String,String> groups;
    public static String groupsString;
    private static Map<String,String> selectedGroups;

    LinearLayout groupLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.edt_selection_activity);
        groupLinearLayout = findViewById(R.id.groupLinearLayout);

        Toast.makeText(this, "Fetching groups", Toast.LENGTH_SHORT).show();
        String url = "https://edt.univ-nantes.fr/chantrerie-gavy/gindex.html";
        UrlRequests urlRequests = new UrlRequests(this, "GROUPS", null);
        urlRequests.execute(url);
    }

    //on resume
    @Override
    protected void onResume(){
        super.onResume();
        getGroups getGroups = new getGroups();
        getGroups.execute();
    }

    @Override
    protected void onStop() {
        super.onStop();

        ArrayList<String> groupsNames = new ArrayList<>(selectedGroups.keySet());

        for (String groupName : groupsNames) {
            String url = "https://edt.univ-nantes.fr/chantrerie-gavy/" + selectedGroups.get(groupName) +".xml";
            UrlRequests urlRequests = new UrlRequests(this, "EDT", groupName);
            urlRequests.execute(url);
        }
    }

    public void closeActivityAction(View view) {
        finish();
    }

    private class getGroups extends AsyncTask<Void, Void, Map<String,String> > {
        @Override
        protected Map<String,String>  doInBackground(Void... voids) {
            Map<String,String> groups = new Hashtable<>();

            while(groupsString.isEmpty()){
                try{
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            String[] lines = groupsString.split("\n");

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
            return groups;
        }

        @Override
        protected void onPostExecute(Map<String,String> groups) {
            //Update the display
            ArrayList<String> groupsNames = new ArrayList<>(groups.keySet());
            selectedGroups = new Hashtable<>();

            for (String groupName : groupsNames) {

                View view = getLayoutInflater().inflate(R.layout.group_selector, null);
                TextView groupNameTextView = view.findViewById(R.id.groupNameTextView);
                CheckBox groupCheckBox = view.findViewById(R.id.groupCheckBox);
                ImageView trashImageView = view.findViewById(R.id.trashImageView);

                groupNameTextView.setText(groupName);

                if(Event.localEdt.contains(groupName)){
                    groupCheckBox.setChecked(true);
                } else {
                    trashImageView.setVisibility(View.GONE);
                }
                groupCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        selectedGroups.put(groupName, groups.get(groupName));
                    } else {
                        selectedGroups.remove(groupName);
                    }
                });

                trashImageView.setOnClickListener(v -> {
                    Event.localEdt.remove(groupName);
                    File file = new File(getFilesDir() + "/" + groupName + ".json");
                    try {
                        file.delete();
                    } catch (Exception e) {
                        Log.e("EdtSelectionActivity", "Error while deleting file " + file.getName());
                    }

                });

                groupLinearLayout.addView(view);
            }
        }
    }
}