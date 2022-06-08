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
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.json.JSONArray;

public class GetReleaseInfo {

	private GetReleaseInfo() {}
	
	static Integer max = 1;
	static Integer index;

	private static Map<LocalDateTime, String> releaseNames;
	private static Map<LocalDateTime, String> releaseID;
	private static List<LocalDateTime> releases;
	
	public static List<Release> getReleaseList(String projName) throws ParseException, JSONException, IOException {
		
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
	
	public static List<Ticket> ticketFilter(List<Ticket> tList) {
		List<Ticket> ticketFiltrati = new ArrayList<>();
		for (Ticket t: tList) {
			if (t.getFV() != null) {
				ticketFiltrati.add(t);
			}
		}
		return ticketFiltrati;
	}
	
	public static List<Ticket> assignTicketIVFromJira(List<Ticket> tList, List<Release> rList) throws ParseException, IOException{
		for (Ticket t: tList) {
			GetJsonFromUrl.returnAffectedVersion(t, rList);
		}
		// ordino i ticket per data di creazione
		Collections.sort(tList, new Comparator<Ticket>() {
			  public int compare(Ticket o1, Ticket o2) {
				  Date date1 = o1.getResolutionDate();
				  Date date2 = o2.getResolutionDate();
			      return date1.compareTo(date2);
			  }
		});
		return tList;
	}
	
	public static List<Ticket> compareTicketReleaseOV(List<Release> relList, List<Ticket> tList) {
		for (Ticket t: tList) {
			if (t.getCreationDate().before(relList.get(0).getDate()) || t.getCreationDate().equals(relList.get(0).getDate())) {
				t.setOV(relList.get(0).getNumber());
			} else {
				for (int i = 1; i < relList.size(); i++) {
					if ((t.getCreationDate().before(relList.get(i).getDate()) || t.getCreationDate().equals(relList.get(i).getDate())) && t.getCreationDate().after(relList.get(i-1).getDate())) {
						t.setOV(relList.get(i).getNumber());
					}
				}
			}
			
		}
		return tList;
	}

	public static List<Ticket> compareTicketReleaseFV(List<Release> relList, List<Ticket> tList) {
		for (Ticket t: tList) {
			if (t.getResolutionDate().before(relList.get(0).getDate()) || t.getResolutionDate().equals(relList.get(0).getDate())) {
				t.setFV(relList.get(0).getNumber());
			} else {
				for (int i = 1; i < relList.size(); i++) {
					if ((t.getResolutionDate().before(relList.get(i).getDate()) || t.getResolutionDate().equals(relList.get(i).getDate())) && t.getResolutionDate().after(relList.get(i-1).getDate())) {
						t.setFV(relList.get(i).getNumber());
					}
				}
			}
			
		}
		return tList;
	}

	public static void addRelease(String strDate, String name, String id) {
		  LocalDate date = LocalDate.parse(strDate);
		  LocalDateTime dateTime = date.atStartOfDay();
		  if (!releases.contains(dateTime))
			  releases.add(dateTime);
		  releaseNames.put(dateTime, name);
		  releaseID.put(dateTime, id);
	}
	
	public static List<Release> assignCommitsToRelease(List<RevCommit> commitList, List<Release> releaseList) {
		Release release1;
		Release release2;
		release1 = releaseList.get(0);
		fillFirstRelease(release1, commitList);
		for (int k = 1; k < releaseList.size(); k++) {
			List<RevCommit> cListPerRelease = new ArrayList<>();
			release2 = releaseList.get(k);
			for (RevCommit c: commitList) {
				Date cDate = new Date(c.getCommitTime() * 1000L);
				if ((cDate.before(release2.getDate()) || cDate.equals(release2.getDate())) && cDate.after(release1.getDate())) {
					cListPerRelease.add(c);
				}
			}
			/**
			 * ordino le commit nelle singole release per data
			 */
			Collections.sort(cListPerRelease, new Comparator<RevCommit>() {
				  public int compare(RevCommit o1, RevCommit o2) {
					  Date date1 = new Date(o1.getCommitTime() * 1000L);
					  Date date2 = new Date(o2.getCommitTime() * 1000L);
				      return date1.compareTo(date2);
				  }
			});
			release2.setRCommitList(cListPerRelease);
			release1 = release2;
		}
		return releaseList;
	}
	
	public static void assignClassesToRelease(List<RevCommit> revList, List<Release> rList, String repo) throws IOException {
		Repository repository = new FileRepository(repo);
		for (Release r: rList) {
			List<String> temp = new ArrayList<>();
			List<String> classesName = new ArrayList<>();
			List<ClassModel> classes = new ArrayList<>();
			List<ClassModel> classesWithDuplicated = new ArrayList<>();
			List<RevCommit> cList = new ArrayList<>();
			for (RevCommit rC: revList) {
				if (r.getRCommitList().contains(rC)) {
					temp.addAll(getPathFromTree(rC, repository));
					for (int i = 0; i < getPathFromTree(rC, repository).size(); i++) {
						cList.add(rC);
					}
				}
			}
			for (int i = 0; i < temp.size(); i++) {
				classesWithDuplicated.add(new ClassModel(temp.get(i), false, cList.get(i).getAuthorIdent().getName()));
				if (!classesName.contains(temp.get(i))) {
					classesName.add(temp.get(i));
					classes.add(new ClassModel(temp.get(i), false, null));
				}
			}
			r.setReleaseClasses(classes);
			r.setClassesWithDuplicated(classesWithDuplicated);
		}
	}
	
	public static List<String> getPathFromTree(RevCommit rC, Repository repo) throws IOException{
		List<String> classList = new ArrayList<>();
		RevTree tree = rC.getTree();
		try(TreeWalk treeWalk = new TreeWalk(repo)){
			treeWalk.addTree(tree);
			treeWalk.setRecursive(true);
			while (treeWalk.next()) {
				String path = treeWalk.getPathString();
				if (path.contains(".java"))
					classList.add(path);
			}
		}
		return classList;
	}
	
	public static List<Release> assignDiffsToRelease(List<RevCommit> revList, List<Release> rList, String repo) throws IOException {
		Repository repository = new FileRepository(repo);
		for (Release r: rList) {
			List<DiffEntry> temp = new ArrayList<>();
			List<DiffEntry> javaClasses = new ArrayList<>();
			List<DiffEntry> diffs = new ArrayList<>();
			for (RevCommit rev: revList) {
				if (r.getRCommitList().contains(rev)) {
					temp.addAll(GetGitInfo.getDiffs(repository, rev));	
				}
			}
			for (DiffEntry d: temp) {
				if (d.toString().contains(".java")) {
					javaClasses.add(d);
				}
			}
			r.setDiffsWithDuplicated(javaClasses);
			diffs = filterDuplicates(javaClasses);
			r.setReleaseDiffs(diffs);
		}
		return rList;
	}
	
	public static List<DiffEntry> filterDuplicates(List<DiffEntry> list) {
		List<DiffEntry> diffs = new ArrayList<>();
		List<String> filenames = new ArrayList<>();
		for (DiffEntry d: list) {
			filenames.add(d.getNewPath());
		}
		Set<String> set = new HashSet<>(filenames);
		filenames.clear();
		filenames.addAll(set);
		for (String filename: filenames) {
			for (DiffEntry d: list) {
				if (filename.equals(d.getNewPath())) {
					diffs.add(d);
					break;
				}
			}
		}
		return diffs;
	}

	public static void fillFirstRelease(Release release0, List<RevCommit> commitList) {
		List<RevCommit> cListPerRelease = new ArrayList<>();
		for (RevCommit c: commitList) {
			Date cDate = new Date(c.getCommitTime() * 1000L);
			if (cDate.before(release0.getDate()) || cDate.equals(release0.getDate())) {
				cListPerRelease.add(c);
			}
		}
		Collections.sort(cListPerRelease, new Comparator<RevCommit>() {
			  public int compare(RevCommit o1, RevCommit o2) {
				  Date date1 = new Date(o1.getCommitTime() * 1000L);
				  Date date2 = new Date(o2.getCommitTime() * 1000L);
			      return date1.compareTo(date2);
			  }
		});
		release0.setRCommitList(cListPerRelease);
	}
		
	public static void parseReleaseDiffs(List<Release> rList) {
		for (Release r: rList) {
			List<ClassModel> parsedClasses = new ArrayList<>();
			for (DiffEntry diff: r.getReleaseDiffs()) {
				ClassModel cM = new ClassModel(diff.getNewPath(), false, null);
				cM.setLocAdded(0);
				cM.setAvgLocAdded(0);
				cM.setMaxLocAdded(0);
				parsedClasses.add(cM);
			}
			r.setParsedReleaseDiffs(parsedClasses);
		}
	}
	
	public static void parseReleaseDiffsWithDuplicated(List<Release> rList) {
		for (Release r: rList) {
			List<ClassModel> parsedClasses = new ArrayList<>();
			for (DiffEntry diff: r.getDiffsWithDuplicated()) {
				ClassModel cM = new ClassModel(diff.getNewPath(), false, null);
				parsedClasses.add(cM);
			}
			r.setParsedDiffsWithDuplicated(parsedClasses);
		}
	}
	
	public static void setTicketListPerClass(List<Ticket> ticketConIV, List<Release> releaseConClassi) {
		/*
		 * per ogni ticket, devo prendere la lista di release comprese tra IV e FV esclusa, e prendere tutte le classi contenute in questa release
		 * e aggiungere il ticket corrispondente a ognuna delle classi.
		 * In questo modo in ogni classe ho la lista dei ticket che l'hanno toccata.
		 * 
		 * Mi serve davvero????
		 * 
		 * ---------------------------------------------FINE METODO-------------------------------------------------	
		 * 
		 * A questo punto, per ogni classe, per ogni ticket (della classe)
		 * 
		 * Per ogni ticket, per ogni release toccata dal ticket (le devo prima settare ma non ci vuole niente), per ogni classe delle release,
		 * settare la bugginess a true
		 * 
		 * 
		 * 
		 * 
		 */
	}
	
	/*public static void dateComparator(List<Release> release, List<Commit> commit) {
		for (int i = 0; i < release.size(); i++) {
			for(int k = 0; k< commit.size(); k++) {
				if(!release.get(i).getDate().after(commit.get(k).getDate()))  {
					int t = k-1;
					if(t<0){
						t = 0;
					}
					release.get(i).setCommit(commit.get(t));
					break;
				}
			}
		}
	}*/

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


	/*public static boolean containsName(List<ClassModel> list, ClassModel c){
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
*/


	/*public static void classesPerRelease(List<Release> releaseList, List<Commit> commitList) {
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
	}*/

	/*public static void assignClassesToRelease(List<Commit> commitList, int firstRef, int lastRef, List<ClassModel> temp) {
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

	/*public static void assignCommitListToRelease(List<Release> releaseList, List<Commit> commitList) throws ParseException, IOException {
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
	}*/

 }

	
