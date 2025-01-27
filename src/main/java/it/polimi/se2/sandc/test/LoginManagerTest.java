package it.polimi.se2.sandc.test;

import static org.junit.jupiter.api.Assertions.*;
import it.polimi.se2.sandc.controller.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
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

class LoginManagerTest {
	
	@Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;
    
    @InjectMocks
    private LoginManager loginManager;
    
    private StringWriter stringWriter;
    private PrintWriter writer;
    private Connection connection;
    
    @BeforeEach
    public void setUp() throws ClassNotFoundException, SQLException, ServletException {
        
    	MockitoAnnotations.initMocks(this);
     // Imposta il PrintWriter per catturare l'output della risposta
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
        loginManager = new LoginManager();
        
       
		String driver = "com.mysql.cj.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/testsandc?serverTimezone=UTC";
		String user = "Elia";
		String password = "Elia";
		Class.forName(driver);
		connection = DriverManager.getConnection(url, user, password);
        
        loginManager.init(connection);
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
    	
    	query = "insert into Student (email, name, address, phoneNumber, psw, studyCourse) values ('user@mail.com', 'user1', 'via tal dei tali', '1234567890', 'user1', 'computer science')";
    	
    	statement.executeUpdate(query);
    
    	query = "insert into company (email, name, address, phoneNumber, psw) values ('company@mail.com', 'company', 'via tal dei tali', '1234567890', 'company')";
    	
    	statement.executeUpdate(query);
    	
    	statement.close();
    }
    
	@Test
	@DisplayName("testing a succesfull student access")
	void testStudentLoginSuccess() throws ServletException, IOException {
		
		when(request.getParameter("email")).thenReturn("user@mail.com");
        when(request.getParameter("pwd")).thenReturn("user1");
        // Esegui il metodo doGet della servlet
        loginManager.doPost(request, response);
        	
        verify(response).setStatus(HttpServletResponse.SC_OK);
        
        
        // Verifica l'output
        String risultato = stringWriter.getBuffer().toString().trim();
        String ris = "\"student\"";
        assertEquals(ris, risultato);
	}
	
	@Test
	@DisplayName("testing a succesfull company access")
	void testCompanyLoginSuccess() throws ServletException, IOException {
		when(request.getParameter("email")).thenReturn("company@mail.com");
        when(request.getParameter("pwd")).thenReturn("company");
        // Esegui il metodo doGet della servlet
        loginManager.doPost(request, response);
        	
        verify(response).setStatus(HttpServletResponse.SC_OK);
        
        
        // Verifica l'output
        String risultato = stringWriter.getBuffer().toString().trim();
        String ris = "\"company\"";
        assertEquals(ris, risultato);
	}
	
	@Test
	@DisplayName("testing a failure student access")
	void testStudentLoginFailure() throws ServletException, IOException {
		when(request.getParameter("email")).thenReturn("elia@mail.com");
        when(request.getParameter("pwd")).thenReturn("elia2");
        // Esegui il metodo doGet della servlet
        loginManager.doPost(request, response);
        	
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        
        // Verifica l'output
        String risultato = stringWriter.getBuffer().toString().trim();
        String ris = "Credentials failed, retry!";
        assertEquals(ris, risultato);
	}
	
	@Test
	@DisplayName("testing a failure company access")
	void testCompanyLoginFailure() throws ServletException, IOException {
		when(request.getParameter("email")).thenReturn("google@gmail.com");
        when(request.getParameter("pwd")).thenReturn("gogle");
        // Esegui il metodo doGet della servlet
        loginManager.doPost(request, response);
        	
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        
        // Verifica l'output
        String risultato = stringWriter.getBuffer().toString().trim();
        String ris = "Credentials failed, retry!";
        assertEquals(ris, risultato);
	}
	
}