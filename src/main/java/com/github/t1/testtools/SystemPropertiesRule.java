package com.github.t1.testtools;

import java.util.*;
import java.util.Map.Entry;

import org.junit.rules.ExternalResource;

public class SystemPropertiesRule extends ExternalResource {
    private final Map<String, String> oldSystemProperties = new HashMap<>();

    public void given(String name) {
        given(name, System.getProperty(name));
    }

    public void given(String name, Object value) {
        String previousValue = System.setProperty(name, value.toString());
        oldSystemProperties.put(name, previousValue);
    }

    @Override
    public void after() {
        Iterator<Entry<String, String>> iter = oldSystemProperties.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<String, String> entry = iter.next();
            iter.remove();
            setSystemProperty(entry.getKey(), entry.getValue());
        }
    }

    public static void setSystemProperty(String key, String value) {
        if (value == null)
            System.clearProperty(key);
        else
            System.setProperty(key, value);
    }
}
