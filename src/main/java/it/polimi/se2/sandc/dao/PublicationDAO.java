package it.polimi.se2.sandc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.se2.sandc.bean.Preferences;
import it.polimi.se2.sandc.bean.Publication;

public class PublicationDAO {
	private Connection connection;


	public PublicationDAO(Connection conn) {
		this.connection = conn;
	}
	
	//student working preferences 
	public List<Publication> retrieveAllWP(String emailStudent) throws SQLException{
		String query = "select * from publication as pu join preference as pr on pu.id = pr.idPublication join workingpreferences as w on w.id = pr.idWorkingPreferences where pu.student = ? order by pu.id;";
		ArrayList<Preferences> ris = new ArrayList <Preferences>();
		ResultSet result2 = null;
		PreparedStatement pstatement2 = null;
		List<Publication> publication = new ArrayList<Publication>();
		
		try {
			pstatement2 = connection.prepareStatement(query);
			pstatement2.setString(1, emailStudent);
			result2 = pstatement2.executeQuery();
			if (!result2.isBeforeFirst()) {// no results, no wp found
				return null;	
			}
			else { 
				Publication currP = new Publication();
				while(result2.next()) {
					int pubID = result2.getInt("idPublication");
					if(currP.getId() == 0 || currP.getId() != pubID) { //we are in a new publication
						if(currP.getId() != pubID && currP.getId() != 0) {
							currP.setChoosenPreferences(ris);
							publication.add(currP);
							ris = new ArrayList<>();
							currP = new Publication();
						}
						currP.setId(result2.getInt("id"));
						Preferences pref = new Preferences();
						pref.setId(result2.getInt("idWorkingPreferences"));
						pref.setText(result2.getString("text"));
						ris.add(pref);
					}else {
						Preferences pref = new Preferences();
						pref.setId(result2.getInt("idWorkingPreferences"));
						pref.setText(result2.getString("text"));
						ris.add(pref);
					}
				}
				currP.setChoosenPreferences(ris);
				publication.add(currP);
			}
		} catch(SQLException e) {
			throw new SQLException("Error while trying to retrieve working preferences");
		}finally {
			try {
				result2.close(); //Devo chiudere result set
			} catch(Exception e) {
				throw new SQLException("Error while trying to close Result Set");
			}
			try {
				pstatement2.close();  //devo chiudere prepared statement
			} catch(Exception e) {
				throw new SQLException("Error while trying to close prepared statement");
			}
		}
		
		return publication;
	}
}

