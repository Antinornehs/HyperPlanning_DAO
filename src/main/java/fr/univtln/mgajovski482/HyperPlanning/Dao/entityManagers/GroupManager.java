package fr.univtln.mgajovski482.HyperPlanning.Dao.entityManagers;


import fr.univtln.mgajovski482.HyperPlanning.Course.Course;
import fr.univtln.mgajovski482.HyperPlanning.Dao.connectionManager.DataBaseManager;
import fr.univtln.mgajovski482.HyperPlanning.GroupInFormation.GroupInFormation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by stephane on 06/11/15.
 */
public class GroupManager implements EntityManager<GroupInFormation,Integer> {

    private Logger logger = Logger.getLogger("logGroupManager");

    public GroupInFormation get(Integer id) {
        try {
            Connection connection = DataBaseManager.getConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM GROUPS \n" +
                    "WHERE id = '" + id + "'");

            while (rs.next()) {
                GroupInFormation group = new GroupInFormation();
                group.setId(id);
                group.setLabel(rs.getString("title"));

                ResultSet rsCourses = statement.executeQuery("SELECT * FROM COURSES_GROUPS \n" +
                        "WHERE GROUPS = '" + id + "'");

                CourseManager cm = new CourseManager();
                List<Course> courseList = new ArrayList<Course>();

                while (rsCourses.next()) {
                    courseList.add(cm.get(rsCourses.getInt("course")));
                }

                group.addCourses(courseList);

                DataBaseManager.releaseConnection(connection);
                return group;
            }
        }catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<GroupInFormation> getAll() {
        List<GroupInFormation> groupList = new ArrayList<GroupInFormation>();
        try {
            Connection connection = DataBaseManager.getConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM GROUPS");
            while (rs.next()) {
                GroupInFormation group = this.get(rs.getInt("id"));
                groupList.add(group);
            }
            DataBaseManager.releaseConnection(connection);
        }catch(SQLException e){
            e.printStackTrace();
            return null;
        }
        return groupList;
    }

    public void insert(GroupInFormation group) {
        try {
            Connection connection = DataBaseManager.getConnection();
            Statement statement = connection.createStatement();

            String query =  "INSERT INTO GROUPS(id, title) "+
                    "VALUES ('" +
                    group.getId() + "', '" +
                    group.getLabel() + "');";

            statement.execute(query);

            for (int mapKey : group.getGroupCoursesMap().keySet()) {
                statement.execute("INSERT INTO COURSES_GROUPS(course, groups) "+
                        "VALUES ('" +
                        group.getGroupCoursesMap().get(mapKey).getId() + "', '" +
                        group.getId() + "');");
            }

            DataBaseManager.releaseConnection(connection);

        }catch(SQLException e){
            logger.warning("failed to insert : \n" + e);
        }
    }

    public void delete(GroupInFormation group) {
        int id = group.getId();
        try {
            Connection connection = DataBaseManager.getConnection();
            Statement statement = connection.createStatement();

            statement.execute("DELETE FROM GROUPS \n" + "WHERE id = '"+id+"'");
            DataBaseManager.releaseConnection(connection);

        }catch(SQLException e){
            logger.warning("failed to delete : "+e);
        }

    }
}
