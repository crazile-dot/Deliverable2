package main;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;

public class ClassModel extends DiffEntry {
	
	private String name;
	private boolean buggy;
	private List<Ticket> ticketList;
	private RevCommit commit;
	private String developer;
	private Date date;
	private int linesAdded;
	private int linesDeleted;
	private int chg; //numero di file committati insieme a lui in quella release
	private int maxChg; //massimo numero di file committati insieme a lui nelle varie revisioni
	private int sumChg; //somma dei valori totali di chg utile per ottenere la media
	private int recurrence; //quante volte una classe appare in una commit di una release, utile per la media
	private int loc; //numero di righe della classe
	private int locAdded; //numero di righe aggiunte in quella classe, in quella revisione
	private int maxLocAdded; //numero massimo di righe aggiunte a quella classe nelle varie revisioni
	private float avgLocAdded; //numero medio di righe aggiunte a quella classe nelle varie revisioni
	private int authors; //numero di autori che hanno modificato quella classe in quella release
	private int age; //eta della classe in settimane
	private int nFix; //numero di bug fixati in quella classe in quella release
	private int nRevisions; //numero di revisioni che toccano quella classe in quella release
	
	public ClassModel() { }
	
	public ClassModel(String name, boolean buggy, String developer) {
		this.name = name;
		this.buggy = buggy;
		this.developer = developer;
	}
	
	public ClassModel(String name) {
		this.name = name;
		this.maxChg = 0;
		this.sumChg = 0;
		this.recurrence = 1;
	}
	
	public String getName() {
		return this.name;
	}
	
	public boolean getBugginess() {
		return this.buggy;
	}
	
	
	public Date getDate() {
		return this.date;
	}
	
	public int getLinesAdded() {
		return this.linesAdded;
	}
	
	public int getLinesDeleted() {
		return this.linesDeleted;
	}
	
	public List<Ticket> getTicketList() {
		return this.ticketList;
	}
	
	public String getDeveloper() { 
		return this.developer;
	}

	public int getChg() {
		return this.chg;
	}
	
	public int getMaxChg() {
		return this.maxChg;
	}
	
	
	public int getSumChg() {
		return this.sumChg;
	}
	
	
	public int getRecurrence() {
		return this.recurrence;
	}
	
	public int getLoc() {
		return this.loc;
	}
	
	public int getLocAdded() {
		return this.locAdded;
	}
	
	public int getMaxLocAdded() {
		return this.maxLocAdded;
	}
	
	public float getAvgLocAdded() {
		return this.avgLocAdded;
	}
	
	public int getAuthors() {
		return this.authors;
	}
	
	public int getAge() {
		return this.age;
	}
	
	public int getNFix() {
		return this.nFix;
	}
	
	public int getNRevisions() {
		return this.nRevisions;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setBugginess(boolean buggy) {
		this.buggy = buggy;
	}
	
	public void setTicketList(List<Ticket> ticketList) {
		this.ticketList = ticketList;
	}
	
	public void setDeveloper(String developer) {
		this.developer = developer;
	}
	
	public void setLinesAdded(int linesAdded) {
		this.linesAdded = linesAdded;
	}
	
	public void setLinesDeleted(int linesDeleted) {
		this.linesDeleted = linesDeleted;
	}
	
	public void setChg(int chg) {
		this.chg = chg;
	}
	
	
	public void setMaxChg(int maxChg) {
		this.maxChg = maxChg;
	}
	
	
	public void setSumChg(int sumChg) {
		this.sumChg = sumChg;
	}
	
	public void setRecurrence(int recurrence) {
		this.recurrence = recurrence;
	}
	
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public void setLoc(int loc) {
		this.loc = loc;
	}
	
	public void setLocAdded(int locAdded) {
		this.locAdded = locAdded;
	}
	
	public void setMaxLocAdded(int maxLocAdded) {
		this.maxLocAdded = maxLocAdded;
	}
	
	public void setAvgLocAdded(float avgLocAdded) {
		this.avgLocAdded = avgLocAdded;
	}
	
	public void setAuthors(int authors) {
		this.authors = authors;
	}
	
	public void setAge(int age) {
		this.age = age;
	}
	
	public void setNFix(int nFix) {
		this.nFix = nFix;
	}
	
	public void setNRevisions(int nRevisions) {
		this.nRevisions = nRevisions;
	}
	
	public void setSingleTicket(Ticket ticket) {
		if(this.ticketList == null) {
			List<Ticket> list = new ArrayList<>();
			this.ticketList = list;
			this.ticketList.add(ticket);
		} else {
			this.ticketList.add(ticket);
		}
	}
		
}
