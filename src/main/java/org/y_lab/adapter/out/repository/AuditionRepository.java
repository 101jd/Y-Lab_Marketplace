package org.y_lab.adapter.out.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.y_lab.adapter.out.repository.interfaces.SaveRepository;
import org.y_lab.application.model.AuditionEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

@Repository
public class AuditionRepository implements SaveRepository<Long, AuditionEntity> {

    private Connection connection;

    private JdbcTemplate template;

    @Autowired
    public AuditionRepository(JdbcTemplate template) {
        this.template = template;
    }

    @Override
    public Long save(AuditionEntity auditionEntity) throws SQLException {
        String sql = "INSERT INTO audition (user_id, time, message) VALUES(?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(con -> {PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        statement.setLong(1, auditionEntity.getUser_id());
        statement.setDate(2, auditionEntity.getTimeStamp());
        statement.setString(3, auditionEntity.getMessage());
        return statement;}, keyHolder);

        return keyHolder.getKeyAs(Long.class);
    }
}
