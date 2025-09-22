package db;

import java.sql.Connection;

public class Database {
	private static final String URL = "jdbc:postgresql://host:'port'/'database'";
	private static final String USER = "";
	private static final String PASSWORD = "";
	
	private static Database instance;
	
	
	public static Database getInstance() {
		if (instance == null) {
			instance = new Database();
		}
		
		return instance;
	}
}
