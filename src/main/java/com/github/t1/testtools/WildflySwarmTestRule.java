package com.github.t1.testtools;

import lombok.SneakyThrows;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.rules.ExternalResource;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.spi.api.Fraction;

import java.net.URI;

public class WildflySwarmTestRule extends ExternalResource {
    private final Swarm swarm = swarm();

    @SneakyThrows private static Swarm swarm() { return new Swarm(); }

    @Override public void before() throws Exception { swarm.start(); }

    @Override @SneakyThrows protected void after() { swarm.stop(); }

    public WildflySwarmTestRule withProperty(String name, Object value) {
        swarm.withProperty(name, (value == null) ? null : value.toString());
        return this;
    }

    public WildflySwarmTestRule withFraction(Fraction<?> fraction) {
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
