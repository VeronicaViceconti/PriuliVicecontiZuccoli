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
import it.polimi.se2.sandc.bean.Student;
import it.polimi.se2.sandc.bean.User;

public class InterviewDAO {
	private Connection connection;


	public InterviewDAO(Connection conn) {
		this.connection = conn;
	}
	
	
	/**
	 * return the question of the given interview
	 * @param idInterview id of the interview 
	 * @return an arrayList of questions
	 * @throws SQLException
	 */
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
	
	/**
	 * update the given question by setting the new answer
	 * @param q the question 
	 * @throws SQLException
	 */
	public void setAnswer(Question q) throws SQLException {
		String query = "update question set answer = ? where id = ?";
		
		try(PreparedStatement statement = connection.prepareStatement(query)){
			statement.setString(1, q.getAnswer());
			statement.setInt(2, q.getId());
			statement.executeUpdate();
		}
	}
	
	/**
	 * the function check if the interview is owned by a match of the given internship 
	 * @param company the company to check
	 * @param idInterview the id of the interview
	 * @return true if the check is correct false otherwise 
	 * @throws SQLException
	 */
	
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
	
	
	/**
	 * the function update the interview to set the confirmYN as given
	 * @param idInterview the id of the interview to update 
	 * @param accept boolean for accept or decline
	 * @throws SQLException
	 */
	public void acceptDeclineInterview(int idInterview, boolean accept) throws SQLException {
		String query = "update interview set confirmedYN = ? where id = ?";
		
		try(PreparedStatement statement = connection.prepareStatement(query)){
			statement.setBoolean(1, accept);
			statement.setInt(2, idInterview);
			
			statement.executeUpdate();
		}
	}

	/**
	 * return the interview with the given id
	 * @param id of the interview
	 * @return the interview
	 * @throws SQLException
	 */
	public Interview selectInterview(Integer id) throws SQLException { //match id, need to find the info about the interview
		String query = "SELECT i.id as intID, dat,f.id as formID,txt,answer FROM matches as m join interview as i on i.idMatch = m.id join form as f on f.id = i.idForm join question as q on q.idForm = f.id where m.id = ?;";
		PreparedStatement pstatement = null;
		ResultSet result2 = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, id);
			result2 = pstatement.executeQuery();
			
			if (!result2.isBeforeFirst()) {// no results, no interview found for that match
				return null;	
			}
			else { 
					result2.next();
					Interview interview = new Interview();
					interview.setId(result2.getInt("intID"));
					Date sqlDate = result2.getDate("dat");
		            if (sqlDate != null) {
		            	interview.setData(sqlDate); 
		            }
		           Form form = new Form();
		           form.setId(result2.getInt("formID"));
		           ArrayList<Question> questions = new ArrayList<>();
		           Question q = new Question();
		           q.setText(result2.getString("txt"));
		           q.setAnswer(result2.getString("answer"));
		           questions.add(q);
		           while(result2.next()) {
		        	   Question q2 = new Question();
		        	   q2.setText(result2.getString("text"));
			           q2.setAnswer(result2.getString("answer"));
			           questions.add(q2);
		           }
		           form.setQuestions(questions);
		           interview.setForm(form);
		           
				return interview;	
			}
			
		} catch(SQLException e) {
			throw new SQLException("Error while finding interview info");
		}finally {
			try {
				pstatement.close(); 
			} catch (Exception e) {
				throw new SQLException("Error while trying to close prepared statement");
			}
		}
		
	}
}
