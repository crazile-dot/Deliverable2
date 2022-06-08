package main;

import java.util.Date;
import java.util.List;

public class Commit {

	private int idNumber;
	private String id;
	private Date date;
	private String message;
	private String author;
	private List<String> stringClasses;
	private List<ClassModel> classes;

	public Commit(String id, Date date, List<String> stringClasses) {
	    this.id = id; 
	    this.date = date;
	    this.stringClasses = stringClasses;
	}
	
	public Commit(String id, Date date, String message, String author) {
	    this.id = id; 
	    this.date = date;
	    this.message = message;
	    this.author = author;
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
	
	public List<ClassModel> getClasses() {
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
	
	public void setClasses(List<ClassModel> classes) {
		this.classes = classes;
	}
}
