package fr.univtln.mgajovski482.HyperPlanning.Dao.entityManagers;

import fr.univtln.mgajovski482.HyperPlanning.Dao.connectionManager.DataBaseManager;
import fr.univtln.mgajovski482.HyperPlanning.Schedule.Schedule;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by stephane on 05/11/15.
 */
public class ScheduleManager implements EntityManager<Schedule, Integer> {

    private Logger logger = Logger.getLogger("logScheduleManager");

    public Schedule get(Integer id) {
        try {
            Connection connection = DataBaseManager.getConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM SCHEDULES \n" +
                    "WHERE id = '" + id.toString() + "'");

            while (rs.next()) {
                Calendar begin = new GregorianCalendar();
                Calendar end = new GregorianCalendar();
                begin.setTime(rs.getTimestamp("beginDateTime"));
                end.setTime(rs.getTimestamp("endDateTime"));

                Schedule sched = new Schedule(begin, end);
                sched.setId(rs.getInt("id"));
                DataBaseManager.releaseConnection(connection);
                return sched;
            }
        }catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Schedule get(Calendar begin, Calendar end){
        try {
            Connection connection = DataBaseManager.getConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM SCHEDULES \n" +
                    "WHERE beginDateTime = '" + datetoString(begin) + "' \n" +
                    "AND endDateTime = '" + datetoString(end) + "'");

            while (rs.next()) {
                Schedule sched = this.get(rs.getInt("id"));
                DataBaseManager.releaseConnection(connection);
                return sched;
            }
        }catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Schedule> getAll() {
        List<Schedule> scheduleList = new ArrayList<Schedule>();
        try {
            Connection connection = DataBaseManager.getConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM SCHEDULES");
            while (rs.next()) {
                Schedule schedule = this.get(rs.getInt("id"));
                scheduleList.add(schedule);
            }
            DataBaseManager.releaseConnection(connection);
        }catch(SQLException e){
            e.printStackTrace();
            return null;
        }
        return scheduleList;
    }

    public void insert(Schedule schedule) {
        int id = schedule.getId();
        String begin = datetoString(schedule.getFromCalendar());
        String end = datetoString(schedule.getToCalendar());

        try {
            Connection connection = DataBaseManager.getConnection();
            Statement statement = connection.createStatement();

            String query =  "INSERT INTO SCHEDULES(id, beginDateTime, endDateTime) "+
                    "VALUES ('" +
                    id + "', '" +
                    begin + "', '" +
                    end + "');";

            statement.execute(query);
            DataBaseManager.releaseConnection(connection);

        }catch(SQLException e){
            logger.warning("failed to insert : \n" + e);
        }
    }

    public void delete(Schedule schedule) {
        int id = schedule.getId();
        try {
            Connection connection = DataBaseManager.getConnection();
            Statement statement = connection.createStatement();

            statement.execute("DELETE FROM SCHEDULES \n" + "WHERE id = '"+id+"'");
            DataBaseManager.releaseConnection(connection);

        }catch(SQLException e){
            logger.warning("failed to delete : "+e);
        }

    }

    public String datetoString(Calendar date){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date dateDate = date.getTime();
        return dateFormat.format(dateDate);
    }
}
