package fr.univtln.mgajovski482.HyperPlanning.Dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws SQLException {
        String bddAdress = "jdbc:h2:tcp://localhost/~/hypertest";
        String bddLogin = "sa";
        String bddPassword = "";
        Connection connection = DriverManager.getConnection(bddAdress,
                                                            bddLogin,
                                                            bddPassword);

    }
}
