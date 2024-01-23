package perso.edt1;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Hashtable;
import java.util.Map;

public class EdtSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.edt_selection_activity);

        Toast.makeText(this, "Fetching groups", Toast.LENGTH_SHORT).show();
        String url = "https://edt.univ-nantes.fr/chantrerie-gavy/gindex.html";
        UrlRequests urlRequests = new UrlRequests(this, "GROUPS");
        urlRequests.execute(url);
    }


    /**
     * Returns a map containing the groups names and id from the inputString.
     *
     * @param inputString Context of the application.
     */
    static void getGroups(String inputString) {
        String[] lines = inputString.split("\n");
        Map<String,String> groups = new Hashtable<>();

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

    }
}