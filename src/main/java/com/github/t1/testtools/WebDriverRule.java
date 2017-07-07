package com.github.t1.testtools;

import lombok.SneakyThrows;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.junit.rules.ExternalResource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.interactions.Actions;

import java.net.*;

@Slf4j
public class WebDriverRule extends ExternalResource {
    @Delegate public final WebDriver driver;
    @Delegate private final WebDriver.Options manage;

    public WebDriverRule() { this(new HtmlUnitDriver(true)); }

    public WebDriverRule(WebDriver driver) {
        this.driver = driver;
        this.manage = driver.manage();
    }

    @Override protected void after() { driver.close(); }


    @SneakyThrows(MalformedURLException.class) public void navigateTo(URI uri) { driver.navigate().to(uri.toURL()); }

    public URI currentUri() { return URI.create(driver.getCurrentUrl()); }

    public Actions buildAction() { return new Actions(driver); }
}
