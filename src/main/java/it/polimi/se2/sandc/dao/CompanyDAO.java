package it.polimi.se2.sandc.dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

import it.polimi.se2.sandc.bean.Company;
import it.polimi.se2.sandc.bean.Internship;
import it.polimi.se2.sandc.bean.Preferences;
import it.polimi.se2.sandc.bean.Publication;
import it.polimi.se2.sandc.bean.User;

public class CompanyDAO {
	private Connection connection;


	public CompanyDAO(Connection conn) {
		this.connection = conn;
	}
	
	//like the otehr one but this time we are filtering on the internships of ONE company
	public List<Internship> searchAvailableInternships(String companyName,String emailStudent) throws SQLException{
		List<Internship> internships = new ArrayList<>();
		String query = null;
		
		query = "SELECT * FROM internship AS i JOIN company ON company.email = i.company WHERE NOT EXISTS (SELECT * FROM matches AS m JOIN publication AS p ON m.idPublication = p.id WHERE m.idInternship = i.id AND p.student = ?) AND openSeats > 0 AND name = ?";
	    PreparedStatement statement = connection.prepareStatement(query);
	    
	    statement.setString(1, emailStudent);
	    statement.setString(2, companyName);
        ResultSet result = statement.executeQuery();
        		
		try {
			if (!result.isBeforeFirst()) {// no results, no publications at all 
				return null;	
			}
			else { //at least one publication
				while (result.next()) {
		            Internship internship = new Internship();
		            internship.setId(result.getInt("id"));
		            Company company = new Company();
		            company.setName(result.getString("company"));
		            company.setAddress(result.getString("address"));
		            internship.setCompany(company);
		            
		            Date sqlDate = result.getDate("startingDate");
		            if (sqlDate != null) {
		                internship.setStartingDate(new Date(sqlDate.getTime())); 
		            }
		            sqlDate = result.getDate("endingDate");
		            if (sqlDate != null) {
		                internship.setEndingDate(new Date(sqlDate.getTime())); 
		            }
		           
		            internship.setOpenSeats(result.getInt("openSeats"));
		            internship.setroleToCover(result.getString("roleToCover"));
		            internships.add(internship);
		        }	
			}
		} catch(SQLException e) {
			throw new SQLException("Error while trying to access credentials");
		}finally {
			try {
				result.close(); //Devo chiudere result set
			} catch(Exception e) {
				throw new SQLException("Error while trying to close Result Set");
			}
			try {
				statement.close();  //devo chiudere prepared statement
			} catch(Exception e) {
				throw new SQLException("Error while trying to close prepared statement");
			}
		}
		return internships;
	}
	
	//search all the internships but not the ones that has already a match with that publication (so with that student)
	public List<Internship> searchAllInternships(String emailStudent) throws SQLException{
		List<Internship> internships = new ArrayList<>();
		String query = null;
		//need to find the internships that doesn't have matches with publications of that student
		query = "SELECT * FROM sandc.internship AS i JOIN company ON i.company = company.email WHERE NOT EXISTS (SELECT * FROM sandc.matches AS m JOIN sandc.publication AS p ON m.idPublication = p.id	WHERE m.idInternship = i.id AND p.student = ? ) AND openSeats > 0;";

	    PreparedStatement statement = connection.prepareStatement(query);
	    statement.setString(1, emailStudent);
	    
        ResultSet result = statement.executeQuery();
        		
		try {
			if (!result.isBeforeFirst()) {// no results => there are no relevant internshps
				return null;	
			}
			else { //at least one publication
				while (result.next()) {
		            Internship internship = new Internship();
		            internship.setId(result.getInt("id"));
		            internship.setOpenSeats(result.getInt("openSeats"));
		            Company company = new Company();
		            company.setName(result.getString("name"));
		            company.setAddress(result.getString("address"));
		            internship.setCompany(company);
		            
		            Date sqlDate = result.getDate("startingDate");
		            if (sqlDate != null) {
		                internship.setStartingDate(new Date(sqlDate.getTime())); 
		            }
		            sqlDate = result.getDate("endingDate");
		            if (sqlDate != null) {
		                internship.setEndingDate(new Date(sqlDate.getTime())); 
		            }
		            
		            internship.setOpenSeats(result.getInt("openSeats"));
		            internship.setroleToCover(result.getString("roleToCover"));
		            internships.add(internship);
		        }	
			}
		} catch(SQLException e) {
			throw new SQLException("Error while trying to access credentials");
		}finally {
			try {
				result.close(); //Devo chiudere result set
			} catch(Exception e) {
				throw new SQLException("Error while trying to close Result Set");
			}
			try {
				statement.close();  //devo chiudere prepared statement
			} catch(Exception e) {
				throw new SQLException("Error while trying to close prepared statement");
			}
		}
		return internships;
	}
	
	public Internship findTheInternship(int ID) throws SQLException {
		String query =  "SELECT * FROM company JOIN internship as i ON company.email = i.company LEFT JOIN requirement as r on r.idInternship = i.id LEFT JOIN workingpreferences as w ON w.id = r.idWorkingPreference WHERE i.id = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		Internship internship = new Internship();
		
		System.out.println("fuori dal try");
		
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, ID);
			result = pstatement.executeQuery();
			if (!result.isBeforeFirst()) {// no results, no internship found
				System.out.println("no match found");
				return null;
			}
			else { //found the internship
				result.next();
				internship.setId(result.getInt("id"));
				Company company = new Company();
	            company.setName(result.getString("company"));
	            company.setAddress(result.getString("address"));
	            internship.setCompany(company);
	            
	            Date sqlDate = result.getDate("startingDate");
	            if (sqlDate != null) {
	                internship.setStartingDate(new Date(sqlDate.getTime())); 
	            }
	            sqlDate = result.getDate("endingDate");
	            if (sqlDate != null) {
	                internship.setEndingDate(new Date(sqlDate.getTime())); 
	            }
	            
	            internship.setjobDescription(result.getString("jobDescription"));
	            internship.setOpenSeats(result.getInt("openSeats"));
	            internship.setroleToCover(result.getString("roleToCover"));	
			}
		} catch(SQLException e) {
			throw new SQLException("Error while trying to access credentials");
		}finally {
			try {
				result.close(); //Devo chiudere result set
			}catch(Exception e) {
				throw new SQLException("Error while trying to close Result Set");
			}
			try {
				pstatement.close();  //devo chiudere prepared statement
			} catch(Exception e) {
				throw new SQLException("Error while trying to close prepared statement");
			}
		}
		
		//need to return all the preferences of that internship
		query =  "SELECT * FROM internship as i JOIN requirement as r on r.idInternship = i.id JOIN workingpreferences as w ON w.id = r.idWorkingPreference WHERE i.id = ?";
		result = null;
		pstatement = null;
		
		try {
			pstatement = connection.prepareStatement(query);
			
			pstatement.setInt(1, ID);
			result = pstatement.executeQuery();
			if (!result.isBeforeFirst()) {// no results, internships hasen't got working preferences
				return internship;
			}
			else { //found the preferences
				
				List<Preferences> preferences = new ArrayList<Preferences>();
				while(result.next()) {
					Preferences pref = new Preferences();
					pref.setId(result.getInt("idWorkingPreference"));
					pref.setText(result.getString("text"));
					preferences.add(pref);
				}
				internship.setPreferences(preferences);
			}
		} catch(SQLException e) {
			throw new SQLException("Error while trying to get preferences");
		}finally {
			try {
				result.close(); //Devo chiudere result set
			}catch(Exception e) {
				throw new SQLException("Error while trying to close Result Set");
			}
			try {
				pstatement.close();  //devo chiudere prepared statement
			} catch(Exception e) {
				throw new SQLException("Error while trying to close prepared statement");
			}
		}
		
		return internship;
	}
	
	public int createInternship(String email ,Internship i) throws SQLException {
		String query = "insert into Internship "
				+ "(company, openSeats, startingDate, endingDate, jobDescription, roleToCover) values"
				+ " (?,?,?,?,?,?)";
		
		
		try(PreparedStatement statement = connection.prepareStatement(query)){
			statement.setString(1, email);
			statement.setInt(2,i.getOpenSeats());
			statement.setDate(3, i.getStartingDate());
			statement.setDate(4, i.getEndingDate());
			statement.setString(5, i.getjobDescription());
			statement.setString(6, i.getroleToCover());
			statement.executeUpdate();
		}
		
		query = "select max(id) as max from Internship where company like ?";
		
		try(PreparedStatement statement = connection.prepareStatement(query)){
			statement.setString(1, email);
			try(ResultSet result = statement.executeQuery()){
				result.next();
				return result.getInt("max");
			}
		}
		
	}
	
	public void addRequirement(User user, int idPref, int idInt) throws SQLException {
		String query = "insert into Requirement (idWorkingPreference, idInternship) values (?, ?)";
		System.out.println(idInt);
		if(user.getWhichUser().equals("student")) {
			return;
		}
		
		try(PreparedStatement statement = connection.prepareStatement(query)){
			statement.setInt(1, idPref);
			statement.setInt(2, idInt);
			
			statement.executeUpdate();
		}
		
	}
	
	public ArrayList<Internship> getOnGoingInternship(String email) throws SQLException {
		ArrayList<Internship> ris = new ArrayList<Internship>();
		
		String query = "select i.*, c.*\n"
				+ "from ((publication as p inner join matches as m on m.idPublication = p.id ) \n"
				+ "		inner join internship as i on m.idInternship = i.id) inner join company as c on c.email = i.company \n"
				+ "where m.id in (select idMatch from interview where confirmedYN = 1) and i.company like ?";
		
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
	
	public ArrayList<Internship> getInternshipWaitingFeedback(String email) throws SQLException{
		
		ArrayList<Internship> ris = new ArrayList<Internship>();
		
		String query = "select i.*, c.*\n"
				+ "from ((publication as p inner join matches as m on m.idPublication = p.id ) \n"
				+ "		inner join internship as i on m.idInternship = i.id) inner join company as c on c.email = i.company \n"
				+ "where m.id in (select idMatch from interview where confirmedYN = 1) and c.email like ? and current_date() > i.endingDate and\n"
				+ "i.id not in (select internship from feedback where studentYN = 0)";
		
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
