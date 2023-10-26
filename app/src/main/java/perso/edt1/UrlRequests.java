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

public class UrlRequests extends AsyncTask<String, Void, String> {

    private WeakReference<Context> contextRef;

    public UrlRequests(Context context) {
        contextRef = new WeakReference<>(context);
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
                    response.append(line + "\n");
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
        // Process the response on the UI thread
        // Update your UI or do any other post-processing here
        //Log.d("Test", result);
        try {
            XmlParser.parseXml(result);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        Context context = contextRef.get();
        saveStringToFile(context, result, "edt.xml");
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

