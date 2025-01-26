package it.polimi.se2.sandc.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

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

import it.polimi.se2.sandc.bean.Internship;
import it.polimi.se2.sandc.bean.User;
import it.polimi.se2.sandc.dao.CompanyDAO;
import it.polimi.se2.sandc.dao.InternshipDAO;
import it.polimi.se2.sandc.dao.StudentDAO;
import it.polimi.se2.sandc.bean.Match;

/**
 * Servlet implementation class FeedbackManager
 */
@WebServlet("/FeedbackManager")
@MultipartConfig
public class FeedbackManager extends HttpServlet {
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
    public FeedbackManager() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		CompanyDAO companydao = new CompanyDAO(connection);
		StudentDAO studentdao = new StudentDAO(connection);
		InternshipDAO internshipdao = new  InternshipDAO(connection);
		HttpSession session = request.getSession();	
		User user = (User) session.getAttribute("user");
		String answer;
		int idMatch = -1;
		
		
		try {
			answer = StringEscapeUtils.escapeJava(request.getParameter("answer"));
			System.out.println(answer);
			idMatch = Integer.parseInt(request.getParameter("idMatch"));
			if(answer == null || idMatch == -1) {
				throw new Exception();
			}
			
		}catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("missing values");
			return;
		}
		
		if(user.getWhichUser().equals("student")) {
			ArrayList<Match> list = new ArrayList<Match> ();
			try {
				list = studentdao.getMatchWaitingFeedback(user.getEmail());
			} catch (SQLException e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println("db problems");
				return;
			}
			Boolean found = false;
			for(Match i : list) {
				if(i.getId() == idMatch) {
					found = true;
				}
			}
			if(!found) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().println("the student isn't in the internship or has already written a feedback");
				return;
			}
		} else {
			ArrayList<Match> list = new ArrayList<Match> ();
			try {
				list = companydao.getMatchWaitingFeedback(user.getEmail());
			} catch (SQLException e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println("db problems");
				return;
			}
			Boolean found = false;
			for(Match i : list) {
				if(i.getId() == idMatch) {
					found = true;
					try {
						internshipdao.writeFeedback(user, i.getPublication().getStudent().getEmail(), i.getInternship().getCompany().getEmail(), answer);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
			if(!found) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().println("the company doesn't own the internship or has already written a feedback");
				return;
			}
		}
		
		response.setStatus(HttpServletResponse.SC_OK);
	}
}
