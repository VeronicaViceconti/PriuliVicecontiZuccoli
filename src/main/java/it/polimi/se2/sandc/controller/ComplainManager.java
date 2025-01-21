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
import it.polimi.se2.sandc.dao.CompanyDAO;
import it.polimi.se2.sandc.dao.InternshipDAO;
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
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		CompanyDAO companydao = new CompanyDAO(connection);
		StudentDAO studentdao = new StudentDAO(connection);
		InternshipDAO internshipdao = new  InternshipDAO(connection);
		HttpSession session = request.getSession();	
		User user = (User) session.getAttribute("user");
		String answer;
		int idInternship = -1;
		
		
		try {
			answer = StringEscapeUtils.escapeJava(request.getParameter("answer"));
			idInternship = Integer.parseInt(request.getParameter("idInternship"));
			if(answer == null || idInternship == -1) {
				throw new Exception();
			}
			
		}catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("missing values");
			return;
		}
		
		if(user.getWhichUser().equals("student")) {
			ArrayList<Internship> list = new ArrayList<Internship> ();
			try {
				list = studentdao.getOnGoingInternship(user.getEmail());
			} catch (SQLException e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println("db problems");
				return;
			}
			Boolean found = false;
			for(Internship i : list) {
				if(i.getId() == idInternship) {
					found = true;
				}
			}
			if(!found) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().println("the student aren't in the internship");
				return;
			}
		} else {
			ArrayList<Internship> list = new ArrayList<Internship> ();
			try {
				list = companydao.getOnGoingInternship(user.getEmail());
			} catch (SQLException e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println("db problems");
				return;
			}
			Boolean found = false;
			for(Internship i : list) {
				if(i.getId() == idInternship) {
					found = true;
				}
			}
			if(!found) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().println("the company doesn't own the internship");
				return;
			}
		}
		
		try {
			internshipdao.writeComplaint(user, idInternship, answer);
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
		
		response.setStatus(HttpServletResponse.SC_OK);
	}

}
