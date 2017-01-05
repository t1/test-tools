package com.github.t1.testtools;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import static com.github.t1.testtools.TestLoggerRule.CamelState.*;
import static java.lang.Character.*;

public class TestLoggerRule extends TestWatcher {
    private long startTime;

    @Override
    protected void starting(Description description) {
        log("> " + toString(description));
        startTime = System.currentTimeMillis();
    }

    @Override
    protected void finished(Description description) {
        log("< " + toString(description) + " [took " + (System.currentTimeMillis() - startTime) + "ms]");
    }

    private String toString(Description description) {
        StringBuilder out = new StringBuilder();
        out.append(description.getClassName());
        if (description.getMethodName() != null) {
            out.append(" # ");
            out.append(camelToSpaces(description.getMethodName()));
        }
        return out.toString();
    }

    enum CamelState {
        lower {
            @Override public CamelState apply(char c, StringBuilder out) {
                if (isDigit(c)) {
                    appendSpace(out).append(c);
                    return digits;
                } else if (isUpperCase(c)) {
                    appendSpace(out).append(toLowerCase(c));
                    return upper;
                } else {
                    out.append(c);
                    return lower;
                }
            }
        },
        upper {
            @Override public CamelState apply(char c, StringBuilder out) {
                if (isDigit(c)) {
                    appendSpace(out).append(c);
                    return digits;
                } else {
                    out.append(toLowerCase(c));
                    return (isUpperCase(c)) ? upper : lower;
                }
            }
        },
        digits {
            @Override public CamelState apply(char c, StringBuilder out) {
                if (isDigit(c)) {
                    out.append(c);
                    return digits;
                }
                CamelState.appendSpace(out).append(toLowerCase(c));
                return (isUpperCase(c)) ? upper : lower;
            }
        };

        public abstract CamelState apply(char c, StringBuilder out);

        private static StringBuilder appendSpace(StringBuilder out) {
            if (out.length() > 0)
                out.append(' ');
            return out;
        }
    }

    public static String camelToSpaces(String string) {
        StringBuilder out = new StringBuilder();
        CamelState state = lower;
        for (char c : string.toCharArray()) {
            state = state.apply(c, out);
        }
        return out.toString();
    }

    public void log(String message) {
        System.out.println("-------------------- " + message);
    }
}
