package org.ftccommunity.ftcxtensible.robot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.ftccommunity.ftcxtensible.RobotContext;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RobotStatus {
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private final ConcurrentLinkedQueue<LogElement> logElements;
    private LogTypes logType;

    private RobotStatus() {
        logElements = new ConcurrentLinkedQueue<>();
    }

    public RobotStatus(RobotContext ctx, LogTypes type) {
        this();
        logType = type;
    }

    public String getLog() {
        if (logType == LogTypes.JSON) {
            GsonBuilder gsonBuilder = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting();
            Gson gson = gsonBuilder.create();
            return gson.toJson(logElements.toArray());
        } else if (logType == LogTypes.HTML) {
            final String header =
                    "<!DOCTYPE html>" +
                            "<head>" +
                            "<title>Robot Log</title>" +
                            "<!-- Latest compiled and minified CSS -->\n" +
                            "<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css\">" +
                            "</head>" +
                            "<body>" +
                            "<table class=\"table table-bordered\">\n";
            final String footer =
                    "</table>" +
                            "</body>";
            StringBuilder builder = new StringBuilder();
            builder.append(header);
            for (LogElement logElement : logElements) {
                if (logElement.level == Level.SEVERE) {
                    builder.append("<tr class=\"danger\">");
                } else if (logElement.level == Level.WARNING) {
                    builder.append("<tr class=\"warning\">");
                } else {
                    builder.append("<tr> ");
                }
                builder.append("<td>").append(logElement.type).append("</td>");
                builder.append("<td>").append(logElement.details).append("</td>");
            }
            builder.append(footer);

            return builder.toString();
        } else {
            StringBuilder builder = new StringBuilder();
            for (LogElement logElement : logElements) {
                builder.append(logElement.type).append("\t\t");
                builder.append(logElement.details);
            }
            return builder.toString();
        }
    }

    public void log(Level level, String tag, String details) {
        synchronized (logElements) {
            logElements.add(new LogElement(level, tag, details));
        }
    }

    public enum LogTypes {
        HTML,
        TEXT,
        JSON
    }

    private class LogElement {
        String type;
        Level level;
        String details;

        public LogElement(Level logLevel, String entryType, String entryDetails) {
            level = logLevel;
            details = entryDetails;
            type = entryType;
        }
    }
}
