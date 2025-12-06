package com._jd;

import java.sql.Date;
import java.time.LocalDate;

/**
 * Audition logs entity
 */
public class AuditionEntity {
    private Long id;
//    private Long user_id;
    private Date timeStamp;
    private String message;

    /**
     * Base constructor
     * Timestamp automatically
     * Id autoincrements in DB
     * @param message about what user does
     */
    public AuditionEntity(String message) {
        this.id = null;
        this.timeStamp = Date.valueOf(LocalDate.now());
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public String getMessage() {
        return message;
    }

}
