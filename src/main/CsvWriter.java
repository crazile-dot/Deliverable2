package main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;



public class CsvWriter {
	
	//static String projName = "BOOKKEEPER";
	static String projName = "S2GRAPH";
	static Integer max = 1;
	static Integer index;
	
	public static void CsvWriteArray(List <Date> createdarray, List <Date> resolutionarray, List <String> versionarray, List <String> Idarray) throws IOException {
		try (BufferedWriter br = new BufferedWriter(new FileWriter("C:\\Users\\Ilenia\\Desktop\\output.csv"))) {
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
		try (BufferedWriter br = new BufferedWriter(new FileWriter("C:\\Users\\Ilenia\\Desktop\\Releases.csv"))) {
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

	public static void csvByWeka(List <WekaData> wList, List<Release> releases) throws IOException {
		String name = "C:\\Users\\Ilenia\\Desktop\\weka-data" + ".csv";
		try (BufferedWriter br = new BufferedWriter(new FileWriter(name))) {
			StringBuilder sb = new StringBuilder();
			sb.append("Dataset");
			sb.append(",");
			sb.append("#trainingRelease");
			sb.append(",");
			sb.append("%training");
			sb.append(",");
			sb.append("%DefectiveInTraining");
			sb.append(",");
			sb.append("%DefectiveInTesting");
			sb.append(",");
			sb.append("Classifier");
			sb.append(",");
			sb.append("Balancing");
			sb.append(",");
			sb.append("FeatureSelection");
			sb.append(",");
			sb.append("Cost Sensitive");
			sb.append(",");
			sb.append("Sensitivity");
			sb.append(",");
			sb.append("TP");
			sb.append(",");
			sb.append("FP");
			sb.append(",");
			sb.append("TN");
			sb.append(",");
			sb.append("FN");
			sb.append(",");
			sb.append("Recall");
			sb.append(",");
			sb.append("Precision");
			sb.append(",");
			sb.append("AUC");
			sb.append(",");
			sb.append("Kappa");
			sb.append("\n");
			br.write(sb.toString());
			for (WekaData w : wList) {
				if(w.getEval() == null) {
					continue;
				}
				StringBuilder sb2 = new StringBuilder();
				sb2.append(w.getTrainingStep());
				sb2.append(",");
				sb2.append(w.getTrainingStep());
				sb2.append(",");
				sb2.append(((w.getTrainingStep())*100/7));
				sb2.append(",");
				sb2.append(getPercentageDefectiveInTraining(releases, w.getTrainingStep()));
				sb2.append(",");
				sb2.append((releases.get(w.getTrainingStep()).getNumOfBuggyClass()*100)/(releases.get(w.getTrainingStep()).getReleaseClasses().size()+1));
				sb2.append(",");
				sb2.append(w.getClassifier());
				sb2.append(",");
				sb2.append(w.getSampling());
				sb2.append(",");
				sb2.append(w.getFeatureSelection());
				sb2.append(",");
				sb2.append(getCostName(w));
				sb2.append(",");
				sb2.append(w.getTreshold());
				sb2.append(",");
				try {
					sb2.append(w.getEval().truePositiveRate(1));
					sb2.append(",");
					sb2.append(w.getEval().falsePositiveRate(1));
					sb2.append(",");
					sb2.append(w.getEval().falsePositiveRate(1));
					sb2.append(",");
					sb2.append(w.getEval().falseNegativeRate(1));
					sb2.append(",");
					sb2.append(w.getEval().recall(1));
					sb2.append(",");
					sb2.append(w.getEval().precision(1));
					sb2.append(",");
					sb2.append(w.getEval().areaUnderROC(1));
					sb2.append(",");
					sb2.append(w.getEval().kappa());
					sb2.append("\n");
					br.write(sb2.toString());
				}
				catch(NullPointerException e) {
					System.out.print("NullPointerException caught");
				}
			}
		}

	}
	
	public static String getCostName(WekaData w) {
		String s = new String();
		if(w.getCostSensitive() == 0) {
			s = "no Cost Sensitive";
		}
		else if(w.getCostSensitive() == 1) {
			s = "Sensitive Treshold";
		}
		else {
			s = "Sensitive learning";
		}
		return s;
	}
	
	
	public static String csvForWeka(List <Release> releases, int counter) throws IOException {
		String name = "C:\\Users\\Ilenia\\Desktop\\Releases" + counter + ".csv";
		try (BufferedWriter br = new BufferedWriter(new FileWriter(name))) {
			StringBuilder sb = new StringBuilder();
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
			sb.append("Bug Fixed");
			sb.append(",");
			sb.append("NAuth");
			sb.append(",");
			sb.append("Number of Commit");
			sb.append(",");
			sb.append("LOC Added");
			sb.append(",");
			sb.append("AVG_LOC Added");
			sb.append(",");
			sb.append("MAX_LOC Added");
			sb.append(",");
			sb.append("Buggy");
			sb.append("\n");
			br.write(sb.toString());
			int size = releases.size();
			for (int i = 0 ; i < size; i++) {
				for (Class c : releases.get(i).getReleaseClasses()) {
					StringBuilder sb2 = new StringBuilder();
					sb2.append(c.getLoc());
					sb2.append(",");
					sb2.append(TempMetrics.classAge(c));
					sb2.append(",");
					sb2.append(c.getChg());
					sb2.append(",");
					sb2.append(c.getMaxChg());
					sb2.append(",");
					sb2.append(TempMetrics.getAVGChg(c));
					sb2.append(",");
					sb2.append(TempMetrics.numberOfBugFixedForRelease(releases.get(i), c));
					sb2.append(",");
					sb2.append(c.getAuthors());
					sb2.append(",");
					sb2.append(c.getNRevisions());
					sb2.append(",");
					sb2.append(c.getLocAdded());
					sb2.append(",");
					sb2.append(c.getMaxLocAdded());
					sb2.append(",");
					sb2.append(c.getAvgLocAdded());
					sb2.append(",");
					sb2.append(c.getBugginess());
					sb2.append("\n");
					br.write(sb2.toString());
				}
			}	
		}
		return name;
	}
	
	
	public static int getPercentageDefectiveInTraining(List<Release> releases, int z) {
		int counter = 0;
		int releaseCounter = 0;
		for(int i = 0; i < z; i ++) {
			counter = counter + releases.get(i).getNumOfBuggyClass();
			releaseCounter = releaseCounter + releases.get(i).getReleaseClasses().size();
		}
		return (counter*100)/releaseCounter;
	}
	
	public static int getDefectiveInTraining(List<Release> releases, int z) {
		int counter = 0;
		for(int i = 0; i < z; i ++) {
			System.out.println("PRINT 1: " + releases.get(i).toString());
			System.out.println(("PRINT 2: " + releases.get(i).getNumOfBuggyClass()));
			counter = counter + releases.get(i).getNumOfBuggyClass();

		}
		return counter;
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
		try (BufferedWriter br = new BufferedWriter(new FileWriter("C:\\Users\\Ilenia\\Intellij-projects\\Releases.csv"))) {
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
					sb2.append(c.getLoc());
					sb2.append(",");
					sb2.append(TempMetrics.classAge(c));
					sb2.append(",");
					sb2.append(c.getChg());
					sb2.append(",");
					sb2.append(c.getMaxChg());
					sb2.append(",");
					sb2.append(TempMetrics.getAVGChg(c));
					sb2.append(",");
					sb2.append(TempMetrics.numberOfBugFixedForRelease(releases.get(i), c));
					sb2.append(",");
					sb2.append(c.getAuthors());
					sb2.append(",");
					sb2.append(c.getNRevisions());
					sb2.append(",");
					sb2.append(c.getLocAdded());
					sb2.append(",");
					sb2.append(c.getMaxLocAdded());
					sb2.append(",");
					sb2.append(c.getAvgLocAdded());
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
	
	
	public static void main(String[] args) throws Exception {
		long inizio = System.currentTimeMillis();
		Integer i = 0;
		Integer j = 0;
		j = i + 1000;
		
		
		String url = "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22"
	               + projName + "%22AND%22issueType%22=%22Bug%22AND(%22status%22=%22closed%22OR"
	               + "%22status%22=%22resolved%22)AND%22resolution%22=%22fixed%22&fields=key,resolutiondate,affectedVersion,versions,created&startAt="
				+ i.toString() + "&maxResults=" + j.toString();
		List<Date> createdarray = main.GetJsonFromUrl.DateArray(url, i , j , "created");
		List<Date> resolutionarray = main.GetJsonFromUrl.DateArray(url, i , j , "resolutiondate");
		List <String> keyArray = main.GetJsonFromUrl.keyArray(url, i, j, "key");
		List <String> version = main.getReleaseInfo.VersionArray(url, i ,1000, "name");
		List<String> id = main.GetJsonFromUrl.IdArray(url, i , j );
		List<Ticket> ticket = new ArrayList<>();
		List<Ticket> ticketConCommit = new ArrayList<>();
		List <Commit> commit = GetGitInfo.getCommitList();
		List<Release> releases = getReleaseInfo.getReleaseList();
		List<String> testingSet = new ArrayList<>();
		List<String> trainingSet = new ArrayList<>();
		int size = getReleaseInfo.getReleaseList().size();
		ticket = GetJsonFromUrl.setTicket(createdarray,resolutionarray,version,keyArray);
		getReleaseInfo.dateComparator(releases,commit);
		for(Ticket t : ticket) {
			GetJsonFromUrl.returnAffectedVersion(t, releases);
		}
		ticketConCommit = GetGitInfo.setClassVersion(ticket,commit,releases);	
		List<Ticket> fin = Proportion.checkIV(ticketConCommit);
		getReleaseInfo.assignClassListToRelease(releases, commit);
		computeBuggyness(releases.subList(0, size/2));
		List<Release> halfReleases = releases.subList(0, size/2);
		getReleaseInfo.assignCommitListToRelease(halfReleases, commit);

		//Metriche
		JSONArray jsonArray = Metrics.getMetrics(halfReleases.subList(0, size/2));
		FileWriter myWriter = new FileWriter("C:\\Users\\Ilenia\\Intellij-projects\\output.txt");
		for(int k = 0; k < jsonArray.length(); k++) {
			myWriter.write(jsonArray.getJSONObject(k).toString());
		}
		for (Release r: releases.subList(0,size/2)) {
			Metrics.setMetric(r.getReleaseClasses(), jsonArray);
		}
		
		csvFinal(releases.subList(0,size/2));
		List<WekaData> wekaList = new ArrayList<>();
		trainingSet = TestWekaEasy.makeTrainingSet(releases.subList(0,size/2));
		testingSet = TestWekaEasy.makeTestingSet(releases.subList(0,size/2));
		for(int z = 1; z< testingSet.size()+1; z ++) {
			wekaList.addAll(TestWekaEasy.wekaAction(testingSet.get(z-1), trainingSet.get(z-1), z, getDefectiveInTraining(releases.subList(0,size/2), z)));
		}
		csvByWeka(wekaList, releases.subList(0,size/2));
		long fine = System.currentTimeMillis();
		System.out.println((fine-inizio)/1000);
		
	}
}
