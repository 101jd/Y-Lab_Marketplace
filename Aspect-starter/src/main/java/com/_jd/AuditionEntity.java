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
     * @param user_id which do something
     * @param message about what user does
     */
    public AuditionEntity(String message) {
        this.id = null;
//        this.user_id = user_id;
        this.timeStamp = Date.valueOf(LocalDate.now());
        this.message = message;
    }

    public Long getId() {
        return id;
    }

//    public Long getUser_id() {
//        return user_id;
//    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public String getMessage() {
        return message;
    }

}
