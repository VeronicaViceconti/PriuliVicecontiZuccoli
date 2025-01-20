package it.polimi.se2.sandc.dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
	
	//search all the publications of a student
	public List<Publication> findStudentPublications(String email) throws SQLException {
		String query2 = null;
		query2 = "SELECT * from publication WHERE student = ?";
		ResultSet result2 = null;
		PreparedStatement pstatement2 = null;
		List<Publication> publications = null;
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
}
