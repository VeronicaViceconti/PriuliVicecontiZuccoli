package it.polimi.se2.sandc.bean;

import java.sql.Date;
import java.util.List;

public class Internship {
	private int id; 
	private int commonId; 
	private Company company; 
	private Date startingDate;
	private Date endingDate;
	private List<CvRequirement> choosenCvRequirements; 
	
	
	public List<CvRequirement> getChoosenCvRequirements() {
		return choosenCvRequirements;
	}
	public void setChoosenCvRequirements(List<CvRequirement> choosenCvRequirements) {
		this.choosenCvRequirements = choosenCvRequirements;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getCommonId() {
		return commonId;
	}
	public void setCommonId(int commonId) {
		this.commonId = commonId;
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
	
	
}