package main;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class TempMetrics {
	
	
	public static int numberOfBugFixedForRelease(Release release, Class c) {
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
	public static long classAge(Class c) {
		ZoneId defaultZoneId = ZoneId.systemDefault();
	    LocalDate localDate = LocalDate.now();
	    Date date = Date.from(localDate.atStartOfDay(defaultZoneId).toInstant());
		long difference_In_Week = ((date.getTime() - c.getDate().getTime())/ (1000 * 60 * 60 * 24 * 7));
		return difference_In_Week;	
	}
	
	
	public static int getAVGChg(Class c) {
		int rec = c.getRecurrence();
		if(rec == 0) {
			rec = 1;
		}
		return c.getSumChg()/rec;
	}

}
