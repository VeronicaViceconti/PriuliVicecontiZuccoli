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
import it.polimi.se2.sandc.bean.Match;
import it.polimi.se2.sandc.bean.Preferences;
import it.polimi.se2.sandc.bean.Publication;
import it.polimi.se2.sandc.bean.User;
import it.polimi.se2.sandc.dao.CompanyDAO;
import it.polimi.se2.sandc.dao.InternshipDAO;
import it.polimi.se2.sandc.dao.PreferenceDAO;
import it.polimi.se2.sandc.dao.PublicationDAO;
import it.polimi.se2.sandc.dao.StudentDAO;
import it.polimi.se2.sandc.dao.UserDAO;

/**
 * Servlet implementation class PublicationManager, manages all the actions related to student/company publications
 */
@WebServlet("/PublicationManager")
@MultipartConfig
public class PublicationManager extends HttpServlet {
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
    public PublicationManager() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession s = request.getSession();
		User user = (User) request.getSession().getAttribute("user");
		String email = user.getEmail();
		String userType = (String) s.getAttribute("userType");
		
		//input control
		if(request.getParameter("page") == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().write("missing values");
			return;
		}
			
		
		if(userType.equalsIgnoreCase("student")) { //we want to use student 
			 switch (request.getParameter("page").toString()) { 
			 	case "getPreferences": //find all the preferences of a student
			 		getPrefereces(email, request, response);
			 		break;
			 	default:
			 		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			 		response.getWriter().println("page not found");
			 		break;
			 } 
		}else { //we want to use company
			 switch (request.getParameter("page").toString()) { 
			 	case "getPreferences": //find all preferenec of a company
			 		getPrefereces(email, request, response);
			 		break;
			 	case "proposedInternships": //find all internships proposed by a company
			 		getAllCompanyInternships(response, email);
			 		break;
			 	case "waitingFeedbackInternships": //find all company internships that are finished and waiting for feedback
			 		waitingFeedbackInternships(response,email);
			 		break;
			 	default:
			 		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			 		response.getWriter().println("page not found");
			 		break;
			 } 
		}
		
	}

    /**
     * Find all the internships that the company need to make a feedback on 
     * @param email company
     * @throws IOException
     */
	private void waitingFeedbackInternships(HttpServletResponse response, String email) throws IOException {
		CompanyDAO company = new CompanyDAO(connection);
		List<Match> matches = new ArrayList<>();
		
		try {
			matches = company.getMatchWaitingFeedback(email);
			
			String matchesString = new Gson().toJson(matches);
	       response.setContentType("application/json");
	       response.getWriter().write(matchesString);       
	       response.setStatus(HttpServletResponse.SC_OK);
		}catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("something was wrong while getting company internships");
			return;
		}	
		
	}
	
	//return alla the internships of a company 
	private void getAllCompanyInternships(HttpServletResponse response, String email) throws IOException {
		InternshipDAO intern = new InternshipDAO(connection);
		List<Internship> internships = new ArrayList<>();
		try {
			internships = intern.getAllICompanyInternships(email);
			
			String internString = new Gson().toJson(internships);
			
	       response.setContentType("application/json");
	       response.getWriter().write(internString);       
	       response.setStatus(HttpServletResponse.SC_OK);
		}catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Something was wrong while getting company internships");
			return;
		}	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession s = request.getSession();
		
		String userType = (String) s.getAttribute("userType");
		String email = ((User) s.getAttribute("user")).getEmail();
		
		//input control
		if(request.getParameter("page") == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().write("missing values");
			return;
		}
		
		if(userType.equalsIgnoreCase("student")) {  //the user is a student
			 switch (request.getParameter("page").toString()) { 
			 	case "sendCvForm": //publish cv 
			 		cvPublication(email, request, response);
			 		break;
			 	case "sendPreferences": //publish preferences
						try {
							preferencePublicationStudent(email, request, response);
						} catch (IOException | SQLException e) {
							// TODO Auto-generated catch block
							response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
							response.getWriter().println("error while adding new student publication!");
						}
			 		break;
			 	default :
			 		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			 		response.getWriter().println("page not found");
			 		break;
			 } 
		}else { //we want to use company
			 switch (request.getParameter("page").toString()) { 
			 	case "sendProjectForm": //publish internship
			 		internshipPublication(email, request, response);
			 		break;
			 	case "sendPreferences": //publish preferences
			 		requirementPublicationCompany(request, response);
			 		break;
			 	default:
			 		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			 		response.getWriter().println("page not found");
			 		break;
			 } 
		}		
	}
	
	/**
	 * 
	 * @param email student
	 * @throws IOException
	 */
	private void cvPublication(String email, HttpServletRequest request, HttpServletResponse response) throws IOException {
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
			//find the cv
			String path = getServletContext().getInitParameter("pathUploadCv")  + email + ".pdf";
			part.write(path);
			
			//update db
			dao.putCv((User) session.getAttribute("user"), path);
			response.setStatus(HttpServletResponse.SC_OK);
			return;
		} catch (IOException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Error saving the cv");
			return;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("connection error with the db");
			return;
		}
	}

	/**
	 * 
	 * @param email user
	 * @throws IOException
	 */
	private void getPrefereces(String email, HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		PreferenceDAO preferecedao = new PreferenceDAO(connection);
		
		//input control
		if(request.getParameter("type") == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("missing values");
			return;
		}
		
		String type = StringEscapeUtils.escapeJava(request.getParameter("type"));
		type = type.trim();
			
		switch (type) {
		case "all":
			try { //return all working preferences of the user 
				ArrayList <Preferences> ris = preferecedao.getWorkingPreferences();
				String preferences = new Gson().toJson(ris);
								
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
	
	/**
	 * Publish the preferences of a new student publication 
	 * @param email student
	 * @throws IOException
	 * @throws SQLException
	 */
	private void preferencePublicationStudent(String email, HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
		PreferenceDAO preferecedao = new PreferenceDAO(connection);
		StudentDAO studentdao = new StudentDAO(connection);
		HttpSession session = request.getSession();	
		User user = (User) session.getAttribute("user");
		
		//take all the preferences in the db
		List <Preferences> pref = null; //all the preferences in the db
		try {
			pref = preferecedao.getWorkingPreferences();
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("connection error with the db");
			return;
		}
		
		//take the ones chosen by the user
		List<Preferences> prefs = new ArrayList<>(); //new preferences chosen by the user
		for(Preferences x : pref) {
			
			if(request.getParameter(x.getText()) != null) {
				prefs.add(x);
			}
		}
		
		//control that there isn't already a publication with the same new preferences
		PublicationDAO pubDAO = new PublicationDAO(connection);
		List<Publication> publications = new ArrayList<>();
		
		publications = pubDAO.retrieveAllWP(email);
		if(publications != null) {
			for(Publication p : publications) {
				List<Preferences> preferencesPubp = p.getChoosenPreferences();
				ArrayList<String> prefText = new ArrayList<>();
				ArrayList<String> prefTextUser = new ArrayList<>();
				
				for(Preferences preff : preferencesPubp)
					prefText.add(preff.getText());
				for(Preferences preff : prefs)
					prefTextUser.add(preff.getText());
				
				if(prefText.containsAll(prefTextUser) && preferencesPubp.size() == prefs.size()) { //already exist one with same preferences
					response.setStatus(HttpServletResponse.SC_OK);
					response.getWriter().println("You already have an equal publication!");
					return;
				}
			}
		}
		
		
		int idpub;
		//create new publication because prerequisites are fine
		try {
			idpub = studentdao.createPublication(user);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("connection error with the db");
			return;
		}
		//add preferences to that publication
		for(Preferences x : prefs) {
			try {
				studentdao.addPreference(user,Integer.parseInt(request.getParameter(x.getText())), idpub);
			} catch (SQLException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "cannot add the preferences");
				return;
			}
		}
		
		
		response.setStatus(HttpServletResponse.SC_OK);
	}
	
	/**
	 * Publish a new internship created by the company
	 * @param email company
	 * @throws IOException
	 */
	private void internshipPublication(String email, HttpServletRequest request, HttpServletResponse response) throws IOException {
		CompanyDAO companydao = new CompanyDAO(connection);		
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

	
	/**
	 * Publish the requirement of a new company internship 
	 * @throws IOException
	 */
	private void requirementPublicationCompany(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PreferenceDAO preferecedao = new PreferenceDAO(connection);
		CompanyDAO companydao = new CompanyDAO(connection);
		HttpSession session = request.getSession();	
		User user = (User) session.getAttribute("user");
		
		List <Preferences> pref = null;
		
		try { //first find all the working preferences
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
		
		//control the ones that are requested for the new internship
		for(Preferences x : pref) {
			
			if(request.getParameter(x.getText()) != null) {
				try { //add new requirements
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
