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
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		HttpSession s = request.getSession();
		
		if (s.getAttribute("user") == null) {
			response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
			response.getWriter().println("Utente non trovato..");
			request.getSession(false).invalidate();
			return;
        }

		String userType = (String) s.getAttribute("userType");
		
		User user = (User) request.getSession().getAttribute("user");
		if(userType.equalsIgnoreCase("student")) { //we want to use student profile -> search company publications
			if(request.getParameter("page") == null)
				return;
			
			//the internship exists, now need to find the correspond student's publication
			 switch (request.getParameter("page").toString()) { //uso lo switch per capire quale azione dobbiamo fare in questa servlet
			 	case "toHomepage":
			 		findPossibleInternships(response,user.getEmail());
			 		break;
			 	case "internshipInfo": //when the page need to open one internship
			 		findInternshipInfo(request,response);
			 		break;
			 	case "openPubAndWP": //when the student click the button apply, so need all the working preferences
			 		retrieveAllWP(response, user.getEmail());
			 		break;
			 	case "addInternshipThenHomepage": //student want to apply to the internship
			 		Integer ID = Integer.parseInt(request.getParameter("IDintern"));
			 		CompanyDAO company = null;
					Internship internship = null;
					company = new CompanyDAO(connection);
					
					try {
						internship = company.findTheInternship(ID);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
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
						pub = student.findStudentPublication(user.getEmail(), Integer.parseInt(request.getParameter("IDpub")));
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
						response.getWriter().println("Publication not found, retry later");
						return;
					}
					if(pub == null) { //the internship searched doesn't exist
						response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
						response.getWriter().println("The user doesn't have publication");
						return;
					}
					//find the 
					//now we can create the match 
			 		createMatchStudent(response,internship.getId(),pub.getId());
			 		
			 		//now need to return to homepage, so return 
			 		findPossibleInternships(response,user.getEmail());
			 		break;
			 	case "showMatches":
			 		showAllStudentMatches(response,user.getEmail());
			 		break;
			 	case "filteredInternships": //when the student search internship of a x company
			 		String x = StringEscapeUtils.escapeJava(request.getParameter("condition"));
			 		findFilteredInternships(response,x,user.getEmail());
			 		break;
			 	case "profileInfo":
			 		findProfileInfo(response,userType,user.getEmail());
			 		break;
			 		default:
			 			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			 } 
		}else {
			if(request.getParameter("page") == null) 
			    return; 
			    
			   //the internship exists, now need to find the correspond student's publication 
			    switch (request.getParameter("page").toString()) { //uso lo switch per capire quale azione dobbiamo fare in questa servlet 
			     case "internshipInfo": //when the page need to open one internship 
			      findInternshipInfo(request,response); 
			      break; 
			     case "profileInfo":
			 		findProfileInfo(response,userType,user.getEmail());
			 		break;
			     case "openOngoingInternships":
			    	 getOngoingInternships(response,user.getEmail());
			    	break;
			      default: 
			       response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); 
			    }			
		}
	}

	//for company
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
			try {
				st = student.getProfileInfos(userType,email);
				
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
				// TODO Auto-generated catch block
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println("Error finding profile infos, retry later");
				return;	
			}
			
			InternshipDAO intern = new InternshipDAO(connection);
			
			Match m = null;
			ArrayList<Match> matches = new ArrayList<Match> ();
			try {
				m = intern.getOngoingInternship(email);
				internInfoOnGoing = new Gson().toJson(m);
				
				matches = student.getMatchWaitingFeedback(email);
				internInfoFeedback = new Gson().toJson(matches);
				
				combinedJson = "[" + stInfo + "," + internInfoOnGoing + "," + internInfoFeedback + "]";
				response.getWriter().write(combinedJson);   
				
			    response.setStatus(HttpServletResponse.SC_OK);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println("Error finding ongoing internship, retry later");
				return;
			}
		}else {
			company = new CompanyDAO(connection);
			String infoCompany = null;
			try {
				cm = company.getProfileInfos(userType,email);
				infoCompany = new Gson().toJson(cm);
			    
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println("Error finding profile infos, retry later");
				return;
			}
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

	private void showAllStudentMatches(HttpServletResponse response,String emailStudent) throws IOException {
		MatchDAO match = new MatchDAO(connection);
		List<Match> matches = null;
		StudentDAO student = new StudentDAO(connection);
		List<Publication> publications = new ArrayList<>();
		 try {
			publications = student.findStudentPublications(emailStudent);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Error finding publication, retry later");
			return;
		}
		 if(publications != null) {
			 try {
					matches = match.findStudentMatches(emailStudent);
					
					String pubsString = new Gson().toJson(matches);
					
				    // Imposta il tipo di contenuto e invia la risposta
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

	private void retrieveAllWP(HttpServletResponse response, String email) throws IOException {
		// TODO Auto-generated method stub
		PublicationDAO pub = new PublicationDAO(connection);
		List<Publication> pubs = null;
		
		 try {
			pubs = pub.retrieveAllWP(email);
			String pubsString = new Gson().toJson(pubs);
		    // Imposta il tipo di contenuto e invia la risposta
	       response.setContentType("application/json");
	       response.getWriter().write(pubsString);       
	       response.setStatus(HttpServletResponse.SC_OK);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal db error, retry later");
			return;
		}
		
	}

	private void findFilteredInternships(HttpServletResponse response, String nameCompany,String emailStudent) throws IOException {
		// TODO Auto-generated method stub
		//questo metodo deve semplicemente cercare tutte le internshipDAO disponibile dell'azienda nameCompany
		
		CompanyDAO company = new CompanyDAO(connection);
		List<Internship> internships = null;
		
		 try {
			internships = company.searchAvailableInternships(nameCompany, emailStudent);
			String internshipString = new Gson().toJson(internships);
			
		    // Imposta il tipo di contenuto e invia la risposta
		       response.setContentType("application/json");
		       response.getWriter().write(internshipString);       
		       response.setStatus(HttpServletResponse.SC_OK);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error while searching internships, retry later");
		}
		
	}

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

	private void createMatchStudent(HttpServletResponse response,int internID,int pubID) throws IOException {
		//creare il match tra internship ID e l'utente 
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
		Match match = null;
		
		 try {
			 
			if( request.getParameter("ID") != null) {
				internship = company.findTheInternship(Integer.parseInt(request.getParameter("ID")));
				String internshipString = new Gson().toJson(internship);
			    // Imposta il tipo di contenuto e invia la risposta
			    response.setContentType("application/json");
			    response.getWriter().write(internshipString);       
			    response.setStatus(HttpServletResponse.SC_OK);
			}else {
				match = company.getMatchInternshipInfo(Integer.parseInt(request.getParameter("IDMatch")));
				String internshipString = new Gson().toJson(match);
			    // Imposta il tipo di contenuto e invia la risposta
			    response.setContentType("application/json");
			    response.getWriter().write(internshipString);       
			    response.setStatus(HttpServletResponse.SC_OK);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server errors, retry later");
		}
	}

	private void findPossibleInternships(HttpServletResponse response,String emailStudent) throws IOException {
		CompanyDAO company = null;
		List<Internship> internships = null;
		company = new CompanyDAO(connection);
		//find the publication of the user
		 try {
				internships = company.searchAllInternships(emailStudent);
				String internshipsString = new Gson().toJson(internships);
			    // Imposta il tipo di contenuto e invia la risposta
			       response.setContentType("application/json");
			       response.getWriter().write(internshipsString);
			       response.setStatus(HttpServletResponse.SC_OK);	
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println("Internal");
				return;
			}   
	}

}
