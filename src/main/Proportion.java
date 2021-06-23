package main;

import java.util.ArrayList;
import java.util.List;

public class Proportion {
	
	public static List<Ticket> checkIV(List<Ticket> ticketList) {
		int p;
		int iV;
		List<Ticket> ret = new ArrayList<>();
		for (int i = 0; i < ticketList.size(); i++) {
			//System.out.println(ticketList.get(i).getId());
			if (ticketList.get(i).getCommit() != null && ticketList.get(i).getFV() != null && ticketList.get(i).getOV() != null && ticketList.get(i).getFV() != ticketList.get(i).getIV()) {
				//se IV � null viene settato a -1
				if (ticketList.get(i).getIV() == -1) {
					iV = computeInconsistentIV(i, ticketList);
					ticketList.get(i).setIV(iV);
					//p = ticketList.get(i).getP();
				} else {
					p = computeP(ticketList.get(i));
				}
				if (ticketList.get(i).getFV() != ticketList.get(i).getIV()) {
					ret.add(ticketList.get(i));
					//System.out.println("Proportion: " + p);
				}
				
			}
			
		}
		return ret;
	}
	
	public static int computeP(Ticket ticket) {
		int top = ticket.getFV() - ticket.getIV();
		//System.out.println("numeratore di p: " + top);
		//System.out.println("ticket: " + ticket.getId() + "ticket FV: " + ticket.getFV() + "ticket IV: " + ticket.getIV()+ "Ticket OV: " +ticket.getOV());
		int bottom = ticket.getFV() - ticket.getOV();
		if (bottom == 0) {
			bottom = 1;
		}
		int p = top/bottom;
		if (p == 0) {
			p = 1;
		}
		//System.out.println("FV: " + ticket.getFV() + " OV: " + ticket.getOV() + " IV: " + ticket.getIV() + " P: " + p);
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
					//System.out.println(l.get(i));
					sum += l.get(i);
				}
				divide++;
			}
		}
		if (divide == 0) {
			divide = 1;
		}
		return sum/divide;
	}
	
	public static int computeIV(int OV, int FV, int p) {
		int IV = FV - ((FV - OV)*p);
		if(IV < 1) {
			IV = 1;
		}
		//System.out.println("FV: " + FV + " OV: " + OV + " IV: " + IV + " P: " + p);
		return IV;
	}
	
	/*public static void main(String[] args) {
		List<Integer> array = new ArrayList<Integer>();
		for(int i = 0; i < 2000; i++) {
			array.add(i);
		}
		System.out.println(getP(array));
	}*/
}
