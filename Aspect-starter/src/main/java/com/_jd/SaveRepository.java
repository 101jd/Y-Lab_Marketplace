package com._jd;

import java.sql.SQLException;

public interface SaveRepository<K, T> {
    /**
     * Saves t to BD
     * @param t
     * @return id of saved t
     * @throws SQLException
     */
    K save(T t) throws SQLException;
}
