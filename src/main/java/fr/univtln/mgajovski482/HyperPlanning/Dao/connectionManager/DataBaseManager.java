package fr.univtln.mgajovski482.HyperPlanning.Dao.connectionManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by sgrassell418 on 20/10/15.
 */
public class DataBaseManager {
        /**
         * The Constant freeConnections.
         */
        private static final Queue<Connection> freeConnections = new LinkedList<Connection>();

        /**
         * The Constant numberOfInitialConnections.
         */
        private static final int numberOfInitialConnections = 5;

        /**
         * The Constant password.
         */
        private static final String password = "admin";

        /**
         * The Constant url.
         */
        private static final String url = "jdbc:h2:tcp://localhost/~/test";

        /**
         * The Constant user.
         */
        private static final String user = "admin";

        static {
            for (int i = 0; i < numberOfInitialConnections; i++) {
                try {
                    freeConnections.add(DriverManager.getConnection(url, user,
                            password));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * Gets the connection.
         *
         * @return the connection
         * @throws SQLException the SQL exception
         */
        public static synchronized Connection getConnection() throws SQLException {
            Connection connection = null;
            if (freeConnections.isEmpty()) {
                connection = DriverManager.getConnection(url, user, password);
            } else {
                connection = freeConnections.remove();
            }
            return connection;
        }

        /**
         * Release connection.
         *
         * @param connection the connection
         */
        public static synchronized void releaseConnection(Connection connection) {
            if (freeConnections.size() < numberOfInitialConnections) {
                freeConnections.add(connection);
            } else {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
}

