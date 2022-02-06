package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

public class RetrieveBuggy {
	
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
      try {
         BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
         String jsonText = readAll(rd);
         JSONArray json = new JSONArray(jsonText);
         return json;
       } finally {
         is.close();
       }
   }

   public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
      InputStream is = new URL(url).openStream();
      try {
         BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
         String jsonText = readAll(rd);
         JSONObject json = new JSONObject(jsonText);
         return json;
       } finally {
         is.close();
       }
   }
   
   public static Date parseStringToDate(String string) throws ParseException{
	   
	   String format = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	   Date date = new SimpleDateFormat(format).parse(string); 
	   
	   return date;
   }

   public static void createFile(JSONObject json) {
	   try {
		      File myFile = new File("C:\\Users\\crazile\\Desktop\\versions.txt");
		      if (myFile.createNewFile()) {
		        System.out.println("File created: " + myFile.getName());
		      } else {
		        System.out.println("File already exists.");
		      }
		    } catch (IOException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
   }
 
}
