package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

public class GetReleaseInfo {

	private GetReleaseInfo() {}
	
	static Integer max = 1;
	static Integer index;

	private static Map<LocalDateTime, String> releaseNames;
	private static Map<LocalDateTime, String> releaseID;
	private static List<LocalDateTime> releases;
	
	public static List<Release> getReleaseList() throws ParseException, JSONException, IOException {
	   String projName = "BOOKKEEPER";

	   /*Fills the arraylist with releases dates and orders them
	   		Ignores releases with missing dates*/
	   releases = new ArrayList<>();
	   Integer i;
	   String url = "https://issues.apache.org/jira/rest/api/2/project/" + projName;
	   JSONObject json = readJsonFromUrl(url);
	   List<Release> releaseList = new ArrayList<>();
	   JSONArray versions = json.getJSONArray("versions");
	   releaseNames = new HashMap<>();
	   releaseID = new HashMap<> ();
	   for (i = 0; i < versions.length(); i++ ) {
		   String name = "";
		   String id = "";
		   if(versions.getJSONObject(i).has("releaseDate")) {
			   if (versions.getJSONObject(i).has("name"))
				   name = versions.getJSONObject(i).get("name").toString();
			   if (versions.getJSONObject(i).has("id"))
				   id = versions.getJSONObject(i).get("id").toString();
			   addRelease(versions.getJSONObject(i).get("releaseDate").toString(), name,id);
		   }
	   }

	   // order releases by date
	   Collections.sort(releases, (LocalDateTime o1, LocalDateTime o2) -> o1.compareTo(o2));
	   for ( i = 0; i < releases.size(); i++) {
		   Integer index = i + 1;
		   releaseList.add(customizeRelease(index, releases.get(i).toString(),
		   releaseNames.get(releases.get(i)),releaseID.get(releases.get(i))));
	   }
	   return releaseList;
	}


	public static void addRelease(String strDate, String name, String id) {
		  LocalDate date = LocalDate.parse(strDate);
		  LocalDateTime dateTime = date.atStartOfDay();
		  if (!releases.contains(dateTime))
			  releases.add(dateTime);
		  releaseNames.put(dateTime, name);
		  releaseID.put(dateTime, id);
	}

	public static void dateComparator(List<Release> release, List<Commit> commit) {
		for (int i = 0; i < release.size(); i++) {
			for(int k = 0; k< commit.size(); k++) {
				if(!release.get(i).getDate().after(commit.get(k).getDate()))  {
					release.get(i).setCommit(commit.get(k-1));
					break;
				}
			}
		}
	}

	public static Release customizeRelease(Integer index, String strDate, String name, String id) throws ParseException {
		  Date releaseDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(strDate);
		  return new Release(id, name, releaseDate, index);
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

	private static String readAll(Reader rd) throws IOException {
		  StringBuilder sb = new StringBuilder();
		  int cp;
		  while ((cp = rd.read()) != -1) {
			 sb.append((char) cp);
		  }
		  return sb.toString();
	   }


	public static List<String> versionArray(String url, int i, int j, String getter) throws IOException, JSONException, ParseException {
		JSONObject json = readJsonFromUrl(url);
		JSONArray issues = json.getJSONArray("issues");
		int counter = 0;
		ArrayList<String> array = new ArrayList<>();
		max = json.getInt("total");
		for (; i < max && i < j; i++) {
			JSONArray versions = issues.getJSONObject(i % 1000).getJSONObject("fields").getJSONArray("versions");
			if(versions.length() != 0 ) {
				  if (versions.getJSONObject(0).has(getter)) {
						 array.add(versions.getJSONObject(0).get(getter).toString());
						 counter = counter +1;
				  }
			   }
			else {
				 array.add("null");
			   }
		}
		index = i;
		return array;
	}


	public static boolean containsName(List<ClassModel> list, ClassModel c){
		boolean q = false;
		int counter = 0;
		for (ClassModel e: list) {
		  if (e.getName().equals(c.getName())) {
			  q = true;
			  e.setRecurrence(e.getRecurrence() + 1);
			  e.setSumChg(e.getSumChg() + c.getChg());
			  if(e.getMaxChg() < c.getChg()) {
				  e.setMaxChg(c.getChg());
			  }
		  }
		  else {
			  c.setMaxChg(c.getChg());
			  c.setRecurrence(c.getRecurrence() + 1);
			  c.setSumChg(c.getSumChg() + c.getChg());
		  }
		  counter = counter +1;
		}
		return q;
	}



	public static void classesPerRelease(List<Release> releaseList, List<Commit> commitList) {
		int lastRef = 0;
		int firstRef = 0;
		List<ClassModel> temp = new ArrayList<>();

		for (Release release : releaseList) {
			lastRef = release.getCommit().getIdNumber();
			assignClassesToRelease(commitList, firstRef, lastRef, temp);
			firstRef = lastRef;
			release.setReleaseClasses(temp);
			temp = new ArrayList<>();
		}
	}

	public static void assignClassesToRelease(List<Commit> commitList, int firstRef, int lastRef, List<ClassModel> temp) {
		  for (Commit commit: commitList) {
			  if (commit.getIdNumber() > firstRef && commit.getIdNumber() <= lastRef && commit.getClasses() != null) {
				  for (ClassModel c: commit.getClasses()) {
						 if (temp.isEmpty() || !containsName(temp, c)) {
							 temp.add(c);
					     }
				  }
			  }
		  }
	}

	public static void assignCommitListToRelease(List<Release> releaseList, List<Commit> commitList) throws ParseException, IOException {
		  int firstRef = 0;
		  int lastRef = 0;
		  List<Commit> temp = new ArrayList<>();
		  for (Release release: releaseList) {
			  lastRef = release.getCommit().getIdNumber();
			  for(Commit c: commitList) {
				  if (c.getIdNumber() > firstRef && c.getIdNumber() <= lastRef) {
					  temp.add(c);
				  }
			  }
			  firstRef = lastRef;
			  release.setRCommitList(temp);
			  temp = new ArrayList<>();
		  }
	}

 }

	
