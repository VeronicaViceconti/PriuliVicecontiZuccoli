package it.polimi.se2.sandc.controller;

import java.io.IOException;

import org.apache.commons.lang.StringEscapeUtils;

import it.polimi.se2.sandc.bean.Student;
import it.polimi.se2.sandc.bean.User;
import it.polimi.se2.sandc.dao.StudentDAO;
import it.polimi.se2.sandc.dao.UserDAO;
import it.polimi.se2.sandc.bean.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class LoginManager
 */
@WebServlet("/LoginManager")
@MultipartConfig
public class LoginManager extends HttpServlet {
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
		//per convertire caratteri speciali tipo ",\,..
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String email = StringEscapeUtils.escapeJava(request.getParameter("email"));
		String pwd = StringEscapeUtils.escapeJava(request.getParameter("pwd"));
		
		
		if (email == null || email.isEmpty() || pwd== null || pwd.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Credentials can't be empty!");
			return;
		}
		
		UserDAO usr = new UserDAO(connection);
		User u = null;
		
		try {
			u = usr.checkCredentials(email, pwd);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error, retry later");
			return;
 		}		
		
		
		// se l'utente non è registrato o credenziali non valide
		if(u == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().println("Credentials failed, retry!");
			return;
		}
		
		//se tutto va bene
		request.getSession().setAttribute("user", u);
		if(u.getWhichUser().equals("student"))
			request.getSession().setAttribute("userType", "student");
		else
			request.getSession().setAttribute("userType", "company");
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		//RequestDispatcher dispatcher = request.getRequestDispatcher("/ProfileManager");
		// Esegue il forward
		//dispatcher.forward(request, response);
	}

	
	public void destroy() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException sqle) {}
	}
}
