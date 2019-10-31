package com.github.t1.testtools;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class SystemPropertiesMemento implements AfterEachCallback, Extension {
    public static Logger log = LoggerFactory.getLogger(SystemPropertiesMemento.class);

    public static String setSystemProperty(String key, String newValue) {
        String oldValue = (newValue == null) ? System.clearProperty(key) : System.setProperty(key, newValue);
        log.debug("change system property {} from {} to {}", key, oldValue, newValue);
        return oldValue;
    }

    private final Map<String, String> oldSystemProperties = new HashMap<>();
    private Logger originalLogger;

    public SystemPropertiesMemento loggingTo(Logger log) {
        this.originalLogger = SystemPropertiesMemento.log;
        SystemPropertiesMemento.log = log;
        return this;
    }

    @Deprecated public SystemPropertiesMemento given(String name) {
        return memoize(name);
    }

    public SystemPropertiesMemento memoize(String name) { return memoize(name, System.getProperty(name)); }

    private SystemPropertiesMemento memoize(String name, String value) {
        oldSystemProperties.put(name, value);
        return this;
    }

    public SystemPropertiesMemento given(String name, Object value) {
        return memoize(name, setSystemProperty(name, (value == null) ? null : value.toString()));
    }

    @Override public void afterEach(ExtensionContext context) {
        restoreAll();
        resetLogger();
    }

    private void restoreAll() {
        while (!oldSystemProperties.isEmpty())
            restore(oldSystemProperties.keySet().iterator().next());
    }

    public void restore(String key) {
        setSystemProperty(key, oldSystemProperties.remove(key));
    }

    private void resetLogger() {
        if (originalLogger != null)
            SystemPropertiesMemento.log = originalLogger;
    }
}
