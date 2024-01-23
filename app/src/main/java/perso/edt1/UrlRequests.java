package perso.edt1;

import android.content.Context;
import android.os.AsyncTask;

import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
/**
 * Read an URL and return the content as a String.
 * The content is then used according to the TAG passed to the constructor.
 */
public class UrlRequests extends AsyncTask<String, Void, String> {

    private final WeakReference<Context> contextRef;
    private String _tag = null;


    /**
     * Constructor for the UrlRequests class.
     *
     * @param context Context of the application.
     * @param tag Used to know what to do with the content of the URL.
     *            Can be "EDT" or "GROUPS".
     */
    public UrlRequests(Context context, String tag) {
        contextRef = new WeakReference<>(context);
        _tag = tag;
    }


    @Override
    protected String doInBackground(String... urls) {
        try {
            URL url = new URL(urls[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Set timeout if needed
            // connection.setConnectTimeout(5000);
            // connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line).append("\n");
                }

                reader.close();
                return response.toString();
            } else {
                // Handle the error here
                return "Error: " + responseCode;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (_tag.equals("EDT")) {
            try {
                XmlParser.parseXml(result);
            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            }
            Context context = contextRef.get();
            JsonFileHandler.main(context, Event.EventsByDay);
        } else if (_tag.equals("GROUPS")){
            EdtSelectionActivity.getGroups(result);
        }
    }

    protected void saveStringToFile(Context context, String myString, String fileName) {
        try {
            File filePath = new File(context.getDir("edtDir", Context.MODE_PRIVATE), fileName);

            FileWriter fileWriter = new FileWriter(filePath);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write(myString);

            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

