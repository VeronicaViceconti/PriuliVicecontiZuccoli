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
import it.polimi.se2.sandc.bean.Form;
import it.polimi.se2.sandc.bean.Internship;
import it.polimi.se2.sandc.bean.Interview;
import it.polimi.se2.sandc.bean.Match;
import it.polimi.se2.sandc.bean.Preferences;
import it.polimi.se2.sandc.bean.Publication;
import it.polimi.se2.sandc.bean.Student;
import it.polimi.se2.sandc.bean.Question;
import it.polimi.se2.sandc.bean.User;

public class NotifDAO {
	private Connection connection;

	public NotifDAO(Connection conn) {
		this.connection = conn;
	}

	public void saveFCMToken( String token, String email, String userType ) throws SQLException {
		
		String query = null;
		if(userType.equalsIgnoreCase("student"))
			query = "update student set token = ? where email = ?";
		else
			query = "update company set token = ? where email = ?";
		
		try(PreparedStatement pstatement = connection.prepareStatement(query)) {
			pstatement.setString(1, token);
			pstatement.setString(2, email);
			
			pstatement.executeUpdate();
			
		} catch(SQLException e) {
			throw new SQLException("Error while controlling ownership");
		}
	}
}