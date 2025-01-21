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
		
		query = "insert into complaint (studentYn, idForm, internship) values (?,?,?)";
		
		try(PreparedStatement statement = connection.prepareStatement(query)){
			statement.setBoolean(1, user.getWhichUser().equals("student"));
			statement.setInt(2, idForm);
			statement.setInt(3, idInternship);
			statement.executeUpdate();
		}
	}
	
	public void writeFeedback(User user, int idInternship, String answer) throws SQLException {
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
		query = "insert into question (txt, answer, idForm) values ('feedback form',?,?)";
		try(PreparedStatement statement = connection.prepareStatement(query)){
			statement.setString(1, answer);
			statement.setInt(2, idForm);
			
			statement.executeUpdate();
		}
		
		query = "insert into feedback (studentYn, idForm, internship) values (?,?,?)";
		
		try(PreparedStatement statement = connection.prepareStatement(query)){
			statement.setBoolean(1, user.getWhichUser().equals("student"));
			statement.setInt(2, idForm);
			statement.setInt(3, idInternship);
			statement.executeUpdate();
		}
	}
}
