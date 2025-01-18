package it.polimi.se2.sandc.bean;

import java.util.List;

public class Form{
	private int id;
	private List<Question> questions;
	
	public List<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	} 
	
}