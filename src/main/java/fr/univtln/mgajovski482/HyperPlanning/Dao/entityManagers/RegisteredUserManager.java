package fr.univtln.mgajovski482.HyperPlanning.Dao.entityManagers;

import fr.univtln.mgajovski482.HyperPlanning.Dao.connectionManager.DataBaseManager;
import fr.univtln.mgajovski482.HyperPlanning.User.RegisteredUser.AbstractRegUser;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by sgrassell418 on 20/10/15.
 */
public class RegisteredUserManager implements EntityManager<AbstractRegUser, String> {

    private Logger logger = Logger.getLogger("logRegisteredUserManager");


    public AbstractRegUser get(String mail){
        System.out.println("Cette fonction n'est pas sensée être appelée...");
        return null;
    }

    public void insert(AbstractRegUser user){
        String firstName = user.getRuPersonalLogs().getFirstName();
        String lastName = user.getRuPersonalLogs().getLastName();
        String birthDate = datetoString(user.getRuPersonalLogs().getDateOfBirth());
        String gender = (user.getRuPersonalLogs().isMale()) ? "M" : "F";
        String mail = user.getRuConnectionLogs().getEmail();
        String password = user.getRuConnectionLogs().getPassword();
        String phone = user.getRuPersonalLogs().getPhoneNumber();
        String address = user.getRuPersonalLogs().getAddress();
        String pc = user.getRuPersonalLogs().getPostalCode();
        String city = user.getRuPersonalLogs().getCity();
        String webSite = user.getRuPersonalLogs().getWebSite();

        try {
            Connection connection = DataBaseManager.getConnection();
            Statement statement = connection.createStatement();

            String query =  "INSERT INTO USERS (firstName, lastname, birthDate, gender, mail, password, phone, address, pc, city, website) "+
                            "VALUES ('" +   firstName + "', '" +
                                            lastName + "', '" +
                                            birthDate + "', '" +
                                            gender + "', '" +
                                            mail + "', '" +
                                            password + "', '" +
                                            phone + "', '" +
                                            address + "', '" +
                                            pc + "', '" +
                                            city + "', '" +
                                            webSite + "');";

            statement.execute(query);
            DataBaseManager.releaseConnection(connection);

        }catch(SQLException e){
            logger.warning("failed to insert : \n" + e);
        }


    }

    public List<AbstractRegUser> getAll() {
        System.out.println("Cette fonction n'est pas sensée être appelée...");
        return null;
    }

    public void delete(AbstractRegUser user){
        String mail = user.getRuConnectionLogs().getEmail();

        try{
            Connection connection = DataBaseManager.getConnection();
            Statement statement = connection.createStatement();
            statement.execute("DELETE FROM USERS \n" +
                    "WHERE mail = '" + mail + "'");
            DataBaseManager.releaseConnection(connection);

        }catch(SQLException e){
            logger.warning("failed to delete : \n"+e);
        }
    }

    public String datetoString(Calendar date){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date dateDate = date.getTime();
        return dateFormat.format(dateDate);
    }
}
