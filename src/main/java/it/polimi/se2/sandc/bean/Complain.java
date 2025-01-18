package it.polimi.se2.sandc.bean;

import java.sql.Date;

public class Complain{
	private int id;
	private boolean student;
	private Form form;
	
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