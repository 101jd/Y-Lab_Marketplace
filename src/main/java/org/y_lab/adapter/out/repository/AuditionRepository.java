package org.y_lab.adapter.out.repository;

import liquibase.exception.LiquibaseException;
import org.y_lab.adapter.out.repository.interfaces.SaveRepository;
import org.y_lab.application.exceptions.SQLRuntimeException;
import org.y_lab.application.model.AuditionEntity;

import java.sql.*;

public class AuditionRepository implements SaveRepository<Long, AuditionEntity> {

    private Connection connection;

    public AuditionRepository() throws SQLException, LiquibaseException {
        this.connection = ConnectionManager.getInstance().getConnection();
    }

    @Override
    public Long save(AuditionEntity auditionEntity) throws SQLException {
        connection.beginRequest();
        try(PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO audition (user_id, time, message) VALUES(?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {

            statement.setLong(1, auditionEntity.getUser_id());
            statement.setDate(2, auditionEntity.getTimeStamp());
            statement.setString(3, auditionEntity.getMessage());

            statement.executeUpdate();

            ResultSet set = statement.getGeneratedKeys();

            Long id = null;
            if (set.next()) {
                id = set.getLong("id");
                return id;
            }else throw new SQLRuntimeException("Save audition failed");
        } catch (SQLException e) {
            throw new SQLRuntimeException(e.getMessage());
        }finally {
            connection.endRequest();
        }
    }
}
