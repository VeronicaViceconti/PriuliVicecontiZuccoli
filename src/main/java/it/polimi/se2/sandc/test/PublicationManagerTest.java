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
import com.google.gson.reflect.TypeToken;

import it.polimi.se2.sandc.bean.Company;
import it.polimi.se2.sandc.bean.Internship;
import it.polimi.se2.sandc.bean.Match;
import it.polimi.se2.sandc.bean.Preferences;
import it.polimi.se2.sandc.bean.User;
import it.polimi.se2.sandc.controller.PublicationManager;

class PublicationManagerTest {
	
	@Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;
    
    @InjectMocks
    private PublicationManager publicationManager;
    
    private StringWriter stringWriter;
    private PrintWriter writer;
    private Connection connection;
	
    @BeforeEach
	public void setUp() throws ClassNotFoundException, SQLException, ServletException {
        
    	MockitoAnnotations.initMocks(this);
     // Imposta il PrintWriter per catturare l'output della risposta
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
        publicationManager = new PublicationManager();
        
       
		String driver = "com.mysql.cj.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/testsandc?serverTimezone=UTC";
		String user = "root";
		String password = "KKlloopp9900";
		Class.forName(driver);
		connection = DriverManager.getConnection(url, user, password);
        
		publicationManager.init(connection);
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
    	
    	query = "delete from feedback";
    	statement.executeUpdate(query);
    	
    	query = "delete from complaint";
    	statement.executeUpdate(query);
    	
    	query = "delete from question";
    	statement.executeUpdate(query);
    	
    	query = "insert into Student (email, name, address, phoneNumber, psw) values ('user@mail.com', 'user1', 'via tal dei tali', '1234567890', 'user1')";
    	
    	statement.executeUpdate(query);
    
    	query = "insert into company (email, name, address, phoneNumber, psw) values ('company@mail.com', 'company', 'via tal dei tali', '1234567890', 'company')";
    	
    	statement.executeUpdate(query);
    	
    	query = "insert into workingpreferences values (1, 'ai'), (2, 'robotic'), (3, 'db'),(4, 'embedded systems'),(5, 'web developement'),(6, 'computer security'), (7, 'database')";
    	
    	statement.executeUpdate(query);
    	
    	statement.close();
    }
    
	@Test
	void getAllThePreferencesTest() throws ServletException, IOException {
		
		User user = new User();
		
		//create the fake session user
		user.setEmail("user@mail.com");
		user.setName("user1");
		user.setWhichUser("student");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("student");
		
		when(request.getParameter("page")).thenReturn("getPreferences");
		when(request.getParameter("type")).thenReturn("all");
		
		
		publicationManager.doGet(request, response);
		
		verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(response).setContentType("application/json");
        
     
        ArrayList<Preferences> pref = new ArrayList<Preferences>();
        
        Preferences p1 = new Preferences();
        p1.setId(1);
        p1.setText("ai");
        pref.add(p1);
        
        Preferences p2 = new Preferences();
        p2.setId(2);
        p2.setText("robotic");
        pref.add(p2);
        
        Preferences p3 = new Preferences();
        p3.setId(3);
        p3.setText("db");
        pref.add(p3);

        Preferences p4 = new Preferences();
        p4.setId(4);
        p4.setText("embedded systems");
        pref.add(p4);
        
        Preferences p5 = new Preferences();
        p5.setId(5);
        p5.setText("web developement");
        pref.add(p5);
        
        Preferences p6 = new Preferences();
        p6.setId(6);
        p6.setText("computer security");
        pref.add(p6);
        
        Preferences p7 = new Preferences();
        p7.setId(7);
        p7.setText("database");
        pref.add(p7);
        
        String expected = new Gson().toJson(pref);
        String result = stringWriter.getBuffer().toString().trim();
        assertEquals(expected, result);
	}
	
	@Test
	void getAllThePreferencesTestNoType() throws ServletException, IOException {
		
		User user = new User();
		
		//create the fake session user
		user.setEmail("user@mail.com");
		user.setName("user1");
		user.setWhichUser("student");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("student");
		
		when(request.getParameter("page")).thenReturn("getPreferences");
		
		
		publicationManager.doGet(request, response);
		
		verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
	}
	
	@Test
	public void preferencePublicationStudentTest() throws ServletException, IOException, SQLException {
		User user = new User();
		
		//create the fake session user
		user.setEmail("user@mail.com");
		user.setName("user1");
		user.setWhichUser("student");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("student");
		
		when(request.getParameter("page")).thenReturn("sendPreferences");
		
		when(request.getParameter("ai")).thenReturn("1");
		when(request.getParameter("robotic")).thenReturn("2");
		when(request.getParameter("web developement")).thenReturn("5");
		when(request.getParameter("database")).thenReturn("7");
		
		Statement statement = connection.createStatement();

		
		publicationManager.doPost(request, response);
		
		verify(response).setStatus(HttpServletResponse.SC_OK);
		
		String query = "select id from publication where student = 'user@mail.com'";

		statement.execute(query);
		ResultSet tmp = statement.getResultSet();
		tmp.next();
		int idPublication = tmp.getInt(1);
		
		//verify new publication via the student 
		
		query = "select * from publication where student like 'user@mail.com' and id = " + idPublication;
		
		statement.execute(query);
		
		tmp = statement.getResultSet();
		assertTrue(tmp.isBeforeFirst());
		
		
		//verify that the preference are inserted
		
		query = "select count(*) from preference where idPublication = " + idPublication;
		statement.execute(query);
		tmp = statement.getResultSet();
		tmp.next();
		assertTrue(tmp.getInt(1) == 4);
		
		query = "select * from preference where idWorkingPreferences = 1 and idPublication = " + idPublication;
		statement.execute(query);
		assertTrue(statement.getResultSet().isBeforeFirst());
		
		query = "select * from preference where idWorkingPreferences = 2 and idPublication = " + idPublication;
		statement.execute(query);
		assertTrue(statement.getResultSet().isBeforeFirst());
		
		query = "select * from preference where idWorkingPreferences = 5 and idPublication = " + idPublication;
		statement.execute(query);
		assertTrue(statement.getResultSet().isBeforeFirst());
		
		query = "select * from preference where idWorkingPreferences = 7 and idPublication = " + idPublication;
		statement.execute(query);
		assertTrue(statement.getResultSet().isBeforeFirst());
		
		statement.close();
	}
	
	
	@Test
	public void preferencePublicationStudentTestFailureSamePreference() throws ServletException, IOException, SQLException {
		User user = new User();
		
		//create the fake session user
		user.setEmail("user@mail.com");
		user.setName("user1");
		user.setWhichUser("student");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("student");
		
		when(request.getParameter("page")).thenReturn("sendPreferences");
		
		when(request.getParameter("ai")).thenReturn("1");
		when(request.getParameter("robotic")).thenReturn("2");
		when(request.getParameter("web developement")).thenReturn("5");
		when(request.getParameter("database")).thenReturn("7");
		
		Statement statement = connection.createStatement();

		String query = "insert into publication values (1, 'user@mail.com')";
		statement.executeUpdate(query);
		
		query = "insert into preference values (1,1), (2,1), (5,1), (7,1)";
		statement.executeUpdate(query);
		
		
		publicationManager.doPost(request, response);
		
		verify(response).setStatus(HttpServletResponse.SC_OK);
	
		String expected = "You already have an equal publication!";
        String result = stringWriter.getBuffer().toString().trim();
        assertEquals(expected, result);
		statement.close();
	}
	
	@Test 
	public void internshipPublicationTestSuccess() throws ServletException, IOException, SQLException {
		User user = new User();
		
		//create the fake session user
		user.setEmail("company@mail.com");
		user.setName("company");
		user.setWhichUser("company");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("company");
		
		when(request.getParameter("page")).thenReturn("sendProjectForm");
		
		when(request.getParameter("description")).thenReturn("project description");
		when(request.getParameter("startingDate")).thenReturn("2025-06-01");
		when(request.getParameter("endingDate")).thenReturn("2025-06-21");
		when(request.getParameter("openSeats")).thenReturn("2");
		when(request.getParameter("role")).thenReturn("software engeneering");
		
		publicationManager.doPost(request, response);
		
		verify(response).setStatus(HttpServletResponse.SC_OK);
		Statement statement = connection.createStatement();
		
		String query = "select id from internship where company = 'company@mail.com'";

		statement.execute(query);
		ResultSet tmp = statement.getResultSet();
		tmp.next();
		int idInternship = tmp.getInt(1);
	
		Company company = new Company();
		
		company.setaddress("via tal dei tali");
		company.setEmail("company@mail.com");
		company.setName("company");
		
		
		Internship internship = new Internship();
		internship.setCompany(company);
		internship.setOpenSeats(2);
		internship.setjobDescription("project description");
		internship.setroleToCover("software engeneering");
		internship.setEndingDate(Date.valueOf(request.getParameter("endingDate")));
		internship.setStartingDate(Date.valueOf(request.getParameter("startingDate")));
		internship.setId(idInternship);
		
		String expected = new Gson().toJson(idInternship);
        String result = stringWriter.getBuffer().toString().trim();
        assertEquals(expected, result);
        
        statement.close();
	}
	
	@Test 
	public void requirementPublicationCompanyTest() throws ServletException, IOException, SQLException {
		User user = new User();
		
		//create the fake session user
		user.setEmail("company@mail.com");
		user.setName("company");
		user.setWhichUser("company");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("company");
		
		
		when(request.getParameter("page")).thenReturn("sendPreferences");
		
		when(request.getParameter("ai")).thenReturn("1");
		when(request.getParameter("robotic")).thenReturn("2");
		when(request.getParameter("web developement")).thenReturn("5");
		when(request.getParameter("database")).thenReturn("7");
		
		when(request.getParameter("idInternship")).thenReturn("1");
		
		Statement statement = connection.createStatement();
		
		String query = "insert into internship values (1, 'company@mail.com', 'software engeneering', 2, '2025-06-01', '2025-06-21', 'job description')";
		statement.execute(query);

		publicationManager.doPost(request, response);
		
		verify(response).setStatus(HttpServletResponse.SC_OK);
		
		
		
		//verify that the preference are inserted
		
		query = "select count(*) from requirement where idInternship = 1";
		statement.execute(query);
		ResultSet tmp = statement.getResultSet();
		tmp.next();
		assertTrue(tmp.getInt(1) == 4);
		
		query = "select * from requirement where idWorkingPreference = 1 and idInternship = 1";
		statement.execute(query);
		assertTrue(statement.getResultSet().isBeforeFirst());
		
		query = "select * from requirement where idWorkingPreference = 2 and idInternship = 1";
		statement.execute(query);
		assertTrue(statement.getResultSet().isBeforeFirst());
		
		query = "select * from requirement where idWorkingPreference = 5 and idInternship = 1";
		statement.execute(query);
		assertTrue(statement.getResultSet().isBeforeFirst());
		
		query = "select * from requirement where idWorkingPreference = 7 and idInternship = 1";
		statement.execute(query);
		assertTrue(statement.getResultSet().isBeforeFirst());
		
		statement.close();
	}
	
	@Test
	public void getProposedInternshipTest() throws ServletException, IOException, SQLException {
		User user = new User();
		
		//create the fake session user
		user.setEmail("company@mail.com");
		user.setName("company");
		user.setWhichUser("company");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("company");
		
		
		when(request.getParameter("page")).thenReturn("proposedInternships");
		
		Statement statement = connection.createStatement();
		
		String query = "insert into internship values (1, 'company@mail.com', 'software engeneering', 2, '2025-06-01', '2025-06-21', 'job description'), (2, 'company@mail.com', 'software engeneering', 3, '2025-06-02', '2025-06-22', 'job description')";
		statement.execute(query);

		publicationManager.doGet(request, response);
		
		verify(response).setStatus(HttpServletResponse.SC_OK);
		verify(response).setContentType("application/json");
		
		Company company = new Company();
		
		company.setaddress("via tal dei tali");
		company.setName("company");
		
		ArrayList<Internship> list = new ArrayList<Internship>();
		
		Internship i1 = new Internship();
		i1.setCompany(company);
		i1.setOpenSeats(2);
		i1.setroleToCover("software engeneering");
		i1.setStartingDate(Date.valueOf("2025-06-01"));
		i1.setEndingDate(Date.valueOf("2025-06-21"));
		i1.setId(1);
		
		Internship i2 = new Internship();
		i2.setCompany(company);
		i2.setOpenSeats(3);
		i2.setroleToCover("software engeneering");
		i2.setStartingDate(Date.valueOf("2025-06-02"));
		i2.setEndingDate(Date.valueOf("2025-06-22"));
		i2.setId(2);
		
		list.add(i1);
		list.add(i2);
		
		String expected = new Gson().toJson(list);
        String result = stringWriter.getBuffer().toString().trim();
        assertEquals(expected, result);
		
		statement.close();
	}
	
	@Test
	public void createNewMatchTest() throws SQLException {
		
		Statement statement = connection.createStatement();
		
		String query = "insert into publication values (1, 'user@mail.com')";
		statement.executeUpdate(query);
		
		query = "insert into preference values (1,1),(2,1),(3,1),(4,1),(5,1),(6,1),(7,1)";
		statement.executeUpdate(query);
		
		query = "insert into internship values (1, 'company@mail.com', 'software engeneering', 2, '2025-06-01', '2025-06-21', 'job description')";
		statement.execute(query);
		
		query = "insert into requirement values (1,1),(2,1),(3,1),(4,1),(5,1),(6,1),(7,1)";
		statement.executeUpdate(query);
		
		query = "select * from matches where idPublication = 1 and idInternship = 1";
		statement.execute(query);
		assertTrue(statement.getResultSet().isBeforeFirst());
		
	}
	
	
	@Test
	public void waitingFeedbackInternshipTest()  throws ServletException, IOException, SQLException {
		User user = new User();
		
		//create the fake session user
		user.setEmail("company@mail.com");
		user.setName("company");
		user.setWhichUser("company");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("company");
		
		
		when(request.getParameter("page")).thenReturn("waitingFeedbackInternships");
		
		
		Statement statement = connection.createStatement();
		String query = "insert into publication values (1, 'user@mail.com')";
		statement.executeUpdate(query);
		
		query = "insert into internship values (2, 'company@mail.com', 'software engeneering', 2, '2025-01-01', '2025-01-02', 'job description')";
		statement.executeUpdate(query);
		
		query = "insert into matches values (1, 1, 1, 1, 2)";
		statement.executeUpdate(query);
		
		query = "insert into form values (1)";
		statement.executeUpdate(query);

		query = "insert into interview values (4, '2025-01-01', 1, 1, 1)";
		statement.executeUpdate(query);
		
		
		publicationManager.doGet(request, response);
		
		TypeToken<ArrayList<Match>> token = new TypeToken<ArrayList<Match>>() {};
		ArrayList<Match> matches = new Gson().fromJson(stringWriter.getBuffer().toString().trim(), token.getType());
		
		assertTrue(matches.size() == 1);
		
		statement.close();
	}
	
	@Test
	public void getTestFailurePageNotSended() throws ServletException, IOException, SQLException {
		User user = new User();
		
		//create the fake session user
		user.setEmail("user@mail.com");
		user.setName("user1");
		user.setWhichUser("student");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("student");
		
		publicationManager.doGet(request, response);
		
		verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
	}
	
	@Test
	public void getTestFailurePageNotFoundStudent() throws ServletException, IOException, SQLException {
		User user = new User();
		
		//create the fake session user
		user.setEmail("user@mail.com");
		user.setName("user1");
		user.setWhichUser("student");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("student");

		when(request.getParameter("page")).thenReturn("asdfasdf");
		
		publicationManager.doGet(request, response);
		
		verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
	}
	
	@Test
	public void postTestFailurePageNotFoundStudent() throws ServletException, IOException, SQLException {
		User user = new User();
		
		//create the fake session user
		user.setEmail("user@mail.com");
		user.setName("user1");
		user.setWhichUser("student");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("student");
		

		when(request.getParameter("page")).thenReturn("asdfasdf");
		publicationManager.doPost(request, response);
		
		verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
	}
	
	@Test
	public void postTestFailurePageNotSended() throws ServletException, IOException, SQLException {
		User user = new User();
		
		//create the fake session user
		user.setEmail("user@mail.com");
		user.setName("user1");
		user.setWhichUser("student");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("student");
		
		
		publicationManager.doPost(request, response);
		
		verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
	}
	
	@Test
	public void getTestFailurePageNotFoundCompany() throws ServletException, IOException, SQLException {
		User user = new User();
		
		//create the fake session user
		user.setEmail("company@mail.com");
		user.setName("company");
		user.setWhichUser("company");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("company");

		when(request.getParameter("page")).thenReturn("asdfasdf");
		
		publicationManager.doGet(request, response);
		
		verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
	}
	
	@Test
	public void postTestFailurePageNotFoundCompany() throws ServletException, IOException, SQLException {
		User user = new User();
		
		//create the fake session user
		user.setEmail("company@mail.com");
		user.setName("company");
		user.setWhichUser("company");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("company");
		

		when(request.getParameter("page")).thenReturn("asdfasdf");
		publicationManager.doPost(request, response);
		
		verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
	}
	
	
}
