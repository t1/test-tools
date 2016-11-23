package com.github.t1.testtools;

import org.junit.rules.ExternalResource;

import java.io.*;

public class SystemOutCaptorRule extends ExternalResource {
    @SuppressWarnings("resource") private final ByteArrayOutputStream err = new ByteArrayOutputStream();
    @SuppressWarnings("resource") private final ByteArrayOutputStream out = new ByteArrayOutputStream();

    private PrintStream oldSystemErr;
    private PrintStream oldSystemOut;

    @SuppressWarnings("UseOfSystemOutOrSystemErr") @Override
    public void before() {
        oldSystemErr = System.err;
        System.setErr(new PrintStream(err));
        oldSystemOut = System.out;
        System.setOut(new PrintStream(out));
    }

    public String out() { return out.toString().trim(); }

    public String err() { return err.toString().trim(); }

    @Override
    public void after() {
        System.setErr(oldSystemErr);
        System.setOut(oldSystemOut);
    }
}
