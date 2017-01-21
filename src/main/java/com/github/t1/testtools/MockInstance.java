package com.github.t1.testtools;

import javax.enterprise.inject.Instance;
import java.lang.annotation.Annotation;
import java.util.*;

import static java.util.Arrays.*;

/**
 * Binds a list of objects to a CDI Instance. Useful for mocking a CDI app in Dropwizard testing.
 *
 * To bind the instances <code>t0</code>, <code>t1</code>, and <code>t2</code> of type <code>T</code>, do this:
 *
 * <pre>
 * <code>
 * bind(new MockInstance&lt;&gt;(t0, t1, t2)
 *     .to(new TypeLiteral&lt;Instance&lt;T&gt;&gt;() {});
 * </code>
 * </pre>
 */
public class MockInstance<T> implements Instance<T> {
    private final List<T> items;

    @SafeVarargs public MockInstance(T... items) { this.items = asList(items); }

    @Override
    public Iterator<T> iterator() { return items.iterator(); }

    @Override
    public T get() {
        assert items.size() == 1;
        return items.get(0);
    }

    @Override
    public Instance<T> select(Annotation... qualifiers) { throw new UnsupportedOperationException(); }

    @Override
    public <U extends T> Instance<U> select(Class<U> subtype, Annotation... qualifiers) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <U extends T> Instance<U> select(javax.enterprise.util.TypeLiteral<U> subtype, Annotation... qualifiers) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isUnsatisfied() { return false; }

    @Override
    public boolean isAmbiguous() { return false; }

    @Override
    public void destroy(T instance) {}
}
