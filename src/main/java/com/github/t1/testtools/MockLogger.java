package com.github.t1.testtools;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Supplier;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static java.util.logging.Level.CONFIG;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.FINER;
import static java.util.logging.Level.FINEST;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;
import static java.util.stream.Collectors.joining;

public class MockLogger extends Logger {
    private final List<LogRecord> logs = new ArrayList<>();

    public MockLogger() {
        super("dummy", null);
    }

    @Override public void log(LogRecord record) {
        logs.add(record);
    }

    @Override public void log(Level level, String msg) {
        log(new LogRecord(level, msg));
    }

    @Override public void log(Level level, Supplier<String> msgSupplier) {
        log(new LogRecord(level, msgSupplier.get()));
    }

    @Override public void log(Level level, String msg, Object param1) {
        log(new LogRecord(level, msg.replace("{}", param1.toString())));
    }

    @Override public void log(Level level, String msg, Object[] params) {
        throw new UnsupportedOperationException();
    }

    @Override public void log(Level level, String msg, Throwable thrown) {
        throw new UnsupportedOperationException(thrown);
    }

    @Override public void log(Level level, Throwable thrown, Supplier<String> msgSupplier) {
        throw new UnsupportedOperationException(thrown);
    }

    @Override public void logp(Level level, String sourceClass, String sourceMethod, String msg) {
        throw new UnsupportedOperationException();
    }

    @Override public void logp(Level level, String sourceClass, String sourceMethod, Supplier<String> msgSupplier) {
        throw new UnsupportedOperationException();
    }

    @Override public void logp(Level level, String sourceClass, String sourceMethod, String msg, Object param1) {
        throw new UnsupportedOperationException();
    }

    @Override public void logp(Level level, String sourceClass, String sourceMethod, String msg, Object[] params) {
        throw new UnsupportedOperationException();
    }

    @Override public void logp(Level level, String sourceClass, String sourceMethod, String msg, Throwable thrown) {
        throw new UnsupportedOperationException(thrown);
    }

    @Override public void logp(Level level, String sourceClass, String sourceMethod, Throwable thrown, Supplier<String> msgSupplier) {
        throw new UnsupportedOperationException(thrown);
    }

    @SuppressWarnings("deprecation") @Override public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg) {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("deprecation") @Override public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg, Object param1) {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("deprecation") @Override public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg, Object[] params) {
        throw new UnsupportedOperationException();
    }

    @Override public void logrb(Level level, String sourceClass, String sourceMethod, ResourceBundle bundle, String msg, Object... params) {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("deprecation") @Override public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg, Throwable thrown) {
        throw new UnsupportedOperationException(thrown);
    }

    @Override public void logrb(Level level, String sourceClass, String sourceMethod, ResourceBundle bundle, String msg, Throwable thrown) {
        throw new UnsupportedOperationException(thrown);
    }

    @Override public void entering(String sourceClass, String sourceMethod) {
        throw new UnsupportedOperationException();
    }

    @Override public void entering(String sourceClass, String sourceMethod, Object param1) {
        throw new UnsupportedOperationException();
    }

    @Override public void entering(String sourceClass, String sourceMethod, Object[] params) {
        throw new UnsupportedOperationException();
    }

    @Override public void exiting(String sourceClass, String sourceMethod) {
        throw new UnsupportedOperationException();
    }

    @Override public void exiting(String sourceClass, String sourceMethod, Object result) {
        throw new UnsupportedOperationException();
    }

    @Override public void throwing(String sourceClass, String sourceMethod, Throwable thrown) {
        throw new UnsupportedOperationException(thrown);
    }

    @Override public void severe(String msg) {
        log(new LogRecord(SEVERE, msg));
    }

    @Override public void warning(String msg) {
        log(new LogRecord(WARNING, msg));
    }

    @Override public void info(String msg) {
        log(new LogRecord(INFO, msg));
    }

    @Override public void config(String msg) {
        log(new LogRecord(CONFIG, msg));
    }

    @Override public void fine(String msg) {
        log(new LogRecord(FINE, msg));
    }

    @Override public void finer(String msg) {
        log(new LogRecord(FINER, msg));
    }

    @Override public void finest(String msg) {
        log(new LogRecord(FINEST, msg));
    }

    @Override public void severe(Supplier<String> msgSupplier) {
        log(new LogRecord(SEVERE, msgSupplier.get()));
    }

    @Override public void warning(Supplier<String> msgSupplier) {
        log(new LogRecord(WARNING, msgSupplier.get()));
    }

    @Override public void info(Supplier<String> msgSupplier) {
        log(new LogRecord(INFO, msgSupplier.get()));
    }

    @Override public void config(Supplier<String> msgSupplier) {
        log(new LogRecord(CONFIG, msgSupplier.get()));
    }

    @Override public void fine(Supplier<String> msgSupplier) {
        log(new LogRecord(FINE, msgSupplier.get()));
    }

    @Override public void finer(Supplier<String> msgSupplier) {
        log(new LogRecord(FINER, msgSupplier.get()));
    }

    @Override public void finest(Supplier<String> msgSupplier) {
        log(new LogRecord(FINEST, msgSupplier.get()));
    }

    @Override public void setLevel(Level newLevel) throws SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override public Level getLevel() {
        throw new UnsupportedOperationException();
    }

    @Override public boolean isLoggable(Level level) {
        return true;
    }

    @Override public String getName() {
        throw new UnsupportedOperationException();
    }

    @Override public void addHandler(Handler handler) throws SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override public void removeHandler(Handler handler) throws SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override public Handler[] getHandlers() {
        throw new UnsupportedOperationException();
    }

    @Override public void setUseParentHandlers(boolean useParentHandlers) {
        throw new UnsupportedOperationException();
    }

    @Override public boolean getUseParentHandlers() {
        throw new UnsupportedOperationException();
    }

    @Override public void setResourceBundle(ResourceBundle bundle) {
        throw new UnsupportedOperationException();
    }

    @Override public Logger getParent() {
        throw new UnsupportedOperationException();
    }

    @Override public void setParent(Logger parent) {
        throw new UnsupportedOperationException();
    }

    @Override public ResourceBundle getResourceBundle() {
        throw new UnsupportedOperationException();
    }

    @Override public String getResourceBundleName() {
        throw new UnsupportedOperationException();
    }

    @Override public void setFilter(Filter newFilter) throws SecurityException {
        throw new UnsupportedOperationException();
    }

    @Override public Filter getFilter() {
        throw new UnsupportedOperationException();
    }

    public String getMessages(Level level) {
        return logs.stream()
            .filter(record -> record.getLevel().intValue() >= level.intValue())
            .map(LogRecord::getMessage)
            .collect(joining("\n"));
    }
}
