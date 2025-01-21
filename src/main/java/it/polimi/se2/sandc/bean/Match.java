package it.polimi.se2.sandc.bean;

public class Match{
	private int id; 
	private Boolean acceptedYNStudent; 
	private Boolean acceptedYNCompany; 
	private Publication publication; 
	private Internship internship;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Boolean isAcceptedStudent() {
		return acceptedYNStudent;
	}
	public void setAcceptedStudent(Boolean accepted) {
		this.acceptedYNStudent = accepted;
	}
	public Boolean isAcceptedCompany() {
		return acceptedYNCompany;
	}
	public void setAcceptedCompany(Boolean accepted) {
		this.acceptedYNCompany = accepted;
	}
	public Publication getPublication() {
		return publication;
	}
	public void setPublication(Publication publication) {
		this.publication = publication;
	}
	public Internship getInternship() {
		return internship;
	}
	public void setInternship(Internship internship) {
		this.internship = internship;
	} 
	
	
}