package com.github.t1.testtools;

import java.util.*;
import java.util.Map.Entry;

import org.junit.rules.ExternalResource;

public class SystemPropertiesRule extends ExternalResource {
    private final Map<String, String> oldSystemProperties = new HashMap<>();

    public void given(String name, Object value) {
        String previousValue = System.setProperty(name, value.toString());
        oldSystemProperties.put(name, previousValue);
    }

    @Override
    public void after() {
        for (Entry<String, String> entry : oldSystemProperties.entrySet())
            if (entry.getValue() == null)
                System.clearProperty(entry.getKey());
            else
                System.setProperty(entry.getKey(), entry.getValue());
    }
}
