package com.github.t1.testtools;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Supplier;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static java.lang.String.join;
import static java.util.logging.Level.CONFIG;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.FINER;
import static java.util.logging.Level.FINEST;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;

public class MockLogger extends Logger {
    private final Map<Level, List<String>> logs = new LinkedHashMap<>();

    public MockLogger() {
        super("dummy", null);
        logs.put(SEVERE, new ArrayList<>());
        logs.put(WARNING, new ArrayList<>());
        logs.put(INFO, new ArrayList<>());
        logs.put(CONFIG, new ArrayList<>());
        logs.put(FINE, new ArrayList<>());
        logs.put(FINER, new ArrayList<>());
        logs.put(FINEST, new ArrayList<>());
    }

    @Override public void log(LogRecord record) {
        logs.get(record.getLevel()).add(record.getMessage());
    }

    @Override public void log(Level level, String msg) {
        logs.get(level).add(msg);
    }

    @Override public void log(Level level, Supplier<String> msgSupplier) {
        logs.get(level).add(msgSupplier.get());
    }

    @Override public void log(Level level, String msg, Object param1) {
        logs.get(level).add(msg.replace("{}", param1.toString()));
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
        logs.get(SEVERE).add(msg);
    }

    @Override public void warning(String msg) {
        logs.get(WARNING).add(msg);
    }

    @Override public void info(String msg) {
        logs.get(INFO).add(msg);
    }

    @Override public void config(String msg) {
        logs.get(CONFIG).add(msg);
    }

    @Override public void fine(String msg) {
        logs.get(FINE).add(msg);
    }

    @Override public void finer(String msg) {
        logs.get(FINER).add(msg);
    }

    @Override public void finest(String msg) {
        logs.get(FINEST).add(msg);
    }

    @Override public void severe(Supplier<String> msgSupplier) {
        logs.get(SEVERE).add(msgSupplier.get());
    }

    @Override public void warning(Supplier<String> msgSupplier) {
        logs.get(WARNING).add(msgSupplier.get());
    }

    @Override public void info(Supplier<String> msgSupplier) {
        logs.get(INFO).add(msgSupplier.get());
    }

    @Override public void config(Supplier<String> msgSupplier) {
        logs.get(CONFIG).add(msgSupplier.get());
    }

    @Override public void fine(Supplier<String> msgSupplier) {
        logs.get(FINE).add(msgSupplier.get());
    }

    @Override public void finer(Supplier<String> msgSupplier) {
        logs.get(FINER).add(msgSupplier.get());
    }

    @Override public void finest(Supplier<String> msgSupplier) {
        logs.get(FINEST).add(msgSupplier.get());
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
        return join("\n", logs.get(level));
    }
}
