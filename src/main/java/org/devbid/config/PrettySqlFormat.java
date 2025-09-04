package org.devbid.config;

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;

public class PrettySqlFormat implements MessageFormattingStrategy {
    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {
        if (sql == null || sql.trim().isEmpty()) {
            return "";
        }

        sql = sql.replaceAll("u[0-9]+_[0-9]+\\.", "");
        sql = sql.replaceAll("(\\b\\w+\\b) u[0-9]+_[0-9]+", "$1");
        sql = sql.replaceAll("(?i)select", "\nSELECT")
                .replaceAll(",", ",\n    ")
                .replaceAll("(?i)from", "\nFROM")
                .replaceAll("(?i)where", "\nWHERE")
                .replaceAll("(?i)values", "\nVALUES")
                .replaceAll("(?i)insert", "\nINSERT")
                .replaceAll("(?i)update", "\nUPDATE")
                .replaceAll("(?i)delete", "\nDELETE");

        return "[DEBUG] ==> Preparing: \n"
                + sql.trim() + "\n"
                + "[DEBUG] <== Total: (" + elapsed + "ms)";
    }

}
