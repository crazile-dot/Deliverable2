package deliverable;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.json.JSONArray;
import org.json.JSONException;



public class CsvWriter {
	
	static String projName = "BOOKKEEPER";
	static Integer max = 1;
	static Integer index;
	
	public static void CsvWriteArray(List <Date> createdarray, List <Date> resolutionarray, List <String> versionarray, List <String> Idarray) throws IOException {
		try (BufferedWriter br = new BufferedWriter(new FileWriter("/Users/mirko/Desktop/output.csv"))) {
			// Write header of the csv file produced in output
			
			StringBuilder sb = new StringBuilder();
			sb.append("creation date: ");
			sb.append(",");
			sb.append("Resolution date");
			sb.append(",");
			sb.append("Version");
			sb.append(",");
			sb.append("Id");
			sb.append(",");
			sb.append("Commit");
			sb.append("\n");
			br.write(sb.toString());
			int size = createdarray.size();
			for (int i = 0 ; i < size; i++) {
				StringBuilder sb2 = new StringBuilder();
				sb2.append(createdarray.get(i));
				sb2.append(",");
				sb2.append(resolutionarray.get(i));
				sb2.append(",");
				sb2.append(versionarray.get(i));
				sb2.append(",");
				sb2.append(Idarray.get(i));
				sb2.append("\n");
				br.write(sb2.toString());
			}
		}

	}
	
	
	public static void CsvVersionArray(List <Release> releases) throws IOException {
		try (BufferedWriter br = new BufferedWriter(new FileWriter("/Users/mirko/Desktop/Releases.csv"))) {
			StringBuilder sb = new StringBuilder();
			sb.append("Number: ");
			sb.append(",");
			sb.append("Id");
			sb.append(",");
			sb.append("Name");
			sb.append(",");
			sb.append("Date");
			sb.append(",");
			sb.append("Commit");
			sb.append("\n");
			br.write(sb.toString());
			int size = releases.size()/2;
			for (int i = 0 ; i < size; i++) {
				StringBuilder sb2 = new StringBuilder();
				sb2.append(releases.get(i).getNumber());
				sb2.append(",");
				sb2.append(releases.get(i).getId());
				sb2.append(",");
				sb2.append(releases.get(i).getDate().toString());
				sb2.append(",");
				sb2.append(releases.get(i).getCommit().toString());
				sb2.append("\n");
				br.write(sb2.toString());
			}	
		}
	}
	
	
	public  static void computeBuggyness(List <Release> releases) {
		for (Release r : releases) {
			for (Class c : r.getReleaseClasses()) {
				c.setBugginess(false);
				if(c.getTicketList()!= null) {
					for(Ticket t: c.getTicketList())
						if(t.getIV() <= r.getNumber() && t.getFV() > r.getNumber()) {
						c.setBugginess(true);
						break;
						}
					}
			}
		}
	}
	
	public static void csvFinal(List <Release> releases) throws IOException {
		try (BufferedWriter br = new BufferedWriter(new FileWriter("C:\\Users\\crazile\\Desktop\\Releases.csv"))) {
			StringBuilder sb = new StringBuilder();
			sb.append("Release");
			sb.append(",");
			sb.append("Name");
			sb.append(",");
			sb.append("LOC");
			sb.append(",");
			sb.append("Age");
			sb.append(",");
			sb.append("CHG");
			sb.append(",");
			sb.append("MAX_CHG");
			sb.append(",");
			sb.append("AVG_CHG");
			sb.append(",");
			sb.append("NFix");
			sb.append(",");
			sb.append("Authors");
			sb.append(",");
			sb.append("NR");
			sb.append(",");
			sb.append("LOC_added");
			sb.append(",");
			sb.append("MAX_LOC_added");
			sb.append(",");
			sb.append("AVG_LOC_added");
			sb.append(",");
			sb.append("Buggy");
			sb.append("\n");
			br.write(sb.toString());
			int size = releases.size();
			for (int i = 0 ; i < size; i++) {
				for (Class c : releases.get(i).getReleaseClasses()) {
					StringBuilder sb2 = new StringBuilder();
					sb2.append(releases.get(i).getNumber());
					sb2.append(",");
					sb2.append(c.getName());
					sb2.append(",");
					sb2.append(TempMetrics.classAge(c));
					sb2.append(",");
					sb2.append(c.getChg());
					sb2.append(",");
					sb2.append(c.getMaxChg());
					sb2.append(",");
					sb2.append(TempMetrics.getAVGChg(c));
					sb2.append(",");
					sb2.append(c.getBugginess());
					sb2.append("\n");
					br.write(sb2.toString());
				}
			}	
		}
	}
	
	public static List<Ticket> filterTickets(List<Ticket> ticketList) {
		List<Ticket> ret = new ArrayList<>();
		for (Ticket t: ticketList) {
			if(t.getFV() != t.getIV()) {
				ret.add(t);
			}
		}
		return ret;
	}
	
	
	public static void main(String[] args) throws IOException, JSONException, ParseException, GitAPIException {
		long inizio = System.currentTimeMillis();
		Integer i = 0;
		Integer j = 0;
		j = i + 1000;
		
		
		String url = "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22"
	               + projName + "%22AND%22issueType%22=%22Bug%22AND(%22status%22=%22closed%22OR"
	               + "%22status%22=%22resolved%22)AND%22resolution%22=%22fixed%22&fields=key,resolutiondate,affectedVersion,versions,created&startAt="
				+ i.toString() + "&maxResults=" + j.toString();
		List<Date> createdarray = deliverable.GetJsonFromUrl.DateArray(url, i , j , "created");
		List<Date> resolutionarray = deliverable.GetJsonFromUrl.DateArray(url, i , j , "resolutiondate");
		List <String> keyArray = deliverable.GetJsonFromUrl.keyArray(url, i, j, "key");
		List <String> version = deliverable.getReleaseInfo.VersionArray(url, i ,1000, "name");
		List<String> id = deliverable.GetJsonFromUrl.IdArray(url, i , j );
		List<Ticket> ticket = new ArrayList<>();
		List<Ticket> ticketConCommit = new ArrayList<>();
		List <Commit> commit = GetGitInfo.getCommitList();
		List<Release> releases = getReleaseInfo.getReleaseList();
		int size = getReleaseInfo.getReleaseList().size();
		ticket = GetJsonFromUrl.setTicket(createdarray,resolutionarray,version,keyArray);
		getReleaseInfo.dateComparator(releases,commit);
		/*for(Ticket t : ticket) {
			GetJsonFromUrl.returnAffectedVersion(t, releases);
		}
		ticketConCommit = GetGitInfo.setClassVersion(ticket,commit,releases);	
		//filterTickets(ticketConCommit);
		List<Ticket> fin = Proportion.checkIV(ticketConCommit);
		for (Ticket t: fin) {
			System.out.println("FV: " + t.getFV() + " OV: " + t.getOV() + " IV: " + t.getIV() + " P: " + t.getP());
		}*/
		//for (int k = 0; k < ticketConCommit.size(); k++) {
			//System.out.println("ticket: " + ticketConCommit.get(k).getId() + "ticket FV: " + ticketConCommit.get(k).getFV() + "ticket IV: " + ticketConCommit.get(k).getIV()+ "Ticket OV: " +ticketConCommit.get(k).getOV());
		//}	
		getReleaseInfo.assignClassListToRelease(releases, commit);
		computeBuggyness(releases.subList(0, size/2));
		//CsvWriteArray(createdarray,resolutionarray,keyArray, id, commit);
		//CsvVersionArray(releases);
		getReleaseInfo.assignCommitListToRelease(releases, commit);
		//Metriche
		JSONArray jsonArray = Metrics.getMetrics(releases.subList(0, size/2));
		FileWriter myWriter = new FileWriter("C:\\Users\\crazile\\Desktop\\output.txt");
		for(int k = 0; k < jsonArray.length(); k++) {
			//System.out.println(jsonArray.getJSONObject(k));
			myWriter.write(jsonArray.getJSONObject(k).toString());
		}
		//
		
		//csvFinal(releases.subList(0,size/2));
		long fine = System.currentTimeMillis();
		System.out.println((fine-inizio)/1000);
		
	}
}
