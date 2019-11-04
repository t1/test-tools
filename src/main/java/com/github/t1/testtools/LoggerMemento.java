package com.github.t1.testtools;

import com.github.t1.log.LogLevel;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class LoggerMemento implements AfterEachCallback, Extension {
    private interface Adapter {
        LogLevel getLogLevel(String name);

        void setLogLevel(String name, LogLevel level);
    }

    private static class LogbackAdapter implements Adapter {
        @Override public LogLevel getLogLevel(String name) {
            return LogLevel.valueOf(getLogger(name).getEffectiveLevel().toString());
        }

        @Override public void setLogLevel(String name, LogLevel level) {
            getLogger(name).setLevel(ch.qos.logback.classic.Level.toLevel(level.name()));
        }

        private ch.qos.logback.classic.Logger getLogger(String name) {
            return (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(name);
        }
    }

    private static class Slf4jLoggerAdapter implements Adapter {
        @Override public LogLevel getLogLevel(String name) {
            return LogLevel.valueOf(getEffectiveLevel(getLogger(name)).toString());
        }

        private java.util.logging.Level getEffectiveLevel(java.util.logging.Logger logger) {
            for (Logger l = logger; l.getParent() != null; l = l.getParent())
                if (l.getLevel() != null)
                    return l.getLevel();
            return java.util.logging.Level.INFO;
        }

        @Override public void setLogLevel(String name, LogLevel level) {
            getLogger(name).setLevel(java.util.logging.Level.parse(level.toString()));
        }

        private java.util.logging.Logger getLogger(String name) {
            try {
                org.slf4j.Logger logger = LoggerFactory.getLogger(name);
                Field field = logger.getClass().getDeclaredField("logger");
                field.setAccessible(true);
                return (java.util.logging.Logger) field.get(logger);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private final Map<String, LogLevel> oldValues = new HashMap<>();
    private final Adapter adapter = findLoggerAdapter();

    private static Adapter findLoggerAdapter() {
        String className = LoggerFactory.getLogger("").getClass().getName();
        switch (className) {
            case "ch.qos.logback.classic.Logger":
                return new LogbackAdapter();
            case "org.slf4j.impl.Slf4jLogger": // org.jboss.slf4j:slf4j-jboss-logmanager
                return new Slf4jLoggerAdapter();
            default:
                throw new IllegalStateException("no LoggerMemento adapter for logger type: '" + className + "'");
        }
    }

    public LoggerMemento with(String name, LogLevel level) {
        oldValues.put(name, getLogLevel(name));
        setLogLevel(name, level);
        return this;
    }

    public LogLevel getLogLevel(String name) { return adapter.getLogLevel(name); }

    public void setLogLevel(String name, LogLevel level) { adapter.setLogLevel(name, level); }

    @Override public void afterEach(ExtensionContext context) { oldValues.forEach(this::setLogLevel); }
}
