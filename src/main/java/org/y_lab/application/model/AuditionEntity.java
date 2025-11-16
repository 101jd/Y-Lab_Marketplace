package org.y_lab.application.model;

import java.sql.Date;
import java.time.LocalDate;

/**
 * Audition logs entity
 */
public class AuditionEntity {
    private Long id;
    private Long user_id;
    private Date time;
    private String message;

    /**
     * Base constructor
     * Timestamp automatically
     * Id autoincrements in DB
     * @param user_id which do something
     * @param message about what user does
     */
    public AuditionEntity(Long user_id, String message) {
        this.id = null;
        this.user_id = user_id;
        this.time = java.sql.Date.valueOf(LocalDate.now());
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public Date getTime() {
        return time;
    }

    public String getMessage() {
        return message;
    }

}
