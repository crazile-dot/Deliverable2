package main;

import java.util.Date;
import java.util.List;

import org.eclipse.jgit.revwalk.RevCommit;

public class Ticket {

	private String name;
	private Date creationDate;
	private Date resolutionDate;
	private String id;
	private RevCommit commit;
	private Integer iV;
	private Integer oV;
	private Integer fV;
	private Integer p;
	private List<Integer> aV;
	
	public Ticket (String name, Date creationDate, Date resolutionDate, String id) {
		this.name = name;
		this.creationDate = creationDate;
		this.resolutionDate = resolutionDate;
		this.id = id;
	}
	
	Ticket() {}
	
	
	public String getName() {
		return this.name;
	}
	
	
	public Date getCreationDate() {
		return this.creationDate;
	}
	
	
	public Date getResolutionDate() {
		return this.resolutionDate;
	}
	
	
	public String getId() {
		return this.id;
	}
	
	
	public RevCommit getCommit() {
		return this.commit;
	}
	
	public Integer getIV() {
		return this.iV;
	}
	
	public Integer getOV() {
		return this.oV;
	}
	
	public Integer getFV() {
		return this.fV;
	}
	
	public Integer getP() {
		return this.p;
	}
	
	public List<Integer> getAV() {
		return this.aV;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	public void setCreationDate(Date cDate) {
		this.creationDate = cDate;
	}
	
	
	public void setResolutionDate(Date rDate ) {
		this.resolutionDate = rDate;
	}
	
	
	public void setId(String id) {
		this.id = id;
	}
	
	
	public void setCommit(RevCommit commit) {
		this.commit = commit;
	}
	
	public void setIV(Integer iV) {
		this.iV = iV;
	}
	
	public void setOV(Integer oV) {
		this.oV = oV;
	}
	
	public void setFV(Integer fV) {
		this.fV = fV;
	}
	
	public void setP(Integer p) {
		this.p = p;
	}
	
	public void setAV(List<Integer> aV) {
		this.aV = aV;
	}
}
