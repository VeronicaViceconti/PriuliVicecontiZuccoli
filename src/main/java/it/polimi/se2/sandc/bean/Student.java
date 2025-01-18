package it.polimi.se2.sandc.bean;

import java.util.List;

public class Student{
	private String email;
	private String cv; 
	private String name;
	private List<Publication> publications;
	
	public List<Publication> getPublications(){
		return publications;
	}
	
	public void setPublications(List<Publication> publications) {
		this.publications = publications;
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getCv() {
		return cv;
	}
	public void setCv(String cv) {
		this.cv = cv;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}