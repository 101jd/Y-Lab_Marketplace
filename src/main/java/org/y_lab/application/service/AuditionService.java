package org.y_lab.application.service;

import liquibase.exception.LiquibaseException;
import org.y_lab.adapter.out.repository.AuditionRepository;
import org.y_lab.adapter.out.repository.interfaces.Repository;
import org.y_lab.adapter.out.repository.interfaces.SaveRepository;
import org.y_lab.application.model.AuditionEntity;
import org.y_lab.application.service.interfaces.AuditionHandler;

import java.sql.SQLException;

public class AuditionService implements AuditionHandler {
    private SaveRepository<Long, AuditionEntity> auditionEntityRepository;
    private static AuditionService instance;


    private AuditionService() throws SQLException, LiquibaseException {
        auditionEntityRepository = new AuditionRepository();
    }

    @Override
    public void save(AuditionEntity entity) throws SQLException {
        auditionEntityRepository.save(entity);
    }

    public static AuditionService getInstance() {
        if (instance == null) {
            try {
                instance = new AuditionService();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (LiquibaseException e) {
                throw new RuntimeException(e);
            }
        }
        return instance;
    }
}
