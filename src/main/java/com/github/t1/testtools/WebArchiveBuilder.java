package com.github.t1.testtools;

import lombok.SneakyThrows;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.*;

import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.Files.*;

@SuppressWarnings("WeakerAccess")
public class WebArchiveBuilder {
    private static final Path SRC_MAIN_JAVA = Paths.get("src/main/java");

    public final PomEquippedResolveStage POM = Maven.resolver().loadPomFromFile("pom.xml");

    public final WebArchive webArchive = ShrinkWrap.create(WebArchive.class);

    public WebArchiveBuilder with(Package rootPackage) {
        addPackageAndDependencies(rootPackage);
        addResources();
        POM.importRuntimeDependencies();
        return this;
    }

    /** include only the main source package, not the tests in the same package */
    public WebArchiveBuilder addPackageAndDependencies(Class<?> type) {
        return addPackageAndDependencies(type.getPackage());
    }

    /** include only the main source package, not the tests in the same package */
    public WebArchiveBuilder addPackageAndDependencies(Package pkg) {
        addPackage(pkg);
        addDependencies(pkg);
        return this;
    }

    @SneakyThrows(IOException.class)
    private void addPackage(Package pkg) {
        Path path = resolvePackagePath(pkg);
        for (Path file : newDirectoryStream(path)) {
            if (isDirectory(file))
                continue;
            if (isSourceFile(file)) {
                String className = toClassName(file);
                webArchive.addClass(className);
            }
        }
    }

    private Path resolvePackagePath(Package pkg) {
        Path path = Paths.get(pkg.getName().replace('.', '/'));
        return SRC_MAIN_JAVA.resolve(path);
    }

    private boolean isSourceFile(Path path) {
        String fileName = path.getFileName().toString();
        return fileName.endsWith(".java") && !"package-info.java".equals(fileName);
    }

    private String toClassName(Path path) {
        String fileName = SRC_MAIN_JAVA.relativize(path).toString();
        assert fileName.endsWith(".java");
        return fileName.substring(0, fileName.length() - 5).replace('/', '.');
    }

    private void addDependencies(Package pkg) {
        if (!pkg.isAnnotationPresent(DependsUpon.class))
            return;
        for (Class<?> dependency : pkg.getAnnotation(DependsUpon.class).packagesOf()) {
            Package dependencyPackage = dependency.getPackage();
            if (!isDirectory(resolvePackagePath(dependencyPackage)))
                continue;
            addPackageAndDependencies(dependencyPackage);
        }
    }

    private void addResources() {
        webArchive.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    public WebArchiveBuilder library(String groupId, String artifactId) {
        webArchive.addAsLibraries(POM
                .resolve(groupId + ":" + artifactId)
                .withTransitivity()
                .asFile());
        return this;
    }

    public WebArchiveBuilder print() {
        System.out.println(
                "------------------------------------------------------------------------------------------");
        System.out.println(webArchive.toString(true));
        System.out.println(
                "------------------------------------------------------------------------------------------");
        return this;
    }

    public WebArchive build() {
        return webArchive;
    }
}
