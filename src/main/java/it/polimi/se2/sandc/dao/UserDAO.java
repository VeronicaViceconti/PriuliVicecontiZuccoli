package it.polimi.se2.sandc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import it.polimi.se2.sandc.bean.User;

public class UserDAO {
	private Connection connection;
	
	public UserDAO(Connection conn) {
		this.connection = conn;
	}
	
	public User sendFormLogin(String email, String psw) throws SQLException {
		String query = "SELECT name, email  FROM student  WHERE email = ? AND psw = ?";
		String query2 = "SELECT name, email  FROM company  WHERE email = ? AND psw = ?";
		ResultSet result = null, result2 = null;
		PreparedStatement pstatement = null,pstatement2 = null;
		
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setString(1, email);
			pstatement.setString(2, psw);
			result = pstatement.executeQuery();
			if (!result.isBeforeFirst()) {// no results, credential check failed or company
				;
			}
			else { //student
				result.next();
				User user = new User();
				user.setName(result.getString("name"));
				user.setEmail(result.getString("email"));
				user.setWhichUser("student");
				return user;	
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
		
		
		try {
			pstatement2 = connection.prepareStatement(query2);
			pstatement2.setString(1, email);
			pstatement2.setString(2, psw);
			result2 = pstatement2.executeQuery();
			if (!result2.isBeforeFirst()) {// no results, credential check failed for both
				return null;	
			}
			else { //company
				result2.next();
				User user = new User();
				user.setName(result2.getString("name"));
				user.setEmail(result2.getString("email"));
				user.setWhichUser("company");
				return user;	
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
	
	public void registerNewUser(String name, String email, String psw,String address,String phoneNumber,String userType) throws SQLException {
		String query = null;
		if(userType.equals("student"))
			query = "INSERT into Student (email,name,address,phoneNumber,cv, psw) VALUES(?, ?, ?, ?, null,?)";
		else
			query = "INSERT into Company (email,name,address,phoneNumber, psw) VALUES(?, ?, ?, ?, ?)";
		PreparedStatement pstatement = null;
		
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setString(1, email);
			pstatement.setString(2, name);
			pstatement.setString(3, address);
			pstatement.setString(4, phoneNumber);
			pstatement.setString(5, psw);
			pstatement.executeUpdate();
		} catch(SQLException e) {
			throw new SQLException("Error while creating new user");
		}finally {
			try {
				pstatement.close();  //devo chiudere prepared statement
			} catch(Exception e) {
				throw new SQLException("Error while trying to close prepared statement");
			}
		}
	}
}
