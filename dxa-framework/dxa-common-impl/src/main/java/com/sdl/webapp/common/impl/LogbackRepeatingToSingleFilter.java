package com.sdl.webapp.common.impl;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class LogbackRepeatingToSingleFilter extends Filter<ILoggingEvent> {

    private Set<EventData> events = new HashSet<>();

    private String loggerName;

    public void setLoggerName(String loggerName) {
        this.loggerName = loggerName;
    }

    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (!Objects.equals(event.getLoggerName(), this.loggerName)) {
            return FilterReply.NEUTRAL;
        }

        EventData eventData = new EventData(event.getMessage(), event.getLevel());
        if (events.contains(eventData)) {
            return FilterReply.DENY;
        } else {
            events.add(eventData);
            return FilterReply.NEUTRAL;
        }
    }

    @EqualsAndHashCode
    @AllArgsConstructor
    private static class EventData {
        private String message;
        private Level level;
    }
}
