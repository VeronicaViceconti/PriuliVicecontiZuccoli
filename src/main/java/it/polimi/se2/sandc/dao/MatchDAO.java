package it.polimi.se2.sandc.dao;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.sql.Date;

import java.util.List;

import it.polimi.se2.sandc.bean.Company;
import it.polimi.se2.sandc.bean.Form;
import it.polimi.se2.sandc.bean.Internship;
import it.polimi.se2.sandc.bean.Interview;
import it.polimi.se2.sandc.bean.Match;
import it.polimi.se2.sandc.bean.Preferences;
import it.polimi.se2.sandc.bean.Publication;
import it.polimi.se2.sandc.bean.Student;
import it.polimi.se2.sandc.bean.Question;
import it.polimi.se2.sandc.bean.User;

public class MatchDAO {
	private Connection connection;

	public MatchDAO(Connection conn) {
		this.connection = conn;
	}

	public void createMatchFromStudent(int pubID, int IDinternship) throws SQLException {
		String query = null;
		query = "INSERT into matches (acceptedYNStudent, idPublication, idInternship) VALUES(true,?, ?)";
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, pubID);
			pstatement.setInt(2, IDinternship);
			pstatement.executeUpdate();

		} catch (SQLException e) {
			throw new SQLException("Error while creating match");
		} finally {
			try {
				pstatement.close(); // devo chiudere prepared statement
			} catch (Exception e) {
				throw new SQLException("Error while trying to close prepared statement");
			}
		}
	}

	public List<Match> findStudentMatches(String emailStudent) throws SQLException {
		String query = null;
		query = "SELECT m.id as matchID, i.id as internID, idPublication,acceptedYNStudent,acceptedYNCompany,roleToCover,startingDate,endingDate,c.name,c.address,jobDescription from matches as m join internship as i on m.idInternship = i.id join company as c on c.email = i.company join publication as pub on pub.id = m.idPublication join student as s on s.email = pub.student WHERE s.email = ? and m.id not in (select idMatch from interview);";
		PreparedStatement pstatement = null;
		ResultSet result2 = null;
		List<Match> matches = new ArrayList<>();
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setString(1, emailStudent);

			result2 = pstatement.executeQuery();
			
			if (!result2.isBeforeFirst()) {// no results, no matches found
				return null;	
			}
			else { 
				while (result2.next()) {
					Match match = new Match();
					match.setId(result2.getInt("matchID"));

					if(result2.getString("acceptedYNStudent") != null) {
			            match.setAcceptedStudent(result2.getBoolean("acceptedYNStudent"));
			        }
					if(result2.getString("acceptedYNCompany") != null) {
			            match.setAcceptedCompany(result2.getBoolean("acceptedYNCompany"));
			        }
					Publication pub = new Publication();
					pub.setId(result2.getInt("idPublication"));
					match.setPublication(pub);
					Internship intern = new Internship();
					intern.setId(result2.getInt("internID"));
					Date sqlDate = result2.getDate("startingDate");
		            if (sqlDate != null) {
		                intern.setStartingDate( new Date(sqlDate.getTime())); 
		            }
		            sqlDate = result2.getDate("endingDate");
		            if (sqlDate != null) {
		                intern.setEndingDate(new Date(sqlDate.getTime())); 
		            }
		            intern.setjobDescription(result2.getString("jobDescription"));
		            intern.setroleToCover(result2.getString("roleToCover"));
		            Company company = new Company();
		            company.setName(result2.getString("name"));
		            company.setaddress(result2.getString("address"));
		            intern.setCompany(company);
		            match.setInternship(intern);
		            matches.add(match);
		        }
				return matches;	
			}
			
		} catch(SQLException e) {
			throw new SQLException("Error while finding student match");
		}finally {
			try {
				pstatement.close();  //devo chiudere prepared statement
			} catch(Exception e) {
				throw new SQLException("Error while trying to close prepared statement");
			}
		}
	}

	public void updateMatchAccepted(int matchID,String userType,int acceptedOrNot) throws SQLException {
		String query = null;
		if(userType.equalsIgnoreCase("student"))
			query = "UPDATE matches set acceptedYNStudent = ? WHERE id = ?";
		else
			query = "UPDATE matches set acceptedYNCompany = ? WHERE id = ?";
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, acceptedOrNot);
			pstatement.setInt(2, matchID);
			pstatement.executeUpdate();

		} catch (SQLException e) {
			throw new SQLException("Error while updating match");
		} finally {
			try {
				pstatement.close(); // devo chiudere prepared statement
			} catch (Exception e) {
				throw new SQLException("Error while trying to close prepared statement");
			}
		}
	}

	//control student ownership
	public Boolean controlOwnership(String email,int matchID,String userType) throws SQLException {
		String query = null;
		if(userType.equalsIgnoreCase("student"))
			query = "SELECT * FROM matches as m join publication as p on m.idPublication = p.id join student as s on s.email = p.student WHERE email = ? and m.id = ?;";
		else
			query = "SELECT * FROM matches as m join internship as i on m.idInternship = i.id join company as c on c.email = i.company WHERE email = ? and m.id = ?;";

		PreparedStatement pstatement = null;
		ResultSet result2 = null;

		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setString(1, email);
			pstatement.setInt(2, matchID);

			result2 = pstatement.executeQuery();

			if (!result2.isBeforeFirst()) {// no results,he doesn't have that match
				return false;
			} else {
				return true;
			}
			
		} catch(SQLException e) {
			throw new SQLException("Error while controlling ownership");
		}finally {
			try {
				pstatement.close();  //devo chiudere prepared statement
			} catch(Exception e) {
				throw new SQLException("Error while trying to close prepared statement");
			}
		}
	}
	
	
	public List<Match> findCompanyMatches(String emailCompany) throws SQLException {
		String query = null;
		query = "SELECT m.id as matchID, i.id as internID, idPublication,acceptedYNStudent,acceptedYNCompany,roleToCover,startingDate,endingDate,c.address,s.name,s.studyCourse from matches as m join internship as i on m.idInternship = i.id join company as c on c.email = i.company join publication as pub on pub.id = m.idPublication join student as s on s.email = pub.student WHERE c.email = ? and m.id not in (select idMatch from interview) and m.acceptedYNCompany is null;";
		PreparedStatement pstatement = null;
		ResultSet result2 = null;
		List<Match> matches = new ArrayList<>();
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setString(1, emailCompany);

			result2 = pstatement.executeQuery();
			
			if (!result2.isBeforeFirst()) {// no results, no matches found
				return null;	
			}
			else { 
				while (result2.next()) {
					Match match = new Match();
					match.setId(result2.getInt("matchID"));
					if(result2.getString("acceptedYNStudent") != null) {
			            match.setAcceptedStudent(result2.getBoolean("acceptedYNStudent"));
			        }
					if(result2.getString("acceptedYNCompany") != null) {
			            match.setAcceptedCompany(result2.getBoolean("acceptedYNCompany"));
			        }
					Publication pub = new Publication();
					pub.setId(result2.getInt("idPublication"));
					Student student = new Student();
					student.setName(result2.getString("name"));
					student.setStudyCourse(result2.getString("studyCourse"));
					pub.setStudent(student);
					match.setPublication(pub);
					Internship intern = new Internship();
					intern.setId(result2.getInt("internID"));
					Date sqlDate = result2.getDate("startingDate");
		            if (sqlDate != null) {
		                intern.setStartingDate( new Date(sqlDate.getTime())); 
		            }
		            sqlDate = result2.getDate("endingDate");
		            if (sqlDate != null) {
		                intern.setEndingDate(new Date(sqlDate.getTime())); 
		            }
		            intern.setroleToCover(result2.getString("roleToCover"));
		            Company company = new Company();
		            company.setaddress(result2.getString("address"));
		            intern.setCompany(company);
		            match.setInternship(intern);
		            matches.add(match);
		        }
				return matches;	
			}
			
		} catch(SQLException e) {
			throw new SQLException("Error while finding match");
		}finally {
			try {
				pstatement.close(); // devo chiudere prepared statement
			} catch (Exception e) {
				throw new SQLException("Error while trying to close prepared statement");
			}
		}
	}

	public void deleteMatch(int matchID) throws SQLException {
		String query = null;
		query = "DELETE from matches WHERE id = ?;";
		PreparedStatement pstatement = null;
		
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, matchID);

			pstatement.executeUpdate();
		} catch(SQLException e) {
			throw new SQLException("Error while deleting match");
		}finally {
			try {
				pstatement.close();  //devo chiudere prepared statement
			} catch(Exception e) {
				throw new SQLException("Error while trying to close prepared statement");
			}
		}
	}
	
	public Interview createInterview(int idMatch) throws SQLException{
		
		String query = "select * \n"
				+ "from publication as p inner join matches as m on p.id = m.idPublication\n"
				+ "where m.id = ? and p.student in (select student\n"
				+ "									from ((interview as i1 inner join matches as m1 on i1.idMatch = m1.id)  inner join publication as p1 on m1.idPublication = p1.id) inner join internship on internship.id = m1.idInternship \n"
				+ "									where confirmedYN is null or (confirmedYN = true and current_date() < endingDate))";
		
		
		try(PreparedStatement statement = connection.prepareStatement(query)){
			statement.setInt(1, idMatch);
			
			try(ResultSet result = statement.executeQuery()){
				if (result.isBeforeFirst()) {// the student has already an interview
					return null;	
				}
			}
		}
		
		Interview interview = new Interview();
		
		query = "insert into Form values ()";
		int idForm = -1;
		try(PreparedStatement statement = connection.prepareStatement(query,Statement.RETURN_GENERATED_KEYS)){
			statement.executeUpdate();
			try(ResultSet ris = statement.getGeneratedKeys()){
				if(ris.next()) {
					idForm = ris.getInt(1);
				}
			}
		}
		
		query = "insert into question (txt, idForm) values (?,?)";
		ArrayList<Question> questions = new ArrayList<Question>();
		
		try(PreparedStatement statement = connection.prepareStatement(query,Statement.RETURN_GENERATED_KEYS)){
			for(int i = 0; i < 3; i++) {
				statement.setString(1, "question " + ((i+1)));
				statement.setInt(2, idForm);
				statement.executeUpdate();
				Question tmp = new Question();
				try(ResultSet ris = statement.getGeneratedKeys()){
					if(ris.next()) {
						tmp.setId(ris.getInt(1));
						tmp.setText("question " + (i+1));
						questions.add(tmp);
					}
				}
			}
		}
		
		Form form = new Form();
		
		form.setId(idForm);
		form.setQuestions(questions);
		interview.setForm(form);
		
		query = "insert into interview (dat, idMatch, idForm) values (curdate(), ?, ?)";
		
		try(PreparedStatement statement = connection.prepareStatement(query,Statement.RETURN_GENERATED_KEYS)){
			statement.setInt(1, idMatch);
			statement.setInt(2, idForm);
			statement.executeUpdate();
			try(ResultSet ris = statement.getGeneratedKeys()){
				if(ris.next()) {
					interview.setId(ris.getInt(1));
				}
			}
		
		}
		interview.setData(new Date(System.currentTimeMillis()));
		return interview;
	}

	public Student openMatch(int matchID) throws SQLException {
		String query = "SELECT w.text FROM matches as m JOIN internship as i on i.id = m.idInternship JOIN publication as p on  p.id = m.idPublication join student as s on s.email = p.student JOIN preference as pref on pref.idPublication = p.id JOIN workingpreferences as w on w.id = pref.idWorkingPreferences WHERE m.id = ?;";
		ArrayList<Preferences> ris = new ArrayList <Preferences>();
		PreparedStatement pstatement = null;
		ResultSet result2 = null;
		
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, matchID);

			result2 = pstatement.executeQuery();
			
			if (!result2.isBeforeFirst()) {// no results, no matches found
				return null;	
			}
			else { 
				while(result2.next()) {
					Preferences tmp = new Preferences();
					tmp.setText(result2.getString("text"));
					ris.add(tmp);
				}
			}
			
		} catch(SQLException e) {
			throw new SQLException("Error while finding match");
		}finally {
			try {
				pstatement.close(); // devo chiudere prepared statement
			} catch (Exception e) {
				throw new SQLException("Error while trying to close prepared statement");
			}
		}
		//find all the other info
		query = "SELECT p.id as idPublication,s.name,s.email,s.address,cv,s.studyCourse,s.phoneNumber,i.id,startingDate,endingDate,roleToCover FROM matches as m JOIN internship as i on i.id = m.idInternship JOIN publication as p on  p.id = m.idPublication join student as s on s.email = p.student WHERE m.id = ? ;";
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, matchID);
			Student student = new Student();
			result2 = pstatement.executeQuery();
			
			if (!result2.isBeforeFirst()) {// no results, no matches found
				return null;	
			}
			else { 
					result2.next();
					Publication pub = new Publication();
					pub.setId(result2.getInt("idPublication"));
					
					student.setName(result2.getString("name"));
					student.setPhoneNumber(result2.getString("phoneNumber"));
					student.setStudyCourse(result2.getString("studyCourse"));
					student.setaddress(result2.getString("address"));
					student.setEmail(result2.getString("email"));
					if(result2.getString("cv") != null)
						student.setCv(result2.getString("cv"));
					List<Publication> publication = new ArrayList<>();
					pub.setChoosenPreferences(ris);
					publication.add(pub);
					student.setPublications(publication);
				return student;	
			}
			
		} catch(SQLException e) {
			throw new SQLException("Error while finding match");
		}finally {
			try {
				pstatement.close(); // devo chiudere prepared statement
			} catch (Exception e) {
				throw new SQLException("Error while trying to close prepared statement");
			}
		}
	}
	
	public Match getMatch(int id) throws SQLException {
		Match ris = new Match();
		
		ris.setId(id);
		
		String query = "select * from (publication as p inner join matches as m on p.id = m.idPublication) "
				+ "inner join internship as i on i.id = m.idInternship "
				+ "where m.id = ?";
		
		try(PreparedStatement statement = connection.prepareStatement(query)){
			statement.setInt(1, id);
			
			try(ResultSet result = statement.executeQuery()){
				if(result.isBeforeFirst()) {
					result.next();
					Publication p = new Publication ();
					p.setId(result.getInt("p.id"));
					
					Student s = new Student();
					
					s.setEmail(result.getString("p.student"));
					p.setStudent(s);
					
					Internship i = new Internship();
					
					i.setId(result.getInt("i.id"));
					
					Company c = new Company();
					
					c.setEmail(result.getString("i.company"));
					i.setCompany(c);
					ris.setPublication(p);
					ris.setInternship(i);
				}
			}
		}
		
		return ris;
	}
}
