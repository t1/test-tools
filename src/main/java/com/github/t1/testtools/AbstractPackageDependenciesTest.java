package com.github.t1.testtools;

import com.github.t1.graph.*;
import com.sun.tools.classfile.*;
import com.sun.tools.classfile.Dependency.*;
import com.sun.tools.jdeps.*;
import org.junit.*;

import java.nio.file.*;
import java.util.ArrayList;
import java.util.*;
import java.util.stream.StreamSupport;

import static java.util.Arrays.*;
import static org.junit.Assert.*;

public abstract class AbstractPackageDependenciesTest {
    static final Map<String, Set<String>> packageDependencies = new TreeMap<>();

    @BeforeClass
    public static void findDependencies() throws Exception {
        Path path = Paths.get("target/classes");
        Archive archive = new Archive(path, ClassFileReader.newInstance(path)) {};
        Finder finder = Dependencies.getClassDependencyFinder();

        archive.reader().getClassFiles()
               .forEach(classFile -> StreamSupport
                       .stream(finder.findDependencies(classFile).spliterator(), false)
                       .filter(dependency -> !isAnnotation(dependency))
                       .filter(dependency -> !self(dependency))
                       .forEach(dependency -> packageDependencies
                               .computeIfAbsent(dependency.getOrigin().getPackageName(), key -> new TreeSet<>())
                               .add(dependency.getTarget().getPackageName())));
    }

    private static boolean self(Dependency dependency) {
        return dependency.getOrigin().getPackageName().equals(dependency.getTarget().getPackageName());
    }

    private static boolean isAnnotation(Dependency dependency) {
        return type(dependency.getTarget()).isAnnotation();
    }

    private static Class<?> type(Location location) {
        try {
            return Class.forName(location.getClassName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected List<String> getAlwaysAllowedPackages() {
        return asList("java.*", "javax.*", "lombok", "org.slf4j", "com.github.t1.log", "com.github.t1.config");
    }

    private List<String> dependenciesOf(Package source) {
        List<String> result = new ArrayList<>();
        if (source.isAnnotationPresent(DependsUpon.class))
            for (Class<?> target : source.getAnnotation(DependsUpon.class).packagesOf())
                result.add(target.getPackage().getName());
        return result;
    }

    @Test
    public void shouldHaveNoCycles() {
        Graph<String> graph = new Graph<>();
        packageDependencies.forEach((key, set) -> {
            Node<String> node = graph.findOrCreateNode(key);
            set.stream()
               .filter(packageDependencies::containsKey)
               .forEach(target -> node.linkedTo(graph.findOrCreateNode(target)));
        });

        graph.topologicalSort();

        System.out.println("----------------------- dependencies:");
        System.out.println(graph.toString());
        System.out.println("-----------------------");
    }

    @Test
    public void shouldHaveOnlyDefinedDependencies() {
        List<String> unexpected = new ArrayList<>();
        packageDependencies
                .keySet().stream()
                .map(Package::getPackage)
                .forEach(sourcePackage -> {
                    List<String> allowed = dependenciesOf(sourcePackage);
                    String source = sourcePackage.getName();
                    packageDependencies
                            .get(source).stream()
                            .filter(target -> !source.equals(target))
                            .filter(target -> !allowed.contains(target))
                            .filter(target -> !isAlwaysAllowed(target))
                            .forEach(target -> unexpected.add(source + " -> " + target));
                });
        if (!unexpected.isEmpty())
            fail("unexpected dependencies:\n" + String.join("\n", unexpected));
    }

    private boolean isAlwaysAllowed(String target) {
        return getAlwaysAllowedPackages()
                .stream()
                .anyMatch(pattern -> {
                    if (pattern.endsWith("*"))
                        return target.startsWith(pattern.substring(0, pattern.length() - 1));
                    else
                        return pattern.equals(target);
                });
    }

    @Test
    public void shouldHaveNoSpecifiedButUnrealizedDependencies() {
        List<String> unrealized = new ArrayList<>();
        packageDependencies
                .keySet().stream()
                .map(Package::getPackage)
                .forEach(sourcePackage ->
                        dependenciesOf(sourcePackage)
                                .stream()
                                .filter(dependency -> !packageDependencies
                                        .get(sourcePackage.getName())
                                        .contains(dependency))
                                .forEach(target -> unrealized.add(
                                        sourcePackage + " -> " + target)));
        if (!unrealized.isEmpty())
            fail("unrealized dependencies:\n" + String.join("\n", unrealized));
    }
}
