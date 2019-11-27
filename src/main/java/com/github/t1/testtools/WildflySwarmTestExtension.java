package com.github.t1.testtools;

import lombok.SneakyThrows;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.spi.api.Fraction;

import java.net.URI;

public class WildflySwarmTestExtension implements Extension, BeforeAllCallback, AfterAllCallback {
    private final Swarm swarm = swarm();

    @SneakyThrows private static Swarm swarm() { return new Swarm(); }

    @Override public void beforeAll(ExtensionContext context) throws Exception { swarm.start(); }

    @Override public void afterAll(ExtensionContext context) throws Exception { swarm.stop(); }

    public WildflySwarmTestExtension withProperty(String name, Object value) {
        swarm.withProperty(name, (value == null) ? null : value.toString());
        return this;
    }

    public WildflySwarmTestExtension withFraction(Fraction<?> fraction) {
        swarm.fraction(fraction);
        return this;
    }


    @SneakyThrows public void deploy(WebArchive archive) { swarm.deploy(archive); }


    public URI baseUri() {
        return URI.create("http://" + bindAddress() + ":" + port());
    }

    public String bindAddress() { return System.getProperty("swarm.bind.address", "localhost"); }

    public String port() { return System.getProperty("swarm.http.port", "8080"); }

    public String deployment() {
        String deployment = swarm.configView().asProperties().getProperty("swarm.current.deployment");
        assert deployment.endsWith(".war");
        deployment = deployment.substring(0, deployment.length() - 4);
        return deployment;
    }
}
