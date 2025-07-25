package it.polimi.se2.sandc.bean;

import java.sql.Date;
import java.util.List;

public class Internship {
	private int id; 
	private int openSeats; 
	private Company company; 
	private Student student;
	private String roleToCover;
	private Date startingDate;
	private Date endingDate;
	private String jobDescription;
	private List<Preferences> preferences;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public void setStudent(Student student) {
		this.student = student;
	}
	
	public Student getStudent() {
		return student;
	}
	
	public void setPreferences(List<Preferences> preferences) {
		this.preferences = preferences;
	}
	public List<Preferences> getPreferences() {
		return preferences;
	}
	
	public String getroleToCover() {
		return roleToCover;
	}
	public void setroleToCover(String roleToCover) {
		this.roleToCover = roleToCover;
	}
	
	public int getOpenSeats() {
		return openSeats;
	}
	public void setOpenSeats(int openSeats) {
		this.openSeats = openSeats;
	}
	public Company getCompany() {
		return company;
	}
	public void setCompany(Company company) {
		this.company = company;
	}
	public Date getStartingDate() {
		return startingDate;
	}
	public void setStartingDate(Date startingDate) {
		this.startingDate = startingDate;
	}
	public Date getEndingDate() {
		return endingDate;
	}
	public void setEndingDate(Date endingDate) {
		this.endingDate = endingDate;
	}
	public String getjobDescription() {
		return jobDescription;
	}
	public void setjobDescription(String jobDescription) {
		this.jobDescription = jobDescription;
	}
	
	
}