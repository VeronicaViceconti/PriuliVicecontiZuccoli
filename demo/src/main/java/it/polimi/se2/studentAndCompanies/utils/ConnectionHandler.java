package it.polimi.se2.studentAndCompanies.utils;

import javax.servlet.ServletContext;
import javax.servlet.UnavailableException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionHandler {
	
	public static Connection getConnection(ServletContext ctx) throws UnavailableException {
		Connection ris = null;
		
		String driver = ctx.getInitParameter("dbDriver");
		String user = ctx.getInitParameter("dbUser");
		String password = ctx.getInitParameter("dbPassword");
		String url = ctx.getInitParameter("dbUrl");
		
		try {
			
			Class.forName(driver); // load the db driver 
		} catch (ClassNotFoundException e) {
			throw new UnavailableException("can't load the driver");
		}
		
		try {
			ris = DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			throw new UnavailableException("Couldn't get db connection");
		}
		return ris;
	}
	
	public static void closeConnection (Connection c) throws SQLException {
		c.close();
	}
}