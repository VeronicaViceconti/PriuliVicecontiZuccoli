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
import it.polimi.se2.sandc.bean.Match;
import it.polimi.se2.sandc.bean.Publication;
import it.polimi.se2.sandc.bean.User;

public class MatchDAO {
	private Connection connection;

	public MatchDAO(Connection conn) {
		this.connection = conn;
	}

	public void createMatchFromStudent(int pubID, int IDinternship) throws SQLException {
		String query = null;
		query = "INSERT into matches (acceptedYNStudent, idPublication, idInternship) VALUES(true,?, ?)";
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, pubID);
			pstatement.setInt(2, IDinternship);
			pstatement.executeUpdate();

		} catch (SQLException e) {
			throw new SQLException("Error while creating match");
		} finally {
			try {
				pstatement.close(); // devo chiudere prepared statement
			} catch (Exception e) {
				throw new SQLException("Error while trying to close prepared statement");
			}
		}
	}

	public List<Match> findStudentMatches(String emailStudent) throws SQLException {
		String query = null;
		query = "SELECT m.id as matchID, i.id as internID, idPublication,acceptedYNStudent,roleToCover,startingDate,endingDate,c.name,c.address,jobDescription from matches as m join internship as i on m.idInternship = i.id join company as c on c.email = i.company join publication as pub on pub.id = m.idPublication join student as s on s.email = pub.student WHERE s.email = ?;";
		PreparedStatement pstatement = null;
		ResultSet result2 = null;
		List<Match> matches = new ArrayList<>();
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setString(1, emailStudent);

			result2 = pstatement.executeQuery();
			
			if (!result2.isBeforeFirst()) {// no results, no matches found
				return null;	
			}
			else { 
				while (result2.next()) {
					Match match = new Match();
					match.setId(result2.getInt("matchID"));
					
					if(result2.getString("acceptedYNStudent") != null) {
						match.setAccepted(result2.getBoolean("acceptedYNStudent"));
					}
					Publication pub = new Publication();
					pub.setId(result2.getInt("idPublication"));
					match.setPublication(pub);
					Internship intern = new Internship();
					intern.setId(result2.getInt("internID"));
					Date sqlDate = result2.getDate("startingDate");
		            if (sqlDate != null) {
		                intern.setStartingDate( new Date(sqlDate.getTime())); 
		            }
		            sqlDate = result2.getDate("endingDate");
		            if (sqlDate != null) {
		                intern.setEndingDate(new Date(sqlDate.getTime())); 
		            }
		            intern.setjobDescription(result2.getString("jobDescription"));
		            intern.setroleToCover(result2.getString("roleToCover"));
		            Company company = new Company();
		            company.setName(result2.getString("name"));
		            company.setAddress(result2.getString("address"));
		            intern.setCompany(company);
		            match.setInternship(intern);
		            matches.add(match);
		        }
				return matches;	
			}
			
		} catch(SQLException e) {
			throw new SQLException("Error while creating match");
		}finally {
			try {
				pstatement.close();  //devo chiudere prepared statement
			} catch(Exception e) {
				throw new SQLException("Error while trying to close prepared statement");
			}
		}
	}

	public void updateMatchAccepted(int matchID) throws SQLException {
		String query = null;
		query = "UPDATE matches set acceptedYNStudent = 1 WHERE id = ?";
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, matchID);
			pstatement.executeUpdate();

		} catch (SQLException e) {
			throw new SQLException("Error while updating match");
		} finally {
			try {
				pstatement.close(); // devo chiudere prepared statement
			} catch (Exception e) {
				throw new SQLException("Error while trying to close prepared statement");
			}
		}
	}

	public Boolean controlOwnership(String email, int matchID) throws SQLException {
		String query = null;
		query = "SELECT * FROM matches as m join publication as p on m.idPublication = p.id join student as s on s.email = p.student WHERE email = ? and m.id = ?;";
		PreparedStatement pstatement = null;
		ResultSet result2 = null;
		List<Match> matches = new ArrayList<>();
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setString(1, email);
			pstatement.setInt(2, matchID);

			result2 = pstatement.executeQuery();

			if (!result2.isBeforeFirst()) {// no results,he doesn't have that match
				return false;
			} else {
				return true;
			}

		} catch (SQLException e) {
			throw new SQLException("Error while updating match");
		} finally {
			try {
				pstatement.close(); // devo chiudere prepared statement
			} catch (Exception e) {
				throw new SQLException("Error while trying to close prepared statement");
			}
		}
	}
}
