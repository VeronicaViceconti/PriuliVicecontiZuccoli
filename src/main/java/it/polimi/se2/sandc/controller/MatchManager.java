package it.polimi.se2.sandc.controller;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
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

import com.google.gson.Gson;

import it.polimi.se2.sandc.bean.Internship;
import it.polimi.se2.sandc.bean.Match;
import it.polimi.se2.sandc.bean.Publication;
import it.polimi.se2.sandc.bean.User;
import it.polimi.se2.sandc.dao.CompanyDAO;
import it.polimi.se2.sandc.dao.MatchDAO;
import it.polimi.se2.sandc.dao.StudentDAO;

/**
 * Servlet implementation class MatchManager
 */
@WebServlet("/MatchManager")
@MultipartConfig
public class MatchManager extends HttpServlet {
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
	
	public void init(Connection connection) {
		this.connection = connection;
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
			 	case "acceptMatch": //when the page need to open one internship
			 		acceptMatch(response,Integer.parseInt(request.getParameter("IDmatch")),user.getEmail(),userType,Integer.parseInt(request.getParameter("accept")));
			 		break;
			 		default:
			 			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); //401
			 } 
		}else { //we want to use company
			switch (request.getParameter("page").toString()) { //uso lo switch per capire quale azione dobbiamo fare in questa servlet
		 	case "showMatches": //when the page need to open one internship
		 		showAllCompanyMatches(response,user.getEmail());
		 		break;
		 	case "acceptMatch": //when the page need to open one internship
		 		acceptMatch(response,Integer.parseInt(request.getParameter("IDmatch")),user.getEmail(),userType,Integer.parseInt(request.getParameter("accept")));
		 		break;
		 	case "openMatch":
		 		Integer idMatch = Integer.parseInt(request.getParameter("IDmatch"));
		 		openMatch(response,idMatch);
		 		break;
		 		default:
		 			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		 } 
		}
	}

	private void openMatch(HttpServletResponse response, Integer idMatch) throws IOException {
		StudentDAO studentdao = new StudentDAO(connection);
		Publication pub = null;
		
		try {
			pub = studentdao.getProfileAndPubPreferences(idMatch);
			
			if(pub.getStudent().getCv() != null) {
				File f = new File(pub.getStudent().getCv());
				if(f.exists()) {
					byte[] fileContent = new byte[(int) f.length()];
					try (FileInputStream fileInputStream = new FileInputStream(f)) {
			            fileInputStream.read(fileContent);
			        }
					pub.getStudent().setCv(Base64.getEncoder().encodeToString(fileContent));
				}
			}
			String pubString = new Gson().toJson(pub);
			
			// Imposta il tipo di contenuto e invia la risposta
	       response.setContentType("application/json");
	       response.getWriter().write(pubString);       
	       response.setStatus(HttpServletResponse.SC_OK);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Error opening infos match, retry later");
			return;
		}
		
	}

	private void acceptMatch(HttpServletResponse response, int matchID, String email,String userType,int acceptedOrNot) throws IOException {
		
		MatchDAO match = new MatchDAO(connection);
		//controll that the student has that match
		try {
			Boolean YN = match.controlOwnership(email,matchID,userType);
			if(!YN) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().println("You don't have this match.");
				return;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Error controlling ownership, retry later");
			return;
		}
		
		try {
			if(acceptedOrNot == 1)
				match.updateMatchAccepted(matchID,userType,acceptedOrNot);
			else
				match.deleteMatch(matchID);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Error accepting matches, retry later");
			return;
		}
	}

	private void showAllCompanyMatches(HttpServletResponse response, String email) throws IOException {
		MatchDAO match = new MatchDAO(connection);
		List<Match> matches = null;

		 try {
				matches = match.findCompanyMatches(email);
				String matchString = new Gson().toJson(matches);
				
				// Imposta il tipo di contenuto e invia la risposta
		       response.setContentType("application/json");
		       response.getWriter().write(matchString);       
		       response.setStatus(HttpServletResponse.SC_OK);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println("Error finding matches, retry later");
				return;
			}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
