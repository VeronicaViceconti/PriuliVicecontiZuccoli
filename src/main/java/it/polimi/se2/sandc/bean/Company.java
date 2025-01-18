package it.polimi.se2.sandc.bean;

import java.util.List;

public class Company {
	private String email; 
	private String name;
	private List<Publication> publications;
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public List<Publication> getPublications(){
		return publications;
	}
	
	public void setPublications(List<Publication> publications) {
		this.publications = publications;
	}
}