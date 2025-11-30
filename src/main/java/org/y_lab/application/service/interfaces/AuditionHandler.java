package org.y_lab.application.service.interfaces;

import org.y_lab.application.model.AuditionEntity;

import java.sql.SQLException;

public interface AuditionHandler {
    void save(AuditionEntity entity) throws SQLException;
}
