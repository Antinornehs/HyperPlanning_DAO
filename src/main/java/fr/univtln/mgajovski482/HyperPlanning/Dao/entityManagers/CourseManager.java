package fr.univtln.mgajovski482.HyperPlanning.Dao.entityManagers;

import fr.univtln.mgajovski482.HyperPlanning.Course.Course;
import fr.univtln.mgajovski482.HyperPlanning.Dao.connectionManager.DataBaseManager;
import fr.univtln.mgajovski482.HyperPlanning.Reservable.Room.Room;
import fr.univtln.mgajovski482.HyperPlanning.Schedule.Schedule;
import fr.univtln.mgajovski482.HyperPlanning.TeachingUnit;
import fr.univtln.mgajovski482.HyperPlanning.User.RegisteredUser.Teacher;

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
public class CourseManager implements EntityManager<Course, Integer> {

    private Logger logger = Logger.getLogger("logCourseManager");

    public Course get(Integer id) {
        try {
            Connection connection = DataBaseManager.getConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM COURSES \n" +
                    "WHERE id = '" + id.toString() + "'");

            while (rs.next()) {

                RoomManager rm = new RoomManager();
                ScheduleManager sm = new ScheduleManager();
                TeachingUnitManager tum = new TeachingUnitManager();
                TeacherManager tm = new TeacherManager();


                Room room = rm.get(rs.getString("room"));
                Schedule schedule = sm.get(rs.getInt("schedule"));
                TeachingUnit tu = tum.get(rs.getString("teaching_Unit"));
                Course.TYPE type = Course.TYPE.CM;

                for (Course.TYPE leType : Course.TYPE.values()) {
                    if(leType.toString().equals(rs.getString("type"))){type = leType;};
                }

                boolean state = rs.getBoolean("state");
                Teacher teacher = tm.get(rs.getString("teacher"));

                Course course = new Course();
                course.setId(id);
                course.setClassRoom(room);
                course.setSchedule(schedule);
                course.setTeachingUnit(tu);
                course.setType(type);
                course.setIsValidated(state);
                course.setTeacher(teacher);

                DataBaseManager.releaseConnection(connection);
                return course;
            }
        }catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Course> getAll() {
        List<Course> courseList = new ArrayList<Course>();
        try {
            Connection connection = DataBaseManager.getConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM COURSES");
            while (rs.next()) {
                Course course = this.get(rs.getInt("id"));
                courseList.add(course);
            }
            DataBaseManager.releaseConnection(connection);
        }catch(SQLException e){
            e.printStackTrace();
            return null;
        }
        return courseList;
    }

    public void insert(Course course) {
        try {
            Connection connection = DataBaseManager.getConnection();
            Statement statement = connection.createStatement();

            String state = "false";
            if(course.isValidated()){state="true";}

            String query =  "INSERT INTO COURSES(id, room, schedule, teaching_Unit, type, state, teacher) "+
                    "VALUES ('" +
                    course.getId() + "', '" +
                    course.getClassRoom().getId() + "', '" +
                    course.getSchedule().getId() + "', '" +
                    course.getTeachingUnit().getCode() + "', '" +
                    course.getType().toString() + "', '" +
                    state + "', '" +
                    course.getTeacher().getRuConnectionLogs().getEmail() + "');";

            statement.execute(query);
            DataBaseManager.releaseConnection(connection);

        }catch(SQLException e){
            logger.warning("failed to insert : \n" + e);
        }
    }

    public void delete(Course course) {
        int id = course.getId();
        try {
            Connection connection = DataBaseManager.getConnection();
            Statement statement = connection.createStatement();

            statement.execute("DELETE FROM COURSES \n" + "WHERE id = '"+id+"'");
            DataBaseManager.releaseConnection(connection);

        }catch(SQLException e){
            logger.warning("failed to delete : "+e);
        }

    }
}
