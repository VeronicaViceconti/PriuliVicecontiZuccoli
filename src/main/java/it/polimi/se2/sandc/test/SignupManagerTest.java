package it.polimi.se2.sandc.test;

import static org.junit.jupiter.api.Assertions.*;
import it.polimi.se2.sandc.controller.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;


import org.junit.*;

class SignupManagerTest {
	
	@Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;
    
    @InjectMocks
    private SignupManager signupManager;
    
    private StringWriter stringWriter;
    private PrintWriter writer;
    private Connection connection;
    
    @BeforeEach
    public void setUp() throws ClassNotFoundException, SQLException, ServletException {
        
    	MockitoAnnotations.initMocks(this);
     // Imposta il PrintWriter per catturare l'output della risposta
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
        signupManager = new SignupManager();
        
       
		String driver = "com.mysql.cj.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/testsandc?serverTimezone=UTC";
		String user = "root";
		String password = "KKlloopp9900";
		Class.forName(driver);
		connection = DriverManager.getConnection(url, user, password);
        
		signupManager.init(connection);
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
    	
    	statement.close();
    }
    
	@Test
	void testStudentSignupSuccess() throws ServletException, IOException, SQLException {
		
		when(request.getParameter("email")).thenReturn("user@mail.com");
        when(request.getParameter("pwd")).thenReturn("user1");
        when(request.getParameter("pwdSame")).thenReturn("user1");
        when(request.getParameter("username")).thenReturn("user1");
        when(request.getParameter("address")).thenReturn("via tal dei tali");
        when(request.getParameter("isStudent")).thenReturn("yes");
        when(request.getParameter("phoneNumber")).thenReturn("1234567890");
        when(request.getParameter("StudyCourse")).thenReturn("software engeneering");
 
        // Esegui il metodo doGet della servlet
        signupManager.doPost(request, response);       	
        verify(response).setStatus(HttpServletResponse.SC_OK);
        
        Statement statement = connection.createStatement();
        
        String query = "select * from student where email = 'user@mail.com'";
        statement.execute(query);
        
        ResultSet result = statement.getResultSet();
        result.next();
        assertTrue(result.getString("psw").equals("user1"));
        assertTrue(result.getString("name").equals("user1"));
        assertTrue(result.getString("address").equals("via tal dei tali"));
        
        assertTrue(result.getString("phoneNumber").equals("1234567890"));
        assertTrue(result.getString("studyCourse").equals("software engeneering"));
        
        statement.close();
	}
	
	@Test
	void testCompanySignupSuccess() throws ServletException, IOException, SQLException {
		when(request.getParameter("email")).thenReturn("company@mail.com");
        when(request.getParameter("pwd")).thenReturn("company");
        when(request.getParameter("pwdSame")).thenReturn("company");
        when(request.getParameter("username")).thenReturn("company");
        when(request.getParameter("address")).thenReturn("via tal dei tali");
        when(request.getParameter("isStudent")).thenReturn("no");
        when(request.getParameter("phoneNumber")).thenReturn("1234567890");
 
        // Esegui il metodo doGet della servlet
        signupManager.doPost(request, response);       	
        verify(response).setStatus(HttpServletResponse.SC_OK);
        
        Statement statement = connection.createStatement();
        
        String query = "select * from company where email = 'company@mail.com'";
        statement.execute(query);
        
        ResultSet result = statement.getResultSet();
        result.next();
        assertTrue(result.getString("psw").equals("company"));
        assertTrue(result.getString("name").equals("company"));
        assertTrue(result.getString("address").equals("via tal dei tali"));
        
        assertTrue(result.getString("phoneNumber").equals("1234567890"));
        
        statement.close();
	}
	
	@Test
	void testMissingField() throws ServletException, IOException {
		when(request.getParameter("email")).thenReturn("company@mail.com");
        when(request.getParameter("pwd")).thenReturn("company");
        when(request.getParameter("pwdSame")).thenReturn("company");
 
        // Esegui il metodo doGet della servlet
        signupManager.doPost(request, response);       	
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        
        
	}
	
	@Test
	void testShortPsw() throws ServletException, IOException {
		when(request.getParameter("email")).thenReturn("company@mail.com");
        when(request.getParameter("pwd")).thenReturn("comp");
        when(request.getParameter("pwdSame")).thenReturn("comp");
        when(request.getParameter("username")).thenReturn("company");
        when(request.getParameter("address")).thenReturn("via tal dei tali");
        when(request.getParameter("isStudent")).thenReturn("no");
        when(request.getParameter("phoneNumber")).thenReturn("1234567890");
 
        // Esegui il metodo doGet della servlet
        signupManager.doPost(request, response);       	
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
               
	}
	
	@Test
	void testDiffPassword() throws ServletException, IOException {
		when(request.getParameter("email")).thenReturn("company@mail.com");
        when(request.getParameter("pwd")).thenReturn("company");
        when(request.getParameter("pwdSame")).thenReturn("compuny");
        when(request.getParameter("username")).thenReturn("company");
        when(request.getParameter("address")).thenReturn("via tal dei tali");
        when(request.getParameter("isStudent")).thenReturn("no");
        when(request.getParameter("phoneNumber")).thenReturn("1234567890");
 
        // Esegui il metodo doGet della servlet
        signupManager.doPost(request, response);       	
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
               
	}
}