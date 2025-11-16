package org.y_lab.adapter.out.repository.interfaces;

import java.sql.SQLException;
import java.util.UUID;

public interface SaveRepo<T> {

    /**
     * Saves t to BD
     * @param t
     * @return id of saved t
     * @throws SQLException
     */
    UUID save(T t) throws SQLException;

    /**
     * Find by Id if exists
     * @param uuid id to find
     * @return found by id t
     * @throws SQLException
     */
    T getById(UUID uuid) throws SQLException;
}
