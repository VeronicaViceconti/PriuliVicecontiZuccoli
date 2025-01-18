package it.polimi.se2.sandc.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;

import it.polimi.se2.sandc.bean.Internship;
import it.polimi.se2.sandc.bean.User;
import it.polimi.se2.sandc.dao.CompanyDAO;
import it.polimi.se2.sandc.dao.UserDAO;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class ProfileManager
 */
@WebServlet("/ProfileManager")
public class ProfileManager extends HttpServlet {
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
		
		HttpSession s = request.getSession();
		
		if (s.getAttribute("user") == null) {
			response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
			response.getWriter().println("Utente non trovato..");
			request.getSession(false).invalidate();
			return;
        }
		String email = StringEscapeUtils.escapeJava(request.getParameter("email"));
		String userType = (String) s.getAttribute("userType");
		
		if (email == null || email.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Something went wrong!");
			return;
		}
		
		if(userType.equals("student")) { //we want to use student profile -> search company publications
			if(request.getParameter("page") == null)
				return;
			
			 switch (request.getParameter("page").toString()) { //uso lo switch per capire quale azione dobbiamo fare in questa servlet
			 	case "toHomepage":
			 		findAllInternships(response);
			 		break;
			 	case "internshipInfo":
			 		findInternshipInfo(request,response);
			 		break;
			 	case "addInternshipThenHomepage": //student want to apply to the internship
			 		int ID = Integer.parseInt(request.getParameter("ID"));
			 		if(existsInternshipID(ID))
			 			createMatch(request,response);
			 		else
			 			response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED); //error 405
			 		break;
			 } 
		}else { //we want to use company's profile
			
		}
	}

	private boolean existsInternshipID(int iD) {
		///control that the ID exists as internship and that available seat > 0
		return false;
	}

	private void createMatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
		//creare il match tra internship ID e l'utente 
		
		CompanyDAO company = null;
		Internship internship = null;
		company = new CompanyDAO(connection);
		
		 try {
			internship = company.findTheInternship(Integer.parseInt(request.getParameter("ID")));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error, retry later");
		}
		
		String internshipString = new Gson().toJson(internship);
    // Imposta il tipo di contenuto e invia la risposta
       response.setContentType("application/json");
       response.getWriter().write(internshipString);
       response.setStatus(HttpServletResponse.SC_OK);
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	private void findInternshipInfo(HttpServletRequest request,HttpServletResponse response) throws IOException {
		
		CompanyDAO company = null;
		Internship internship = null;
		company = new CompanyDAO(connection);
		
		 try {
			internship = company.findTheInternship(Integer.parseInt(request.getParameter("ID")));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error, retry later");
		}
		
		String internshipString = new Gson().toJson(internship);
    // Imposta il tipo di contenuto e invia la risposta
       response.setContentType("application/json");
       response.getWriter().write(internshipString);
       response.setStatus(HttpServletResponse.SC_OK);
	}

	private void findAllInternships(HttpServletResponse response) throws IOException {
		CompanyDAO company = null;
		List<Internship> internships = null;
		company = new CompanyDAO(connection);
		 try {
			internships = company.searchAllInternships();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error, retry later");
		}
		
		String internshipsString = new Gson().toJson(internships);
    // Imposta il tipo di contenuto e invia la risposta
       response.setContentType("application/json");
       response.getWriter().write(internshipsString);
       response.setStatus(HttpServletResponse.SC_OK);	   
	}

}
