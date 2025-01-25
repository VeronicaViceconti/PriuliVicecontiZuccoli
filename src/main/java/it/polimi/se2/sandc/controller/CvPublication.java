package it.polimi.se2.sandc.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
import it.polimi.se2.sandc.dao.StudentDAO;

import it.polimi.se2.sandc.bean.User;

/**
 * Servlet implementation class CvPublication
 */
@WebServlet("/CvPublication")
@MultipartConfig
public class CvPublication extends HttpServlet {
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
    public CvPublication() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
		
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		System.out.println("saving the pdf");
		HttpSession session = request.getSession();
		String email = 	((User) session.getAttribute("user")).getEmail();	
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

}
