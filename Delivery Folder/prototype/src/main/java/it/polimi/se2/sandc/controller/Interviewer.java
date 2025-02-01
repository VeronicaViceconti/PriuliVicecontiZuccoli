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

import com.google.gson.Gson;

import it.polimi.se2.sandc.bean.Interview;
import it.polimi.se2.sandc.bean.Question;
import it.polimi.se2.sandc.bean.User;
import it.polimi.se2.sandc.dao.CompanyDAO;
import it.polimi.se2.sandc.dao.InterviewDAO;
import it.polimi.se2.sandc.dao.MatchDAO;
import it.polimi.se2.sandc.dao.PreferenceDAO;

/**
 * Servlet implementation class Interviewer, manages the interview that a company does to the students
 */
@WebServlet("/Interviewer")
@MultipartConfig
public class Interviewer extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private Connection connection = null;   

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
    public Interviewer() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession s = request.getSession();
		
		if (s.getAttribute("user") == null) {
			response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
			response.getWriter().println("Utente non trovato..");
			request.getSession(false).invalidate();
			return;
        }
		String email =  ((User) s.getAttribute("user")).getEmail();
		String userType = (String) s.getAttribute("userType");
		
		if(userType.equals("student")) { //student can't use interview manager
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().println("the user isn't authorized");
		}else { //company
			//different actions based on the company current page
			 switch (request.getParameter("page")) {  
			 	case "requestForm":
			 		createInterview(email, request, response);
			 		break;
			 	case "submitSelection":
			 		submitSelection(email, request, response);
			 		break;
			 	case "getResponse":
			 		getAnswers(email, request, response);
			 		break;
			 	case "getInterviewInfo":
			 		Integer id = Integer.parseInt(request.getParameter("ID"));
			 		selectInterview(response,id);
			 		break;
			 	default:
			 		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			 		response.getWriter().println("page not found");
			 		break;
			 } 
		}
		
	}

    /**
     * Return the info about a particular interview
     * @param id, interview id to be searched
     * @throws IOException
     */
	private void selectInterview(HttpServletResponse response,Integer id) throws IOException {
		
		InterviewDAO interviewDAO = new InterviewDAO(connection);
		Interview interview = null;
		try {
			interview = interviewDAO.selectInterview(id);
			String result = new Gson().toJson(interview);
			response.setContentType("application/json");
			response.getWriter().write(result);
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("No interview found!");
			return;
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession s = request.getSession();
		
		String email =  ((User) s.getAttribute("user")).getEmail();
		String userType = (String) s.getAttribute("userType");
		
		if(userType.equals("student")) { //student can't be here
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().println("the user isn't authorized");
		}else { //company
			//different actions based on the company current page
			 switch (request.getParameter("page").toString()) { 
			 	case "submitInterview":
			 		submitInterview(email, request, response);
			 		break;
			 	case "submitSelection":
			 		submitSelection(email, request, response);
			 		break;
			 	default:
			 		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			 		response.getWriter().println("page not found");
			 		break;
			 } 
		}
	}
	
	/**
	 * Creates a new interview 
	 * @param email of the company who wants to create an interview
	 * @throws IOException
	 */
	private void createInterview(String email, HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		MatchDAO matchdao = new MatchDAO(connection);
		HttpSession session = request.getSession();	
		User user = (User) session.getAttribute("user");
		
		int idMatch;
		//control an idMatch is passed as parameter
		try {
			if(request.getParameter("match") == null) {
				throw new Exception();
			}
			idMatch = Integer.parseInt(request.getParameter("match"));
		}catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("the match is missing");
			return;
		}
		
		//control the company has that match
		try {
			if(!matchdao.controlOwnership(email, idMatch, user.getWhichUser())) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().println("the company it isn't in the match");
				return;
			}
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("db problem");
			return;
		}
		
		//create an interview for that match
		try {
			Interview interview = matchdao.createInterview(idMatch);
			//if the student of that match has already accepted another interview, so already has another ongoing internship
			if(interview == null) {
				response.getWriter().write("the Student is no available for an interview, retry later");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			String result = new Gson().toJson(interview);
			response.setContentType("application/json");
			response.getWriter().write(result);
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "db problem cannot create the interview");
			return;
		}
	}
	
	/**
	 * Submit the new interview just made
	 * @param email of the company that is submitting the interview
	 * @throws IOException
	 */
	private void submitInterview(String email, HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		InterviewDAO interviewdao = new InterviewDAO(connection);
		
		int idInterview;
		try {
			if(request.getParameter("interview") == null) {
				throw new Exception();
			}
			idInterview = Integer.parseInt(request.getParameter("interview"));
		}catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("the interview is missing");
			return;
		}
		
		//check that the user owns the internship of the interview
		try {
			if(!interviewdao.checkOwnerShip(email, idInterview)) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().println("the company doesn't own the intership relative to the interview");
				return;
			}
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("db problem");
			return;
		}
		
		ArrayList<Question> questions = new ArrayList<Question>();
		
		try {
			questions = interviewdao.getQuestions(idInterview);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("db problem");
			return;
		}
		
		for(Question q : questions) {
			q.setAnswer(StringEscapeUtils.escapeJava(request.getParameter(String.valueOf(q.getId()))));
		}
		
		//add all the answers to the interview
		for(Question q : questions) {
			try {
				interviewdao.setAnswer(q);
			} catch (SQLException e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println("db problem");
				return;
			}
		}
		
		response.setStatus(HttpServletResponse.SC_OK);
	}
	
	/**
	 * The company has accepted or denied the interview, this method update the db based on the company response
	 * @param email of the company 
	 * @throws IOException
	 */
	private void submitSelection(String email, HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		InterviewDAO interviewdao = new InterviewDAO(connection);		
		
		int idInterview, selected;
		try {
			if(request.getParameter("interview") == null || request.getParameter("selected") == null) {
				throw new Exception();
			}
			idInterview = Integer.parseInt(request.getParameter("interview"));
			selected = Integer.parseInt(request.getParameter("selected"));
			
		}catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("the match is missing");
			return;
		}
	
		
		try {
			//controll the company has that interview
			if(!interviewdao.checkOwnerShip(email, idInterview)) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().println("the company doesn't owns the intership relative to the interview");
				return;
			}
			//update db
			interviewdao.acceptDeclineInterview(idInterview, (selected >0));
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("db problem");
			return;
		}
		response.setStatus(HttpServletResponse.SC_OK);
	}
	
	/**
	 * Find all the answer of a particular interview 
	 * @param email company
	 * @throws IOException
	 */
	private void getAnswers(String email, HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		MatchDAO matchdao = new MatchDAO(connection);
		HttpSession session = request.getSession();	
		User user = (User) session.getAttribute("user");
		
		int idMatch;
		try {
			if(request.getParameter("match") == null) {
				throw new Exception();
			}
			idMatch = Integer.parseInt(request.getParameter("match"));
		}catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("the match is missing");
			return;
		}
		
		//control the company has that match
		try {
			if(!matchdao.controlOwnership(email, idMatch, user.getWhichUser())) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().println("the company it isn't in the match");
				return;
			}
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("db problem");
			return;
		}
		
		//return interview answers 
		try {
			Interview interview = matchdao.getAnswers(idMatch);
			if(interview == null) {
				response.getWriter().write("the student hasn't done the interview");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			String result = new Gson().toJson(interview);
			response.setContentType("application/json");
			response.getWriter().write(result);
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "db problem cannot create the interview");
			return;
		}
	}
	
}
