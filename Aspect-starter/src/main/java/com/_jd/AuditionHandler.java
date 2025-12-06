package com._jd;


import java.sql.SQLException;

public interface AuditionHandler {
    void save(AuditionEntity entity) throws SQLException;
}
