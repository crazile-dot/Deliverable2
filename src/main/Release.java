package main;

import java.util.Date;
import java.util.List;

public class Release {
	
	private String id;
	private String name;
	private Date date;
	private Integer number;
	private Commit commit;
	private List<Commit> rCommitList;
	private List<ClassModel> releaseClasses;
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

	public Commit getCommit() {
		return this.commit;
	}
	
	public List<Commit> getRCommitList() {
		return this.rCommitList;
	}
	
	public List<ClassModel> getReleaseClasses() {
		return this.releaseClasses;
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
	
	public void setCommit(Commit commit) {
		this.commit = commit;
	}
	
	public void setRCommitList(List<Commit> rCommitList) {
		this.rCommitList = rCommitList;
	}
	
	public void setReleaseClasses(List<ClassModel> releaseClasses) {
		this.releaseClasses = releaseClasses;
	}
}
