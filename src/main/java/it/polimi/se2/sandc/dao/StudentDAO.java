package it.polimi.se2.sandc.dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.polimi.se2.sandc.bean.Company;
import it.polimi.se2.sandc.bean.Internship;
import it.polimi.se2.sandc.bean.Match;
import it.polimi.se2.sandc.bean.Preferences;
import it.polimi.se2.sandc.bean.Publication;
import it.polimi.se2.sandc.bean.Student;
import it.polimi.se2.sandc.bean.User;

public class StudentDAO {
	private Connection connection;
	
	public StudentDAO(Connection conn) {
		this.connection = conn;
	}
	
	/**
	 * the function add the path to the pdf file of the cv of the given user
	 * @param user user to add the path
	 * @param path path to add
	 * @return
	 * @throws SQLException
	 */
	public boolean putCv(User user, String path) throws SQLException {
		String email = user.getEmail();
		String query = "update Student set cv = ? where email = ?";
		
		if(user.getWhichUser().equals("company")) {
			return false;
		}
		
		try(PreparedStatement statement = connection.prepareStatement(query)){
			statement.setString(1, path);
			statement.setString(2, email);
			
			statement.executeUpdate();
		}
		return true;
	}
	
	public int createPublication(User user) throws SQLException {
		String email = user.getEmail();
		String query = "insert into Publication (student) values (?)";
	
		if(user.getWhichUser().equals("company")) {
			return -1;
		}
		
		try(PreparedStatement statement = connection.prepareStatement(query)){
			statement.setString(1, email);
			
			statement.executeUpdate();
			
		}
		
		query = "select max(id) as max from Publication where student like ?";
		
		try(PreparedStatement statement = connection.prepareStatement(query)){
			statement.setString(1, email);
			try(ResultSet result = statement.executeQuery()){
				result.next();
				return result.getInt("max");
			}
		}
		
		
	}
	
	/**
	 * add a new preferences fot the given user 
	 * @param user student to add the preference
	 * @param idPref the preference to add
	 * @param idPub the publication where to add the preference
	 * @throws SQLException
	 */
	public void addPreference(User user, int idPref, int idPub) throws SQLException {
		String query = "insert into preference (idWorkingPreferences, idPublication) values (?, ?)";
		if(user.getWhichUser().equals("company")) {
			return;
		}
		
		try(PreparedStatement statement = connection.prepareStatement(query)){
			statement.setInt(1, idPref);
			statement.setInt(2, idPub);
			
			statement.executeUpdate();
		}
		
	}
	
	/**
	 * return the info of the given student
	 * @param userType the type of the user
	 * @param email the email of the user
	 * @return the student 
	 * @throws SQLException
	 */
	public Student getProfileInfos(String userType, String email) throws SQLException {
		String query = null;
		Student user = new Student();
		
		query = "SELECT p.id,  w.text \n"
				+ "FROM publication as p join student as s on s.email = p.student JOIN preference as pref on pref.idPublication = p.id JOIN workingpreferences as w on w.id = pref.idWorkingPreferences \n"
				+ "where s.email = ?\n"
				+ "order by p.id asc";
		PreparedStatement pstatement = null;
		ResultSet result2 = null;
		List<Publication> publication = new ArrayList<>();
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setString(1, email);

			result2 = pstatement.executeQuery();
			
			if (!result2.isBeforeFirst()) {// no results
				publication = null;
			}
			else {
				Publication p = null;
				while(result2.next()) {
					if(p == null) {
						p = new Publication();
						p.setId(result2.getInt("id"));
						p.setChoosenPreferences(new ArrayList<Preferences>());
					}
					
					if(p.getId() != result2.getInt("id")) {
						publication.add(p);
						p = new Publication();
						p.setId(result2.getInt("id"));
						p.setChoosenPreferences(new ArrayList<Preferences>());
					}
					
					
					Preferences tmp = new Preferences();
					tmp.setText(result2.getString("text"));
					
					p.getChoosenPreferences().add(tmp);
				}
				publication.add(p);
			}
			
		} catch(SQLException e) {
			throw new SQLException("Error while finding infos");
		}finally {
			try {
				pstatement.close(); 
			} catch (Exception e) {
				throw new SQLException("Error while trying to close prepared statement");
			}
		}
		
		query = "SELECT p.id as idPublication,s.name,s.email,s.address,cv,s.studyCourse,s.phoneNumber,i.id FROM matches as m JOIN internship as i on i.id = m.idInternship JOIN publication as p on  p.id = m.idPublication right join student as s on s.email = p.student WHERE s.email = ? ;";
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setString(1, email);
			result2 = pstatement.executeQuery();
			
			if (!result2.isBeforeFirst()) {// no results
				return null;	
			}
			else { 
					result2.next();					
					user.setName(result2.getString("name"));
					user.setPhoneNumber(result2.getString("phoneNumber"));
					user.setStudyCourse(result2.getString("studyCourse"));
					user.setaddress(result2.getString("address"));
					user.setEmail(result2.getString("email"));
					if(result2.getString("cv") != null) {
						user.setCv(result2.getString("cv"));
					}
					
					user.setPublications(publication);
				return user;	
			}
			
		} catch(SQLException e) {
			throw new SQLException("Error while finding publications");
		}finally {
			try {
				pstatement.close(); 
			} catch (Exception e) {
				throw new SQLException("Error while trying to close prepared statement");
			}
		}
	}
	
	/**
	 * return the list of the publication of the given student
	 * @param email of the student
	 * @return a list of publication
	 * @throws SQLException
	 */
	public List<Publication> findStudentPublications(String email) throws SQLException {
		String query2 = null;
		query2 = "SELECT * from publication WHERE student = ?";
		ResultSet result2 = null;
		PreparedStatement pstatement2 = null;
		List<Publication> publications = new ArrayList<>();
		try {
			pstatement2 = connection.prepareStatement(query2);
			pstatement2.setString(1, email);
			result2 = pstatement2.executeQuery();
			if (!result2.isBeforeFirst()) {// no results, no email found 
				return null;	
			}
			else { //company
				while (result2.next()) {
					Publication pub = new Publication();
					pub.setId(result2.getInt("id"));
					Student student = new Student();
					student.setEmail(result2.getString("student"));
					pub.setStudent(student);
		            
		            publications.add(pub);
		        }	
				
				return publications;	
			}
		} catch(SQLException e) {
			throw new SQLException("Error while trying to find publications student");
		}finally {
			try {
				result2.close(); 
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
	 * return the publication info of the given student with the given id
	 * @param email of the student
	 * @param idP publication to retrieves
	 * @return the publication
	 * @throws SQLException
	 */
	//find THAT specific publication of a student
	public Publication findStudentPublication(String email,Integer idP) throws SQLException {
		String query2 = null;
		query2 = "SELECT * from publication WHERE student = ? and id = ?;";
		ResultSet result2 = null;
		PreparedStatement pstatement2 = null;
		Publication publication = new Publication();
		try {
			pstatement2 = connection.prepareStatement(query2);
			pstatement2.setString(1, email);
			pstatement2.setInt(2, idP);
			result2 = pstatement2.executeQuery();
			if (!result2.isBeforeFirst()) {// no results, no email found 
				return null;	
			}
			else { //company
				result2.next();
				publication.setId(result2.getInt("id"));
				Student student = new Student();
				student.setEmail(result2.getString("student"));
				publication.setStudent(student);
				
				return publication;	
			}
		} catch(SQLException e) {
			throw new SQLException("Error while trying to find student publication");
		}finally {
			try {
				result2.close();
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
	 * return the matches that are waiting for feedback of the given student
	 * @param email the student to retrieve the matches
	 * @return an arraylist of matches
	 * @throws SQLException
	 */
	public ArrayList<Match> getMatchWaitingFeedback(String email) throws SQLException{
		
		ArrayList<Match> ris = new ArrayList<Match>();
		
		String query = "select i.*, c.*, p.*, m.*, s.*\n"
				+ "from (((publication as p inner join matches as m on m.idPublication = p.id )\n"
				+ "inner join internship as i on m.idInternship = i.id) inner join company as c on c.email = i.company) inner join student as s on p.student = s.email\n"
				+ "where m.id in (select idMatch from interview where confirmedYN = 1) and s.email like ? and current_date() > i.endingDate and\n"
				+ "s.email not in (select studentID from feedback where studentYN = 1 and feedback.idMatch = m.id)";
		
		try(PreparedStatement statement = connection.prepareStatement(query)){
			statement.setString(1, email);
			
			try(ResultSet result = statement.executeQuery()){
				if(!result.isBeforeFirst())
					return null;
				while(result.next()) {
					Company c = new Company();
					
					c.setEmail(result.getString("c.email"));
					c.setaddress(result.getString("c.address"));
					c.setName(result.getString("c.name"));
					
					Internship i = new Internship();
					
					i.setCompany(c);
					i.setId(result.getInt("i.id"));
					i.setOpenSeats(result.getInt("i.openSeats"));
					i.setStartingDate(result.getDate("i.startingDate"));
					i.setEndingDate(result.getDate("i.endingDate"));
					i.setjobDescription(result.getString("i.jobDescription"));
					i.setroleToCover(result.getString("i.roleToCover"));
					
					Student s = new Student();
					s.setEmail(result.getString("s.email"));
					s.setName(result.getString("s.name"));
					
					
					Publication p = new Publication();
					
					p.setId(result.getInt("p.id"));
					p.setStudent(s);
					
					Match m = new Match();
					m.setId(result.getInt("m.id"));
					m.setInternship(i);
					m.setPublication(p);
					ris.add(m);
				}
			}
			
		}
		return ris;
	}

	/**
	 * return the publication containing the student information and preferences of the give match
	 * @param idMatch match to retrieve the student
	 * @return the publication
	 * @throws SQLException
	 */
	public Publication getProfileAndPubPreferences(Integer idMatch) throws SQLException {
		Student student = new Student();
		Publication pub = new Publication();
		
		String query = null;
		query = "SELECT * FROM matches as m join publication as p on m.idPublication = p.id join student as s on s.email = p.student WHERE m.id = ?;";
		
		ResultSet result = null;
		PreparedStatement pstatement2 = null;
		try {
			pstatement2 = connection.prepareStatement(query);
			pstatement2.setInt(1, idMatch);
			result = pstatement2.executeQuery();
			if (!result.isBeforeFirst()) {// no results 
				return null;	
			}
			else { 
				result.next();
	            student.setaddress(result.getString("address"));
	            if(result.getString("cv") != null)
	            	student.setCv(result.getString("cv"));
	            student.setEmail(result.getString("email"));
	            student.setName(result.getString("name"));
	            student.setPhoneNumber(result.getString("phoneNumber"));
	            student.setStudyCourse(result.getString("studyCourse"));
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
		pub.setStudent(student);
		//find publication and it's preferences
		query = "SELECT w.text FROM matches as m join publication as p on m.idPublication = p.id left join student as s on s.email = p.student join preference as pr on pr.idPublication = p.id join workingpreferences as w on w.id = pr.idWorkingPreferences WHERE m.id = ?;";
		ArrayList<Preferences> ris = new ArrayList <Preferences>();
		PreparedStatement pstatement = null;
		ResultSet result2 = null;
		
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, idMatch);

			result2 = pstatement.executeQuery();
			
			if (!result2.isBeforeFirst()) {// no results
				return pub; //only student info because he doesn't have preferences
			}
			else { 
				while(result2.next()) {
					Preferences tmp = new Preferences();
					tmp.setText(result2.getString("text"));
					ris.add(tmp);
				}
				
				pub.setChoosenPreferences(ris);
				return pub;
			}
			
		} catch(SQLException e) {
			throw new SQLException("Error while finding infos");
		}finally {
			try {
				pstatement.close(); 
			} catch (Exception e) {
				throw new SQLException("Error while trying to close prepared statement");
			}
		}
	}
}
