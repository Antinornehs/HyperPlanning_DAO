package fr.univtln.mgajovski482.HyperPlanning.Dao.entityManagers;

import fr.univtln.mgajovski482.HyperPlanning.User.RegisteredUser.RegisteredUserLogs.RUConnectionLogs;
import fr.univtln.mgajovski482.HyperPlanning.User.RegisteredUser.RegisteredUserLogs.RUPersonalLogs;
import fr.univtln.mgajovski482.HyperPlanning.User.RegisteredUser.Teacher;
import fr.univtln.mgajovski482.HyperPlanning.Dao.connectionManager.DataBaseManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by stephane on 30/10/15.
 */
public class TeacherManager  implements EntityManager<Teacher, String> {

    private final RegisteredUserManager rum = new RegisteredUserManager();

    private Logger logger = Logger.getLogger("logTeacherManager");


    public Teacher get(String mail){
        try {
            Connection connection = DataBaseManager.getConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM USERS \n" +
                    "WHERE mail = '" + mail + "'");
            while (rs.next()) {
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");

                Calendar birthDate = new GregorianCalendar();
                birthDate.setTime(rs.getDate("birthDate"));

                String gender = rs.getString("gender");
                boolean bGender = true;
                if(gender.equals("F")){
                    bGender = false;
                }

                String password = rs.getString("password");

                String phone = rs.getString("phone");
                String address = rs.getString("address");
                String pc = rs.getString("pc");
                String city = rs.getString("city");
                String webSite = rs.getString("website");

                ResultSet rst = statement.executeQuery("SELECT * FROM TEACHERS \n" +
                        "WHERE mail = '" + mail + "'");
                while (rst.next()) {
                    Teacher.Grade grade = Teacher.Grade.INSIDER;
                    if (rst.getString("grade").equals("outsider")){
                        grade = Teacher.Grade.OUTSIDER;
                    }
                    Teacher u = new Teacher((
                            new RUPersonalLogs.RUPersonalLogsBuilder
                                    (RUPersonalLogs.Status.TEACHER, bGender, firstName, lastName, birthDate)
                                    .phoneNumber(phone)
                                    .address(address)
                                    .postalCode(pc)
                                    .city(city)
                                    .webSite(webSite).build()),
                            new RUConnectionLogs(mail, password),
                            grade, rst.getString("domain"));


                    DataBaseManager.releaseConnection(connection);
                    return u;
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public void insert(Teacher teacher) {
        rum.insert(teacher);
        String mail = teacher.getRuConnectionLogs().getEmail();
        String domain = teacher.getDomain();
        String grade = "insider";
        if(teacher.getGrade().equals(Teacher.Grade.OUTSIDER)){
            grade="outsider";
        }
        try {
            Connection connection = DataBaseManager.getConnection();
            Statement statement = connection.createStatement();

            String query =  "INSERT INTO TEACHERS (mail, grade, domain) "+
                    "VALUES ('" +
                    mail + "', '" +
                    grade + "', '" +
                    domain + "');";

            statement.execute(query);
            DataBaseManager.releaseConnection(connection);

        }catch(SQLException e){
            logger.warning("failed to insert : " + e);
        }
    }

    public List<Teacher> getAll() {
        List<Teacher> TeacherList = new ArrayList<Teacher>();
        try {
            Connection connection = DataBaseManager.getConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM TEACHERS");
            while (rs.next()) {
                Teacher teacher = this.get(rs.getString("mail")); //C'est quand même plus court comme cela.
                TeacherList.add(teacher);
            }
            DataBaseManager.releaseConnection(connection);
        }catch(SQLException e){
            e.printStackTrace();
            return null;
        }
        return TeacherList;
    }

    public void delete(Teacher teacher) {
        String mail = teacher.getRuConnectionLogs().getEmail();
        try {
            Connection connection = DataBaseManager.getConnection();
            Statement statement = connection.createStatement();

            statement.execute("DELETE FROM TEACHERS \n" + "WHERE mail = '"+mail+"'");
            DataBaseManager.releaseConnection(connection);
            rum.delete(teacher);
        }catch(SQLException e){
            logger.warning("failed to delete : "+e);
        }

    }
}