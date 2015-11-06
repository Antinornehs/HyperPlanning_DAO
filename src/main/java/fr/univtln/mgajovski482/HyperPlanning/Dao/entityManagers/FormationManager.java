package fr.univtln.mgajovski482.HyperPlanning.Dao.entityManagers;

import fr.univtln.mgajovski482.HyperPlanning.Dao.connectionManager.DataBaseManager;
import fr.univtln.mgajovski482.HyperPlanning.Formation;
import fr.univtln.mgajovski482.HyperPlanning.TeachingUnit;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by stephane on 29/10/15.
 */
public class FormationManager implements EntityManager<Formation, String> {

    private Logger logger = Logger.getLogger("logFormationManager");

    public Formation get(String s) {
        try {
            Connection connection = DataBaseManager.getConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM FORMATIONS \n" +
                    "WHERE codeForm = '" + s + "'");

            while (rs.next()) {
                TeachingUnitManager tum = new TeachingUnitManager();
                TeacherManager tm = new TeacherManager();
                Formation form = new Formation(rs.getString("Title"),rs.getString("Grade"), tm.get(rs.getString("Director")));
                ResultSet rstu = statement.executeQuery("SELECT * FROM FORMATIONS_TUS \n" +
                        "WHERE codeForm = '" + s + "'");
                while (rstu.next()){
                    TeachingUnit tu = tum.get(rstu.getString("codeTU"));
                    form.addTeachingUnits(tu);
                }
                DataBaseManager.releaseConnection(connection);
                return form;
            }
        }catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Formation> getAll() {
        List<Formation> formList = new ArrayList<Formation>();
        try {
            Connection connection = DataBaseManager.getConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM FORMATIONS");
            while (rs.next()) {
                Formation form = this.get(rs.getString("codeTU"));
                formList.add(form);
            }
            DataBaseManager.releaseConnection(connection);
        }catch(SQLException e){
            e.printStackTrace();
            return null;
        }
        return formList;
    }

    public void insert(Formation formation) {
        String id = formation.getId();
        String grade = formation.getGrade();
        String label = formation.getLabel();
        String director = formation.getDirector().getRuConnectionLogs().getEmail();

        try {
            Connection connection = DataBaseManager.getConnection();
            Statement statement = connection.createStatement();

            String query =  "INSERT INTO FORMATIONS (codeForm, title, grade, director) "+
                            "VALUES ('" +
                            id + "', '" +
                            label + "', '" +
                            grade + "', '" +
                            director + "');";

            statement.execute(query);

            for(TeachingUnit tu : formation.getTeachingUnits()) {
                String tuQuery = "INSERT INTO FORMATIONS_TUS (formation, tu) "+
                        "VALUES ('" +
                        id + "', '" +
                        tu.getCode() + "');";;
                statement.execute(tuQuery);
            }


            DataBaseManager.releaseConnection(connection);

        }catch(SQLException e){
            logger.warning("failed to insert this formation : \n" + e);
        }
    }

    public void delete(Formation formation) {
        String id = formation.getId();
        try {
            Connection connection = DataBaseManager.getConnection();
            Statement statement = connection.createStatement();

            statement.execute("DELETE FROM FORMATIONS_TU \n" + "WHERE formation = '"+id+"'");
            statement.execute("DELETE FROM FORMATIONS \n" + "WHERE codeForm = '" + id + "'");
            DataBaseManager.releaseConnection(connection);
        }catch(SQLException e){
            logger.warning("failed to delete : "+e);
        }

    }
}
