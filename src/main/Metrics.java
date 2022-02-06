package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Metrics {

	private Metrics() {}

	private static final String FILENAME = "FileName";
	private static final String REL = "Release";
	private static final String LOC = "LOC";
	private static final String N_AUTH = "LOC";
	private static final String NR = "NR";
	private static final String LOC_ADDED = "LOC_added";

	public static void workOnOneCommit(int countRev, int linesAdded, JSONObject jsonDataset, JSONArray jsonArray, Commit commit, Repository repository, Set<String> countDevelopers, Release release) throws JSONException{
		try {
			DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
			df.setRepository(repository);
			df.setDiffComparator(RawTextComparator.DEFAULT);
			df.setDetectRenames(true);
			RevCommit rev = castToRevCommit(repository, commit);
			if (countDevelopers.isEmpty() || !countDevelopers.contains(rev.getAuthorIdent().getEmailAddress().toString())) {
				countDevelopers.add(rev.getAuthorIdent().getEmailAddress().toString());
			}
			countRev++;
			List<DiffEntry> diffs = getDiffs(repository, rev);
			for (DiffEntry diff : diffs) {
				linesAdded = 0;
				if(diff.toString().contains(".java") && new File("C:\\Users\\crazile\\git\\bookkeeper\\" + diff.toString().substring(14).replace("]", "")).exists()) {
					for(Edit edit : df.toFileHeader(diff).toEditList()) {
						linesAdded += edit.getEndB() - edit.getBeginB();
					}
					jsonDataset.put(FILENAME, diff.toString().substring(14).replace("]", ""));
					jsonDataset.put(REL, release.getNumber());
					jsonDataset.put(LOC, countLines(diff.toString().substring(14).replace("]", "")));
					jsonDataset.put(N_AUTH, countDevelopers.size());
					jsonDataset.put(NR, countRev);
					jsonDataset.put(LOC_ADDED, linesAdded);
					jsonArray.put(jsonDataset);
					jsonDataset = new JSONObject();
				}
			}

		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public static JSONArray getMetrics(Repository repository, Release release, Set<String> countDevelopers) throws JSONException {
		int countRev = 0;
		int linesAdded = 0;
		JSONObject jsonDataset = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		for(Commit commit: release.getRCommitList()) {
			workOnOneCommit(countRev, linesAdded, jsonDataset, jsonArray, commit, repository, countDevelopers, release);
		}
		return jsonArray;
	}

	
	public static long countLines(String fileName) {

	      long lines = 0;
	      try (BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\crazile\\git\\bookkeeper\\" + fileName))) {
	          while (reader.readLine() != null) lines++;
	      } catch (IOException e) {
	          e.printStackTrace();
	      }
	      return lines;

	 }
	
	
	public static RevCommit castToRevCommit(Repository repository, Commit commit) throws IOException {
		RevCommit rev = null;
		ObjectId commitId = ObjectId.fromString(commit.getId());
		try (RevWalk revWalk = new RevWalk(repository)) {
		  rev = revWalk.parseCommit(commitId);
		}
		return rev;
	}
	
	public static List<DiffEntry> getDiffs(Repository repository, RevCommit rev) throws IOException {
		List<DiffEntry> diffs;
		DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
		df.setRepository(repository);
		df.setDiffComparator(RawTextComparator.DEFAULT);
		df.setContext(0);
		df.setDetectRenames(true);
		if (rev.getParentCount() != 0) {
			RevCommit parent = (RevCommit) rev.getParent(0).getId();
			diffs = df.scan(parent.getTree(), rev.getTree());
		} else {
			RevWalk rw = new RevWalk(repository);
			ObjectReader reader = rw.getObjectReader();
			diffs = df.scan(new EmptyTreeIterator(), new CanonicalTreeParser(null, reader, rev.getTree()));
		}
		
		return diffs;
 }
	
	public static boolean exists(JSONArray releaseArray, JSONObject obj) throws JSONException {
		if (releaseArray.length() == 0) {
			return false;
		} else {
			return getElement(releaseArray, obj) != -1;
		}
		
	}
	
	public static int getElement(JSONArray releaseArray, JSONObject obj) throws JSONException {
		for (int i = 0; i < releaseArray.length(); i++) {
			if(releaseArray.getJSONObject(i).get(FILENAME).toString().equals(obj.get(FILENAME).toString())
					&& releaseArray.getJSONObject(i).get(REL).toString().equals(obj.get(REL).toString())) {
				return i;
			}
		}
		return -1;
	}
	

	
	public static JSONArray getMetrics(List<Release> releaseList) throws IOException, JSONException, NoHeadException, GitAPIException, ParseException{
				
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = builder.setGitDir(new File("C:\\Users\\Ilenia\\Intellij-projects\\bookkeeper\\.git"))
		  .readEnvironment() // scan environment GIT_* variables
		  .findGitDir() // scan up the file system tree
		  .build();

		HashSet<String> countDevelopers = null;
		JSONArray commitJsonArray = new JSONArray();
		JSONArray releaseJsonArray;
        
        for(Release r: releaseList) {
        	countDevelopers = new HashSet<>();
        	commitJsonArray = getMetrics(repository, r, countDevelopers);
	    }
        releaseJsonArray = generateJsonArray(commitJsonArray);
        
        return releaseJsonArray;
	}
        
	
	public static String getLoc(JSONArray commitJsonArray, int i, JSONObject releaseObject) throws JSONException {
			
			String loc;
			
			if (commitJsonArray.getJSONObject(i).get("LOC").equals(releaseObject.get("LOC"))) {
				loc = commitJsonArray.getJSONObject(i).get(N_AUTH).toString();
			} else {
				loc = ((Integer) Math.max(Integer.parseInt(commitJsonArray.getJSONObject(i).get("LOC").toString()), Integer.parseInt(releaseObject.get("LOC").toString()))).toString();
			}
			
			return loc;
		}
	
	public static String getNAuth(JSONArray commitJsonArray, int i, JSONObject releaseObject) throws JSONException {
		
		String nAuth;
		
		if (commitJsonArray.getJSONObject(i).get(N_AUTH).equals(releaseObject.get(N_AUTH))) {
			nAuth = commitJsonArray.getJSONObject(i).get(N_AUTH).toString();
		} else {
			nAuth = ((Integer) Math.max(Integer.parseInt(commitJsonArray.getJSONObject(i).get(N_AUTH).toString()), Integer.parseInt(releaseObject.get(N_AUTH).toString()))).toString();
		}
		
		return nAuth;
	}

	public static String getNR(JSONArray commitJsonArray, int i, JSONObject releaseObject) throws JSONException {
	
		String nR;
		
		if (commitJsonArray.getJSONObject(i).get("NR").equals(releaseObject.get("NR"))) {
			nR = commitJsonArray.getJSONObject(i).get("NR").toString();
		} else {
			nR = ((Integer) Math.max(Integer.parseInt(commitJsonArray.getJSONObject(i).get("NR").toString()), Integer.parseInt(releaseObject.get("NR").toString()))).toString();
		}
		
		return nR;
	}
	
	
	public static String getLocAdded(JSONArray commitJsonArray, int i, JSONObject releaseObject) throws JSONException {
		
		Integer locAdded = Integer.parseInt(commitJsonArray.getJSONObject(i).get("LOC").toString()) + Integer.parseInt(releaseObject.get("LOC").toString());
		
		return locAdded.toString();
	}
	

    public static JSONArray generateJsonArray(JSONArray commitJsonArray) throws JSONException {
    	
    	String nAuth = "";
    	String nR = "";
    	String loc = "";
    	String locAdded = "";

		JSONArray releaseJsonArray = new JSONArray();

        for (int i = 0; i < commitJsonArray.length(); i++) {
        	
        	if (!exists(releaseJsonArray, commitJsonArray.getJSONObject(i))) { //exists controlla l'esistenza dell'object dentro il releasejsonarray
        		releaseJsonArray.put(commitJsonArray.get(i));
        	} else {
        		
        		nAuth = getNAuth(commitJsonArray, i, releaseJsonArray.getJSONObject(getElement(releaseJsonArray, commitJsonArray.getJSONObject(i))));
        		nR = getNR(commitJsonArray, i, releaseJsonArray.getJSONObject(getElement(releaseJsonArray, commitJsonArray.getJSONObject(i))));
        		loc = getLoc(commitJsonArray, i, releaseJsonArray.getJSONObject(getElement(releaseJsonArray, commitJsonArray.getJSONObject(i))));
        		locAdded = getLocAdded(commitJsonArray, i, releaseJsonArray.getJSONObject(getElement(releaseJsonArray, commitJsonArray.getJSONObject(i))));
        		
        		releaseJsonArray.getJSONObject(getElement(releaseJsonArray, commitJsonArray.getJSONObject(i))).put(N_AUTH, nAuth);
        		releaseJsonArray.getJSONObject(getElement(releaseJsonArray, commitJsonArray.getJSONObject(i))).put("NR", nR);
        		releaseJsonArray.getJSONObject(getElement(releaseJsonArray, commitJsonArray.getJSONObject(i))).put("LOC", loc);
        		releaseJsonArray.getJSONObject(getElement(releaseJsonArray, commitJsonArray.getJSONObject(i))).put("LOC_added", locAdded);
        		
        	}
        }
        return releaseJsonArray;
        
    }
    
    public static void setMetric(List <ClassModel> classes, JSONArray array) throws JSONException {
		for (ClassModel c: classes) {
			for(int i = 0; i < array.length(); i++) {
				if(array.getJSONObject(i).has(FILENAME) && c.getName().contains(array.getJSONObject(i).get(FILENAME).toString())) {
					c.setLoc(Integer.parseInt(array.getJSONObject(i).get("LOC").toString()));
					c.setLocAdded(Integer.parseInt(array.getJSONObject(i).get("LOC_added").toString()));
					c.setAuthors(Integer.parseInt(array.getJSONObject(i).get(N_AUTH).toString()));
					c.setNRevisions(Integer.parseInt(array.getJSONObject(i).get("NR").toString()));
			
				}
			}
		}
		computeMaxAdded(classes);
		computeAvgAdded(classes);
		
	}
    
    public static void computeMaxAdded(List<ClassModel> classes) {
    	int temp;
    	int max = 0;
    	for (ClassModel c: classes) {
    		temp = c.getLocAdded();
    		if (temp > max) {
    			max = temp;
    		}
    	}
    	for (ClassModel c: classes) {
    		c.setMaxLocAdded(max);
    	}
    }
    
    public static void computeAvgAdded(List<ClassModel> classes) {
    	float sum = 0;
    	float add;
    	float divide = 1;
    	for (ClassModel c: classes) {
    		add = c.getLocAdded();
    		sum += add;
    		divide++;
    	}
    	for (ClassModel c : classes) {
    		c.setAvgLocAdded(sum/divide);
    	}
    }
	
}
