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
import it.polimi.se2.sandc.dao.NotifDAO;
import it.polimi.se2.sandc.dao.StudentDAO;

/**
 * Servlet implementation class MatchManager, manages all the actions related to the company or student matches 
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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession s = request.getSession();
		String userType = (String) s.getAttribute("userType");
		User user = (User) request.getSession().getAttribute("user");

		switch (request.getParameter("page").toString()) {
		case "saveToken":
			/*
			 * System.out.println(user.getEmail());
			 * System.out.println(request.getParameter("token"));
			 * System.out.println(userType);
			 */

			if (request.getParameter("token") != null) {
				saveFCMToken(response, request.getParameter("token"), user.getEmail(), userType);
			} else {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("missing values");
				return;
			}
			break;
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession s = request.getSession();
		String userType = (String) s.getAttribute("userType");
		User user = (User) request.getSession().getAttribute("user");
		
		if(userType.equalsIgnoreCase("student")) { //if user is student
			
			 switch (request.getParameter("page").toString()) { 
			 	case "acceptMatch": //student accepted a match
			 		//control input
			 		if(request.getParameter("IDmatch") != null && request.getParameter("accept") != null) {
			 			//update match on db
			 			acceptMatch(response,Integer.parseInt(request.getParameter("IDmatch")),user.getEmail(),userType,Integer.parseInt(request.getParameter("accept")));
			 		}else {
			 			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
						response.getWriter().println("missing values");
						return;
			 		}
			 		break;
			 	default:
			 			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			 } 
		}else { //we want to use company
			switch (request.getParameter("page").toString()) { 
		 	case "showMatches": //show matches info
		 		showAllCompanyMatches(response,user.getEmail());
		 		break;
		 	case "acceptMatch": //company accept a match
		 		//input control
		 		if(request.getParameter("IDmatch") != null && request.getParameter("accept") != null) {
		 			//update match on db
		 			acceptMatch(response,Integer.parseInt(request.getParameter("IDmatch")),user.getEmail(),userType,Integer.parseInt(request.getParameter("accept")));
		 		}else {
		 			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					response.getWriter().println("missing values");
					return;
		 		}
		 		break;
		 	case "openMatch": //open match info
		 		if(request.getParameter("IDmatch") != null) {
		 			Integer idMatch = Integer.parseInt(request.getParameter("IDmatch"));
			 		openMatch(response,idMatch);
		 		}else {
		 			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					response.getWriter().println("missing values");
					return;
		 		}
		 		break;
		 	default:
		 		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		 		break;
			}
		}
	}

	/**
	 * Find all the information about the correlated student + main match infos
	 * @param idMatch to be opened
	 * @throws IOException
	 */
	private void openMatch(HttpServletResponse response, Integer idMatch) throws IOException {
		StudentDAO studentdao = new StudentDAO(connection);
		Publication pub = null;

		try {
			pub = studentdao.getProfileAndPubPreferences(idMatch);

			//manages the cv info 
			if(pub.getStudent().getCv() != null) {
				File f = new File(pub.getStudent().getCv());
				if (f.exists()) {
					byte[] fileContent = new byte[(int) f.length()];
					try (FileInputStream fileInputStream = new FileInputStream(f)) {
						fileInputStream.read(fileContent);
					}
					pub.getStudent().setCv(Base64.getEncoder().encodeToString(fileContent));
				}
			}
			String pubString = new Gson().toJson(pub);
	
	       response.setContentType("application/json");
	       response.getWriter().write(pubString);       
	       response.setStatus(HttpServletResponse.SC_OK);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Error opening infos match, retry later");
			return;
		}

	}


	/**
	 * 
	 * @param matchID to be accepted
	 * @param email of the user
	 * @param userType, string that indicate if the current user is student or company
	 * @param acceptedOrNot, 1 or 0
	 * @throws IOException
	 */
	private void acceptMatch(HttpServletResponse response, int matchID, String email,String userType,int acceptedOrNot) throws IOException {
		
		MatchDAO match = new MatchDAO(connection);
		//control that the student has that match
		try {
			Boolean YN = match.controlOwnership(email, matchID, userType);
			if (!YN) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().println("You don't have this match.");
				return;
			}
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Error controlling ownership, retry later");
			return;
		}

		try {
			//if the match is accepted, update the db 
			if(acceptedOrNot == 1)
				match.updateMatchAccepted(matchID,userType,acceptedOrNot);
			//if not accepted, the match is no more important
			else
				match.deleteMatch(matchID);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Error accepting matches, retry later");
			return;
		}
	}

	/**
	 * Show all the matches and all the info about them
	 * @param email company
	 * @throws IOException
	 */
	private void showAllCompanyMatches(HttpServletResponse response, String email) throws IOException {
		MatchDAO match = new MatchDAO(connection);
		List<Match> matches = null;

		try {
			matches = match.findCompanyMatches(email);
			String matchString = new Gson().toJson(matches);

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

	private void saveFCMToken(HttpServletResponse response, String token, String userEmail, String userType)
			throws IOException {
		NotifDAO notif = new NotifDAO(connection);

		try {
			notif.saveFCMToken(token, userEmail, userType);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Error: internal server error");
			return;
		}
	}
}
