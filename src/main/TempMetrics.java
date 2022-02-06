package main;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class TempMetrics {

	private TempMetrics() {}
	
	public static int numberOfBugFixedForRelease(Release release, ClassModel c) {
		int counter = 0;
		if(c.getTicketList()!= null) {
			for(Ticket t : c.getTicketList()) {
				if(t.getFV() == release.getNumber()) {
					counter = counter + 1;
				}
			}
		}
		return counter;
	}
	
	
	//Da fare per le classi
	public static long classAge(ClassModel c) {
		ZoneId defaultZoneId = ZoneId.systemDefault();
	    LocalDate localDate = LocalDate.now();
	    Date date = Date.from(localDate.atStartOfDay(defaultZoneId).toInstant());
		return ((date.getTime() - c.getDate().getTime())/ (1000 * 60 * 60 * 24 * 7));
	}
	
	
	public static int getAVGChg(ClassModel c) {
		int rec = c.getRecurrence();
		if(rec == 0) {
			rec = 1;
		}
		return c.getSumChg()/rec;
	}

}
