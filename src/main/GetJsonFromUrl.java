package main;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GetJsonFromUrl {

	private GetJsonFromUrl () {}

	static Integer max = 1;
	static Integer index;
	private static String issue = "issues";
	private static String total = "total";
	
	public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		try (BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));) {
			String jsonText = readAll(rd);
			return new JSONObject(jsonText);
		} finally {
			is.close();
		}
	}
	
	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}
	
	public static Date parseStringToDate(String string) throws ParseException {
	
		String format = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
		return new SimpleDateFormat(format).parse(string);
	}
	
	public static Date parseStringToAffectedDate(String string) throws ParseException {

		String format = "yyyy-MM-dd";
		return new SimpleDateFormat(format).parse(string);
	}
	
	public static List<Date> dateArray(String url, int i, int j, String getter) throws IOException, JSONException, ParseException {
	
		JSONObject json = readJsonFromUrl(url);
		JSONArray issues = json.getJSONArray(issue);
		ArrayList<Date> array = new ArrayList<>();
		max = json.getInt(total);
		for (; i < max && i < j; i++) {
			JSONObject field = issues.getJSONObject(i % 1000);
			String fieldobject = field.getJSONObject("fields").get(getter).toString();
			array.add(parseStringToDate(fieldobject));
		}
		index = i;
		return array;
	}
	
	public static List<String> keyArray(String url, int i, int j) throws IOException, JSONException, ParseException {

		JSONObject json = readJsonFromUrl(url);
		JSONArray issues = json.getJSONArray(issue);
		ArrayList<String> array = new ArrayList<>();
		max = json.getInt(total);
		for (; i < max && i < j; i++) {
			JSONObject field = issues.getJSONObject(i % 1000);
			String fieldobject = field.getString("key");
			array.add(fieldobject);
		}
		index = i;
		return array;
	}
	
	
	public static List<String> idArray(String url, int i, int j) throws IOException, JSONException, ParseException {
	
		JSONObject json = readJsonFromUrl(url);
		JSONArray issues = json.getJSONArray(issue);
		ArrayList<String> array = new ArrayList<>();
		max = json.getInt(total);
		for (; i < max && i < j; i++) {
			JSONObject field = issues.getJSONObject(i % 1000);
			String fieldobject = field.getString("id");
			array.add(fieldobject);
		}
		index = i;
		return array;
	}
	
	
	public static List<Ticket> setTicket(List<Date> cDate, List<Date> rDate, List<String> name, List<String> id){
		List<Ticket> ticketList = new ArrayList<>();
		for(int i = 0; i< cDate.size(); i++) {
			Ticket t = new Ticket(name.get(i), cDate.get(i), rDate.get(i), id.get(i));
			ticketList.add(t);
		}
		return ticketList;
	}
	
	
	public static boolean setFVOV(Ticket ticket, List<Release> releases) {
		for (Release r: releases) {
			if(ticket.getCreationDate().before(r.getDate()) && ticket.getOV() == null) {
				ticket.setOV(r.getNumber());
			}
			if(ticket.getCommit().getDate().before(r.getDate())) {
				ticket.setFV(r.getNumber());
				break;
			}
			else if (ticket.getFV() == null) {
				ticket.setFV(releases.size());
			}
		}
		return true;
	}
	
	
	public static void returnAffectedVersion(Ticket ticket, List<Release> releases) throws IOException, JSONException, ParseException {
		ticket.setIV(-1);
		Integer i = 0;
		String ticketName = ticket.getId();
		String url = "https://issues.apache.org/jira/rest/api/latest/issue/"+ ticketName;
	    JSONObject json = readJsonFromUrl(url);
	    JSONObject fields = json.getJSONObject("fields");
	    JSONArray versions = fields.getJSONArray("versions");

		if(versions.length() != 0 && versions.getJSONObject(i).has("releaseDate")){
			String date = versions.getJSONObject(i).get("releaseDate").toString();
			Date iV = parseStringToAffectedDate(date);
			if(iV.before(ticket.getCreationDate())) {
				for(Release r: releases) {
					if(iV.before(r.getDate())) {
						ticket.setIV(r.getNumber());
						break;
					}
				}
			}
		}
	}
}
