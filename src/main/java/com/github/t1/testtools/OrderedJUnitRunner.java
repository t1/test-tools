package com.github.t1.testtools;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.*;

import java.io.*;
import java.util.List;

import static java.nio.charset.StandardCharsets.*;
import static java.util.Comparator.*;
import static java.util.stream.Collectors.*;

/**
 * JUnit launches tests in an arbitrary but fixed order (by their checksum). Use this {@link org.junit.runner.Runner}
 * to run the tests in the order they are defined in the source file.
 *
 * NOTE: Handle With Care! Tests should generally independent of each other, so the should be runnable in any order.
 * It's a smell to rely on the order, but sometimes it's necessary (esp. with integration tests).
 *
 * @implNote Since Java 1.7 {@link Class#getDeclaredMethods()} returns no particular order, so we look into the byte code.
 */
public class OrderedJUnitRunner extends BlockJUnit4ClassRunner {
    private static final int BUFFER_SIZE = 8192;

    public OrderedJUnitRunner(Class type) throws InitializationError {
        super(type);
    }

    @Override
    protected List<FrameworkMethod> computeTestMethods() {
        return super.computeTestMethods().stream().sorted(comparing(this::getMethodIndex)).collect(toList());
    }

    private int getMethodIndex(FrameworkMethod frameworkMethod) {
        String pattern = toStringWithLengthPrefix(frameworkMethod);
        String classFile = new String(read(frameworkMethod.getDeclaringClass()), UTF_8);

        return classFile.indexOf(pattern);
    }

    private String toStringWithLengthPrefix(FrameworkMethod frameworkMethod) {
        String methodName = frameworkMethod.getName();
        int length = methodName.length();
        return toByte(length >> 8) + toByte(length) + methodName;
    }

    private String toByte(int character) { return Character.toString((char) character); }

    private byte[] read(Class type) {
        try (InputStream inputStream = type.getResourceAsStream(type.getSimpleName() + ".class")) {
            return readAllBytes(inputStream);
        } catch (IOException e) {
            return new byte[0];
        }
    }

    private byte[] readAllBytes(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        copy(in, out);
        return out.toByteArray();
    }

    public static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[BUFFER_SIZE];
        while (true) {
            int n = in.read(buf);
            if (n < 0)
                break;
            out.write(buf, 0, n);
        }
    }
}
