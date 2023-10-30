package perso.edt1;

import android.content.Context;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class LoadXMLedt {

    public static void loadEdt(File DirectoryPath, Context context){

        ArrayList<File> files = getFiles(DirectoryPath);
        if(files.size() == 0){
            Toast.makeText(context, "No Local Edt", Toast.LENGTH_SHORT).show();
        } else {
            for (File file : files) {
                if (file.getName().endsWith(".xml")) {
                    loadXML(file);
                }
            }
        }
    }

    private static void loadXML(File file) {
        if(file.exists()){
            try {
                FileInputStream fis = new FileInputStream(file);
                BufferedReader br = new BufferedReader(new InputStreamReader(fis));
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
                    sb.append(line).append('\n');
                }

                String fileContent = sb.toString();
                try {
                    XmlParser.parseXml(fileContent);
                } catch (XmlPullParserException | IOException e) {
                    e.printStackTrace();
                }

                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    protected static ArrayList<File> getFiles(File directory){
        ArrayList<File> files = new ArrayList<>();
        File[] filesArray = directory.listFiles();

        assert filesArray != null;
        for(File file : filesArray){
            if(file.isDirectory()){
                files.addAll(getFiles(file));
            }else{
                files.add(file);
            }
        }

        return files;
    }
}
