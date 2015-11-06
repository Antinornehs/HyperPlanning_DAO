package fr.univtln.mgajovski482.HyperPlanning.Dao.entityManagers;


import fr.univtln.mgajovski482.HyperPlanning.Dao.connectionManager.DataBaseManager;
import fr.univtln.mgajovski482.HyperPlanning.TeachingUnit;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by stephane on 26/10/15.
 */
public class TeachingUnitManager implements EntityManager<TeachingUnit, String> {

    private Logger logger = Logger.getLogger("logTUManager");

    public TeachingUnit get(String s) {

        try {
            Connection connection = DataBaseManager.getConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM TEACHING_UNITS \n" +
                    "WHERE codeTU = '" + s + "'");

            while (rs.next()) {
                TeachingUnit tu = new TeachingUnit(rs.getString("codeTU"), rs.getString("title"))
                                                    .setEcts(rs.getInt("ects"))
                                                    .setHoursProvided(rs.getInt("hoursProvided"))
                                                    .setDescriptions(rs.getString("description"));
                DataBaseManager.releaseConnection(connection);
                return tu;
            }
        }catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<TeachingUnit> getAll() {
        List<TeachingUnit> tuList = new ArrayList<TeachingUnit>();
        try {
            Connection connection = DataBaseManager.getConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM TEACHING_UNITS");
            while (rs.next()) {
                TeachingUnit tu = this.get(rs.getString("codeTU"));
                tuList.add(tu);
            }
            DataBaseManager.releaseConnection(connection);
        }catch(SQLException e){
            e.printStackTrace();
            return null;
        }
        return tuList;
    }

    public void insert(TeachingUnit teachingUnit) {
        String code = teachingUnit.getCode();
        String label = teachingUnit.getLabel();
        int ects = teachingUnit.getEcts();
        int hoursProvided = teachingUnit.getHoursProvided();
        String description = teachingUnit.getDescriptions();

        try {
            Connection connection = DataBaseManager.getConnection();
            Statement statement = connection.createStatement();

            String query =  "INSERT INTO TEACHING_UNITS (codeTU, title, ects, hoursProvided, description) "+
                    "VALUES ('" +
                    code + "', '" +
                    label + "', " +
                    ects + ", " +
                    hoursProvided + ", '" +
                    description + "');";

            statement.execute(query);
            DataBaseManager.releaseConnection(connection);

        }catch(SQLException e){
            logger.warning("failed to insert : \n" + e);
        }


    }


    public void delete(TeachingUnit teachingUnit) {
        String codeTU = teachingUnit.getCode();
        try {
            Connection connection = DataBaseManager.getConnection();
            Statement statement = connection.createStatement();

            statement.execute("DELETE FROM TEACHING_UNITS \n" + "WHERE codeTU = '"+codeTU+"'");
            DataBaseManager.releaseConnection(connection);

        }catch(SQLException e){
            logger.warning("failed to delete : "+e);
        }

    }
}
