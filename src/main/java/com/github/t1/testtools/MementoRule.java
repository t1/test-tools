package com.github.t1.testtools;

import lombok.*;
import org.junit.rules.ExternalResource;

import java.util.Optional;
import java.util.function.*;

@RequiredArgsConstructor
public class MementoRule<T> extends ExternalResource {
    @NonNull
    private final Supplier<T> supplier;
    @NonNull
    private final Consumer<T> consumer;
    @NonNull
    private final Optional<T> newValue;

    private T origValue;

    public MementoRule(Supplier<T> supplier, Consumer<T> consumer) {
        this(supplier, consumer, Optional.empty());
    }

    public MementoRule(Supplier<T> supplier, Consumer<T> consumer, T newValue) {
        this(supplier, consumer, Optional.of(newValue));
    }

    @Override
    public void before() {
        newValue.ifPresent(this::set);
    }

    public void set(T value) {
        this.origValue = supplier.get();
        consumer.accept(value);
    }

    @Override
    public void after() {
        consumer.accept(origValue);
    }
}
