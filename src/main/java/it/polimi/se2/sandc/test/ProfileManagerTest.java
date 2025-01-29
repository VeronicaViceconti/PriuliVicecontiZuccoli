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
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import it.polimi.se2.sandc.bean.Company;
import it.polimi.se2.sandc.bean.Feedback;
import it.polimi.se2.sandc.bean.Internship;
import it.polimi.se2.sandc.bean.Match;
import it.polimi.se2.sandc.bean.Preferences;
import it.polimi.se2.sandc.bean.Publication;
import it.polimi.se2.sandc.bean.Student;
import it.polimi.se2.sandc.bean.User;
import it.polimi.se2.sandc.controller.FeedbackManager;
import it.polimi.se2.sandc.controller.ProfileManager;


class ProfileManagerTest {
	@Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;
    
    @InjectMocks
    private ProfileManager profileManager;
    
    private StringWriter stringWriter;
    private PrintWriter writer;
    private Connection connection;
    
    
	@BeforeEach
	public void setUp() throws Exception {
		
		MockitoAnnotations.initMocks(this);
		// Imposta il PrintWriter per catturare l'output della risposta
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
        profileManager = new ProfileManager();
        
       
		String driver = "com.mysql.cj.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/testsandc?serverTimezone=UTC";
		String user = "Elia";
		String password = "Elia";
		Class.forName(driver);
		connection = DriverManager.getConnection(url, user, password);
        
		profileManager.init(connection);
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
    	
    	query = "insert into Student (email, name, address, phoneNumber, psw, studyCourse) values ('user@mail.com', 'user1', 'via tal dei tali', '1234567890', 'user1', 'computer science')";
    	statement.executeUpdate(query);
    
    	query = "insert into company (email, name, address, phoneNumber, psw) values ('company@mail.com', 'company', 'via tal dei tali', '1234567890', 'company')";
    	statement.executeUpdate(query);
    	
    	query = "insert into workingpreferences values (1, 'ai'), (2, 'robotic'), (3, 'db'),(4, 'embedded systems'),(5, 'web developement'),(6, 'computer security'), (7, 'database')";
    	statement.executeUpdate(query);
    	
    	query = "insert into publication values (1, 'user@mail.com')";
		statement.executeUpdate(query);
		
		query = "insert into internship values (1, 'company@mail.com', 'software engeneering', 2, '2025-01-01', '2026-01-02', 'job description')";
		statement.executeUpdate(query);
		
		query = "insert into matches values (1, 1, 1,1,1)";
		statement.executeUpdate(query);

		query = "insert into form values (1)";
		statement.executeUpdate(query);
		
		query = "insert into preference values (1,1), (2,1)";
		statement.executeUpdate(query);
	
		query = "insert into interview values (1, '2025-01-01', 1, 1, 1)";
		statement.executeUpdate(query);
		
    	statement.close();
    }

	@Test
	public void findPossibleInternshipStudent() throws ServletException, IOException, SQLException {
		User user = new User();
		
		//create the fake session user
		user.setEmail("user@mail.com");
		user.setName("user1");
		user.setWhichUser("student");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("student");
		
		when(request.getParameter("page")).thenReturn("toHomepage");
		
		Statement statement = connection.createStatement();
		String query = "insert into internship values (2, 'company@mail.com', 'software engeneering', 2, '2025-01-01', '2027-01-02', 'job description'),  (3, 'company@mail.com', 'software engeneering', 2, '2025-01-01', '2025-01-02', 'job description')";
		statement.executeUpdate(query);
		
		profileManager.doGet(request, response);
		
		verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(response).setContentType("application/json");
        

        TypeToken<ArrayList<Internship>> token = new TypeToken<ArrayList<Internship>>() {};
        ArrayList<Internship> ris = new Gson().fromJson(stringWriter.getBuffer().toString().trim(), token.getType());
        
        assertTrue(ris.size() == 2);
        
        assertTrue(ris.stream().anyMatch(x -> x.getId() == 3));
        assertTrue(ris.stream().anyMatch(x -> x.getId() == 2));
	}
	
	@Test 
	public void findInternshipInfoTestInternshipId() throws SQLException, ServletException, IOException {
		User user = new User();
		
		//create the fake session user
		user.setEmail("user@mail.com");
		user.setName("user1");
		user.setWhichUser("student");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("student");
		
		when(request.getParameter("page")).thenReturn("internshipInfo");
		when(request.getParameter("ID")).thenReturn("1");
		
		profileManager.doGet(request, response);
		
		verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(response).setContentType("application/json");
        
        Internship ris = new Gson().fromJson(stringWriter.getBuffer().toString().trim(), Internship.class);
        
        assertTrue(ris.getId() == 1);
        
        assertEquals(ris.getCompany().getName(), "company");     
	}
	
	@Test 
	public void findInternshipInfoTestMatchId() throws SQLException, ServletException, IOException {
		User user = new User();
		
		//create the fake session user
		user.setEmail("user@mail.com");
		user.setName("user1");
		user.setWhichUser("student");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("student");
		
		when(request.getParameter("page")).thenReturn("internshipInfo");
		when(request.getParameter("IDMatch")).thenReturn("1");
		
		profileManager.doGet(request, response);
		
		verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(response).setContentType("application/json");
        
        Match ris = new Gson().fromJson(stringWriter.getBuffer().toString().trim(), Match.class);
        
        assertTrue(ris.getInternship().getId() == 1);
        
        assertEquals(ris.getInternship().getCompany().getName(), "company");     
	}
	
	@Test 
	public void findInternshipInfoMissingValues() throws SQLException, ServletException, IOException {
		User user = new User();
		
		//create the fake session user
		user.setEmail("user@mail.com");
		user.setName("user1");
		user.setWhichUser("student");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("student");
		
		when(request.getParameter("page")).thenReturn("internshipInfo");
		
		profileManager.doGet(request, response);
		
		verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
	}
	
	@Test 
	public void retrieveAllWPTest() throws SQLException, ServletException, IOException {
		User user = new User();
		
		//create the fake session user
		user.setEmail("user@mail.com");
		user.setName("user1");
		user.setWhichUser("student");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("student");
		
		when(request.getParameter("page")).thenReturn("openPubAndWP");
		when(request.getParameter("IDMatch")).thenReturn("1");
		
		profileManager.doGet(request, response);
		
		verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(response).setContentType("application/json");
        
        TypeToken<ArrayList<Publication>> token = new TypeToken<ArrayList<Publication>>() {};
        ArrayList<Publication> ris = new Gson().fromJson(stringWriter.getBuffer().toString().trim(), token.getType());
        
        assertTrue(ris.size() == 1);
        Publication pub = ris.get(0);
       
        assertTrue(pub.getId() == 1);
		assertTrue(pub.getChoosenPreferences().stream().anyMatch(x -> x.getText().equals("ai")));
		assertTrue(pub.getChoosenPreferences().stream().anyMatch(x -> x.getText().equals("robotic")));
	}
	
	@Test
	public void addInternshipHomePageTest() throws SQLException, ServletException, IOException {
		User user = new User();
		
		//create the fake session user
		user.setEmail("user@mail.com");
		user.setName("user1");
		user.setWhichUser("student");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("student");
		
		when(request.getParameter("page")).thenReturn("addInternshipThenHomepage");
		when(request.getParameter("IDintern")).thenReturn("2");
		when(request.getParameter("IDpub")).thenReturn("1");
		
		Statement statement = connection.createStatement();
		String query = "insert into internship values (2, 'company@mail.com', 'software engeneering', 2, '2025-01-01', '2025-01-02', 'job description')";
		statement.executeUpdate(query);
	
		
		profileManager.doGet(request, response);
		
		query = "select * from matches where idInternship = 2 and idPublication = 1";
		statement.execute(query);
		assertTrue(statement.getResultSet().isBeforeFirst());
	}
	
	@Test
	public void addInternshipHomePageTestIdPubnNull() throws SQLException, ServletException, IOException {
		User user = new User();
		
		//create the fake session user
		user.setEmail("user@mail.com");
		user.setName("user1");
		user.setWhichUser("student");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("student");
		
		when(request.getParameter("page")).thenReturn("addInternshipThenHomepage");
		when(request.getParameter("IDintern")).thenReturn("2");
		
		Statement statement = connection.createStatement();
		String query = "insert into internship values (2, 'company@mail.com', 'software engeneering', 2, '2025-01-01', '2025-01-02', 'job description')";
		statement.executeUpdate(query);
	
		
		profileManager.doGet(request, response);
		
		verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
	}
	
	@Test
	public void addInternshipHomePageTestIdInternNull() throws SQLException, ServletException, IOException {
		User user = new User();
		
		//create the fake session user
		user.setEmail("user@mail.com");
		user.setName("user1");
		user.setWhichUser("student");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("student");
		
		when(request.getParameter("page")).thenReturn("addInternshipThenHomepage");
		when(request.getParameter("IDpub")).thenReturn("1");
		
		Statement statement = connection.createStatement();
		String query = "insert into internship values (2, 'company@mail.com', 'software engeneering', 2, '2025-01-01', '2025-01-02', 'job description')";
		statement.executeUpdate(query);
	
		
		profileManager.doGet(request, response);
		
		verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
	}
	
	@Test
	public void addInternshipHomePageTestInternshipNotExists() throws SQLException, ServletException, IOException {
		User user = new User();
		
		//create the fake session user
		user.setEmail("user@mail.com");
		user.setName("user1");
		user.setWhichUser("student");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("student");
		
		when(request.getParameter("page")).thenReturn("addInternshipThenHomepage");
		when(request.getParameter("IDintern")).thenReturn("3");
		when(request.getParameter("IDpub")).thenReturn("1");
		
		Statement statement = connection.createStatement();
		String query = "insert into internship values (2, 'company@mail.com', 'software engeneering', 2, '2025-01-01', '2025-01-02', 'job description')";
		statement.executeUpdate(query);
	
		
		profileManager.doGet(request, response);
		
		verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
	}
	
	@Test
	public void addInternshipHomePageTestPublicaionNotExists() throws SQLException, ServletException, IOException {
		User user = new User();
		
		//create the fake session user
		user.setEmail("user@mail.com");
		user.setName("user1");
		user.setWhichUser("student");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("student");
		
		when(request.getParameter("page")).thenReturn("addInternshipThenHomepage");
		when(request.getParameter("IDintern")).thenReturn("2");
		when(request.getParameter("IDpub")).thenReturn("2");
		
		Statement statement = connection.createStatement();
		String query = "insert into internship values (2, 'company@mail.com', 'software engeneering', 2, '2025-01-01', '2025-01-02', 'job description')";
		statement.executeUpdate(query);
	
		
		profileManager.doGet(request, response);
		
		verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
	}
	
	@Test
	public void showAllStudentMatchesTest() throws SQLException, ServletException, IOException {
		User user = new User();
		
		//create the fake session user
		user.setEmail("user@mail.com");
		user.setName("user1");
		user.setWhichUser("student");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("student");
		
		when(request.getParameter("page")).thenReturn("showMatches");
		
		Statement statement = connection.createStatement();
		String query = "insert into internship values (2, 'company@mail.com', 'software engeneering', 2, '2025-01-01', '2025-01-02', 'job description')";
		statement.executeUpdate(query);
		query = "delete from interview";
    	statement.executeUpdate(query);
		profileManager.doGet(request, response);
		
		verify(response).setStatus(HttpServletResponse.SC_OK);
		verify(response).setContentType("application/json");


		TypeToken<ArrayList<Match>> token = new TypeToken<ArrayList<Match>>() {};
		ArrayList<Match> ris = new Gson().fromJson(stringWriter.getBuffer().toString().trim(), token.getType());
		
		assertTrue(ris.size() == 1);
		assertTrue(ris.get(0).getId() == 1);
	}
	
	
	@Test
	public void filteredInternshipTestStudent() throws SQLException, ServletException, IOException {
		User user = new User();
		
		//create the fake session user
		user.setEmail("user@mail.com");
		user.setName("user1");
		user.setWhichUser("student");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("student");
		
		when(request.getParameter("page")).thenReturn("filteredInternships");
		when(request.getParameter("condition")).thenReturn("company");
		
		Statement statement = connection.createStatement();
		String query = "insert into internship values (2, 'company@mail.com', 'software engeneering', 2, '2025-01-01', '2028-01-30', 'job description')";
		statement.executeUpdate(query);
		
		profileManager.doGet(request, response);
		
		verify(response).setStatus(HttpServletResponse.SC_OK);
		verify(response).setContentType("application/json");


		TypeToken<ArrayList<Internship>> token = new TypeToken<ArrayList<Internship>>() {};
		ArrayList<Internship> ris = new Gson().fromJson(stringWriter.getBuffer().toString().trim(), token.getType());
		
		assertTrue(ris.size() == 1);
		assertTrue(ris.get(0).getId() == 2);
	}
	
	@Test
	public void filteredInternshipTestCompany() throws SQLException, ServletException, IOException {
		User user = new User();
		
		//create the fake session user
		user.setEmail("company@mail.com");
		user.setName("company");
		user.setWhichUser("company");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("company");
		
		when(request.getParameter("page")).thenReturn("filteredInternships");
		when(request.getParameter("condition")).thenReturn("user1");
		
		profileManager.doGet(request, response);
		
		verify(response).setStatus(HttpServletResponse.SC_OK);
		verify(response).setContentType("application/json");

		TypeToken<ArrayList<Match>> token = new TypeToken<ArrayList<Match>>() {};
		ArrayList<Match> ris = new Gson().fromJson(stringWriter.getBuffer().toString().trim(), token.getType());
		
		assertTrue(ris.size() == 1);
		assertTrue(ris.get(0).getInternship().getStudent().getName().equals("user1"));
	}
	
	
	@Test
	public void profileInfoStudentTestOnGoing() throws SQLException, ServletException, IOException {
		User user = new User();
		
		//create the fake session user
		user.setEmail("user@mail.com");
		user.setName("user1");
		user.setWhichUser("student");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("student");
		
		when(request.getParameter("page")).thenReturn("profileInfo");
		when(request.getParameter("condition")).thenReturn("user1");
		
		profileManager.doGet(request, response);
		
		verify(response).setStatus(HttpServletResponse.SC_OK);
		JsonArray jsonArray = new JsonParser().parse(stringWriter.getBuffer().toString().trim()).getAsJsonArray();
		
		Student student = new Gson().fromJson(jsonArray.get(0), Student.class);
		
		Match m = new Gson().fromJson(jsonArray.get(1), Match.class);
		
		TypeToken<ArrayList<Match>> token = new TypeToken<ArrayList<Match>>() {};
		ArrayList<Match> waitFeedback = new Gson().fromJson(jsonArray.get(2), token.getType());
		
		assertTrue(student.getEmail().equals("user@mail.com"));
		
		assertTrue(m.getId() == 1);
		
		assertTrue(waitFeedback.size() == 0);
	}
	
	@Test
	public void profileInfoStudentTestFeedback() throws SQLException, ServletException, IOException {
		User user = new User();
		
		//create the fake session user
		user.setEmail("user@mail.com");
		user.setName("user1");
		user.setWhichUser("student");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("student");
		
		when(request.getParameter("page")).thenReturn("profileInfo");
		when(request.getParameter("condition")).thenReturn("user1");
		
		Statement statement = connection.createStatement();
		String query = "update internship set endingDate = '2025-01-02' where id = 1";
		statement.executeUpdate(query);
		
		profileManager.doGet(request, response);
		
		
		verify(response).setStatus(HttpServletResponse.SC_OK);
		
		JsonArray jsonArray = new JsonParser().parse(stringWriter.getBuffer().toString().trim()).getAsJsonArray();
		
		Student student = new Gson().fromJson(jsonArray.get(0), Student.class);
		
		Match m = new Gson().fromJson(jsonArray.get(1), Match.class);
		
		TypeToken<ArrayList<Match>> token = new TypeToken<ArrayList<Match>>() {};
		ArrayList<Match> waitFeedback = new Gson().fromJson(jsonArray.get(2), token.getType());
		
		assertTrue(student.getEmail().equals("user@mail.com"));
		
		assertTrue(m == null);
		
		assertTrue(waitFeedback.size() == 1);
		
		assertTrue(waitFeedback.get(0).getId() == 1);
	}
	
	
	@Test 
	public void findInternshipInfoTestInternshipIdCompany() throws SQLException, ServletException, IOException {
		User user = new User();
		
		//create the fake session user
		user.setEmail("company@mail.com");
		user.setName("company");
		user.setWhichUser("company");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("company");
		
		when(request.getParameter("page")).thenReturn("internshipInfo");
		when(request.getParameter("ID")).thenReturn("1");
		
		profileManager.doGet(request, response);
		
		verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(response).setContentType("application/json");
        
        Internship ris = new Gson().fromJson(stringWriter.getBuffer().toString().trim(), Internship.class);
        
        assertTrue(ris.getId() == 1);
        
        assertEquals(ris.getCompany().getName(), "company");     
	}
	
	@Test 
	public void profileInfoCompanyTest() throws SQLException, ServletException, IOException {
		User user = new User();
		
		//create the fake session user
		user.setEmail("company@mail.com");
		user.setName("company");
		user.setWhichUser("company");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("company");
		
		when(request.getParameter("page")).thenReturn("profileInfo");
		
		Statement statement = connection.createStatement();
		String query = "insert form values (2)";
		statement.executeUpdate(query);
		
		query = "insert into feedback values (1, 1, 2, 'user@mail.com', 'company@mail.com', 1)";
		statement.executeUpdate(query);
		
		
		 query = "insert into question values (2, 'feedback form', 'good internship', 2)";
		 statement.executeUpdate(query);
		 
		 query = "update internship set endingDate = '2025-01-02' where id = 1";
		 statement.executeUpdate(query);
	
		profileManager.doGet(request, response);
		
		
		
		verify(response).setStatus(HttpServletResponse.SC_OK);
		
		JsonArray jsonArray = new JsonParser().parse(stringWriter.getBuffer().toString().trim()).getAsJsonArray();
		
		Company company = new Gson().fromJson(jsonArray.get(0), Company.class);
		
		TypeToken<ArrayList<Feedback>> token = new TypeToken<ArrayList<Feedback>>() {};
		ArrayList<Feedback> feedbacks = new Gson().fromJson(jsonArray.get(1), token.getType());
		
		assertTrue(company.getEmail().equals("company@mail.com"));
		
		
		assertTrue(feedbacks.size() == 1);
		assertTrue(feedbacks.get(0).getForm().getQuestions().size() == 1);
		assertEquals(feedbacks.get(0).getForm().getQuestions().get(0).getAnswer(), "good internship");
	}
	
	@Test
	public void openOngoingInternshipTest() throws SQLException, ServletException, IOException {
		User user = new User();
		
		//create the fake session user
		user.setEmail("company@mail.com");
		user.setName("company");
		user.setWhichUser("company");
		
		when(session.getAttribute("user")).thenReturn(user);
		when(session.getAttribute("userType")).thenReturn("company");
		
		when(request.getParameter("page")).thenReturn("openOngoingInternships");
	
		profileManager.doGet(request, response);
		
		verify(response).setStatus(HttpServletResponse.SC_OK);
		
		System.out.println(stringWriter.getBuffer().toString().trim());
		
		TypeToken<ArrayList<Match>> token = new TypeToken<ArrayList<Match>>() {};
		ArrayList<Match> matches = new Gson().fromJson(stringWriter.getBuffer().toString().trim(), token.getType());
		
		assertTrue(matches.size() == 1);
		assertTrue(matches.get(0).getId() == 1);
	}
}
