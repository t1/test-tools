package com.github.t1.testtools;

import lombok.experimental.UtilityClass;
import org.assertj.core.api.Condition;
import org.assertj.core.description.Description;

import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

@UtilityClass
public class AssertJHelpers {
    @SafeVarargs public static <T> Condition<T> xAllOf(Condition<T>... conditions) {
        List<Condition<T>> failing = new ArrayList<>();
        return new Condition<T>(t -> {
            failing.clear();
            for (Condition<T> condition : conditions)
                if (!condition.matches(t))
                    failing.add(condition);
            return failing.isEmpty();
        }, "dummy")
                .as(description(() -> "all of:\n - " + Stream
                        .of(conditions)
                        .map(condition -> condition.description() + (failing.contains(condition) ? " <failed>" : ""))
                        .collect(joining("\n - "))));
    }

    public static <FROM, TO> Condition<TO> map(Condition<FROM> inner, Function<TO, FROM> function, String mapName) {
        return new Condition<TO>(t -> inner.matches(function.apply(t)), "")
                .as(description(() -> mapName + ": " + inner.description()));
    }

    public static Description description(Supplier<String> supplier) {
        return new Description() {
            @Override public String value() {
                return supplier.get();
            }
        };
    }
}
