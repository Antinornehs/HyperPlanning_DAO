package fr.univtln.mgajovski482.HyperPlanning.Dao.entityManagers;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by sgrassell418 on 20/10/15.
 */
public interface EntityManager<E, ID> {
    E get(ID id);
    List<E> getAll();
    void insert(E e);
    void delete(E e);
    //void update(E e);
    //void getUpdate(E e);
    //void setAutoCommit(boolean b);
    //void rollBack();
    //void close();
}
