package org.y_lab.adapter.out.repository.interfaces;

import java.sql.SQLException;
import java.util.UUID;

public interface SaveRepository<K, T> {
    /**
     * Saves t to BD
     * @param t
     * @return id of saved t
     * @throws SQLException
     */
    K save(T t) throws SQLException;
}
