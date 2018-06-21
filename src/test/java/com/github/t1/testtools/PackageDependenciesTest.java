package com.github.t1.testtools;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

import java.util.List;

public class PackageDependenciesTest extends AbstractPackageDependenciesTest {
    protected List<String> getAlwaysAllowedPackages() {
        Builder<String> builder = ImmutableList.builder();
        builder.addAll(super.getAlwaysAllowedPackages());
        builder.add("com.github.t1.log", "com.github.t1.config");
        return builder.build();
    }
}
