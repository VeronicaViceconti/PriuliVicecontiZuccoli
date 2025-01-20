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

public class MatchDAO {
	private Connection connection;


	public MatchDAO(Connection conn) {
		this.connection = conn;
	}
	
	public void createMatchFromStudent(int pubID, int IDinternship) throws SQLException {
		String query = null;
		query = "INSERT into matches (acceptedYN, idPublication, idInternship) VALUES(null,?, ?)";
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, pubID);
			pstatement.setInt(2, IDinternship);
			pstatement.executeUpdate();
			
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
}
