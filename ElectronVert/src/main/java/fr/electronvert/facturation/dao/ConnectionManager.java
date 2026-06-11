package fr.electronvert.facturation.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class ConnectionManager {

    private ConnectionManager() {}

    public static Connection getConnection() throws SQLException {
        Properties prop = new Properties();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            prop.load(ConnectionManager.class.getClassLoader().getResourceAsStream("db.properties"));
            String url = prop.getProperty("db.url");
            String user = prop.getProperty("db.user");
            String password = prop.getProperty("db.password");
            return DriverManager.getConnection(url, user, password);
        }
        catch (IOException e) {
            throw new RuntimeException("Impossible de charger db.properties", e);
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver MySQL introuvable", e);
        }
    }


}
