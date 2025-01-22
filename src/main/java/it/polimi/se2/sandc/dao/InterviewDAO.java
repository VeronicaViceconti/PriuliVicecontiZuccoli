package it.polimi.se2.sandc.dao;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.sql.Date;

import java.util.List;

import it.polimi.se2.sandc.bean.Company;
import it.polimi.se2.sandc.bean.Form;
import it.polimi.se2.sandc.bean.Internship;
import it.polimi.se2.sandc.bean.Interview;
import it.polimi.se2.sandc.bean.Match;
import it.polimi.se2.sandc.bean.Publication;
import it.polimi.se2.sandc.bean.Question;
import it.polimi.se2.sandc.bean.User;

public class InterviewDAO {
	private Connection connection;


	public InterviewDAO(Connection conn) {
		this.connection = conn;
	}
	
	public ArrayList<Question> getQuestions(int idInterview) throws SQLException{
		ArrayList<Question> list = new ArrayList<Question>();
		
		String query = "SELECT q.* \r\n"
				+ "FROM interview as i inner join question as q on i.idForm = q.idForm\r\n"
				+ "where i.id = ?";
		
		try(PreparedStatement statement = connection.prepareStatement(query)){
			
			statement.setInt(1, idInterview);
			
			try(ResultSet result = statement.executeQuery()){
				while(result.next()) {
					Question q = new Question();
					
					q.setId(result.getInt("id"));
					q.setText(result.getString("txt"));
					q.setAnswer(result.getString("answer"));
					
					list.add(q);
				}
			}
		}
		
		return list;
	}
	
	public void setAnswer(Question q) throws SQLException {
		String query = "update question set answer = ? where id = ?";
		
		try(PreparedStatement statement = connection.prepareStatement(query)){
			statement.setString(1, q.getAnswer());
			statement.setInt(2, q.getId());
			statement.executeUpdate();
		}
	}
	
	public boolean checkOwnerShip(String company, int idInterview) throws SQLException {
		
		String query = "select *\r\n"
				+ "from (interview as i inner join matches as m on i.idMatch = m.id) inner join internship on m.idInternship = internship.id  \r\n"
				+ "where internship.company like ? and i.id = ?";
		try(PreparedStatement statement = connection.prepareStatement(query)){
			statement.setString(1, company);
			statement.setInt(2, idInterview);
			
			try(ResultSet result = statement.executeQuery()){
				return result.isBeforeFirst();
			}
		}
	}
	
	public void acceptDeclineInterview(int idInterview, boolean accept) throws SQLException {
		String query = "update interview set confirmedYN = ? where id = ?";
		
		try(PreparedStatement statement = connection.prepareStatement(query)){
			statement.setBoolean(1, accept);
			statement.setInt(2, idInterview);
			
			statement.executeUpdate();
		}
	}
}
