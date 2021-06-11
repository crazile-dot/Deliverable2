package deliverable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Class {
	
	private String name;
	private boolean buggy;
	private List<Ticket> ticketList;
	
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
	
	public List<Ticket> getTicketList() {
		return this.ticketList;
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
}
