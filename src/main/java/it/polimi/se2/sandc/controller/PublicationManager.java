package it.polimi.se2.sandc.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;
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
import javax.servlet.http.Part;

import org.apache.commons.lang.StringEscapeUtils;

import com.google.gson.Gson;

import it.polimi.se2.sandc.bean.Internship;
import it.polimi.se2.sandc.bean.Preferences;
import it.polimi.se2.sandc.bean.User;
import it.polimi.se2.sandc.dao.CompanyDAO;
import it.polimi.se2.sandc.dao.InternshipDAO;
import it.polimi.se2.sandc.dao.PreferenceDAO;
import it.polimi.se2.sandc.dao.StudentDAO;
import it.polimi.se2.sandc.dao.UserDAO;

/**
 * Servlet implementation class PublicationManager
 */
@WebServlet("/PublicationManager")
@MultipartConfig
public class PublicationManager extends HttpServlet {
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
    public PublicationManager() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession s = request.getSession();
		if (s.getAttribute("user") == null) {
			response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
			response.getWriter().println("Utente non trovato..");
			request.getSession(false).invalidate();
			return;
        }
		User user = (User) request.getSession().getAttribute("user");
		String email = user.getEmail();
		String userType = (String) s.getAttribute("userType");
		
		if(userType.equalsIgnoreCase("student")) { //we want to use student profile 
			if(request.getParameter("page") == null)
				return;
			
			 switch (request.getParameter("page").toString()) { //uso lo switch per capire quale azione dobbiamo fare in questa servlet
			 	case "getPreferences":
			 		getPrefereces(email, request, response);
			 		break;
			 	default:
			 		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			 		response.getWriter().println("page not found");
			 		break;
			 } 
		}else { //we want to use company
			 switch (request.getParameter("page").toString()) { //uso lo switch per capire quale azione dobbiamo fare in questa servlet
			 	case "getPreferences":
			 		getPrefereces(email, request, response);
			 		break;
			 	case "proposedInternships":
			 		getAllCompanyInternships(response, email);
			 		break;
			 	default:
			 		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			 		response.getWriter().println("page not found");
			 		break;
			 } 
		}
		
	}

	//return alla the internships of a company 
	private void getAllCompanyInternships(HttpServletResponse response, String email) throws IOException {
		InternshipDAO intern = new InternshipDAO(connection);
		List<Internship> internships = new ArrayList<>();
		try {
			internships = intern.getAllICompanyInternships(email);
			
			String internString = new Gson().toJson(internships);
			
			// Imposta il tipo di contenuto e invia la risposta
	       response.setContentType("application/json");
	       response.getWriter().write(internString);       
	       response.setStatus(HttpServletResponse.SC_OK);
		}catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("something was wrong while getting company internships");
			return;
		}	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession s = request.getSession();
		
		String userType = (String) s.getAttribute("userType");
		String email = ((User) s.getAttribute("user")).getEmail();
		if(userType.equalsIgnoreCase("student")) { //we want to use student profile -> search company publications
			if(request.getParameter("page") == null)
				return;
			
			 switch (request.getParameter("page").toString()) { //uso lo switch per capire quale azione dobbiamo fare in questa servlet
			 	case "sendCvForm":
			 		cvPublication(email, request, response);
			 		break;
			 	case "sendPreferences":
			 		preferencePublicationStudent(email, request, response);
			 		break;
			 	default :
			 		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			 		response.getWriter().println("page not found");
			 		break;
			 } 
		}else { //we want to use company's profile
			 switch (request.getParameter("page").toString()) { //uso lo switch per capire quale azione dobbiamo fare in questa servlet
			 	case "sendProjectForm":
			 		internshipPublication(email, request, response);
			 		break;
			 	case "sendPreferences":
			 		requirementPublicationCompany(email, request, response);
			 		break;
			 	default:
			 		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			 		response.getWriter().println("page not found");
			 		break;
			 } 
		}		
	}
	
	
	private void cvPublication(String email, HttpServletRequest request, HttpServletResponse response) throws IOException {
		System.out.println("saving the pdf");
		HttpSession session = request.getSession();	
		Part part = null;
		
		//check that the user is a student not a company
		if(((User) session.getAttribute("user")).getWhichUser().equals("company")) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().println("not autorized");
			return;
		}
		
		try {
			part = request.getPart("cv");
			if(part == null) {
				throw new Exception("missing post values");
			}
		}catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("missing values");
			return;
		}
		
		StudentDAO dao = new StudentDAO(connection);
		try {
			String path = getServletContext().getInitParameter("pathUploadCv")  + email + ".pdf";
			System.out.println("saving the pdf");
			//imageDao.AddImage(username, title, description, path);
			System.out.println(path);
			part.write(path);
			
			dao.putCv((User) session.getAttribute("user"), path);
			response.setStatus(HttpServletResponse.SC_OK);
			return;
		} catch (IOException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("error saving the cv");
			return;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("connection error with the db");
			return;
		}
	}

	
	private void getPrefereces(String email, HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		PreferenceDAO preferecedao = new PreferenceDAO(connection);
		
		String type = StringEscapeUtils.escapeJava(request.getParameter("type"));
		type = type.trim();
		
		if(type == null || type.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("missing values");
			return;
		}
			
		switch (type) {
		case "all":
			try {
				ArrayList <Preferences> ris = preferecedao.getWorkingPreferences();
				String preferences = new Gson().toJson(ris);
				
			    // Imposta il tipo di contenuto e invia la risposta
				
		       response.setContentType("application/json");
		       response.getWriter().write(preferences);
		       response.setStatus(HttpServletResponse.SC_OK);
		       
			} catch (SQLException e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println("connection error with the db");
				return;
			}
			break;

		default:
			break;
		}
	}
	
	private void preferencePublicationStudent(String email, HttpServletRequest request, HttpServletResponse response) throws IOException {
		PreferenceDAO preferecedao = new PreferenceDAO(connection);
		StudentDAO studentdao = new StudentDAO(connection);
		HttpSession session = request.getSession();	
		User user = (User) session.getAttribute("user");
		
		List <Preferences> pref = null;
		
		try {
			pref = preferecedao.getWorkingPreferences();
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("connection error with the db");
			return;
		}
		int idpub;
		try {
			idpub = studentdao.createPublication(user);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("connection error with the db");
			return;
		}
		
		for(Preferences x : pref) {
			
			if(request.getParameter(x.getText()) != null) {
				System.out.println(request.getParameter(x.getText()));
				try {
					studentdao.addPreference(user,Integer.parseInt(request.getParameter(x.getText())), idpub);
				} catch (SQLException e) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "cannot add the preferences");
					return;
				}
			}
		}
		
		response.setStatus(HttpServletResponse.SC_OK);
	}
	
	private void internshipPublication(String email, HttpServletRequest request, HttpServletResponse response) throws IOException {
		CompanyDAO companydao = new CompanyDAO(connection);
		StudentDAO studentdao = new StudentDAO(connection);
		HttpSession session = request.getSession();	
		User user = (User) session.getAttribute("user");
		
		Internship i = new Internship();
		
		try {
			i.setjobDescription(StringEscapeUtils.escapeJava(request.getParameter("description")));
			i.setStartingDate(Date.valueOf(request.getParameter("startingDate")));
			i.setEndingDate(Date.valueOf(request.getParameter("endingDate")));
			i.setOpenSeats(Integer.parseInt(request.getParameter("openSeats")));
			i.setroleToCover(StringEscapeUtils.escapeJava(request.getParameter("role")));
			if(i.getEndingDate() == null || i.getStartingDate() == null || i.getjobDescription() == null 
					|| i.getroleToCover() == null || request.getParameter("openSeats") == null) {
				throw new Exception();
			}
		}catch (Exception e ) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("missing values");
			return;
		}
		int ris;
		try {
			ris = companydao.createInternship(email, i);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "db problems");
			return;
		}
		
		
		response.setStatus(HttpServletResponse.SC_OK);
		
		response.setContentType("application/json");
		String tmp = new Gson().toJson(ris);
		response.getWriter().write(tmp);
	}

	private void requirementPublicationCompany(String email, HttpServletRequest request, HttpServletResponse response) throws IOException {
		PreferenceDAO preferecedao = new PreferenceDAO(connection);
		CompanyDAO companydao = new CompanyDAO(connection);
		HttpSession session = request.getSession();	
		User user = (User) session.getAttribute("user");
		
		List <Preferences> pref = null;
		
		try {
			pref = preferecedao.getWorkingPreferences();
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("connection error with the db");
			return;
		}
		int idInt;
		try {
			idInt = Integer.parseInt(request.getParameter("idInternship"));
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("the id internship is missing");
			return;
		}
		
		for(Preferences x : pref) {
			
			if(request.getParameter(x.getText()) != null) {
				System.out.println(request.getParameter(x.getText()));
				try {
					companydao.addRequirement(user, Integer.parseInt(request.getParameter(x.getText())), idInt);
				} catch (SQLException e) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "cannot add the requirement");
					return;
				}
			}
		}
	
		response.setStatus(HttpServletResponse.SC_OK);
	}

}
