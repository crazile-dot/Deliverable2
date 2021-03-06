package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.json.JSONArray;

public class CsvWriter {
	static String projName = "BOOKKEEPER";
	static String projName2 = "AVRO"; // promemoria
	
	private static final String REPO = "C://Users//Ilenia//Desktop//ISW//bookkeeper//.git";
	private static final String REPO2 = "C://Users//Ilenia//Desktop//ISW//avro//.git";
	
	private static final String out = "C://Users//Ilenia//Desktop//ISW//deliverable_output//releasesBookkeeper.csv";
	private static final String out2 = "C://Users//Ilenia//Desktop//ISW//deliverable_output//releasesAvro.csv";


	static Integer max = 1;
	static Integer index;
	private static Logger logger;

	/*public static void csvByWeka(List <WekaData> wList, List<Release> releases) throws IOException {
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
				} catch(NullPointerException e) {

				}
			}
		}

	}
	
	public static String getCostName(WekaData w) {
		String s = "";
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
				for (ClassModel c : releases.get(i).getReleaseClasses()) {
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
			counter = counter + releases.get(i).getNumOfBuggyClass();

		}
		return counter;
	}

	public static void computeBuggy(List<Release> releases) {
		for (Release r : releases) {
			computeElement(r);
		}
	}

	public  static void computeElement(Release release) {
		for (ClassModel c : release.getReleaseClasses()) {
			c.setBugginess(false);
			if(c.getTicketList()!= null) {
				for(Ticket t: c.getTicketList())
					if(t.getIV() <= release.getNumber() && t.getFV() > release.getNumber()) {
					c.setBugginess(true);
					break;
					}
			}
		}
	}
	*/
	
	public static void csvFinal(List <Release> releases) throws IOException {
		try (BufferedWriter br = new BufferedWriter(new FileWriter(out))) {
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
			for (Release r: releases) {
				for (ClassModel cM1: r.getReleaseClasses()) {
					RetrieveBuggy.setBuggyField(cM1, r);
					StringBuilder sb2 = new StringBuilder();
					sb2.append(r.getNumber());
					sb2.append(",");
					sb2.append(cM1.getName());
					sb2.append(",");
					sb2.append("");
					sb2.append(",");
					sb2.append("");
					sb2.append(",");
					sb2.append("");
					sb2.append(",");
					sb2.append("");
					sb2.append(",");
					sb2.append("");
					sb2.append(",");
					sb2.append("");
					sb2.append(",");
					sb2.append(cM1.getAuthors());
					sb2.append(",");
					sb2.append(cM1.getNRevisions());
					sb2.append(",");
					sb2.append(cM1.getLocAdded());
					sb2.append(",");
					sb2.append(cM1.getMaxLocAdded());
					sb2.append(",");
					sb2.append(cM1.getAvgLocAdded());
					sb2.append(",");
					sb2.append(cM1.getBugginess());
					sb2.append("\n");
					br.write(sb2.toString());
				}
			}	
		}
	}
	
	public static void main(String[] args) throws Exception {
		
	    long start = System.currentTimeMillis();

		Integer i = 0;
		Integer j = 0;
		j = i + 1000;

		String url = "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22"
	               + projName + "%22AND%22issueType%22=%22Bug%22AND(%22status%22=%22closed%22OR"
	               + "%22status%22=%22resolved%22)AND%22resolution%22=%22fixed%22&fields=key,resolutiondate,affectedVersion,versions,created&startAt="
				+ i.toString() + "&maxResults=" + j.toString();
		
		/**
		 * Dal json estraggo le info che mi servono e creo una lista di oggetti Ticket
		 */
		List<Date> createdArray = main.GetJsonFromUrl.dateArray(url, i , j , "created");
		List<Date> resolutionArray = main.GetJsonFromUrl.dateArray(url, i , j , "resolutiondate");
		List<String> keyArray = main.GetJsonFromUrl.keyArray(url, i, j);
		List<String> versionArray = GetReleaseInfo.versionArray(url, i ,1000, "name");
		//main.GetJsonFromUrl.idArray(url, i , j );
		List<Ticket> ticketList = GetJsonFromUrl.setTicket(createdArray, resolutionArray, versionArray, keyArray);
		
		/**
		 * Ora prendo le commit 
		 */
		List<RevCommit> revCommitList = GetGitInfo.getCommitList(REPO);
		//List<Commit> commitList = GetGitInfo.parseRevCommits(revCommitList); 
		
		/**
		 * prendo le release e vi associo le commit corrispondenti, ordinate
		 */
		List<Release> releases = GetReleaseInfo.getReleaseList(projName);
		List<Release> halfReleases = releases.subList(0, releases.size()/2);
		List<Release> releaseConCommit = GetReleaseInfo.assignCommitsToRelease(revCommitList, halfReleases);	

		/**
		 * prendo i file .java dalle commit e li associo alle release corrispondenti
		 * (da una parte metto tutti i file .java relativi alla commit,
		 * dall'altra i file toccati dalla commit, cio? le diff)
		 */
		List<Release> releaseConClassi = GetReleaseInfo.assignDiffsToRelease(revCommitList, releaseConCommit, REPO);
		GetReleaseInfo.parseReleaseDiffs(releaseConClassi);		
		GetReleaseInfo.parseReleaseDiffsWithDuplicated(releaseConClassi);
		GetReleaseInfo.assignClassesToRelease(revCommitList, releaseConClassi, REPO);
		
		/**
		 * Ora assegno le commit di resolution, la FV e la OV ad ogni ticket
		 */
		List<Ticket> ticketConCommit = GetGitInfo.compareTicketCommit(revCommitList, ticketList);
		List<Ticket> ticketConFV = GetReleaseInfo.compareTicketReleaseFV(releaseConClassi, ticketConCommit);
		List<Ticket> ticketFiltrati = GetReleaseInfo.ticketFilter(ticketConFV);
		List<Ticket> ticketConOV = GetReleaseInfo.compareTicketReleaseOV(releaseConClassi, ticketFiltrati);
		
		/**
		 * ora devo prendere le IV per ogni ticket da jira (poi su quelle che non ce l'hanno, si fa proportion)
		 */
		List<Ticket> ticketConIV = GetReleaseInfo.assignTicketIVFromJira(ticketConOV, releaseConClassi);
		Proportion.checkIV(ticketConIV);
		Proportion.computeAVs(ticketConIV);
		
		/**
		 * assegno lista di ticket ad ogni classe
		 */
		//List<Release> releaseConTicket = setTicketListPerClass(ticketConIV, releaseConClassi);
		
		/**
		 * ora devo calcolare la bugginess per ogni file in ogni release
		 */
		RetrieveBuggy.computeBugginess(ticketConIV, releaseConClassi);
		
		/**
		 * calcoliamo le metriche 
		 */
		Metrics.computeMetrics(releaseConClassi, REPO);
		
		/**
		 * genero il csv con i risultati
		 */
		csvFinal(releaseConClassi);
		
		/*
		int k = 0;
		int l = 0;
		for (Ticket t: ticketConIV) {
			if (t.getP() != 0 && t.getP() != null) {
				System.out.println(t.getP());
				k++;
			} else {
				l++;
			}
		}
		System.out.println(k);
		System.out.println(l);
*/
		
		/*List <Commit> commit = GetGitInfo.getCommits();
		List<Release> releases = GetReleaseInfo.getReleaseList();
		/*List<String> testingSet;
		List<String> trainingSet;
		int size = GetReleaseInfo.getReleaseList().size();
		
		GetReleaseInfo.dateComparator(releases,commit);
		for(Ticket t : ticket) {
			GetJsonFromUrl.returnAffectedVersion(t, releases);
		}
		ticketConCommit = GetGitInfo.setVersion(ticket,commit,releases);
		Proportion.checkIV(ticketConCommit);
		GetReleaseInfo.classesPerRelease(releases, commit);
		computeBuggy(releases.subList(0, size/2));
		List<Release> halfReleases = releases.subList(0, size/2);
		GetReleaseInfo.assignCommitListToRelease(halfReleases, commit);

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
		csvByWeka(wekaList, releases.subList(0,size/2));*/
	    long end = System.currentTimeMillis();   
	    System.out.println("Tempo di esecuzione: " + Float.valueOf(end-start)/Float.valueOf(60000) + " m");

	}
}