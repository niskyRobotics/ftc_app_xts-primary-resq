package org.ftccommunity.ftcxtensible.robot.formatters;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Created by David on 8/30/2015.
 */
public class JsonFormatter extends Formatter {
    /**
     * Converts stopMode {@link LogRecord} object into stopMode string representation. The
     * resulted string is usually localized and includes the message field of
     * the record.
     *
     * @param r the log record to be formatted into stopMode string.
     * @return the formatted string.
     */
    @Override
    public String format(LogRecord r) {
        Gson gson = new GsonBuilder().create();
        JSON json = JSON.fromLogRecord(r, formatMessage(r));
        return gson.toJson(json);
    }

}
