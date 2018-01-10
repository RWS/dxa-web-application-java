package com.sdl.webapp.common.impl.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Folter for logback to prevent repeating messages.
 *
 * @dxa.publicApi
 */
public class LogbackRepeatingToSingleFilter extends Filter<ILoggingEvent> {

    private Set<EventData> events = new HashSet<>(16);

    @Setter
    private String loggerName;

    /**
     * {@inheritDoc}
     */
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
