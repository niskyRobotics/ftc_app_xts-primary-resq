package org.ftccommunity.ftcxtensible.robot.formatters;

import java.util.Date;
import java.util.logging.LogRecord;

/**
 * Created by David on 8/30/2015.
 */
class JSON {
    private String level;
    private Date date;
    private String message;

    private JSON(String lvl, Date timestamp, String mess) {
        level = lvl;
        date = timestamp;
        message = mess;
    }

    public static JSON fromLogRecord(LogRecord r, String mess) {
        String lvl = r.getLevel().toString();
        Date time = new Date(r.getMillis());
        return new JSON(lvl, time, mess);
    }
}
