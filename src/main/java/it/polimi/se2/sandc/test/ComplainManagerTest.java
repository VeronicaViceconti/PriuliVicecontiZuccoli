package it.polimi.se2.sandc.test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.gson.Gson;

import it.polimi.se2.sandc.bean.Company;
import it.polimi.se2.sandc.bean.Internship;
import it.polimi.se2.sandc.bean.Preferences;
import it.polimi.se2.sandc.bean.User;
import it.polimi.se2.sandc.controller.ComplainManager;


class ComplainManagerTest {
	@Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;
    
    @InjectMocks
    private ComplainManager complainManager;
    
    private StringWriter stringWriter;
    private PrintWriter writer;
    private Connection connection;
    
    
	@BeforeEach
	public void setUp() throws Exception {
		
		MockitoAnnotations.initMocks(this);
		// Imposta il PrintWriter per catturare l'output della risposta
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
        complainManager = new ComplainManager();
        
       
		String driver = "com.mysql.cj.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/testsandc?serverTimezone=UTC";
		String user = "Elia";
		String password = "Elia";
		Class.forName(driver);
		connection = DriverManager.getConnection(url, user, password);
        
		complainManager.init(connection);
        try {
			when(response.getWriter()).thenReturn(writer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        when(request.getSession()).thenReturn(session);
        insertData();
	}
	
	private void insertData() throws SQLException {
    	
    	Statement statement = connection.createStatement();
    	
    	String query = "delete from student";
    	statement.executeUpdate(query);
    	
    	query = "delete from company";
    	statement.executeUpdate(query);
    	
    	query = "delete from workingpreferences";
    	statement.executeUpdate(query);
    	
    	query = "delete from publication";
    	statement.executeUpdate(query);
    	
    	query = "delete from preference";
    	statement.executeUpdate(query);
    	
    	query = "delete from internship";
    	statement.executeUpdate(query);
    	
    	query = "delete from matches";
    	statement.executeUpdate(query);
    	
    	query = "delete from requirement";
    	statement.executeUpdate(query);
    	
    	query = "delete from interview";
    	statement.executeUpdate(query);
    	
    	query = "delete from form";
    	statement.executeUpdate(query);
    	
    	query = "delete from complaint";
    	statement.executeUpdate(query);
    	
    	query = "insert into Student (email, name, address, phoneNumber, psw, studyCourse) values ('user@mail.com', 'user1', 'via tal dei tali', '1234567890', 'user1', 'computer science')";
    	statement.executeUpdate(query);
    
    	query = "insert into company (email, name, address, phoneNumber, psw) values ('company@mail.com', 'company', 'via tal dei tali', '1234567890', 'company')";
    	statement.executeUpdate(query);
    	
    	query = "insert into workingpreferences values (1, 'ai'), (2, 'robotic'), (3, 'db'),(4, 'embedded systems'),(5, 'web developement'),(6, 'computer security'), (7, 'database')";
    	statement.executeUpdate(query);
    	
    	query = "insert into publication values (1, 'user@mail.com')";
		statement.executeUpdate(query);
		
		query = "insert into internship values (1, 'company@mail.com', 'software engeneering', 2, '2025-06-01', '2025-06-21', 'job description')";
		statement.executeUpdate(query);
		
		query = "insert into matches values (1, 1, 1, 1, 1)";
		statement.executeUpdate(query);

		query = "insert into form values (1)";
		statement.executeUpdate(query);
	
		query = "insert into interview values (1, '2025-01-01', 1, 1, 1)";
		statement.executeUpdate(query);
		
    	statement.close();
    }

	@Test
	public void postComplainStudentSuccess() throws ServletException, IOException, SQLException {
		User user = new User();
		
		//create the fake session user
		user.setEmail("user@mail.com");
		user.setName("user1");
		user.setWhichUser("student");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("student");
		
		when(request.getParameter("answer")).thenReturn("answer text");
		when(request.getParameter("idMatch")).thenReturn("1");
		
		
		complainManager.doPost(request, response);
		
		verify(response).setStatus(HttpServletResponse.SC_OK);
        
		Statement statement = connection.createStatement();
		
		String query = "select * from  complaint where studentID ='user@mail.com' and companyID = 'company@mail.com' and studentYn = 1";
		
		statement.execute(query);
		
		ResultSet tmp = statement.getResultSet();
		
		assertTrue(tmp.isBeforeFirst());
		tmp.next();
		
		int idForm = tmp.getInt("idForm");
		
		query = "select * from question where answer = 'answer text' and idForm = " + idForm;
		statement.execute(query);
		
		tmp = statement.getResultSet();
		assertTrue(tmp.isBeforeFirst());
	}
	
	@Test
	public void postComplainStudentFailure() throws ServletException, IOException, SQLException {
		User user = new User();
		
		//create the fake session user
		user.setEmail("user2@mail.com");
		user.setName("user2");
		user.setWhichUser("student");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("student");
		
		when(request.getParameter("answer")).thenReturn("answer text");
		when(request.getParameter("idMatch")).thenReturn("1");
		
		Statement statement = connection.createStatement();
		
		String query = "insert into Student (email, name, address, phoneNumber, psw) values ('user2@mail.com', 'user2', 'via tal dei tali', '1234567890', 'user1')";
    	statement.executeUpdate(query);
		
		complainManager.doPost(request, response);
    	verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    	
	}
	
	@Test
	public void postComplainCompanySuccess() throws ServletException, IOException, SQLException {
		User user = new User();
		
		//create the fake session user
		user.setEmail("company@mail.com");
		user.setName("company");
		user.setWhichUser("company");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("company");
		
		when(request.getParameter("answer")).thenReturn("answer text");
		when(request.getParameter("idMatch")).thenReturn("1");
		
		
		complainManager.doPost(request, response);
		
		verify(response).setStatus(HttpServletResponse.SC_OK);
        
		Statement statement = connection.createStatement();
		
		String query = "select * from  complaint where studentID ='user@mail.com' and companyID = 'company@mail.com' and studentYn = 0";
		
		statement.execute(query);
		
		ResultSet tmp = statement.getResultSet();
		
		assertTrue(tmp.isBeforeFirst());
		tmp.next();
		
		int idForm = tmp.getInt("idForm");
		
		query = "select * from question where answer = 'answer text' and idForm = " + idForm;
		statement.execute(query);
		
		tmp = statement.getResultSet();
		assertTrue(tmp.isBeforeFirst());
	}

	
	@Test
	public void postComplainCompanyFailure() throws ServletException, IOException, SQLException {
		User user = new User();
		
		//create the fake session user
		user.setEmail("user2@mail.com");
		user.setName("user2");
		user.setWhichUser("company");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("company");
		
		when(request.getParameter("answer")).thenReturn("answer text");
		when(request.getParameter("idMatch")).thenReturn("1");
		
		Statement statement = connection.createStatement();
		
		String query = "insert into company (email, name, address, phoneNumber, psw) values ('user2@mail.com', 'user2', 'via tal dei tali', '1234567890', 'user1')";
    	statement.executeUpdate(query);
		
		complainManager.doPost(request, response);
    	verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    	
	}
}
