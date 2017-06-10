package com.github.t1.testtools;

import lombok.SneakyThrows;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.junit.rules.ExternalResource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.net.*;

@Slf4j
public class WebDriverRule extends ExternalResource {
    @Delegate public final WebDriver driver = new HtmlUnitDriver();
    @Delegate private final WebDriver.Options manage = driver.manage();

    @SneakyThrows(MalformedURLException.class) public void navigateTo(URI uri) { driver.navigate().to(uri.toURL()); }

    public URI currentUri() { return URI.create(driver.getCurrentUrl()); }
}
