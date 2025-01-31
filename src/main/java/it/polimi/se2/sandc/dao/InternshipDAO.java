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
import it.polimi.se2.sandc.bean.Internship;
import it.polimi.se2.sandc.bean.Match;
import it.polimi.se2.sandc.bean.Publication;
import it.polimi.se2.sandc.bean.Student;
import it.polimi.se2.sandc.bean.User;

public class InternshipDAO {
	private Connection connection;


	public InternshipDAO(Connection conn) {
		this.connection = conn;
	}
	
	
	/**
	 * insert in the database a complaint with the given data
	 * @param user who write the complaints
	 * @param student the student in the match who refer the complaint
	 * @param company the email of the company who refer the complaint 
	 * @param answer the text of the complaint
	 * @param idMatch the referred match
	 * @throws SQLException
	 */
	public void writeComplaint(User user, String student, String company, String answer, int idMatch) throws SQLException {
		String query;
		
		//create a new form
		query = "insert into Form values ()";
		int idForm = -1;
		try(PreparedStatement statement = connection.prepareStatement(query,Statement.RETURN_GENERATED_KEYS)){
			statement.executeUpdate();
			try(ResultSet ris = statement.getGeneratedKeys()){
				if(ris.next()) {
					idForm = ris.getInt(1);
				}
			}
		}
		if(idForm == -1) {
			throw new SQLException();
		}
		
		//insert the response of the question in the db, referred to the id
		query = "insert into question (txt, answer, idForm) values ('complaint form',?,?)";
		try(PreparedStatement statement = connection.prepareStatement(query)){
			statement.setString(1, answer);
			statement.setInt(2, idForm);
			
			statement.executeUpdate();
		}
		
		//insert the complaint in the table
		query = "insert into complaint (studentYn, idForm, studentID, companyID, idMatch) values (?,?,?,?,?)";
		
		try(PreparedStatement statement = connection.prepareStatement(query)){
			statement.setBoolean(1, user.getWhichUser().equals("student"));
			statement.setInt(2, idForm);
			statement.setString(3, student);
			statement.setString(4, company);
			statement.setInt(5, idMatch);
			statement.executeUpdate();
		}
	}
	
	/**
	 * return the match relative to the on going internship for the given student
	 * @param email the email of the student
	 * @return the match or null if there isn't 
	 * @throws SQLException
	 */
	public Match getOngoingInternship(String email) throws SQLException {
		String query = null;
			query = "SELECT m.id, inter.id as idInter,c.name,c.address,inter.startingDate,inter.endingDate,roleToCover FROM interview as i join matches as m on i.idMatch = m.id join publication as p on p.id = m.idPublication join student as s on s.email = p.student join internship as inter on inter.id = m.idInternship join company as c on c.email = inter.company where s.email = ? and endingDate > curdate();";
		
		ResultSet result = null;
		PreparedStatement pstatement2 = null;
		Internship internship = new Internship();
		try {
			pstatement2 = connection.prepareStatement(query);
			pstatement2.setString(1, email);
			result = pstatement2.executeQuery();
			if (!result.isBeforeFirst()) {// no results, no email found 
				return null;	
			}
			else { //company
				result.next();
				internship.setId(result.getInt("idInter"));
				Company company = new Company();
	            company.setName(result.getString("name"));
	            company.setaddress(result.getString("address"));
	            internship.setCompany(company);
	            
	            Date sqlDate = result.getDate("startingDate");
	            if (sqlDate != null) {
	                internship.setStartingDate(new Date(sqlDate.getTime())); 
	            }
	            sqlDate = result.getDate("endingDate");
	            if (sqlDate != null) {
	                internship.setEndingDate(new Date(sqlDate.getTime())); 
	            }
	            internship.setroleToCover(result.getString("roleToCover"));
				
	            Match m = new Match();
	            m.setId(result.getInt("m.id"));
	            m.setInternship(internship);
	            return m;
			}
		} catch(SQLException e) {
			throw new SQLException("Error while trying to find student publication");
		}finally {
			try {
				result.close(); 
			} catch(Exception e) {
				throw new SQLException("Error while trying to close Result Set");
			}
			try {
				pstatement2.close();  
			} catch(Exception e) {
				throw new SQLException("Error while trying to close prepared statement");
			}
		}
			
	}
	
	/**
	 * return the matches relative to the on going internships for the given company
	 * @param email the email of the company
	 * @return a list of match
	 * @throws SQLException
	 */
		public List<Match> getOngoingInternships(String email) throws SQLException {
			String query = null;
			query = "SELECT inter.id as idInter,c.address,c.name companyName,s.name studentName,s.studyCourse,s.email studentEmail,openSeats,startingDate,endingDate,roleToCover,m.id FROM interview as i join matches as m on i.idMatch = m.id join publication as p on p.id = m.idPublication join student as s on s.email = p.student join internship as inter on inter.id = m.idInternship join company as c on c.email = inter.company where c.email = ? AND confirmedYN is true and current_date() < endingDate;";			
			ResultSet result = null;
			PreparedStatement pstatement2 = null;
			
			List<Match> matches = new ArrayList<>();
			try {
				pstatement2 = connection.prepareStatement(query);
				pstatement2.setString(1, email);
				result = pstatement2.executeQuery();
				if (!result.isBeforeFirst()) {// no results, no ongoing internships 
					return null;	
				}
				else { //company
					while(result.next()) {
						Match match = new Match();
						match.setId(result.getInt("id"));
						
						Internship internship = new Internship();
						internship.setId(result.getInt("idInter"));
						Company company = new Company();
			            company.setName(result.getString("companyName"));
			            company.setaddress(result.getString("address"));
			            Student student = new Student();
			            student.setStudyCourse(result.getString("studyCourse"));
			            student.setName(result.getString("studentName"));
			            internship.setCompany(company);
			            internship.setStudent(student);
			            internship.setOpenSeats(result.getInt("openSeats"));
			            
			            Date sqlDate = result.getDate("startingDate");
			            if (sqlDate != null) {
			                internship.setStartingDate(new Date(sqlDate.getTime())); 
			            }
			            sqlDate = result.getDate("endingDate");
			            if (sqlDate != null) {
			                internship.setEndingDate(new Date(sqlDate.getTime())); 
			            }
			            internship.setroleToCover(result.getString("roleToCover"));
			            
			            match.setInternship(internship);
			            matches.add(match);
					}
		            return matches;
				}
			} catch(SQLException e) {
				throw new SQLException("Error while trying to find student publication");
			}finally {
				try {
					result.close(); 
				} catch(Exception e) {
					throw new SQLException("Error while trying to close Result Set");
				}
				try {
					pstatement2.close();  
				} catch(Exception e) {
					throw new SQLException("Error while trying to close prepared statement");
				}
			}
	} 
	
		
	/**
	 * 
	 * insert in the database a feedback with the given data
	 * @param user who write the feedback
	 * @param student the student in the match who refer the feedback
	 * @param company the email of the company who refer the feedback 
	 * @param answer the text of the feedback
	 * @param idMatch the referred match
	 * @throws SQLException
	 */
	public void writeFeedback(User user, String student, String company, String answer, int idMatch) throws SQLException {
		String query;
		
		//create the new form in the db
		query = "insert into Form values ()";
		int idForm = -1;
		try(PreparedStatement statement = connection.prepareStatement(query,Statement.RETURN_GENERATED_KEYS)){
			statement.executeUpdate();
			try(ResultSet ris = statement.getGeneratedKeys()){
				if(ris.next()) {
					idForm = ris.getInt(1);
				}
			}
		}
		if(idForm == -1) {
			throw new SQLException();
		}
		
		//insert the answer of the question
		query = "insert into question (txt, answer, idForm) values ('feedback form',?,?)";
		try(PreparedStatement statement = connection.prepareStatement(query)){
			statement.setString(1, answer);
			statement.setInt(2, idForm);
			
			statement.executeUpdate();
		}
		
		
		//insert the feedback in the db
		query = "insert into feedback (studentYn, idForm, studentID, companyId, idMatch) values (?,?,?,?,?)";
		
		try(PreparedStatement statement = connection.prepareStatement(query)){
			statement.setBoolean(1, user.getWhichUser().equals("student"));
			statement.setInt(2, idForm);
			statement.setString(3, student);
			statement.setString(4, company);
			statement.setInt(5, idMatch);
			statement.executeUpdate();
		}
	}

	/**
	 * return all the internship of the given company
	 * @param email the email of the company
	 * @return a list of internship
	 * @throws SQLException
	 */
	public List<Internship> getAllICompanyInternships(String email) throws SQLException {
		String query = null;
		query = "SELECT * FROM internship AS i JOIN company ON i.company = company.email WHERE company = ?;";
		PreparedStatement pstatement = null;
		ResultSet result = null;
		List<Internship> intern = new ArrayList<>();
		
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setString(1, email);

			result = pstatement.executeQuery();
			
			if (!result.isBeforeFirst()) {// no results,he doesn't have internships
				return null;	
			}
			else {
				while(result.next()) {
					Internship internship = new Internship();
		            internship.setId(result.getInt("id"));
		            internship.setOpenSeats(result.getInt("openSeats"));
		            Company company = new Company();
		            company.setName(result.getString("name"));
		            company.setaddress(result.getString("address"));
		            internship.setCompany(company);
		            
		            Date sqlDate = result.getDate("startingDate");
		            if (sqlDate != null) {
		                internship.setStartingDate(new Date(sqlDate.getTime())); 
		            }
		            sqlDate = result.getDate("endingDate");
		            if (sqlDate != null) {
		                internship.setEndingDate(new Date(sqlDate.getTime())); 
		            }
		            internship.setroleToCover(result.getString("roleToCover"));
		            intern.add(internship);
				}
			}
			
		} catch(SQLException e) {
			throw new SQLException("Error while getting company internships");
		}finally {
			try {
				pstatement.close();  
			} catch(Exception e) {
				throw new SQLException("Error while trying to close prepared statement");
			}
		}
		return intern;
	}

	/**
	 * return the list of the ongoing internship of the given company with the given student name
	 * @param email of the company
	 * @param nameToSearch name of the student to search
	 * @return a list of match
	 * @throws SQLException
	 */
	public List<Match> getFilteredOngoingInternships(String email, String nameToSearch) throws SQLException {
		
		String query = null;
		query = "SELECT inter.id as idInter,c.address,c.name companyName,s.name studentName,s.studyCourse,s.email studentEmail,startingDate,endingDate,roleToCover,m.id FROM interview as i join matches as m on i.idMatch = m.id join publication as p on p.id = m.idPublication join student as s on s.email = p.student join internship as inter on inter.id = m.idInternship join company as c on c.email = inter.company where c.email = ? AND confirmedYN is true and current_date() < endingDate and s.name = ?;";			
		ResultSet result = null;
		PreparedStatement pstatement2 = null;
		
		List<Match> matches = new ArrayList<>();
		try {
			pstatement2 = connection.prepareStatement(query);
			pstatement2.setString(1, email);
			pstatement2.setString(2,nameToSearch);
			result = pstatement2.executeQuery();
			if (!result.isBeforeFirst()) {// no results, no ongoing internships 
				return null;	
			}
			else { //company
				while(result.next()) {
					Match match = new Match();
					match.setId(result.getInt("id"));
					
					Internship internship = new Internship();
					internship.setId(result.getInt("idInter"));
					Company company = new Company();
		            company.setName(result.getString("companyName"));
		            company.setaddress(result.getString("address"));
		            Student student = new Student();
		            student.setStudyCourse(result.getString("studyCourse"));
		            student.setName(result.getString("studentName"));
		            internship.setCompany(company);
		            internship.setStudent(student);
		            
		            Date sqlDate = result.getDate("startingDate");
		            if (sqlDate != null) {
		                internship.setStartingDate(new Date(sqlDate.getTime())); 
		            }
		            sqlDate = result.getDate("endingDate");
		            if (sqlDate != null) {
		                internship.setEndingDate(new Date(sqlDate.getTime())); 
		            }
		            internship.setroleToCover(result.getString("roleToCover"));
		            
		            match.setInternship(internship);
		            matches.add(match);
				}
	            return matches;
			}
		} catch(SQLException e) {
			throw new SQLException("Error while trying to find student publication");
		}finally {
			try {
				result.close(); 
			} catch(Exception e) {
				throw new SQLException("Error while trying to close Result Set");
			}
			try {
				pstatement2.close(); 
			} catch(Exception e) {
				throw new SQLException("Error while trying to close prepared statement");
			}
		}
		
	}
	
}
