package com.github.t1.testtools;

import java.util.*;

import org.junit.rules.ExternalResource;
import org.slf4j.*;

public class SystemPropertiesRule extends ExternalResource {
    public static Logger log = LoggerFactory.getLogger(SystemPropertiesRule.class);

    public static String setSystemProperty(String key, String value) {
        String oldValue = (value == null) ? System.clearProperty(key) : System.setProperty(key, value);
        log.debug("change system property {} from {} to {}", key, oldValue, value);
        return oldValue;
    }

    private final Map<String, String> oldSystemProperties = new HashMap<>();
    private Logger originalLogger;

    public SystemPropertiesRule loggingTo(Logger log) {
        this.originalLogger = SystemPropertiesRule.log;
        SystemPropertiesRule.log = log;
        return this;
    }

    public void given(String name) {
        oldSystemProperties.put(name, System.getProperty(name));
    }

    public void given(String name, Object value) {
        String previousValue = setSystemProperty(name, value.toString());
        oldSystemProperties.put(name, previousValue);
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
