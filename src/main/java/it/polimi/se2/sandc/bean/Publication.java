package it.polimi.se2.sandc.bean;

import java.util.List;

public class Publication{
	private int id; 
	private Student student;
	private List<Preferences> choosenPreferences;
	
	public List<Preferences> getChoosenPreferences() {
		return choosenPreferences;
	}
	public void setChoosenPreferences(List<Preferences> choosenPreferences) {
		this.choosenPreferences = choosenPreferences;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Student getStudent() {
		return student;
	}
	public void setStudent(Student student) {
		this.student = student;
	}
}