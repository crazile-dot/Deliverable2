package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.util.io.DisabledOutputStream;

public class GetGitInfo {

	private GetGitInfo() {}

	public static List<RevCommit> getCommitList(String repository) throws IOException, GitAPIException {
		List<RevCommit> commitList = new ArrayList<>();
		
		Repository repo = new FileRepository(repository);
	    Git git = new Git(repo);
	    RevWalk walk = new RevWalk(repo);

	    List<Ref> branches = git.branchList().call();

	    for (Ref branch : branches) {
	        String branchName = branch.getName();
	        
	        Iterable<RevCommit> commits = git.log().all().call();

	        for (RevCommit commit : commits) {
	            boolean foundInThisBranch = false;

	            RevCommit targetCommit = walk.parseCommit(repo.resolve(
	                    commit.getName()));
	            for (Map.Entry<String, Ref> e : repo.getAllRefs().entrySet()) {
	                if (e.getKey().startsWith(Constants.R_HEADS)) {
	                    if (walk.isMergedInto(targetCommit, walk.parseCommit(
	                            e.getValue().getObjectId()))) {
	                        String foundInBranch = e.getValue().getName();
	                        if (branchName.equals(foundInBranch)) {
	                            foundInThisBranch = true;
	                            break;
	                        }
	                    }
	                }
	            }

	            if (foundInThisBranch) {
	                commitList.add(commit);
	            }
	        }
	    }
	    return commitList;
	}
	
	public static List<Commit> parseRevCommits(List<RevCommit> revCommitList) {
		List<Commit> commitList = new ArrayList<>();
		
		for (RevCommit rC: revCommitList) {
			Commit commit = new Commit(rC.getName(), new Date(rC.getCommitTime() * 1000L), rC.getFullMessage(), rC.getAuthorIdent().getName());
			commitList.add(commit);
		}
		return commitList;
	}
	
	public static List<Ticket> compareTicketCommit(List<RevCommit> commitList, List<Ticket> ticketList) {
		List<Ticket> tList = new ArrayList<>();
		for (Ticket t: ticketList) {
			for (RevCommit c:commitList) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				if (sdf.format(t.getResolutionDate()).equals(sdf.format(new Date(c.getCommitTime() * 1000L)))) {
					t.setCommit(c);
				}
			}
			if(t.getCommit() != null) {
				tList.add(t);
			}
		}
		return tList;
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
 
	/*public static void checkLine(String line, List<String> idList, List<Date> dateList) throws  ParseException{
		if (line.startsWith(search)) {
			String d = line.substring(7);
			idList.add(d);
		} else if (line.startsWith("Date:")) {
			String d = line.substring(7);
			Date commitDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(d);
			dateList.add(commitDate);
		}
	}

	public static List<String> checkOtherLine(String line, List<String> classesList, List<Commit> commitList, List<String> idList, int i) {
		if (line != null && line.endsWith(".java")) {
			classesList.add(line.substring(2));
		} else if (line != null && line.startsWith(search) && i != 0 && !classesList.isEmpty()) {
			commitList.get(idList.indexOf(line.substring(7)) - 1).setStringClasses(classesList);
			return new ArrayList<>();
		}
		return classesList;
	}

	public static void fillCommitList(List<String> idList, List<Date> dateList, List<Commit> commitList) {
		for (int i = 0; i < idList.size(); i++) {
			Commit commit = new Commit();
			commit.setIdNumber(i);
			commit.setId(idList.get(i));
			commit.setDate(dateList.get(i));
			commitList.add(commit);
		}
	}

	public static void setClasses(List<Commit> commitList) {
		for (Commit commit: commitList) {
			List<ClassModel> cList = new ArrayList<>();
			if (commit.getStringClasses() != null) {
				List<String> sList = commit.getStringClasses();
				for (String s: sList) {
					ClassModel c = new ClassModel(s);
					c.setDate(commit.getDate());
					c.setChg(commit.getStringClasses().size());
					cList.add(c);
				}
			}
			commit.setClasses(cList);
		}
	}


	public static void setId(String line, BufferedReader is, List<String> idList) throws IOException{
		while (!done && ((line = is.readLine()) != null)) {
			if (line.startsWith(search)) {
				String s = line.substring(7);
				idList.add(s);
			}
		}
	}

	public static void setCommits(List<Commit> commitList, String e, Ticket t, List<Release> releases, List<Ticket> ticketList) {
		for(Commit c: commitList) {
			if(c.getId().equals(e)) {
				t.setCommit(c);
				GetJsonFromUrl.setFVOV(t, releases);
				if(t.getOV() != null && t.getFV() >= t.getOV() && !ticketList.contains(t)) {
					ticketList.add(t);
				}
				List <ClassModel> classes = c.getClasses();
				setSingleTicket(classes, t);
			}
		}
	}

	public static void setSingleTicket(List<ClassModel> classes, Ticket t) {
		for(ClassModel cl: classes) {
			cl.setSingleTicket(t);
		}
	}
	
	public static List<Ticket> setVersion(List <Ticket> ticket, List <Commit> commitList, List<Release> releases) throws IOException{
		List<Ticket> ticketList = new ArrayList<>();
		for(Ticket t : ticket) {
			BufferedReader is;  // reader for output of process
		    String line = "";
		    List<String> idList = new ArrayList<>();
			File dir = new File("C:\\Users\\Ilenia\\Intellij-projects\\" + projName);
			String ticketId = t.getId();
		    final Process p = Runtime.getRuntime().exec("git log --grep=" + ticketId + " --date=iso-strict --name-status --stat HEAD  --date-order --reverse", null, dir);
		    is = new BufferedReader(new InputStreamReader(p.getInputStream()));
		    setId(line, is, idList);
		    for(String e: idList) {
				setCommits(commitList, e, t, releases, ticketList);
		    }
		}
	    return ticketList;
	
	}*/

}
