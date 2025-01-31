package it.polimi.se2.sandc.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
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
import org.apache.tomcat.util.http.fileupload.FileUtils;

import it.polimi.se2.sandc.bean.Company;
import it.polimi.se2.sandc.bean.Feedback;
import it.polimi.se2.sandc.bean.Internship;
import it.polimi.se2.sandc.bean.Match;
import it.polimi.se2.sandc.bean.Publication;
import it.polimi.se2.sandc.bean.Student;
import it.polimi.se2.sandc.bean.User;
import it.polimi.se2.sandc.dao.CompanyDAO;
import it.polimi.se2.sandc.dao.InternshipDAO;
import it.polimi.se2.sandc.dao.MatchDAO;
import it.polimi.se2.sandc.dao.PublicationDAO;
import it.polimi.se2.sandc.dao.StudentDAO;
import it.polimi.se2.sandc.dao.UserDAO;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class ProfileManager
 */
@WebServlet("/ProfileManager")
@MultipartConfig
public class ProfileManager extends HttpServlet {
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
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		HttpSession s = request.getSession();

		String userType = (String) s.getAttribute("userType");
		
		User user = (User) request.getSession().getAttribute("user");
		user.setWhichUser(userType);
		if(userType.equalsIgnoreCase("student")) { //we want to use student profile -> search company publications
			if(request.getParameter("page") == null)
				return;
			//the internship exists, now need to find the correspond student's publication
			 switch (request.getParameter("page").toString()) { 
			 	case "toHomepage": //when going to homepage, need all the available internship
			 		findPossibleInternships(response,user.getEmail());
			 		break;
			 	case "internshipInfo": //when  need to open one internship
			 		findInternshipInfo(request,response,user);
			 		break;
			 	case "openPubAndWP": //when the student click the button apply, so need all the working preferences
			 		retrieveAllWP(response, user.getEmail());
			 		break;
			 	case "addInternshipThenHomepage": //student want to apply to the internship
			 		
			 		if(request.getParameter("IDintern") == null) {
						response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
						response.getWriter().println("missing values");
						return;
					}
			 		
			 		Integer ID = Integer.parseInt(request.getParameter("IDintern"));
			 		CompanyDAO company = null;
					Internship internship = null;
					company = new CompanyDAO(connection);
					
					try {
						internship = company.findTheInternship(ID);
					} catch (SQLException e) {
						response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
						response.getWriter().println("Internship not found, retry later");
						return;
					}
					
					if(internship == null) { //the internship searched doesn't exist
						response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
						response.getWriter().println("The internship doesn't exist");
						return;
					}
					
					StudentDAO student = new StudentDAO(connection);
					Publication pub = new Publication();
					try {
						if(request.getParameter("IDpub") == null) {
							response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
							response.getWriter().println("missing values");
							return;
						}
						pub = student.findStudentPublication(user.getEmail(), Integer.parseInt(request.getParameter("IDpub")));
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
						response.getWriter().println("Publication not found, retry later");
						return;
					}
					if(pub == null) { //the publication searched doesn't exist
						response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
						response.getWriter().println("The user doesn't have publication");
						return;
					}
					
					//found the publication
					//now we can create the match 
			 		createMatchStudent(response,internship.getId(),pub.getId());
			 		
			 		//now need to return to homepage, so return all the available internships
			 		findPossibleInternships(response,user.getEmail());
			 		break;
			 	case "showMatches": //show all student matches 
			 		showAllStudentMatches(response,user.getEmail());
			 		break;
			 	case "filteredInternships": //when the student search internship of a particular company
			 		String x = StringEscapeUtils.escapeJava(request.getParameter("condition")); //condition = company to search
			 		findFilteredInternships(response,x,user);
			 		break;
			 	case "profileInfo":
			 		findProfileInfo(response,userType,user.getEmail());
			 		break;
			 		default:
			 			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			 } 
		}else {
			if(request.getParameter("page") == null) 
			    return; 
			    
			    switch (request.getParameter("page").toString()) { 
			     case "internshipInfo": //when the page need to open one internship 
			      findInternshipInfo(request,response,user); 
			      break; 
			     case "profileInfo":
			 		findProfileInfo(response,userType,user.getEmail());
			 		break;
			     case "openOngoingInternships": //open all the ongoing internship and basic infos
			    	 getOngoingInternships(response,user.getEmail());
			    	break;
			     case "filteredInternships": //when the company search on ongoing internship a student
			 		String x = StringEscapeUtils.escapeJava(request.getParameter("condition"));
			 		findFilteredInternships(response,x,user);
			 		break;
			      default: 
			       response.setStatus(HttpServletResponse.SC_NOT_FOUND); 
			    }			
		}
	}

	/**
	 * Find all the company ongoing internships
	 * @param email company
	 * @throws IOException
	 */
	private void getOngoingInternships(HttpServletResponse response, String email) throws IOException {
		InternshipDAO intern = new InternshipDAO(connection);
		List<Match> matches = new ArrayList<>();
		try {
			matches = intern.getOngoingInternships(email);
			String internString = new Gson().toJson(matches);
			
			response.setContentType("application/json");
			response.getWriter().write(internString);   
	       
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Error finding ongoing internships, retry later");
			return;
		}
	}

	/**
	 * Find all the info about a user
	 * @param userType, student or company
	 * @param email user
	 * @throws IOException
	 */
	private void findProfileInfo(HttpServletResponse response, String userType, String email) throws IOException {
		StudentDAO student = null;
		CompanyDAO company = null;
		Student st = null;
		Company cm = null;
		String combinedJson = null;
        
        
		if(userType.equalsIgnoreCase("student")) {
			String stInfo = null;
			String internInfoOnGoing = null;
			String internInfoFeedback = null;
			student = new StudentDAO(connection);
			try { //find student info
				st = student.getProfileInfos(userType,email);
				
				//manage cv info
				if(st != null && st.getCv() != null) {
					File f = new File(st.getCv());
					if(f.exists()) {
						byte[] fileContent = new byte[(int) f.length()];
						try (FileInputStream fileInputStream = new FileInputStream(f)) {
				            fileInputStream.read(fileContent);
				        }
						st.setCv(Base64.getEncoder().encodeToString(fileContent));
					}
				}
				
				stInfo = new Gson().toJson(st);
		       
			} catch (SQLException e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println("Error finding profile infos, retry later");
				return;	
			}
			
			InternshipDAO intern = new InternshipDAO(connection);
			
			Match m = null;
			ArrayList<Match> matches = new ArrayList<Match> ();
			try { //find the ongoing internship
				m = intern.getOngoingInternship(email);
				internInfoOnGoing = new Gson().toJson(m);
				
				matches = student.getMatchWaitingFeedback(email);
				internInfoFeedback = new Gson().toJson(matches);
				
				combinedJson = "[" + stInfo + "," + internInfoOnGoing + "," + internInfoFeedback + "]";
				response.getWriter().write(combinedJson);   
				
			    response.setStatus(HttpServletResponse.SC_OK);
			} catch (SQLException e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println("Error finding ongoing internship, retry later");
				return;
			}
		}else { //company
			company = new CompanyDAO(connection);
			String infoCompany = null;
			try { //company info
				cm = company.getProfileInfos(userType,email);
				infoCompany = new Gson().toJson(cm);
			    
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println("Error finding profile infos, retry later");
				return;
			}
			//find all the feedbacks the student have made to that company
			List<Feedback> feedbacks = new ArrayList<>();
			String feedbacksCompany = null;
			try {
				feedbacks = company.getFeedbacks(email);
				infoCompany = new Gson().toJson(cm);
				feedbacksCompany = new Gson().toJson(feedbacks);
				combinedJson = "[" + infoCompany + "," + feedbacksCompany + "]";
				response.getWriter().write(combinedJson);       
			    response.setStatus(HttpServletResponse.SC_OK);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println("Error finding feedbacks infos, retry later");
				return;
			}
		}
		
		
	}

	/**
	 * Find all student matches and their infos
	 * @param emailStudent
	 * @throws IOException
	 */
	private void showAllStudentMatches(HttpServletResponse response,String emailStudent) throws IOException {
		MatchDAO match = new MatchDAO(connection);
		List<Match> matches = null;
		StudentDAO student = new StudentDAO(connection);
		List<Publication> publications = new ArrayList<>();
		
		 try { //search all his publications
			publications = student.findStudentPublications(emailStudent);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Error finding publication, retry later");
			return;
		}
		 
		 if(publications != null) { //at least one publication
			 try {//can find the matches 
					matches = match.findStudentMatches(emailStudent);
					
					String pubsString = new Gson().toJson(matches);
					
			       response.setContentType("application/json");
			       response.getWriter().write(pubsString);   
			       response.setStatus(HttpServletResponse.SC_OK);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					response.getWriter().println("Error finding matches, retry later");
					return;
				}
		 }
	}

	/**
	 * Return all the working preferences of a student
	 * @param email student
	 * @throws IOException
	 */
	private void retrieveAllWP(HttpServletResponse response, String email) throws IOException {
		PublicationDAO pub = new PublicationDAO(connection);
		List<Publication> pubs = null;
		
		 try {
			pubs = pub.retrieveAllWP(email);
			String pubsString = new Gson().toJson(pubs);
	       response.setContentType("application/json");
	       response.getWriter().write(pubsString);       
	       response.setStatus(HttpServletResponse.SC_OK);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal db error, retry later");
			return;
		}
		
	}

	/**
	 * 
	 * @param nameToSearch, student/company based on the current usertype
	 * @param user (student or company)
	 * @throws IOException
	 */
	private void findFilteredInternships(HttpServletResponse response, String nameToSearch,User user) throws IOException {
		
		if(user.getWhichUser().equalsIgnoreCase("student")) {
			CompanyDAO company = new CompanyDAO(connection);
			List<Internship> internships = null;
			
			 try {//nametosearch = company name the student want to filter on
				internships = company.searchAvailableInternships(nameToSearch, user.getEmail());
				String internshipString = new Gson().toJson(internships);
				
			       response.setContentType("application/json");
			       response.getWriter().write(internshipString);       
			       response.setStatus(HttpServletResponse.SC_OK);
			} catch (SQLException e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println("Internal server error while searching ongoing internships, retry later");
				return;
			}
		}else { //company
			InternshipDAO intern = new InternshipDAO(connection);
			List<Match> matches = new ArrayList<>();
			try {//nametosearch = student name the company want to filter on
				matches = intern.getFilteredOngoingInternships(user.getEmail(), nameToSearch);
				String internString = new Gson().toJson(matches);
				
				response.setContentType("application/json");
				response.getWriter().write(internString);   
				response.setStatus(HttpServletResponse.SC_OK);
			} catch (SQLException e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println("Internal server error while searching ongoing internships, retry later");
				return;
			}
		}
		
		
	}
/*
	private List<Publication> findStudentPublications(HttpServletResponse response, String email) throws IOException {
		// TODO Auto-generated method stub
		StudentDAO student = new StudentDAO(connection);
		List<Publication> publications = null;
		
		 try {
			publications = student.findStudentPublications(email);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error, retry later");
			return null;
		}
		return publications;
	}
*/
	/**
	 * Create a new match for a student with a company
	 * @param internID, the internship ID to create a match with
	 * @param pubID, publication ID that the student want to match with that internship
	 * @throws IOException
	 */
	private void createMatchStudent(HttpServletResponse response,int internID,int pubID) throws IOException {
		MatchDAO match = new MatchDAO(connection);
		
		try {
			match.createMatchFromStudent(pubID, internID);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error while creating match for student, retry later");
		}
	}

	/**
	 * Find the info of an internship
	 * @param user
	 * @throws IOException
	 * 
	 */
	private void findInternshipInfo(HttpServletRequest request,HttpServletResponse response,User user) throws IOException {
		
		CompanyDAO company = null;
		Internship internship = null;
		company = new CompanyDAO(connection);
		MatchDAO matchDAO = new MatchDAO(connection);
		Match match = null;
		
		 try {
			 //input control
			if( request.getParameter("ID") != null) {
			
				internship = company.findTheInternship(Integer.parseInt(request.getParameter("ID")));
				
				String internshipString = new Gson().toJson(internship);
			    response.setContentType("application/json");
			    response.getWriter().write(internshipString);       
			    response.setStatus(HttpServletResponse.SC_OK);
			}else {
				if(request.getParameter("IDMatch") ==  null) {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					response.getWriter().println("missing values");
					return;
				}
				
				//control match ownership 
				Integer idmatch = Integer.parseInt(request.getParameter("IDMatch"));
				try {
					Boolean YN =matchDAO.controlOwnership(user.getEmail(),idmatch,user.getWhichUser() );

					if(!YN) {
						response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
						response.getWriter().println("You don't have this match.");
						return;
					}
				} catch (SQLException e) {
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					response.getWriter().println("Error controlling ownership, retry later");
					return;
				}
				
				match = company.getMatchInternshipInfo(idmatch);
				
				String internshipString = new Gson().toJson(match);
				response.setContentType("application/json");
			    response.getWriter().write(internshipString);       
			    response.setStatus(HttpServletResponse.SC_OK);
			}
			
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server errors, retry later");
			return;
		}
	}

	/**
	 * Find the available internships for a student
	 * @param emailStudent
	 * @throws IOException
	 */
	private void findPossibleInternships(HttpServletResponse response,String emailStudent) throws IOException {
		CompanyDAO company = null;
		List<Internship> internships = null;
		company = new CompanyDAO(connection);

		 try {
				internships = company.searchAllInternships(emailStudent);
				
				String internshipsString = new Gson().toJson(internships);
			       response.setContentType("application/json");
			       response.getWriter().write(internshipsString);
			       response.setStatus(HttpServletResponse.SC_OK);	
			} catch (SQLException e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println("Internal error when finding available internships");
				return;
			}   
	}
}
