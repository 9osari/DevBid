package org.devbid.config;

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;

public class PrettySqlFormat implements MessageFormattingStrategy {
    @Override
    public String formatMessage(int connectionId, String now, long elapsed,
                                String category, String prepared, String sql, String url) {
        return String.format("%s | OperationTime : %dms%nHeFormatSql(P6Spy sql,Hibernate format):%n%s",
                             now, elapsed,
                             org.hibernate.engine.jdbc.internal.FormatStyle.BASIC.getFormatter().format(sql));
    }
}

