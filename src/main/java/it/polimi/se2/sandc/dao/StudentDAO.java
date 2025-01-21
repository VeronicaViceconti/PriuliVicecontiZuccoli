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
		System.out.println(idPref);
		if(user.getWhichUser().equals("company")) {
			return;
		}
		
		try(PreparedStatement statement = connection.prepareStatement(query)){
			statement.setInt(1, idPref);
			statement.setInt(2, idPub);
			
			statement.executeUpdate();
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
					pub.setStudent(result2.getString("student"));
		            
		            publications.add(pub);
		        }	
				
				return publications;	
			}
		} catch(SQLException e) {
			throw new SQLException("Error while trying to access credentials");
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
				publication.setStudent(result2.getString("student"));
				
				return publication;	
			}
		} catch(SQLException e) {
			throw new SQLException("Error while trying to access credentials");
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
					c.setAddress(result.getString("address"));
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
