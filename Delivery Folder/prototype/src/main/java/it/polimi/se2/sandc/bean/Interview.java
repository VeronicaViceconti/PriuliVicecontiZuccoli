package it.polimi.se2.sandc.bean;

import java.sql.Date;

public class Interview{
	private int id; 
	private Date data; 
	private Match match;
	private Form form; 
	
	public Form getForm() {
		return form;
	}
	public void setForm(Form form) {
		this.form = form;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public Match getMatch() {
		return match;
	}
	public void setMatch(Match match) {
		this.match = match;
	} 
	
	
}