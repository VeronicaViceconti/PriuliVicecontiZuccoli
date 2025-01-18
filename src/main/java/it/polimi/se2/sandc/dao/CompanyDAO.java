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
	
	public List<Internship> searchAllInternships() throws SQLException{
		List<Internship> internships = new ArrayList<>();
	    String query = "SELECT * FROM internship WHERE openSeats > 0"; // Modifica in base alla tua tabella
	    PreparedStatement statement = connection.prepareStatement(query);
        ResultSet result = statement.executeQuery();
        		
		try {
			if (!result.isBeforeFirst()) {// no results, no publications at all 
				return null;	
			}
			else { //at least one publication
				while (result.next()) {
		            Internship internship = new Internship();
		            internship.setId(result.getInt("id"));
		            internship.setCommonId(result.getInt("commonId"));
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
			} catch(Exception e) {
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
