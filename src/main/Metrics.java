package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
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

	/*private static final String FILENAME = "FileName";
	private static final String REL = "Release";
	private static final String LOC = "LOC";
	private static final String N_AUTH = "n_auth";
	private static final String NR = "NR";
	private static final String LOC_ADDED = "LOC_added";
	private static String projName = "rampart\\";

	public static void fillJson(JSONObject jsonDataset, JSONArray jsonArray, DiffEntry diff, Release release, Set<String> countDevelopers, int countRev, int linesAdded) throws JSONException{
		jsonDataset.put(FILENAME, diff.toString().substring(14).replace("]", ""));
		jsonDataset.put(REL, release.getNumber());
		jsonDataset.put(LOC, countLines(diff.toString().substring(14).replace("]", "")));
		jsonDataset.put(N_AUTH, countDevelopers.size());
		jsonDataset.put(NR, countRev);
		jsonDataset.put(LOC_ADDED, linesAdded);
		jsonArray.put(jsonDataset);
	}

	public static void updateLinesAdded(DiffFormatter df, DiffEntry diff, int linesAdded) throws IOException {
		for (Edit edit : df.toFileHeader(diff).toEditList()) {
			linesAdded += edit.getEndB() - edit.getBeginB();
		}
	}

	public static JSONArray getMetrics(Repository repository, Release release, Set<String> countDevelopers) throws JSONException {
		int countRev = 0;
		int linesAdded = 0;
		JSONObject jsonDataset = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		for(Commit commit: release.getRCommitList()) {
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
				List<DiffEntry> diffs = GetGitInfo.getDiffs(repository, rev);
				for (DiffEntry diff : diffs) {
					linesAdded = 0;
					if (diff.toString().contains(".java") && new File("C:\\Users\\crazile\\git\\" + projName + diff.toString().substring(14).replace("]", "")).exists()) {
						updateLinesAdded(df, diff, linesAdded);
						fillJson(jsonDataset, jsonArray, diff, release, countDevelopers, countRev, linesAdded);
						jsonDataset = new JSONObject();
					}
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return jsonArray;
	}

	
	public static long countLines(String fileName) {

	      long lines = 0;
	      try (BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\crazile\\git\\" + projName + fileName))) {
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
		Repository repository = builder.setGitDir(new File("C:\\Users\\Ilenia\\Intellij-projects\\" + projName + ".git"))
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

		if (commitJsonArray.getJSONObject(i).get(LOC).equals(releaseObject.get(LOC))) {
			loc = commitJsonArray.getJSONObject(i).get(N_AUTH).toString();
		} else {
			loc = ((Integer) Math.max(Integer.parseInt(commitJsonArray.getJSONObject(i).get(LOC).toString()), Integer.parseInt(releaseObject.get(LOC).toString()))).toString();
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
		
		if (commitJsonArray.getJSONObject(i).get(NR).equals(releaseObject.get(NR))) {
			nR = commitJsonArray.getJSONObject(i).get(NR).toString();
		} else {
			nR = ((Integer) Math.max(Integer.parseInt(commitJsonArray.getJSONObject(i).get(NR).toString()), Integer.parseInt(releaseObject.get(NR).toString()))).toString();
		}
		
		return nR;
	}
	
	
	public static String getLocAdded(JSONArray commitJsonArray, int i, JSONObject releaseObject) throws JSONException {
		
		Integer locAdded = Integer.parseInt(commitJsonArray.getJSONObject(i).get(LOC).toString()) + Integer.parseInt(releaseObject.get(LOC).toString());
		
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
        		releaseJsonArray.getJSONObject(getElement(releaseJsonArray, commitJsonArray.getJSONObject(i))).put(NR, nR);
        		releaseJsonArray.getJSONObject(getElement(releaseJsonArray, commitJsonArray.getJSONObject(i))).put(LOC, loc);
        		releaseJsonArray.getJSONObject(getElement(releaseJsonArray, commitJsonArray.getJSONObject(i))).put(LOC_ADDED, locAdded);
        		
        	}
        }
        return releaseJsonArray;
        
    }
    
    public static void setMetric(List <ClassModel> classes, JSONArray array) throws JSONException {
		for (ClassModel c: classes) {
			for(int i = 0; i < array.length(); i++) {
				if(array.getJSONObject(i).has(FILENAME) && c.getName().contains(array.getJSONObject(i).get(FILENAME).toString())) {
					c.setLoc(Integer.parseInt(array.getJSONObject(i).get(LOC).toString()));
					c.setLocAdded(Integer.parseInt(array.getJSONObject(i).get(LOC_ADDED).toString()));
					c.setAuthors(Integer.parseInt(array.getJSONObject(i).get(N_AUTH).toString()));
					c.setNRevisions(Integer.parseInt(array.getJSONObject(i).get(NR).toString()));
			
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
    }*/
	
	public static void computeMetrics(List<Release> releases, String repo) throws MissingObjectException, CorruptObjectException, IOException {
		Repository repository = new FileRepository(repo);
		for (Release r: releases) {
			computeNAuth(r);
			computeNR(r);
			setLocAddedDeleted(r, repository);
			computeLocAdded(r);
			setLocAdded(r);
		}
	}
	
	public static void setLocAddedDeleted(Release release, Repository repo) throws MissingObjectException, CorruptObjectException, IOException {
	    DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
	    df.setRepository(repo);
	    df.setDiffComparator(RawTextComparator.DEFAULT);
	    df.setDetectRenames(true);
	    int linesDeleted = 0;
		int linesAdded = 0;
		for (DiffEntry diff: release.getDiffsWithDuplicated()) {
			for (Edit edit : df.toFileHeader(diff).toEditList()) {
	            linesDeleted += edit.getEndA() - edit.getBeginA();
	            linesAdded += edit.getEndB() - edit.getBeginB();
	        }
			for (ClassModel cM: release.getParsedDiffsWithDuplicated()) {
				if (cM.getName().equals(diff.getNewPath())) {
					cM.setLinesAdded(linesAdded);
					cM.setLinesDeleted(linesDeleted);
				}
			}
		}
	}
	
	public static void computeLocAdded(Release r) {
		List<ClassModel> temp = new ArrayList<>();
		for (ClassModel c: r.getParsedDiffsWithDuplicated()) {
			temp.add(c);
		}
		while (!temp.isEmpty()) {
			int linesAdded = 0;
			List<Integer> addedList = new ArrayList<>();
			ClassModel cM1 = temp.get(0);
			for (ClassModel cM2: r.getParsedDiffsWithDuplicated()) {
				if (cM1.getName().equals(cM2.getName())) {
					linesAdded += cM2.getLinesAdded();
					addedList.add(cM2.getLinesAdded());
					temp.remove(cM2);
				}
			}
			for (ClassModel cM: r.getParsedReleaseDiffs()) {
				if (cM1.getName().equals(cM.getName())) {
					cM.setLocAdded(linesAdded);
					cM.setAvgLocAdded(linesAdded/addedList.size());
					cM.setMaxLocAdded(Collections.max(addedList));					
				}
			}
		}
	}
	
	public static void setLocAdded(Release r) {
		for (ClassModel cM1: r.getParsedReleaseDiffs()) {
			int locAdded = 0;
			int maxLocAdded = 0;
			float avgLocAdded = 0;
			for (ClassModel cM2: r.getReleaseClasses()) {
				if (cM1.getName().equals(cM2.getName())) {
					locAdded = cM1.getLocAdded();
					maxLocAdded = cM1.getMaxLocAdded();
					avgLocAdded = cM1.getAvgLocAdded();
					cM2.setLocAdded(locAdded);
					cM2.setMaxLocAdded(maxLocAdded);
					cM2.setAvgLocAdded(avgLocAdded);
				}
			}
		}
	}
	
	public static void computeNR(Release r) {
		List<ClassModel> temp = new ArrayList<>();
		for (ClassModel c: r.getClassesWithDuplicated()) {
			temp.add(c);
		}
		while (!temp.isEmpty()) {
			int k = 0;
			ClassModel cM1 = temp.get(0);
			for (ClassModel cM2: r.getClassesWithDuplicated()) {
				if (cM1.getName().equals(cM2.getName())) {
					k++;
					temp.remove(cM2);
				}
			}
			for (ClassModel cM: r.getReleaseClasses()) {
				if (cM1.getName().equals(cM.getName())) {
					cM.setNRevisions(k);
				}
			}
		}
	}
	
	public static void computeNAuth(Release r) {
		for (ClassModel cM1: r.getReleaseClasses()) {
			List<String> authList = new ArrayList<>();
			for (ClassModel cM2: r.getClassesWithDuplicated()) {
				if (cM1.getName().equals(cM2.getName())) {
					authList.add(cM2.getDeveloper());
				}
			}
			Set<String> set = new HashSet<>(authList);
			authList.clear();
			authList.addAll(set);
			cM1.setAuthors(authList.size());
		}
	}
	
	
}
