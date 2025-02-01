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
import it.polimi.se2.sandc.bean.Interview;
import it.polimi.se2.sandc.bean.Match;
import it.polimi.se2.sandc.bean.Preferences;
import it.polimi.se2.sandc.bean.Publication;
import it.polimi.se2.sandc.bean.Student;
import it.polimi.se2.sandc.bean.User;
import it.polimi.se2.sandc.controller.MatchManager;


class MatchManagerTest {
	@Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;
    
    @InjectMocks
    private MatchManager matchManager;
    
    private StringWriter stringWriter;
    private PrintWriter writer;
    private Connection connection;
    
    
	@BeforeEach
	public void setUp() throws Exception {
		
		MockitoAnnotations.initMocks(this);
		// Imposta il PrintWriter per catturare l'output della risposta
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
        matchManager = new MatchManager();
        
       
		String driver = "com.mysql.cj.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/testsandc?serverTimezone=UTC";
		String user = "root";
		String password = "KKlloopp9900";
		Class.forName(driver);
		connection = DriverManager.getConnection(url, user, password);
        
		matchManager.init(connection);
        try {
			when(response.getWriter()).thenReturn(writer);
		} catch (IOException e) {
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
    	
    	query = "delete from feedback";
    	statement.executeUpdate(query);
    	
    	query = "delete from complaint";
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
	public void acceptMatchStudent() throws ServletException, IOException, SQLException {
		User user = new User();
		
		//create the fake session user
		user.setEmail("user@mail.com");
		user.setName("user1");
		user.setWhichUser("student");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("student");
		

		when(request.getParameter("page")).thenReturn("acceptMatch");
		when(request.getParameter("IDmatch")).thenReturn("1");
		when(request.getParameter("accept")).thenReturn("1");
		
		Statement statement = connection.createStatement();
		
		String query = "select * from matches where id = 1 and acceptedYNStudent = 1";
		
		statement.execute(query);
		
		assertFalse(statement.getResultSet().isBeforeFirst());
		
		matchManager.doGet(request, response);
		
		query = "select * from matches where id = 1 and acceptedYNStudent = 1";
		
		statement.execute(query);
		
		assertTrue(statement.getResultSet().isBeforeFirst());
	}
	
	@Test
	public void acceptMatchCompany() throws ServletException, IOException, SQLException {
		User user = new User();
		
		//create the fake session user
		user.setEmail("company@mail.com");
		user.setName("company");
		user.setWhichUser("company");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("company");
		

		when(request.getParameter("page")).thenReturn("acceptMatch");
		when(request.getParameter("IDmatch")).thenReturn("1");
		when(request.getParameter("accept")).thenReturn("1");
		
		Statement statement = connection.createStatement();
		
		String query = "select * from matches where id = 1 and acceptedYNCompany = 1";
		
		statement.execute(query);
		
		assertFalse(statement.getResultSet().isBeforeFirst());
		
		matchManager.doGet(request, response);
		
		query = "select * from matches where id = 1 and acceptedYNCompany = 1";
		
		statement.execute(query);
		
		assertTrue(statement.getResultSet().isBeforeFirst());
	}

	@Test
	public void declineMatchStudent() throws ServletException, IOException, SQLException {
		User user = new User();
		
		//create the fake session user
		user.setEmail("user@mail.com");
		user.setName("user1");
		user.setWhichUser("student");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("student");
		

		when(request.getParameter("page")).thenReturn("acceptMatch");
		when(request.getParameter("IDmatch")).thenReturn("1");
		when(request.getParameter("accept")).thenReturn("0");
		
		
		
		matchManager.doGet(request, response);
		
		Statement statement = connection.createStatement();
		String query = "select * from matches where id = 1";
		
		statement.execute(query);
		
		assertFalse(statement.getResultSet().isBeforeFirst());
	}
	
	@Test
	public void declineMatchCompany() throws ServletException, IOException, SQLException {
		User user = new User();
		
		//create the fake session user
		user.setEmail("company@mail.com");
		user.setName("company");
		user.setWhichUser("company");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("company");
		

		when(request.getParameter("page")).thenReturn("acceptMatch");
		when(request.getParameter("IDmatch")).thenReturn("1");
		when(request.getParameter("accept")).thenReturn("0");
		
		
		
		matchManager.doGet(request, response);
		
		Statement statement = connection.createStatement();
		String query = "select * from matches where id = 1";
		
		statement.execute(query);
		
		assertFalse(statement.getResultSet().isBeforeFirst());
	}
	
	@Test
	public void acceptAMatchNotOwned() throws ServletException, IOException, SQLException {
		User user = new User();
		
		//create the fake session user
		user.setEmail("company1@mail.com");
		user.setName("company1");
		user.setWhichUser("company");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("company");
		

		when(request.getParameter("page")).thenReturn("acceptMatch");
		when(request.getParameter("IDmatch")).thenReturn("1");
		when(request.getParameter("accept")).thenReturn("1");
		
		
		
		matchManager.doGet(request, response);
		
		verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}
	
	@Test
	public void showAllCompanyMatchesTest () throws ServletException, IOException, SQLException {
		User user = new User();
		
		//create the fake session user
		user.setEmail("company@mail.com");
		user.setName("company");
		user.setWhichUser("company");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("company");
		

		when(request.getParameter("page")).thenReturn("showMatches");
		
		
		Statement statement = connection.createStatement();
		String query = "insert into matches (id, idPublication, idInternship) values (2, 2, 1)";
		statement.executeUpdate(query);
		
		query = "update internship set endingDate = '2030-11-12' where id = 1";
		statement.executeUpdate(query);
		
		query = "update internship set startingDate = '2030-11-11' where id = 1";
		statement.executeUpdate(query);
		
		matchManager.doGet(request, response);
		
		ArrayList<Match> list = new ArrayList<Match>();
		
		Company company = new Company();
		company.setaddress("via tal dei tali");
		
		Internship internship = new Internship();
		internship.setCompany(company);
		internship.setId(1);
		internship.setroleToCover("software engeneering");
		internship.setStartingDate(Date.valueOf("2030-11-11"));
		internship.setEndingDate(Date.valueOf("2030-11-12"));
		internship.setCompany(company);
		
		Student user1 = new Student();
		user1.setName("user1");
		user1.setStudyCourse("computer science");
		
		Student user2 = new Student();
		user2.setName("user2");
		user2.setStudyCourse("computer science");
		
		Publication pub1 = new Publication();
		pub1.setId(1);
		pub1.setStudent(user1);
		
		Publication pub2 = new Publication();
		pub2.setId(2);
		pub2.setStudent(user2);
		
		
		Match m1 = new Match();
		m1.setId(1);
		m1.setInternship(internship);
		m1.setPublication(pub1);
		m1.setconfirmedYN(false);
		
		Match m2 = new Match();
		m2.setId(2);
		m2.setInternship(internship);
		m2.setPublication(pub2);
		m2.setconfirmedYN(false);
		list.add(m1);
		list.add(m2);
		

		verify(response).setStatus(HttpServletResponse.SC_OK);
		verify(response).setContentType("application/json");
		
		String expected = new Gson().toJson(list);
        String result = stringWriter.getBuffer().toString().trim();
        assertEquals(expected, result);
	}	
	
	
	
	@Test
	public void openMatchTest () throws ServletException, IOException, SQLException {
		User user = new User();
		
		//create the fake session user
		user.setEmail("copmany@mail.com");
		user.setName("company");
		user.setWhichUser("company");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("company");
		

		when(request.getParameter("page")).thenReturn("openMatch");
		when(request.getParameter("IDmatch")).thenReturn("1");
		
		Statement statement = connection.createStatement();
		String query = "insert into preference values (1, 1), (2,1)";
		
		statement.execute(query);
		
		matchManager.doGet(request, response);

		verify(response).setStatus(HttpServletResponse.SC_OK);
		verify(response).setContentType("application/json");
		
		Publication pub = new Gson().fromJson(stringWriter.getBuffer().toString().trim(), Publication.class);
		
		assertEquals(pub.getStudent().getEmail(), "user@mail.com");
		
		assertTrue(pub.getChoosenPreferences().size() == 2);
		assertTrue(pub.getChoosenPreferences().stream().anyMatch(x -> x.getText().equals("ai")));
		assertTrue(pub.getChoosenPreferences().stream().anyMatch(x -> x.getText().equals("robotic")));
	}	
	
	@Test
	public void studentWrongPage () throws ServletException, IOException, SQLException {
		User user = new User();
		
		//create the fake session user
		user.setEmail("user@mail.com");
		user.setName("user1");
		user.setWhichUser("student");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("student");
		

		when(request.getParameter("page")).thenReturn("openMat");
		when(request.getParameter("IDmatch")).thenReturn("1");
		
		matchManager.doGet(request, response);

		verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
		
	}	
	
	@Test
	public void companyWrongPage () throws ServletException, IOException, SQLException {
		User user = new User();
		
		//create the fake session user
		user.setEmail("company@mail.com");
		user.setName("company");
		user.setWhichUser("company");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("company");
		

		when(request.getParameter("page")).thenReturn("openMat");
		when(request.getParameter("IDmatch")).thenReturn("1");
		
		matchManager.doGet(request, response);

		verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
		
	}	

	@Test 
	public void companyTestNoMatchAcceptMatch () throws ServletException, IOException{
		User user = new User();
		
		//create the fake session user
		user.setEmail("company@mail.com");
		user.setName("company");
		user.setWhichUser("company");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("company");
		

		when(request.getParameter("page")).thenReturn("acceptMatch");
		
		matchManager.doGet(request, response);

		verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
	}
	
	@Test 
	public void studentTestNoMatchAcceptMatch () throws ServletException, IOException{
		User user = new User();
		
		//create the fake session user
		user.setEmail("user@mail.com");
		user.setName("user1");
		user.setWhichUser("student");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("student");
		

		when(request.getParameter("page")).thenReturn("acceptMatch");
		
		matchManager.doGet(request, response);

		verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
	}
	
	
	@Test 
	public void companyTestNoMatchOpenMatch () throws ServletException, IOException{
		User user = new User();
		
		//create the fake session user
		user.setEmail("company@mail.com");
		user.setName("company");
		user.setWhichUser("company");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("company");
		

		when(request.getParameter("page")).thenReturn("openMatch");
		
		matchManager.doGet(request, response);

		verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);

	}
	
	@Test 
	public void saveFCMTokenTest_Company() throws ServletException, IOException, SQLException{
		User user = new User();
		
		user.setEmail("company@mail.com");
		user.setName("company");
		user.setWhichUser("company");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("company");
		
		when(request.getParameter("page")).thenReturn("saveToken");	
		when(request.getParameter("token")).thenReturn("TestToken");
		
		matchManager.doPost(request, response);
		
		Statement statement = connection.createStatement();
		String query = "select * from company where email = 'company@mail.com' and token='TestToken' " ;
		
		statement.execute(query);		
		assertTrue(statement.getResultSet().isBeforeFirst());
	}
	
	@Test
	public void saveFCMTokenTest_Student() throws ServletException, IOException, SQLException{
		User user = new User();
		
		user.setEmail("user@mail.com");
		user.setName("mario");
		user.setWhichUser("student");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("student");
		
		when(request.getParameter("page")).thenReturn("saveToken");	
		when(request.getParameter("token")).thenReturn("TestToken");
		
		matchManager.doPost(request, response);
		
		Statement statement = connection.createStatement();
		String query = "select * from student where email = 'user@mail.com' and token='TestToken'";
		
		statement.execute(query);		
		assertTrue(statement.getResultSet().isBeforeFirst());
	}
}
