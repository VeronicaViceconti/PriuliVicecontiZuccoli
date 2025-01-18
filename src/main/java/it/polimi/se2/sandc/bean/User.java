package it.polimi.se2.sandc.bean;

public class User {
	private String email;
	private String whichUser; 
	private String name;
	
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
	
	public String getWhichUser() {
		return whichUser;
	}
	public void setWhichUser(String whichUser) {
		this.whichUser = whichUser;
	}
	
}
