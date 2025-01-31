package it.polimi.se2.sandc.dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import it.polimi.se2.sandc.bean.Preferences;
import it.polimi.se2.sandc.bean.Student;
import it.polimi.se2.sandc.bean.User;

public class PreferenceDAO {
	private Connection connection;
	
	public PreferenceDAO(Connection conn) {
		this.connection = conn;
	}
	
	/**
	 * return all the working preferences 
	 * @return an arrayList of the Preferences
	 * @throws SQLException
	 */
	public ArrayList<Preferences> getWorkingPreferences() throws SQLException {
		String query = "select * from workingpreferences";
		ArrayList<Preferences> ris = new ArrayList <Preferences>();
		
		
		try(PreparedStatement statement = connection.prepareStatement(query)){
			try(ResultSet result = statement.executeQuery()){			
				while(result.next()) {
					Preferences tmp = new Preferences();
					tmp.setId(result.getInt("id"));
					tmp.setText(result.getString("text"));
					ris.add(tmp);
				}
			}
		}
		
		return ris;
	}
	
}
