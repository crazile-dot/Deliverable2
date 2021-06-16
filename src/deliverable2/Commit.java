package deliverable2;

import java.util.Date;
import java.util.List;

public class Commit {

	private int idNumber;
	private String id;
	private Date date;
	private List<String> stringClasses;
	private List<Class> classes;

	public Commit(String id, Date date, List<String> stringClasses) throws Exception {
		    this.id = id; 
		    this.date = date;
		    this.stringClasses = stringClasses;
		  }
	
	Commit() {
		
	}
	
	public int getIdNumber() {
		return this.idNumber;
	}
	
	public String getId() {
		return this.id;
	}
	
	public Date getDate() {
		return this.date;
	}
	
	public List<String> getStringClasses() {
		return this.stringClasses;
	}
	
	public List<Class> getClasses() {
		return this.classes;
	}
	
	public void setIdNumber(int idNumber) {
		this.idNumber = idNumber;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public void setStringClasses(List<String> stringClasses) {
		this.stringClasses = stringClasses;
	}
	
	public void setClasses(List<Class> classes) {
		this.classes = classes;
	}
}
