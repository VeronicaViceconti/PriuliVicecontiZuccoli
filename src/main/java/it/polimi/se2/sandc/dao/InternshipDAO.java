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
import it.polimi.se2.sandc.bean.User;

public class InternshipDAO {
	private Connection connection;


	public InternshipDAO(Connection conn) {
		this.connection = conn;
	}
	
	
	public void writeComplaint(User user, int idInternship, String answer) throws SQLException {
		String query;
		
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
		query = "insert into question (txt, answer, idForm) values ('complaint form',?,?)";
		try(PreparedStatement statement = connection.prepareStatement(query)){
			statement.setString(1, answer);
			statement.setInt(2, idForm);
			
			statement.executeUpdate();
		}
		
		query = "insert into complaint (studentYn, idForm) values (?,?)";
		
		try(PreparedStatement statement = connection.prepareStatement(query)){
			statement.setBoolean(1, user.getWhichUser().equals("student"));
			statement.setInt(2, idForm);
			
			statement.executeUpdate();
		}
	}


	public List<Internship> getAllICompanyInternships(String email) throws SQLException {
		String query = null;
		query = "SELECT * FROM sandc.internship AS i JOIN company ON i.company = company.email WHERE company = ?;";
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
		            internship.setroleToCover(result.getString("roleToCover"));
		            intern.add(internship);
				}
			}
			
		} catch(SQLException e) {
			throw new SQLException("Error while getting company internships");
		}finally {
			try {
				pstatement.close();  //devo chiudere prepared statement
			} catch(Exception e) {
				throw new SQLException("Error while trying to close prepared statement");
			}
		}
		return intern;
	}
}
