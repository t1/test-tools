package com.github.t1.testtools;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class FileMemento implements BeforeEachCallback, AfterEachCallback, Extension, AutoCloseable {
    @SneakyThrows(IOException.class)
    public static String readFile(Path path) { return new String(Files.readAllBytes(path)); }

    @SneakyThrows(IOException.class)
    public static void writeFile(Path path, String contents) { Files.write(path, contents.getBytes()); }

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

    @Override public void beforeEach(ExtensionContext context) { setup(); }

    public FileMemento setup() {
        this.existed = Files.isRegularFile(getPath());
        if (this.existed)
            this.orig = read();
        return this;
    }

    public String read() {
        return readFile(getPath());
    }

    public void write(String contents) { writeFile(getPath(), contents); }

    @Override public void afterEach(ExtensionContext context) { restore(); }

    @Override public void close() { restore(); }

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
