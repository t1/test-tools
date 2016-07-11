package com.github.t1.testtools;

import lombok.*;
import org.junit.rules.ExternalResource;

import java.io.*;
import java.nio.file.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class FileMemento extends ExternalResource implements AutoCloseable {
    public static String readFile(Path path) throws IOException {
        return new String(Files.readAllBytes(path));
    }

    private final Supplier<Path> pathSupplier;
    private Path path;

    @Getter
    @Setter
    private String orig;

    private boolean existed;

    public FileMemento(String path) { this(Paths.get(path)); }

    public FileMemento(File file) { this(file.toPath()); }

    public FileMemento(Path path) { this(() -> path); }

    public Path getPath() {
        if (path == null)
            path = pathSupplier.get();
        return path;
    }

    @Override
    protected void before() throws IOException { setup(); }

    public FileMemento setup() throws IOException {
        this.existed = Files.isRegularFile(getPath());
        if (this.existed)
            this.orig = read();
        return this;
    }

    public String read() throws IOException {
        return readFile(getPath());
    }

    public void write(String contents) throws IOException {
        Files.write(getPath(), contents.getBytes());
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
