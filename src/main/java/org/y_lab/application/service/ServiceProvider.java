package org.y_lab.application.service;

import liquibase.exception.LiquibaseException;
import org.y_lab.application.exceptions.SQLRuntimeException;
import org.y_lab.application.service.interfaces.Service;

import java.sql.SQLException;

/**
 * Use this class to get singleton instance of Service
 */
public class ServiceProvider {
    private static Service service;

    public static Service getService() {
            if (service == null) {
                try {
                    service = new PlatformServiceImpl();
                } catch (SQLException e) {
                    throw new SQLRuntimeException(e.getMessage());
                } catch (LiquibaseException e) {
                    throw new RuntimeException(e);
                }
            }
            return service;
    }
}
