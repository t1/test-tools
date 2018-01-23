package com.github.t1.testtools;

import org.junit.rules.ExternalResource;
import org.slf4j.*;

import java.util.*;

public class SystemPropertiesRule extends ExternalResource {
    public static Logger log = LoggerFactory.getLogger(SystemPropertiesRule.class);

    public static String setSystemProperty(String key, String newValue) {
        String oldValue = (newValue == null) ? System.clearProperty(key) : System.setProperty(key, newValue);
        log.debug("change system property {} from {} to {}", key, oldValue, newValue);
        return oldValue;
    }

    private final Map<String, String> oldSystemProperties = new HashMap<>();
    private Logger originalLogger;

    public SystemPropertiesRule loggingTo(Logger log) {
        this.originalLogger = SystemPropertiesRule.log;
        SystemPropertiesRule.log = log;
        return this;
    }

    @Deprecated public SystemPropertiesRule given(String name) {
        return memoize(name);
    }

    public SystemPropertiesRule memoize(String name) { return memoize(name, System.getProperty(name)); }

    private SystemPropertiesRule memoize(String name, String value) {
        oldSystemProperties.put(name, value);
        return this;
    }

    public SystemPropertiesRule given(String name, Object value) {
        return memoize(name, setSystemProperty(name, (value == null) ? null : value.toString()));
    }

    @Override
    public void after() {
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
            SystemPropertiesRule.log = originalLogger;
    }
}
