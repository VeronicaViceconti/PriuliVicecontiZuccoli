package it.polimi.se2.sandc.bean;

import java.sql.Date;

public class Feedback{
	private int id;
	private boolean student;
	private Student student1;
	private Form form;
	
	public Student getStudent() {
		return student1;
	}
	public void setStudent(Student student) {
		student1 = student;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public boolean isStudent() {
		return student;
	}
	public void setStudent(boolean student) {
		this.student = student;
	}
	public Form getForm() {
		return form;
	}
	public void setForm(Form form) {
		this.form = form;
	}
	
	
}