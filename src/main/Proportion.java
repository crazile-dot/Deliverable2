package main;

import java.util.ArrayList;
import java.util.List;

public class Proportion {

	private Proportion() {}

	public static void checkIV(List<Ticket> ticketList) {
		int iV;
		if (ticketList.get(0).getIV() == -1) {
			ticketList.get(0).setIV(1);
		}
		for (int i = 0; i < ticketList.size(); i++) {
			if (ticketList.get(i).getIV() == -1) {
				iV = computeInconsistentIV(i, ticketList);
				ticketList.get(i).setIV(iV);
			} else {
				computeP(ticketList.get(i));
			}	
		}
	}
	
	public static int computeP(Ticket ticket) {
		int top = ticket.getFV() - ticket.getIV();
		int bottom = ticket.getFV() - ticket.getOV();
		if (bottom == 0) {
			bottom = 1;
		}
		int p = top/bottom;
		if (p == 0) {
			p = 1;
		}
		ticket.setP(p);
		return p;
		
	}
	
	public static int computeInconsistentIV(int index, List<Ticket> ticketList) {
		int iV;
		int p;
		List<Integer> pList = new ArrayList<>();
		for (int i = 0; i < index; i++) {
			pList.add(ticketList.get(i).getP());
		}
		p = getP(pList);
		if (p == 0) {
			p = 1;
		}
		ticketList.get(index).setP(p);
		iV = computeIV(ticketList.get(index).getOV(), ticketList.get(index).getFV(), p);
		
		return iV;
	}
	
	public static int getP(List<Integer> l) {
		int size = l.size(); 
		int sum = 0;
		int divide = 0;
		if (!l.isEmpty()) {
			for (int i = size-(size/100); i < size; i++) {
				if(l.get(i) != null)  {
					sum += l.get(i);
				}
				divide++;
			}
		} else {
			sum = 1;
		}
		if (divide == 0) {
			divide = 1;
		}
		return sum/divide;
	}
	
	public static int computeIV(int oV, int fV, int p) {
		int iV = fV - ((fV - oV)*p);
		if(iV < 1) {
			iV = 1;
		}
		return iV;
	}
	
	public static void computeAVs(List<Ticket> ticketList) {
		for (Ticket t: ticketList) {
			List<Integer> aVs = new ArrayList<>();
			for (int i = t.getIV(); i < t.getFV(); i++) {
				aVs.add(i);
			}
			t.setAV(aVs);
		}
	}

}
