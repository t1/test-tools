package com.github.t1.testtools;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class SystemOutCaptorExtension implements BeforeEachCallback, AfterEachCallback, Extension {
    @SuppressWarnings("resource") private final ByteArrayOutputStream err = new ByteArrayOutputStream();
    @SuppressWarnings("resource") private final ByteArrayOutputStream out = new ByteArrayOutputStream();

    private PrintStream oldSystemErr;
    private PrintStream oldSystemOut;

    @Override public void beforeEach(ExtensionContext context) {
        oldSystemErr = System.err;
        System.setErr(new PrintStream(err));
        oldSystemOut = System.out;
        System.setOut(new PrintStream(out));
    }

    public String out() { return out.toString().trim(); }

    public String err() { return err.toString().trim(); }

    @Override public void afterEach(ExtensionContext context) {
        System.setErr(oldSystemErr);
        System.setOut(oldSystemOut);
    }
}
