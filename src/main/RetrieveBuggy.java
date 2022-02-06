package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

public class RetrieveBuggy {

    private RetrieveBuggy() {}

    private static Logger logger;
	
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
         sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONArray readJsonArrayFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try(
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

                ) {
            String jsonText = readAll(rd);
            return new JSONArray(jsonText);
        } finally {
            is.close();
        }
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try(
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

        ) {
            String jsonText = readAll(rd);
            return new JSONObject(jsonText);
        } finally {
            is.close();
        }
    }

    public static Date parseStringToDate(String string) throws ParseException{

        String format = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
        return new SimpleDateFormat(format).parse(string);
    }

    public static void createFile() {
        try {
            File myFile = new File("C:\\Users\\crazile\\Desktop\\versions.txt");
            if (myFile.createNewFile()) {
                logger.log(Level.INFO, "File created: " + myFile.getName());
            } else {
                logger.log(Level.INFO, "File already exists.");
            }
        } catch (IOException e) {
            logger.log(Level.INFO, "An error occurred.");
            e.printStackTrace();
        }
    }
 
}
