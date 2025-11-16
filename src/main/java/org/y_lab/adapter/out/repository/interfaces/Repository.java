package org.y_lab.adapter.out.repository.interfaces;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public interface Repository<K, T> extends SimpleRepository<K, T> {


    /**
     * Updates changes on t
     * @param id of modifying t
     * @param t modified
     * @return modified t
     * @throws SQLException
     */
    T update(K id, T t) throws SQLException;

    /**
     * Delete t from BD
     *
     * @param t to delete
     * @return deleted t
     * @throws SQLException
     */
    boolean delete(T t) throws SQLException;



    /**
     * Get all records in BD
     * @return all t's
     * @throws SQLException
     */
    List<T> getAll() throws SQLException;
}
