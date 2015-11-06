package fr.univtln.mgajovski482.HyperPlanning.Dao.entityManagers;

import fr.univtln.mgajovski482.HyperPlanning.Dao.connectionManager.DataBaseManager;
import fr.univtln.mgajovski482.HyperPlanning.Reservable.Room.Room;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by stephane on 05/11/15.
 */
public class RoomManager implements EntityManager<Room,String> {

    private Logger logger = Logger.getLogger("logRoomManager");

    public Room get(String s) {
        try {
            Connection connection = DataBaseManager.getConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM ROOMS \n" +
                    "WHERE id = '" + s + "'");

            while (rs.next()) {
                Room room = new Room(rs.getString("id"), rs.getInt("capacity"));
                DataBaseManager.releaseConnection(connection);
                return room;
            }
        }catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Room> getAll() {
        List<Room> roomList = new ArrayList<Room>();
        try {
            Connection connection = DataBaseManager.getConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM ROOMS");
            while (rs.next()) {
                Room room = this.get(rs.getString("id"));
                roomList.add(room);
            }
            DataBaseManager.releaseConnection(connection);
        }catch(SQLException e){
            e.printStackTrace();
            return null;
        }
        return roomList;
    }

    public void insert(Room room) {
        String id = room.getId();
        int capacity = room.getCapacity();

        try {
            Connection connection = DataBaseManager.getConnection();
            Statement statement = connection.createStatement();

            String query =  "INSERT INTO ROOMS (id, capacity) "+
                    "VALUES ('" +
                    id + "', '" +
                    capacity + "');";

            statement.execute(query);
            DataBaseManager.releaseConnection(connection);

        }catch(SQLException e){
            logger.warning("failed to insert : \n" + e);
        }
    }

    public void delete(Room room) {
        String id = room.getId();
        try {
            Connection connection = DataBaseManager.getConnection();
            Statement statement = connection.createStatement();

            statement.execute("DELETE FROM ROOMS \n" + "WHERE id = '"+id+"'");
            DataBaseManager.releaseConnection(connection);

        }catch(SQLException e){
            logger.warning("failed to delete : "+e);
        }

    }
}
