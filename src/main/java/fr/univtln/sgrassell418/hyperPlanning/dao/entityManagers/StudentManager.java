package fr.univtln.sgrassell418.hyperPlanning.dao.entityManagers;

import fr.univtln.mgajovski482.HyperPlanning.User.RegisteredUser.RegisteredUserLogs.RUConnectionLogs;
import fr.univtln.mgajovski482.HyperPlanning.User.RegisteredUser.RegisteredUserLogs.RUPersonalLogs;
import fr.univtln.mgajovski482.HyperPlanning.User.RegisteredUser.Student;
import fr.univtln.sgrassell418.hyperPlanning.dao.connectionManager.DataBaseManager;

import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by sgrassell418 on 20/10/15.
 */
public class StudentManager implements EntityManager<Student, String> {

    private final RegisteredUserManager rum = new RegisteredUserManager();

    private Logger logger = Logger.getLogger("logStudentManager");


    public Student get(String mail){
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
                String formation = rs.getString("formation");

                Student stu = new Student((
                new RUPersonalLogs.RUPersonalLogsBuilder
                        (RUPersonalLogs.Status.STUDENT, bGender, firstName, lastName, birthDate)
                        .phoneNumber(phone)
                        .address(address)
                        .postalCode(pc)
                        .city(city)
                        .webSite(webSite).build()),
                new RUConnectionLogs(mail, password));

                FormationManager fm = new FormationManager();

                stu.addFormations(fm.get(formation));

                DataBaseManager.releaseConnection(connection);
                return stu;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public void insert(Student student) {
        rum.insert(student);
        String query =  "INSERT INTO STUDENTS (mail, formation) "+
                "VALUES ('" +
                student.getRuConnectionLogs().getEmail() + "', '" +
                student.getFormation().getId() + "');";
        try {
            Connection connection = DataBaseManager.getConnection();
            Statement statement = connection.createStatement();

            statement.execute(query);
            DataBaseManager.releaseConnection(connection);

        }catch(SQLException e){
            logger.warning("failed to insert : " + e);
        }
    }

    public List<Student> getAll() {
        List<Student> studentList = new ArrayList<Student>();
        try {
            Connection connection = DataBaseManager.getConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM STUDENTS");
            while (rs.next()) {
                Student st = this.get(rs.getString("mail"));
                studentList.add(st);
            }
            DataBaseManager.releaseConnection(connection);
        }catch(SQLException e){
            e.printStackTrace();
            return null;
        }
        return studentList;
    }

    public void delete(Student student) {
        String mail = student.getRuConnectionLogs().getEmail();
        try {
            Connection connection = DataBaseManager.getConnection();
            Statement statement = connection.createStatement();

            statement.execute("DELETE FROM STUDENTS \n" + "WHERE mail = '"+mail+"'");
            DataBaseManager.releaseConnection(connection);
            rum.delete(student);
        }catch(SQLException e){
            logger.warning("failed to delete : "+e);
        }

    }
}
