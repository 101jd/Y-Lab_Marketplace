package org.y_lab.adapter.out.repository.interfaces;

import java.sql.SQLException;
import java.util.UUID;

public interface SimpleRepository<K, T> extends SaveRepository<K, T> {



    /**
     * Find by Id if exists
     * @param uuid id to find
     * @return found by id t
     * @throws SQLException
     */
    T getById(Long uuid) throws SQLException;
}
