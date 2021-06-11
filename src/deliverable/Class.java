package deliverable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Class {
	
	private String name;
	private boolean buggy;
	private List<Ticket> ticketList;
	private Date date;
	private int chg; //numero di file committati insieme a lui
	private int maxChg; //massimo numero di file committati insieme a lui nella release
	private int sumChg; //somma dei valori totali di chg utile per ottenere la media
	private int recurrence; //quante volte una classe appare in una commit di una release, utile per la media
	
	public Class() {
		
	}
	
	public Class(String name) {
		this.name = name;
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
	
	
	public List<Ticket> getTicketList() {
		return this.ticketList;
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
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setBugginess(boolean buggy) {
		this.buggy = buggy;
	}
	
	public void setTicketList(List<Ticket> ticketList) {
		this.ticketList = ticketList;
	}
	
	public void setSingleTicket(Ticket ticket) {
		if(this.ticketList == null) {
			List<Ticket> ticketList = new ArrayList();
			this.ticketList = ticketList;
			this.ticketList.add(ticket);
		}
		else {
		this.ticketList.add(ticket);
		}
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
	
}
