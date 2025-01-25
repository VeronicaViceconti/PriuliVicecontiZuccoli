package it.polimi.se2.sandc.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;

import it.polimi.se2.sandc.bean.Preferences;
import it.polimi.se2.sandc.bean.User;
import it.polimi.se2.sandc.bean.Internship;
import it.polimi.se2.sandc.bean.Match;
import it.polimi.se2.sandc.dao.CompanyDAO;
import it.polimi.se2.sandc.dao.InternshipDAO;
import it.polimi.se2.sandc.dao.MatchDAO;
import it.polimi.se2.sandc.dao.PreferenceDAO;
import it.polimi.se2.sandc.dao.StudentDAO;

/**
 * Servlet implementation class ComplainManager
 */
@WebServlet("/ComplainManager")
@MultipartConfig
public class ComplainManager extends HttpServlet {
	private Connection connection = null;
	private static final long serialVersionUID = 1L;

	public void init(Connection connection) {
	    this.connection = connection;
	  }
	
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
     * @see HttpServlet#HttpServlet()
     */
    public ComplainManager() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		CompanyDAO companydao = new CompanyDAO(connection);
		StudentDAO studentdao = new StudentDAO(connection);
		InternshipDAO internshipdao = new  InternshipDAO(connection);
		MatchDAO matchdao = new  MatchDAO(connection);
		HttpSession session = request.getSession();	
		User user = (User) session.getAttribute("user");
		String answer;
		int idMatch = -1;
		
		try {
			answer = StringEscapeUtils.escapeJava(request.getParameter("answer"));
			idMatch = Integer.parseInt(request.getParameter("idMatch"));
			if(answer == null || request.getParameter("idMatch") == null) {
				throw new Exception();
			}
			
		}catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("missing values");
			return;
		}
		
		Match m = null;
		try {
			m = matchdao.getMatch(idMatch);
		} catch (SQLException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("connection error with the db");
			return;
		}
		
		if(user.getWhichUser().equals("student")) {
			if(!m.getPublication().getStudent().getEmail().equals(user.getEmail())) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().println("the student isn't in the match");
				return;
			}
		} else {
			if(!m.getInternship().getCompany().getEmail().equals(user.getEmail())) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().println("the company isn't in the match");
				return;
			}
		}
		
		try {
			internshipdao.writeComplaint(user, m.getPublication().getStudent().getEmail(), m.getInternship().getCompany().getEmail(), answer);
		} catch (SQLException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("connection error with the db");
			return;
		}
		
		response.setStatus(HttpServletResponse.SC_OK);
	}

}
