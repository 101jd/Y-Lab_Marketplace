package com._jd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class AuditionService implements AuditionHandler {
    private SaveRepository<Long, AuditionEntity> auditionEntityRepository;



    @Autowired
    public AuditionService(SaveRepository<Long, AuditionEntity> auditionEntityRepository) {
        this.auditionEntityRepository = auditionEntityRepository;
    }

    @Override
    public void save(AuditionEntity entity) throws SQLException {
        auditionEntityRepository.save(entity);
    }
}
