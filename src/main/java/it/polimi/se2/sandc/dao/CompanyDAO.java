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
		            internship.setCompany(company);
		            
		            Date sqlDate = result.getDate("startingDate");
		            if (sqlDate != null) {
		                internship.setStartingDate(new Date(sqlDate.getTime())); 
		            }
		            sqlDate = result.getDate("endingDate");
		            if (sqlDate != null) {
		                internship.setEndingDate(new Date(sqlDate.getTime())); 
		            }
		            
		            internship.setOfferedConditions(result.getString("offeredConditions"));
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
		query = "SELECT * FROM internship AS i WHERE NOT EXISTS (SELECT * FROM matches AS m JOIN publication AS p ON m.idPublication = p.id WHERE m.idInternship = i.id AND p.student = ? )  AND openSeats > 0;";
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
		            Company company = new Company();
		            company.setName(result.getString("company"));
		            internship.setCompany(company);
		            
		            Date sqlDate = result.getDate("startingDate");
		            if (sqlDate != null) {
		                internship.setStartingDate(new Date(sqlDate.getTime())); 
		            }
		            sqlDate = result.getDate("endingDate");
		            if (sqlDate != null) {
		                internship.setEndingDate(new Date(sqlDate.getTime())); 
		            }
		            
		            internship.setOfferedConditions(result.getString("offeredConditions"));
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
		String query =  "SELECT * FROM company JOIN internship ON company.email = internship.company WHERE id = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, ID);
			result = pstatement.executeQuery();
			if (!result.isBeforeFirst()) {// no results, no internship found
				return null;
			}
			else { //found the internship
				result.next();
				Internship internship = new Internship();
				internship.setId(result.getInt("id"));
				Company company = new Company();
	            company.setName(result.getString("company"));
	            internship.setCompany(company);
	            
	            Date sqlDate = result.getDate("startingDate");
	            if (sqlDate != null) {
	                internship.setStartingDate(new Date(sqlDate.getTime())); 
	            }
	            sqlDate = result.getDate("endingDate");
	            if (sqlDate != null) {
	                internship.setEndingDate(new Date(sqlDate.getTime())); 
	            }
	            
	            internship.setOfferedConditions(result.getString("offeredConditions"));
				return internship;	
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
	}
}
