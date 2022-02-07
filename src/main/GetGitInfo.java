package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class GetGitInfo {

	private GetGitInfo() {}
	
	private static final String PROGRAM = "git log --date=iso-strict --name-status --stat HEAD --date-order --reverse";
	private static boolean done = false;
	private static String search = "commit";
	private static String projName = "bookkeeper";


	public static void checkLine(String line, List<String> idList, List<Date> dateList) throws  ParseException{
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

	//Con questa funzione prendo una lista di commit (oggetto Commit con id e data) dalla directory del progetto
	public static List<Commit> getCommits() throws IOException, ParseException{
		BufferedReader is;  // reader for output of process
	    String line;
	    List<Commit> commitList = new ArrayList<>();
	    List<String> idList = new ArrayList<>();
	    List<Date> dateList = new ArrayList<>();
	    List<String> classesList = new ArrayList<>();
	    File dir = new File("C:\\Users\\Ilenia\\Intellij-projects\\" + projName);
	    final Process p = Runtime.getRuntime().exec(PROGRAM, null, dir);
	    is = new BufferedReader(new InputStreamReader(p.getInputStream()));
	    int countLines = 0;
	    while (!done && ((line = is.readLine()) != null)) {
			checkLine(line, idList, dateList);
			countLines++;
	    }
	    if (idList.size() == dateList.size()) {
		    fillCommitList(idList, dateList, commitList);
	    }
	    final Process p2 = Runtime.getRuntime().exec(PROGRAM, null, dir);
	    BufferedReader is2 = new BufferedReader(new InputStreamReader(p2.getInputStream()));
	    for (int i = 0; i < countLines; i++) {
	    	line = is2.readLine();
	    	classesList = checkOtherLine(line, classesList, commitList, idList, i);
	    }
	    if (!commitList.isEmpty()) {
	    	setClasses(commitList);
	    }
	    return commitList;
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
	
	}

}
