package com.github.t1.testtools;

import lombok.*;
import org.junit.*;

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import static com.github.t1.testtools.AbstractPackageDependenciesTest.*;
import static com.github.t1.testtools.DependencyGraphTest.DependencyBuilder.*;
import static java.nio.charset.StandardCharsets.*;
import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;

public class DependencyGraphTest {
    @Value
    @RequiredArgsConstructor(staticName = "givenDependency")
    public static class DependencyBuilder {
        String source;

        public void on(String... targets) { packageDependencies.put(source, asSet(targets)); }
    }

    private static Set<String> asSet(String... elements) { return new LinkedHashSet<>(asList(elements)); }

    private String out() throws IOException { return new String(Files.readAllBytes(DEPENDENCIES_DOT), UTF_8); }

    private final AbstractPackageDependenciesTest test = new AbstractPackageDependenciesTest() {};

    @After
    public void tearDown() throws Exception { packageDependencies.clear(); }

    @Test
    public void shouldProduceEmptyGraph() throws Exception {
        test.shouldProduceDotFile();

        assertThat(out()).isEqualTo(""
                + "strict digraph {\n"
                + "    node [shape=box];\n"
                + "\n"
                + "}\n");
    }

    @Test
    public void shouldProduceGraphWithThreeNodes() throws Exception {
        givenDependency("a").on("b", "c");
        givenDependency("b").on("");
        givenDependency("c").on("");

        test.shouldProduceDotFile();

        assertThat(out()).isEqualTo(""
                + "strict digraph {\n"
                + "    node [shape=box];\n"
                + "\n"
                + "    a -> b;\n"
                + "    a -> c;\n"
                + "}\n");
    }

    @Test
    public void shouldProduceGraphWithCommonPackage() throws Exception {
        givenDependency("p.a").on("p.b", "p.c");
        givenDependency("p.b").on("");
        givenDependency("p.c").on("d");

        test.shouldProduceDotFile();

        assertThat(out()).isEqualTo(""
                + "strict digraph {\n"
                + "    node [shape=box];\n"
                + "\n"
                + "    p_a [label=\"a\"];\n"
                + "    p_b [label=\"b\"];\n"
                + "    p_c [label=\"c\"];\n"
                + "\n"
                + "    p_a -> p_b;\n"
                + "    p_a -> p_c;\n"
                + "}\n");
    }

    @Test
    public void shouldProduceGraphWithCommonPackageStartingWithParent() throws Exception {
        givenDependency("p.q").on("p.q.a", "p.q.b");
        givenDependency("p.q.a").on("");
        givenDependency("p.q.b").on("c");

        test.shouldProduceDotFile();

        assertThat(out()).isEqualTo(""
                + "strict digraph {\n"
                + "    node [shape=box];\n"
                + "\n"
                + "    p_q [label=\"q\"];\n"
                + "    p_q_a [label=\"a\"];\n"
                + "    p_q_b [label=\"b\"];\n"
                + "\n"
                + "    p_q -> p_q_a;\n"
                + "    p_q -> p_q_b;\n"
                + "}\n");
    }

    @Test
    public void shouldProduceGraphWithSuffixMatchingPackage() throws Exception {
        givenDependency("p.q.a").on("p.q.b", "p.qx.c");
        givenDependency("p.q.b").on("");
        givenDependency("p.qx.c").on("d");

        test.shouldProduceDotFile();

        assertThat(out()).isEqualTo(""
                + "strict digraph {\n"
                + "    node [shape=box];\n"
                + "\n"
                + "    subgraph cluster_q {\n"
                + "        graph [label=\"q\"];\n"
                + "        p_q_a [label=\"a\"];\n"
                + "        p_q_b [label=\"b\"];\n"
                + "    }\n"
                + "    subgraph cluster_qx {\n"
                + "        graph [label=\"qx\"];\n"
                + "        p_qx_c [label=\"c\"];\n"
                + "    }\n"
                + "\n"
                + "    p_q_a -> p_q_b;\n"
                + "    p_q_a -> p_qx_c;\n"
                + "}\n");
    }

    @Test
    public void shouldProduceGraphWithPackageClusters() throws Exception {
        givenDependency("p.q.a").on("p.q.b", "p.r.a");
        givenDependency("p.q.b").on("x", "y");
        givenDependency("p.r.a").on("p.r.b");
        givenDependency("p.r.b").on("");

        test.shouldProduceDotFile();

        assertThat(out()).isEqualTo(""
                + "strict digraph {\n"
                + "    node [shape=box];\n"
                + "\n"
                + "    subgraph cluster_q {\n"
                + "        graph [label=\"q\"];\n"
                + "        p_q_a [label=\"a\"];\n"
                + "        p_q_b [label=\"b\"];\n"
                + "    }\n"
                + "    subgraph cluster_r {\n"
                + "        graph [label=\"r\"];\n"
                + "        p_r_a [label=\"a\"];\n"
                + "        p_r_b [label=\"b\"];\n"
                + "    }\n"
                + "\n"
                + "    p_q_a -> p_q_b;\n"
                + "    p_q_a -> p_r_a;\n"
                + "    p_r_a -> p_r_b;\n"
                + "}\n");
    }
}
