package com.github.t1.testtools;

import lombok.*;
import org.junit.rules.ExternalResource;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class FileMemento extends ExternalResource implements AutoCloseable {
    public static String readFile(Path path) throws IOException {
        return new String(Files.readAllBytes(path));
    }

    @Getter
    private final Path path;

    @Getter
    @Setter
    private String orig;

    private boolean existed;

    public FileMemento(String path) {
        this(Paths.get(path));
    }

    @Override
    protected void before() throws IOException { setup(); }

    public FileMemento setup() throws IOException {
        this.existed = Files.isRegularFile(path);
        if (this.existed)
            this.orig = read();
        return this;
    }

    public String read() throws IOException {
        return readFile(path);
    }

    public void write(String contents) throws IOException {
        Files.write(path, contents.getBytes());
    }

    @Override protected void after() {
        restore();
    }

    @Override public void close() { restore(); }

    @SneakyThrows(IOException.class)
    public void restore() {
        if (existed)
            write(orig);
    }

    public ShutdownHook restoreOnShutdown() {
        ShutdownHook hook = new ShutdownHook();
        Runtime.getRuntime().addShutdownHook(hook);
        return hook;
    }

    public class ShutdownHook extends Thread {
        private int amount = 0;
        private TimeUnit unit;

        public ShutdownHook after(int amount, TimeUnit unit) {
            this.amount = amount;
            this.unit = unit;
            return this;
        }

        @SneakyThrows(InterruptedException.class)
        @Override public void run() {
            if (amount > 0)
                unit.sleep(amount);
            FileMemento.this.restore();
        }
    }
}
