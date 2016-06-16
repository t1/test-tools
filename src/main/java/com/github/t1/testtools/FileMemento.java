package com.github.t1.testtools;

import lombok.*;
import org.junit.rules.ExternalResource;

import java.io.IOException;
import java.nio.file.*;

@RequiredArgsConstructor
public class FileMemento extends ExternalResource {
    public static String readFile(Path path) throws IOException {
        return new String(Files.readAllBytes(path));
    }

    @Getter
    private final Path path;
    @Getter
    private String orig;

    public FileMemento(String path) {
        this(Paths.get(path));
    }

    @Override
    protected void before() throws IOException {
        this.orig = read();
    }

    public String read() throws IOException {
        return readFile(path);
    }

    public void write(String contents) throws IOException {
        Files.write(path, contents.getBytes());
    }

    @Override
    protected void after() {
        restore();
    }

    @SneakyThrows(IOException.class)
    public void restore() {
        write(orig);
    }

}
