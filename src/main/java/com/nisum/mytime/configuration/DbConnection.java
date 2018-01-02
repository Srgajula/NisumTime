package com.nisum.mytime.configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbConnection {
	
	private static Logger log = LoggerFactory.getLogger(DbConnection.class);

	private static Connection connection = null;

	public static Connection getDBConnection(String dbURL) throws SQLException {
		try {
			Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
			connection = DriverManager.getConnection(dbURL);
		} catch (ClassNotFoundException cnfex) {
			log.error("Problem in loading or " + "registering MS Access JDBC driver", cnfex);
		}
		return connection;
	}
}
