package main;

import java.util.Date;
import java.util.List;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.revwalk.RevCommit;

public class Release {
	
	private String id;
	private String name;
	private Date date;
	private Integer number;
	private RevCommit commit;
	private List<RevCommit> rCommitList;
	private List<DiffEntry> releaseDiffs;
	private List<DiffEntry> diffsWithDuplicated;
	private List<ClassModel> parsedReleaseDiffs;
	private List<ClassModel> parsedDiffsWithDuplicated;
	private List<ClassModel> releaseClasses;
	private List<ClassModel> classesWithDuplicated;
	private Integer numOfBuggyClass = 0;
	
	
	public Release(String id, String name, Date date, int number) {
		this.id = id;
		this.name = name;
		this.date = date;
		this.number = number;
	}
	
	public String getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Date getDate() {
		return this.date;
	}
	
	public Integer getNumber() {
		return this.number;
	}

	public RevCommit getCommit() {
		return this.commit;
	}
	
	public List<RevCommit> getRCommitList() {
		return this.rCommitList;
	}
	
	public List<DiffEntry> getReleaseDiffs() {
		return this.releaseDiffs;
	}
	
	public List<DiffEntry> getDiffsWithDuplicated() {
		return this.diffsWithDuplicated;
	}
	
	public List<ClassModel> getParsedReleaseDiffs() {
		return this.parsedReleaseDiffs;
	}
	
	public List<ClassModel> getParsedDiffsWithDuplicated() {
		return this.parsedDiffsWithDuplicated;
	}
	
	public List<ClassModel> getReleaseClasses() {
		return this.releaseClasses;
	}
	
	public List<ClassModel> getClassesWithDuplicated() {
		return this.classesWithDuplicated;
	}
	
	public Integer getNumOfBuggyClass() {
		return this.numOfBuggyClass;
	}
	
	public void setNumOfBuggyClass(Integer numOfBuggyClass) {
		this.numOfBuggyClass = numOfBuggyClass;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public void setNumber(Integer number) {
		this.number = number;
	}
	
	public void setCommit(RevCommit commit) {
		this.commit = commit;
	}
	
	public void setRCommitList(List<RevCommit> rCommitList) {
		this.rCommitList = rCommitList;
	}
	
	public void setReleaseDiffs(List<DiffEntry> releaseDiffs) {
		this.releaseDiffs = releaseDiffs;
	}
	
	public void setDiffsWithDuplicated(List<DiffEntry> diffsWithDuplicated) {
		this.diffsWithDuplicated = diffsWithDuplicated;
	}
	
	public void setParsedReleaseDiffs(List<ClassModel> parsedReleaseDiffs) {
		this.parsedReleaseDiffs = parsedReleaseDiffs;
	}
	
	public void setParsedDiffsWithDuplicated(List<ClassModel> parsedDiffsWithDuplicated) {
		this.parsedDiffsWithDuplicated = parsedDiffsWithDuplicated;
	}
	
	public void setClassesWithDuplicated(List<ClassModel> classesWithDuplicated) {
		this.classesWithDuplicated = classesWithDuplicated;
	}
	
	public void setReleaseClasses(List<ClassModel> releaseClasses) {
		this.releaseClasses = releaseClasses;
	}
	
	
}
