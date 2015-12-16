package com.github.t1.testtools;

import java.io.IOException;
import java.nio.file.*;

import org.junit.rules.ExternalResource;

import lombok.*;

@RequiredArgsConstructor
public class FileMemento extends ExternalResource {
    public static String readFile(Path path) throws IOException {
        return new String(Files.readAllBytes(path));
    }

    @Getter
    private final Path path;
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
    @SneakyThrows(IOException.class)
    protected void after() {
        write(orig);
    }
}
