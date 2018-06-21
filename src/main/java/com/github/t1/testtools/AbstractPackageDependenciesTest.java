package com.github.t1.testtools;

import com.github.t1.graph.Graph;
import com.github.t1.graph.Node;
import com.sun.tools.classfile.Dependencies;
import com.sun.tools.classfile.Dependency;
import com.sun.tools.classfile.Dependency.Finder;
import com.sun.tools.classfile.Dependency.Location;
import com.sun.tools.jdeps.Archive;
import com.sun.tools.jdeps.ClassFileReader;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.fail;

public abstract class AbstractPackageDependenciesTest {
    private static final boolean IS_MAVEN = isMaven();

    private static boolean isMaven() {
        return Files.exists(Paths.get("pom.xml"));
    }

    public static final Path DEPENDENCIES_DOT = Paths.get(IS_MAVEN ? "target" : "out").resolve("dependencies.dot");

    static final Map<String, Set<String>> packageDependencies = new TreeMap<>();

    @BeforeClass
    public static void findDependencies() throws Exception {
        Path path = Paths.get(IS_MAVEN ? "target" : "out/production").resolve("classes");
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

    private static boolean isAnnotation(Dependency dependency) { return type(dependency.getTarget()).isAnnotation(); }

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
            fail(unexpected.size() + " unexpected dependencies:\n" + String.join("\n", unexpected));
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

    /**
     * Produces a file <code>target/dependencies.dot</code> with a directed graph of the inner dependencies,
     * i.e. the dependencies between the packages that have outgoing edges; external dependencies (on libraries, etc.)
     * are omitted.
     * <p>
     * To produce a PNG file, you'll need, e.g., the <code>graphviz</code> package,
     * which contains the <code>dot</code> program. Then you can call, e.g.:
     * <code>dot target/dependencies.dot -Tpng -o target/dependencies.png</code>
     */
    @Test
    public void shouldProduceDotFile() {
        Path common = findCommon(packageDependencies.keySet());
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(DEPENDENCIES_DOT))) {
            printHeader(out);
            printClusters(out, common);
            printEdges(out, common);
            printFooter(out);
        } catch (IOException e) {
            throw new RuntimeException("can'r write " + DEPENDENCIES_DOT, e);
        }
    }

    private void printHeader(PrintWriter out) {
        out.println("strict digraph {");
        out.println("    node [shape=box];");
        out.println();
    }

    private void printClusters(PrintWriter out, Path common) {
        AtomicBoolean oneMore = new AtomicBoolean(false);
        allRoots(common)
                .forEach(pkg -> {
                    List<Path> nodes = allNodes(common)
                            .filter(node -> node.startsWith(common.resolve(pkg)))
                            .collect(toList());
                    if (nodes.size() == 0) {
                        oneMore.set(true);
                        out.println("    " + toId(common) + " [label=\"" + common.getFileName() + "\"];");
                    } else if (nodes.size() == 1 && nodes.get(0).equals(common.resolve(pkg))) {
                        if (!nodes.get(0).equals(pkg)) {
                            oneMore.set(true);
                            out.println("    " + toId(nodes.get(0)) + " [label=\"" + pkg + "\"];");
                        }
                    } else {
                        oneMore.set(true);
                        out.println("    subgraph cluster_" + toId(pkg) + " {");
                        out.println("        graph [label=\"" + pkg.getFileName() + "\"];");
                        nodes.forEach(node ->
                                out.println("        " + toId(node) + " [label=\"" + node.getFileName() + "\"];")
                        );
                        out.println("    }");
                    }
                });
        if (oneMore.get())
            out.println();
    }

    private Stream<Path> allRoots(Path common) {
        return packageDependencies
                .keySet().stream()
                .map(AbstractPackageDependenciesTest::toPath)
                .map(path -> shorten(common, path))
                .map(AbstractPackageDependenciesTest::getRoot)
                .distinct();
    }

    protected Stream<Path> allNodes(Path common) {
        Set<String> all = new HashSet<>();
        all.addAll(packageDependencies.keySet());
        packageDependencies.values().forEach(all::addAll);
        return all.stream().map(AbstractPackageDependenciesTest::toPath);
    }

    private void printEdges(PrintWriter out, Path common) {
        packageDependencies
                .keySet()
                .forEach(source -> packageDependencies
                        .get(source)
                        .forEach(target -> {
                            if (packageDependencies.keySet().contains(target))
                                out.println(""
                                        + "    " + toId(toPath(source))
                                        + " -> " + toId(toPath(target)) + ";");
                        }));
    }

    private void printFooter(PrintWriter out) { out.println("}"); }


    private static Path findCommon(Set<String> strings) {
        Path result = Paths.get("");
        if (strings.iterator().hasNext()) {
            Path first = toPath(strings.iterator().next());
            for (int i = 1; i <= first.getNameCount(); i++) {
                Path common = first.subpath(0, i);
                if (!strings.stream().allMatch(s -> toPath(s).startsWith(common)))
                    break;
                result = common;
            }
        }
        return result;
    }

    private static Path shorten(Path common, Path path) {
        return common.getNameCount() == 0 || common.equals(path) || !path.startsWith(common)
                ? path
                : path.subpath(common.getNameCount(), path.getNameCount());
    }

    private static Path getRoot(Path path) { return path.getName(0); }

    private static Path toPath(String text) { return Paths.get("", text.split("\\.")); }

    private static String toId(Path path) { return path.toString().replace('/', '_'); }
}
