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
import java.time.LocalDate;
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
import it.polimi.se2.sandc.bean.Interview;
import it.polimi.se2.sandc.bean.Match;
import it.polimi.se2.sandc.bean.Preferences;
import it.polimi.se2.sandc.bean.Publication;
import it.polimi.se2.sandc.bean.Question;
import it.polimi.se2.sandc.bean.User;
import it.polimi.se2.sandc.controller.Interviewer;


class InterviewerTest {
	@Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;
    
    @InjectMocks
    private Interviewer interviewer;
    
    private StringWriter stringWriter;
    private PrintWriter writer;
    private Connection connection;
    
    
	@BeforeEach
	public void setUp() throws Exception {
		
		MockitoAnnotations.initMocks(this);
		// Imposta il PrintWriter per catturare l'output della risposta
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
        interviewer = new Interviewer();
        
       
		String driver = "com.mysql.cj.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/testsandc?serverTimezone=UTC";
		String user = "Elia";
		String password = "Elia";
		Class.forName(driver);
		connection = DriverManager.getConnection(url, user, password);
        
		interviewer.init(connection);
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
    	
    	query = "delete from question";
    	statement.executeUpdate(query);
    	
    	query = "insert into Student (email, name, address, phoneNumber, psw, studyCourse) values ('user@mail.com', 'user1', 'via tal dei tali', '1234567890', 'user1', 'computer science'), ('user2@mail.com', 'user2', 'via tal dei tali', '1234567890', 'user2', 'computer science')";
    	statement.executeUpdate(query);
    
    	query = "insert into company (email, name, address, phoneNumber, psw) values ('company@mail.com', 'company', 'via tal dei tali', '1234567890', 'company')";
    	statement.executeUpdate(query);
    	
    	query = "insert into workingpreferences values (1, 'ai'), (2, 'robotic'), (3, 'db'),(4, 'embedded systems'),(5, 'web developement'),(6, 'computer security'), (7, 'database')";
    	statement.executeUpdate(query);
    	
    	query = "insert into publication values (1, 'user@mail.com'), (2, 'user2@mail.com')";
		statement.executeUpdate(query);
		
		query = "insert into internship values (1, 'company@mail.com', 'software engeneering', 2, '2025-01-01', '2025-01-02', 'job description')";
		statement.executeUpdate(query);
		
		query = "insert into matches (id, idPublication, idInternship) values (1, 1, 1)";
		statement.executeUpdate(query);
		
    	statement.close();
    }

	@Test
	void createInterviewTest() throws ServletException, IOException, SQLException {
		User user = new User();
		
		//create the fake session user
		user.setEmail("company@mail.com");
		user.setName("company");
		user.setWhichUser("company");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("company");
		

		when(request.getParameter("page")).thenReturn("requestForm");
		when(request.getParameter("match")).thenReturn("1");
		
		Statement statement = connection.createStatement();
		
		String query = "select * from interview where idMatch = 1 and dat = curdate()";
		statement.execute(query);
		
		assertFalse(statement.getResultSet().isBeforeFirst());
		
		interviewer.doGet(request, response);
		
		verify(response).setStatus(HttpServletResponse.SC_OK);
		verify(response).setContentType("application/json");
		
		Interview interview = new Gson().fromJson(stringWriter.getBuffer().toString().trim(), Interview.class);
		
		assertEquals(interview.getData(), Date.valueOf(LocalDate.now()));	
	
		query = "select * from interview where idMatch = 1 and dat = curdate()";
		statement.execute(query);
		
		assertTrue(statement.getResultSet().isBeforeFirst());
	}	
	
	@Test
	public void createInterviewTestMatchNull() throws ServletException, IOException, SQLException {
		User user = new User();
		
		//create the fake session user
		user.setEmail("company@mail.com");
		user.setName("company");
		user.setWhichUser("company");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("company");
		

		when(request.getParameter("page")).thenReturn("requestForm");
		
		Statement statement = connection.createStatement();
		
		interviewer.doGet(request, response);
		
		verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
	}	
	@Test
	public void createInterviewTestCompanyNotOwner() throws ServletException, IOException, SQLException {
		User user = new User();
		
		//create the fake session user
		user.setEmail("company@mail.com");
		user.setName("company");
		user.setWhichUser("company");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("company");

		when(request.getParameter("match")).thenReturn("2");
		when(request.getParameter("page")).thenReturn("requestForm");
		
		Statement statement = connection.createStatement();
		
		String query = "insert into company (email, name, address, phoneNumber, psw) values ('company2@mail.com', 'company2', 'via tal dei tali', '1234567890', 'company')";
    	statement.executeUpdate(query);
    	
    	query = "insert into internship values (2, 'company2@mail.com', 'software engeneering', 2, '2025-01-01', '2025-01-02', 'job description')";
		statement.executeUpdate(query);
		
		interviewer.doGet(request, response);
		
		verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}	
	
	@Test
	public void createInterviewTestStudentUnavailable() throws ServletException, IOException, SQLException {
		User user = new User();
		
		//create the fake session user
		user.setEmail("company@mail.com"); 	 	
		user.setName("company");
		user.setWhichUser("company");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("company");

		when(request.getParameter("match")).thenReturn("1");
		when(request.getParameter("page")).thenReturn("requestForm");
		
		Statement statement = connection.createStatement();
		
		String query = "insert into form values (1)";
    	statement.executeUpdate(query);
		
    	query = "insert into company (email, name, address, phoneNumber, psw) values ('company2@mail.com', 'company2', 'via tal dei tali', '1234567890', 'company')";
    	statement.executeUpdate(query);
    	
    	query = "insert into internship values (2, 'company2@mail.com', 'software engeneering', 2, '2025-01-01', '2025-01-02', 'job description')";
		statement.executeUpdate(query);
    
		query = "insert into matches (id, idPublication, idInternship) values (2, 1, 2)";
		statement.executeUpdate(query);
		
    	query = "insert into interview (id, dat, idMatch, idForm) values (2, curdate(), 2, 1)";
    	statement.executeUpdate(query);
    	
		interviewer.doGet(request, response);
		
		verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
	}	
	
	@Test
	public void submitInterviewTest() throws SQLException, ServletException, IOException {
	
		User user = new User();
		
		//create the fake session user
		user.setEmail("company@mail.com");
		user.setName("company");
		user.setWhichUser("company");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("company");
		

		when(request.getParameter("page")).thenReturn("submitInterview");
		when(request.getParameter("interview")).thenReturn("1");
		when(request.getParameter("1")).thenReturn("answer 1");
		when(request.getParameter("2")).thenReturn("answer 2");
		when(request.getParameter("3")).thenReturn("answer 3");
		
		Statement statement = connection.createStatement();
		
		String query = "insert into form values (1)";
		statement.executeUpdate(query);
		
		query = "insert into question (id, txt, idForm) values (1, 'question 1', 1), (2, 'question 2', 1), (3, 'question 3', 1)";
		statement.executeUpdate(query);
		
		query = "insert into interview (id, dat, idMatch, idForm) values (1, curdate(), 1, 1)";
		statement.executeUpdate(query);
		interviewer.doPost(request, response);
		
		verify(response).setStatus(HttpServletResponse.SC_OK);
		
		query = "select * from question where id = 1 and answer like 'answer 1'";
		statement.execute(query);
		assertTrue(statement.getResultSet().isBeforeFirst());
		
		query = "select * from question where id = 2 and answer like 'answer 2'";
		statement.execute(query);
		assertTrue(statement.getResultSet().isBeforeFirst());
		
		query = "select * from question where id = 3 and answer like 'answer 3'";
		statement.execute(query);
		assertTrue(statement.getResultSet().isBeforeFirst());
		
	}
	
	@Test
	public void submitInterviewTestCompanyNotOwner() throws SQLException, ServletException, IOException {
	
		User user = new User();
		
		//create the fake session user
		user.setEmail("company1@mail.com");
		user.setName("company");
		user.setWhichUser("company");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("company");
		

		when(request.getParameter("page")).thenReturn("submitInterview");
		when(request.getParameter("interview")).thenReturn("1");
		when(request.getParameter("1")).thenReturn("answer 1");
		when(request.getParameter("2")).thenReturn("answer 2");
		when(request.getParameter("3")).thenReturn("answer 3");
		
		Statement statement = connection.createStatement();
		
		String query = "insert into form values (1)";
		statement.executeUpdate(query);
		
		query = "insert into question (id, txt, idForm) values (1, 'question 1', 1), (2, 'question 2', 1), (3, 'question 3', 1)";
		statement.executeUpdate(query);
		
		query = "insert into interview (id, dat, idMatch, idForm) values (1, curdate(), 1, 1)";
		statement.executeUpdate(query);
		
		query = "insert into company (email, name, address, phoneNumber, psw) values ('company1@mail.com', 'company', 'via tal dei tali', '1234567890', 'company')";
    	statement.executeUpdate(query);
		
		interviewer.doPost(request, response);
	
		verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		
	}
	
	@Test
	public void submitInterviewTestInterviewNull() throws SQLException, ServletException, IOException {
	
		User user = new User();
		
		//create the fake session user
		user.setEmail("company@mail.com");
		user.setName("company");
		user.setWhichUser("company");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("company");
		

		when(request.getParameter("page")).thenReturn("submitInterview");
		when(request.getParameter("1")).thenReturn("answer 1");
		when(request.getParameter("2")).thenReturn("answer 2");
		when(request.getParameter("3")).thenReturn("answer 3");
		
		Statement statement = connection.createStatement();
		
		String query = "insert into form values (1)";
		statement.executeUpdate(query);
		
		query = "insert into question (id, txt, idForm) values (1, 'question 1', 1), (2, 'question 2', 1), (3, 'question 3', 1)";
		statement.executeUpdate(query);
		
		query = "insert into interview (id, dat, idMatch, idForm) values (1, curdate(), 1, 1)";
		statement.executeUpdate(query);
		
		interviewer.doPost(request, response);
	
		verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
		
	}
	

	@Test
	public void submitSelectionAccepted() throws SQLException, ServletException, IOException {
	
		User user = new User();
		
		//create the fake session user
		user.setEmail("company@mail.com");
		user.setName("company");
		user.setWhichUser("company");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("company");
		

		when(request.getParameter("page")).thenReturn("submitSelection");
		when(request.getParameter("interview")).thenReturn("1");
		when(request.getParameter("selected")).thenReturn("1");
		
		Statement statement = connection.createStatement();
		
		String query = "insert into form values (1)";
		statement.executeUpdate(query);
		
		query = "insert into interview (id, dat, idMatch, idForm) values (1, curdate(), 1, 1)";
		statement.executeUpdate(query);
		interviewer.doPost(request, response);
		
		verify(response).setStatus(HttpServletResponse.SC_OK);
		
		query = "select * from interview where id = 1 and confirmedYN = 1";
		statement.execute(query);
		assertTrue(statement.getResultSet().isBeforeFirst());
	}
	
	@Test
	public void submitSelectionDeclined() throws SQLException, ServletException, IOException {
		
		User user = new User();
		
		//create the fake session user
		user.setEmail("company@mail.com");
		user.setName("company");
		user.setWhichUser("company");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("company");
		

		when(request.getParameter("page")).thenReturn("submitSelection");
		when(request.getParameter("interview")).thenReturn("1");
		when(request.getParameter("selected")).thenReturn("0");
		
		Statement statement = connection.createStatement();
		
		String query = "insert into form values (1)";
		statement.executeUpdate(query);
		
		query = "insert into interview (id, dat, idMatch, idForm) values (1, curdate(), 1, 1)";
		statement.executeUpdate(query);
		interviewer.doPost(request, response);
		
		verify(response).setStatus(HttpServletResponse.SC_OK);
		
		query = "select * from interview where id = 1";
		statement.execute(query);
		assertFalse(statement.getResultSet().isBeforeFirst());
	}
	
	@Test
	public void submitSelectionDeclinedCompanyNotOwner() throws SQLException, ServletException, IOException {
		
		User user = new User();
		
		//create the fake session user
		user.setEmail("company1@mail.com");
		user.setName("company");
		user.setWhichUser("company");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("company");
		

		when(request.getParameter("page")).thenReturn("submitSelection");
		when(request.getParameter("interview")).thenReturn("1");
		when(request.getParameter("selected")).thenReturn("0");
		
		Statement statement = connection.createStatement();
		
		String query = "insert into form values (1)";
		statement.executeUpdate(query);
		
		query = "insert into interview (id, dat, idMatch, idForm) values (1, curdate(), 1, 1)";
		statement.executeUpdate(query);
		interviewer.doPost(request, response);
		
		verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}
	
	@Test
	public void getResponseTest() throws SQLException, ServletException, IOException {
		User user = new User();
		
		//create the fake session user
		user.setEmail("company@mail.com");
		user.setName("company");
		user.setWhichUser("company");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("company");
		

		when(request.getParameter("page")).thenReturn("getResponse");
		when(request.getParameter("match")).thenReturn("1");
		
		Statement statement = connection.createStatement();
		
		String query = "insert into form values (1)";
		statement.executeUpdate(query);
		
		query = "insert into question (id, txt, answer, idForm) values (1, 'question 1', 'answer 1' ,1), (2, 'question 2', 'answer 2',1), (3, 'question 3', 'answer 3',1)";
		statement.executeUpdate(query);
		
		query = "insert into interview (id, dat, idMatch, idForm) values (1, curdate(), 1, 1)";
		statement.executeUpdate(query);
		
		interviewer.doGet(request, response);
		
		verify(response).setStatus(HttpServletResponse.SC_OK);
		verify(response).setContentType("application/json");
		
		Interview interview = new Gson().fromJson(stringWriter.getBuffer().toString().trim(), Interview.class);
		for(Question q : interview.getForm().getQuestions()) {
			switch (q.getText()) {
			case "question 1":
				assertTrue(q.getAnswer().equals("answer 1"));
				break;
			case "question 2":
				assertTrue(q.getAnswer().equals("answer 2"));			
							break;
			case "question 3":
				assertTrue(q.getAnswer().equals("answer 3"));
				break;
			default:
				break;
			}
		}
		
		
		
		
		assertEquals(interview.getData(), Date.valueOf(LocalDate.now()));	
	}
}
