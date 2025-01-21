package it.polimi.se2.sandc.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;

import it.polimi.se2.sandc.bean.User;
import it.polimi.se2.sandc.dao.UserDAO;

/**
 * Servlet implementation class SignupManager
 */
@WebServlet("/SignupManager")
@MultipartConfig
public class SignupManager extends HttpServlet {
	private Connection connection = null;
	private static final long serialVersionUID = 1L;

	public void init() throws ServletException {
		try {
			ServletContext context = getServletContext();
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);

		} catch (ClassNotFoundException e) {
		    e.printStackTrace();
			throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
		    e.printStackTrace();
			throw new UnavailableException("Couldn't get db connection");
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String email = StringEscapeUtils.escapeJava(request.getParameter("email"));
		String name = StringEscapeUtils.escapeJava(request.getParameter("name"));
		String pwd = StringEscapeUtils.escapeJava(request.getParameter("pwd"));
		String address = StringEscapeUtils.escapeJava(request.getParameter("address"));
		String userType = request.getParameter("choose");
    	String  phoneNumber = StringEscapeUtils.escapeJava(request.getParameter("phoneNumber"));
		UserDAO usr = new UserDAO(connection);
				
		if (email == null || email.isEmpty() || name == null || name.trim().isEmpty() ||
				pwd == null || pwd.trim().isEmpty() || address == null || phoneNumber == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Credentials can't be empty!");
			return;
		}
		
		if (name.length() < 5 || pwd.length() <5) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Username and password must be at least 5 characters long.");
	       	return;
	    }
		
		try {
			usr.registerNewUser(name, email, pwd, address, phoneNumber,userType);
			//se tutto va bene
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
		} catch (SQLException e) {
			response.getWriter().println("User already exists");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			
			return;
 		}		
	}

}
