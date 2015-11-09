package com.github.t1.deployer.tools;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.*;

import jdepend.framework.*;

public abstract class AbstractPackageDependenciesTest {
    private final JDepend jdepend = new JDepend();
    private final DependencyConstraint constraint = new DependencyConstraint();

    @Before
    public final void initAbstractPackageDependenciesTest() throws Exception {
        jdepend.addDirectory("target/classes");
        setupFilters();
        setupDependencies(getDependencyEntryPoints());
        jdepend.analyze();
    }

    protected void setupFilters() {
        PackageFilter filter = new PackageFilter();
        filter.addPackage("java.*");
        filter.addPackage("javax.*");
        filter.addPackage("lombok");
        filter.addPackage("org.slf4j");
        filter.addPackage("com.github.t1.log");
        filter.addPackage("com.github.t1.config");
        jdepend.setFilter(filter);
    }

    public abstract List<Class<?>> getDependencyEntryPoints();

    private void setupDependencies(List<Class<?>> types) {
        for (Class<?> type : types)
            loadDependenciesOf(type.getPackage());
    }

    private JavaPackage loadDependenciesOf(Package pkg) {
        JavaPackage result = new JavaPackage(pkg.getName());
        for (Package target : dependenciesOf(pkg))
            result.dependsUpon(loadDependenciesOf(target));
        constraint.addPackage(result);
        return result;
    }

    private List<Package> dependenciesOf(Package source) {
        List<Package> result = new ArrayList<>();
        if (source.isAnnotationPresent(DependsUpon.class))
            for (Class<?> target : source.getAnnotation(DependsUpon.class).packagesOf())
                result.add(target.getPackage());
        return result;
    }

    private abstract static class DependencyPredicate {
        public abstract boolean apply(JavaPackage javaPackage, JavaPackage efferent);

        @SuppressWarnings({ "unchecked", "rawtypes" })
        protected boolean containsDependency(Collection packages, JavaPackage javaPackage, JavaPackage efferent) {
            for (JavaPackage candidate : (Collection<JavaPackage>) packages) {
                if (equals(javaPackage, candidate)) {
                    Collection<JavaPackage> candidateEfferents = candidate.getEfferents();
                    for (JavaPackage candidateEfferent : candidateEfferents) {
                        if (equals(efferent, candidateEfferent)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        protected boolean equals(JavaPackage left, JavaPackage right) {
            return right.getName().equals(left.getName());
        }
    }

    @Test
    public void shouldHaveNoCycles() {
        checkDependencies("cyclic dependencies", jdepend.getPackages(), new DependencyPredicate() {
            @Override
            public boolean apply(JavaPackage javaPackage, JavaPackage efferent) {
                return efferent.containsCycle();
            }
        });
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void checkDependencies(String message, Collection packages, DependencyPredicate predicate) {
        StringBuilder out = new StringBuilder();
        for (JavaPackage javaPackage : (Collection<JavaPackage>) packages) {
            for (JavaPackage efferent : (Collection<JavaPackage>) javaPackage.getEfferents()) {
                if (predicate.apply(javaPackage, efferent)) {
                    out.append(javaPackage.getName()).append(" -> ").append(efferent.getName()).append("\n");
                }
            }
        }
        if (out.length() > 0) {
            fail(message + ":\n" + out);
        }
    }

    @Test
    public void shouldHaveOnlyDefinedDependencies() {
        checkDependencies("unexpected dependencies", jdepend.getPackages(), new DependencyPredicate() {
            @Override
            public boolean apply(JavaPackage javaPackage, JavaPackage efferent) {
                return !isExpected(javaPackage, efferent);
            }

            private boolean isExpected(JavaPackage javaPackage, JavaPackage efferent) {
                return containsDependency(constraint.getPackages(), javaPackage, efferent);
            }
        });
    }

    @Test
    public void shouldHaveNoSpecifiedButUnrealizedDependencies() {
        checkDependencies("specified but unrealized dependencies", constraint.getPackages(), new DependencyPredicate() {
            @Override
            public boolean apply(JavaPackage javaPackage, JavaPackage efferent) {
                return !containsDependency(jdepend.getPackages(), javaPackage, efferent);
            }
        });
    }
}
