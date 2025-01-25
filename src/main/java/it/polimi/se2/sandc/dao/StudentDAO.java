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
import it.polimi.se2.sandc.bean.Preferences;
import it.polimi.se2.sandc.bean.Publication;
import it.polimi.se2.sandc.bean.Student;
import it.polimi.se2.sandc.bean.User;

public class StudentDAO {
	private Connection connection;
	
	public StudentDAO(Connection conn) {
		this.connection = conn;
	}
	
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
	
	public Student getProfileInfos(String userType, String email) throws SQLException {
		String query = null; 
		  Student user = new Student(); 
		   
		  query = "SELECT w.text FROM \n"
		  		+ "publication as p join student as s on s.email = p.student JOIN preference as pref on pref.idPublication = p.id JOIN workingpreferences as w on w.id = pref.idWorkingPreferences where s.email = ?";
		  ArrayList<Preferences> ris = new ArrayList <Preferences>(); 
		  PreparedStatement pstatement = null; 
		  ResultSet result2 = null; 
		   
		  try { 
		   pstatement = connection.prepareStatement(query); 
		   pstatement.setString(1, email); 
		 
		   result2 = pstatement.executeQuery(); 
		    
		   if (!result2.isBeforeFirst()) {// no results, no matches found
		   } 
		   else {  
		    while(result2.next()) { 
		     Preferences tmp = new Preferences(); 
		     tmp.setText(result2.getString("text")); 
		     ris.add(tmp); 
		     System.out.println("preference ->"+tmp.getText());
		    } 
		     
		   } 
		    
		  } catch(SQLException e) { 
		   throw new SQLException("Error while finding match"); 
		  }finally { 
		   try { 
		    pstatement.close(); // devo chiudere prepared statement 
		   } catch (Exception e) { 
		    throw new SQLException("Error while trying to close prepared statement"); 
		   } 
		  } 
		   
		  query = "SELECT p.id as idPublication,s.name,s.email,s.address,cv,s.studyCourse,s.phoneNumber,i.id FROM matches as m JOIN internship as i on i.id = m.idInternship JOIN publication as p on  p.id = m.idPublication join student as s on s.email = p.student WHERE p.student = ? ;"; 
		  try { 
		   pstatement = connection.prepareStatement(query); 
		   pstatement.setString(1, email); 
		   result2 = pstatement.executeQuery(); 
		    
		   if (!result2.isBeforeFirst()) {// no results, no matches found 
		    return null;  
		   } 
		   else {  
		     result2.next(); 
		     Publication pub = new Publication(); 
		     pub.setId(result2.getInt("idPublication")); 
		      
		     user.setName(result2.getString("name")); 
		     user.setPhoneNumber(result2.getString("phoneNumber")); 
		     user.setStudyCourse(result2.getString("studyCourse")); 
		     user.setaddress(result2.getString("address")); 
		     user.setEmail(result2.getString("email")); 
		     if(result2.getString("cv") != null) 
		      user.setCv(result2.getString("cv")); 
		     List<Publication> publication = new ArrayList<>(); 
		     pub.setChoosenPreferences(ris); 
		     publication.add(pub); 
		     user.setPublications(publication); 
		    return user;  
		   } 
		    
		  } catch(SQLException e) { 
		   throw new SQLException("Error while finding match"); 
		  }finally { 
		   try { 
		    pstatement.close(); // devo chiudere prepared statement 
		   } catch (Exception e) { 
		    throw new SQLException("Error while trying to close prepared statement"); 
		   } 
		  }
	}
	
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
				result2.close(); //Devo chiudere result set
			} catch(Exception e) {
				throw new SQLException("Error while trying to close Result Set");
			}
			try {
				pstatement2.close();  //devo chiudere prepared statement
			} catch(Exception e) {
				throw new SQLException("Error while trying to close prepared statement");
			}
		}
	}
	
	//find THAT specific publication of a student
	public Publication findStudentPublication(String email,int idWP) throws SQLException {
		String query2 = null;
		query2 = "SELECT * from publication JOIN preference ON publication.id = preference.idPublication WHERE student = ? and idWorkingPreferences = ?;";
		ResultSet result2 = null;
		PreparedStatement pstatement2 = null;
		Publication publication = new Publication();
		try {
			pstatement2 = connection.prepareStatement(query2);
			pstatement2.setString(1, email);
			pstatement2.setInt(2, idWP);
			result2 = pstatement2.executeQuery();
			if (!result2.isBeforeFirst()) {// no results, no email found 
				return null;	
			}
			else { //company
				result2.next();
				publication.setId(result2.getInt("id"));
				Student student = new Student();
				student.setName(result2.getString("name"));
				publication.setStudent(student);
				
				return publication;	
			}
		} catch(SQLException e) {
			throw new SQLException("Error while trying to find student publication");
		}finally {
			try {
				result2.close(); //Devo chiudere result set
			} catch(Exception e) {
				throw new SQLException("Error while trying to close Result Set");
			}
			try {
				pstatement2.close();  //devo chiudere prepared statement
			} catch(Exception e) {
				throw new SQLException("Error while trying to close prepared statement");
			}
		}
	}
	
	public ArrayList<Internship> getOnGoingInternship(String email) throws SQLException{
		
		ArrayList<Internship> ris = new ArrayList<Internship>();
		
		String query = "select i.*, c.*\n"
				+ "from ((publication as p inner join matches as m on m.idPublication = p.id ) \n"
				+ "		inner join internship as i on m.idInternship = i.id) inner join company as c on c.email = i.company \n"
				+ "where m.id in (select idMatch from interview where confirmedYN = 1) and p.student like ? ";
		
		try(PreparedStatement statement = connection.prepareStatement(query)){
			statement.setString(1, email);
			
			try(ResultSet result = statement.executeQuery()){
				while(result.next()) {
					Company c = new Company();
					
					c.setEmail(result.getString("email"));
					c.setaddress(result.getString("address"));
					c.setName(result.getString("name"));
					
					Internship i = new Internship();
					
					i.setCompany(c);
					i.setId(result.getInt("id"));
					i.setOpenSeats(result.getInt("openSeats"));
					i.setStartingDate(result.getDate("startingDate"));
					i.setStartingDate(result.getDate("endingDate"));
					i.setjobDescription(result.getString("jobDescription"));
					
					ris.add(i);
				}
			}
			
		}
		
		return ris;
	}
	
	public ArrayList<Internship> getInternshipWaitingFeedback(String email) throws SQLException{
		
		ArrayList<Internship> ris = new ArrayList<Internship>();
		
		String query = "select i.*, c.*\n"
				+ "from ((publication as p inner join matches as m on m.idPublication = p.id ) \n"
				+ "		inner join internship as i on m.idInternship = i.id) inner join company as c on c.email = i.company \n"
				+ "where m.id in (select idMatch from interview where confirmedYN = 1) and p.student like ? and current_date() > i.endingDate and\n"
				+ "i.id not in (select internship from feedback where studentYN = 1)";
		
		try(PreparedStatement statement = connection.prepareStatement(query)){
			statement.setString(1, email);
			
			try(ResultSet result = statement.executeQuery()){
				while(result.next()) {
					Company c = new Company();
					
					c.setEmail(result.getString("email"));
					c.setaddress(result.getString("address"));
					c.setName(result.getString("name"));
					
					Internship i = new Internship();
					
					i.setCompany(c);
					i.setId(result.getInt("id"));
					i.setOpenSeats(result.getInt("openSeats"));
					i.setStartingDate(result.getDate("startingDate"));
					i.setStartingDate(result.getDate("endingDate"));
					i.setjobDescription(result.getString("jobDescription"));
					
					ris.add(i);
				}
			}
			
		}
		return ris;
	}
}
