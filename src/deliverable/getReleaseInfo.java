package deliverable;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;


public class getReleaseInfo {
	
	static Integer max = 1;
	static Integer index;
	
	   public static HashMap<LocalDateTime, String> releaseNames;
	   public static HashMap<LocalDateTime, String> releaseID;
	   public static ArrayList<LocalDateTime> releases;
	   public static Integer numVersions;

	/*public static void main(String[] args) throws IOException, JSONException, ParseException {
		   
		   String projName ="BOOKKEEPER";
		 //Fills the arraylist with releases dates and orders them
		   //Ignores releases with missing dates
		   releases = new ArrayList<LocalDateTime>();
		         Integer i;
		         String url = "https://issues.apache.org/jira/rest/api/2/project/" + projName;
		         JSONObject json = readJsonFromUrl(url);
		         List<Release> ReleaseList = new ArrayList<>();
		         JSONArray versions = json.getJSONArray("versions");
		         releaseNames = new HashMap<LocalDateTime, String>();
		         releaseID = new HashMap<LocalDateTime, String> ();
		         for (i = 0; i < versions.length(); i++ ) {
		            String name = "";
		            String id = "";
		            if(versions.getJSONObject(i).has("releaseDate")) {
		               if (versions.getJSONObject(i).has("name"))
		                  name = versions.getJSONObject(i).get("name").toString();
		               if (versions.getJSONObject(i).has("id"))
		                  id = versions.getJSONObject(i).get("id").toString();
		               addRelease(versions.getJSONObject(i).get("releaseDate").toString(),
		                          name,id);
		            }
		         }
		         // order releases by date
		         Collections.sort(releases, new Comparator<LocalDateTime>(){
		            //@Override
		            public int compare(LocalDateTime o1, LocalDateTime o2) {
		                return o1.compareTo(o2);
		            }
		         });
		         if (releases.size() < 6)
		            return;
		         FileWriter fileWriter = null;
			 try {
		            fileWriter = null;
		            String outname = projName + "VersionInfo.csv";
						    //Name of CSV for output
						    fileWriter = new FileWriter(outname);
		            fileWriter.append("Index,Version ID,Version Name,Date");
		            fileWriter.append("\n");
		            numVersions = releases.size();
		            for ( i = 0; i < releases.size(); i++) {
		               Integer index = i + 1;
		               ReleaseList.add(CustomRelease(index, releases.get(i).toString(),
		            		   releaseNames.get(releases.get(i)),releaseID.get(releases.get(i))));
		               fileWriter.append(index.toString());
		               fileWriter.append(",");
		               fileWriter.append(releaseID.get(releases.get(i)));
		               fileWriter.append(",");
		               fileWriter.append(releaseNames.get(releases.get(i)));
		               fileWriter.append(",");
		               fileWriter.append(releases.get(i).toString());
		               fileWriter.append("\n");
		            }

		         } catch (Exception e) {
		            System.out.println("Error in csv writer"); 
		            e.printStackTrace();
		         } finally {
		            try {
		               fileWriter.flush();
		               fileWriter.close();
		            } catch (IOException e) {
		               System.out.println("Error while flushing/closing fileWriter !!!");
		               e.printStackTrace();
		            }
		         }
			 System.out.println(ReleaseList);
		         return;
		   }*/
	
	
      public static List<Release> getReleaseList() throws ParseException, JSONException, IOException {
		   String projName = "BOOKKEEPER";
		 //Fills the arraylist with releases dates and orders them
		   //Ignores releases with missing dates
		   releases = new ArrayList<LocalDateTime>();
		   Integer i;
		   String url = "https://issues.apache.org/jira/rest/api/2/project/" + projName;
		   JSONObject json = readJsonFromUrl(url);
		   List<Release> releaseList = new ArrayList<>();
		   JSONArray versions = json.getJSONArray("versions");
		   releaseNames = new HashMap<LocalDateTime, String>();
		   releaseID = new HashMap<LocalDateTime, String> ();
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
		   Collections.sort(releases, new Comparator<LocalDateTime>(){
		   //@Override
		   public int compare(LocalDateTime o1, LocalDateTime o2) {
		   return o1.compareTo(o2);
		    }});
		   for ( i = 0; i < releases.size(); i++) {
			   Integer index = i + 1;
			   
		       releaseList.add(customizeRelease(index, releases.get(i).toString(),
		       releaseNames.get(releases.get(i)),releaseID.get(releases.get(i))));
		       
		   }
		   //System.out.println("data release: " + releaseList.get(0).getDate().toString());
		   return releaseList;
      }
     
		
	  public static void addRelease(String strDate, String name, String id) {
		      LocalDate date = LocalDate.parse(strDate);
		      LocalDateTime dateTime = date.atStartOfDay();
		      if (!releases.contains(dateTime))
		         releases.add(dateTime);
		      releaseNames.put(dateTime, name);
		      releaseID.put(dateTime, id);
		      return;
	  }
	  
	  public static void dateComparator(List<Release> release, List<Commit> commit) {
			for (int i = 0; i < release.size(); i++) {
				for(int k = 0; k< commit.size(); k++) {		
					//System.out.println("commit: " + release.get(i).getDate());
					if(release.get(i).getDate().after(commit.get(k).getDate()))  {
						//System.out.println("sono dopo");
						continue;
					}
					else {
						//System.out.println("sono prima");
						release.get(i).setCommit(commit.get(k-1));
						break;
					}
				}
			}
		}
	   
	   public static Release customizeRelease(Integer index, String strDate, String name, String id) throws ParseException {		   	 
		      Date releaseDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(strDate);  
		   	  Release release = new Release(id, name, releaseDate, index);
		      return release;
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
	   
	   private static String readAll(Reader rd) throws IOException {
		      StringBuilder sb = new StringBuilder();
		      int cp;
		      while ((cp = rd.read()) != -1) {
		         sb.append((char) cp);
		      }
		      return sb.toString();
		   }

	   
	   public static List<String> VersionArray(String url, int i, int j, String getter) throws IOException, JSONException, ParseException {

			JSONObject json = readJsonFromUrl(url);
			JSONArray issues = json.getJSONArray("issues");
			//System.out.println(issues);
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
		
	   
	   public static boolean containsName(List<Class> list, Class c){
   	    //return list.stream().map(Class::getName).filter(name::equals).findFirst().isPresent();
	   	  boolean q = false;
	   	  
	   	  int counter = 0;
	   	  for (Class e: list) {
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
				  //System.out.println(counter);
	   	  }
	   	  return q;
	   }
	   
	
			   
		   
	   public static void assignClassListToRelease(List<Release> releaseList, List<Commit> commitList) throws ParseException, IOException {
	    	  int firstRef = 0;
	    	  int lastRef = 0;
	    	  List<Class> temp = new ArrayList<>();
	    	  //List<Class> releaseClassList = new ArrayList<>();
	    	  for (Release release: releaseList) {
	    		  lastRef = release.getCommit().getIdNumber();
	    		  for (Commit commit: commitList) {
	    			  if (commit.getIdNumber() > firstRef && commit.getIdNumber() <= lastRef && commit.getClasses() != null) {
	    				  for (Class c: commit.getClasses()) {
	    						 if (temp.isEmpty() || !containsName(temp, c)) {
	    							 temp.add(c);
	    					  }
	    				  }
	    			  }  
	    		  }
	    		  firstRef = lastRef;
	    		  /*for (Class e: temp) {
	    			  releaseClassList.add(e);
	    		  }*/
	    		  //release.setReleaseClasses(releaseClassList);
	    		  release.setReleaseClasses(temp);
	    		  //releaseClassList = new ArrayList<>();
	    		  temp = new ArrayList<>();
	    		  //System.out.println(releaseList.get(0).getReleaseClasses().size());

	    	  }
    		  //System.out.println(releaseList.get(0).getReleaseClasses().size());
	  
	   }
	   
	   public static void assignCommitListToRelease(List<Release> releaseList, List<Commit> commitList) throws ParseException, IOException {
	    	  int firstRef = 0;
	    	  int lastRef = 0;
	    	  List<Commit> temp = new ArrayList<>();
	    	  //List<Class> releaseClassList = new ArrayList<>();
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
	   
	   /*public static void main(String[] args) throws IOException, JSONException, ParseException{
	    	  List<Release> rList = getReleaseList();
	    	  List<Commit> cList = deliverable.GetGitInfo.getCommitList();
	    	  
	    	  dateComparator(rList, cList);
	    	  assignClassListToRelease(rList, cList);

	    	  //Release rel = rList.get(11);
	    	  /*for (Class elem: rel.getReleaseClasses()) {
	    		  System.out.println(elem.getName());
	    	  }*/
    		  //System.out.println(rList.get(0).getReleaseClasses().size());

	    	  /*for (Commit c: cList) {
	    		  System.out.println(cList.indexOf(c));
	    		  System.out.println(c.getDate());
	    	  }*/
	   //}
 }

	