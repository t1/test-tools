package com.github.t1.testtools;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Condition;
import org.glassfish.jersey.logging.LoggingFeature;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import javax.json.*;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.client.*;
import javax.ws.rs.core.*;
import java.io.*;
import java.net.*;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.*;
import static java.util.logging.Level.*;
import static javax.ws.rs.core.MediaType.*;
import static javax.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.*;
import static org.glassfish.jersey.logging.LoggingFeature.Verbosity.*;

@Slf4j
public abstract class AbstractPage<P extends AbstractPage> {
    private static final java.util.logging.Logger LOGGER =
            java.util.logging.Logger.getLogger(AbstractPage.class.getPackage().getName());
    private static final LoggingFeature LOGGING = new LoggingFeature(LOGGER, FINE, PAYLOAD_TEXT, null);
    private static final Client CLIENT = ClientBuilder.newClient().register(LOGGING);

    private static final Pattern STATUS_OK = Pattern.compile("\\{.*\"status\":\"ok\".*}");

    public static Condition<WebElement> tagName(String expected) {
        return new Condition<>(element -> element.getTagName().equals(expected), "tag name '%s'", expected);
    }

    public static Condition<WebElement> attr(String name, String expected) {
        return new Condition<>(element -> element.getAttribute(name).equals(expected), "%s=\"%s\"", name, expected);
    }

    public static Condition<WebElement> text(String expected) {
        return new Condition<>(element -> element.getText().equals(expected), "text body [%s]", expected);
    }

    @SneakyThrows(UnsupportedEncodingException.class)
    public static String urlEncode(URI uri) { return URLEncoder.encode(uri.toString(), UTF_8.name()); }

    public static URI stripQuery(URI uri) {
        if (uri != null && uri.getQuery() != null)
            uri = UriBuilder.fromUri(uri).replaceQuery(null).build();
        return uri;
    }

    public static JsonObject readJsonObject(Response response) {
        try (JsonReader reader = Json.createReader(response.readEntity(InputStream.class))) {
            checkContentType(response, APPLICATION_JSON_TYPE); // close response even when failing
            return reader.readObject();
        }
    }

    public static void checkContentType(Response response, MediaType mediaType) {
        String contentType = response.getHeaderString("Content-Type");
        if (!mediaType.isCompatible(MediaType.valueOf(contentType)))
            throw new ServerErrorException(Response
                    .status(BAD_GATEWAY)
                    .entity("unexpected content type: " + contentType + " expected: " + mediaType)
                    .build());
    }


    private final WebDriverRule driver;
    private final URI uri;

    public AbstractPage(WebDriverRule driver, URI uri) {
        this.driver = driver;
        this.uri = uri;
        PageFactory.initElements(driver.driver, this);
    }

    @Override public String toString() { return "page:" + uri; }

    public P navigateTo() {
        navigateTo(uri);
        return self();
    }

    @SuppressWarnings("unchecked") private P self() { return (P) this; }

    private void navigateTo(URI uri) {
        log.debug("navigate to {}", uri);
        driver.navigateTo(uri);
    }

    public URI fullUri() { return uri; }

    public URI pageUri() {
        return stripQuery(currentUri());
    }

    public URI currentUri() { return driver.currentUri(); }

    private String getPageSource() { return driver.getPageSource(); }

    public void deleteAllCookies() {
        navigateTo(URI.create(uri + "/health"));
        assertThat(getPageSource()).matches(STATUS_OK);
        if (!driver.getCookies().isEmpty()) {
            log.debug("delete cookies on {}: {}", uri, driver.getCookies());
            driver.deleteAllCookies();
        }
    }

    public boolean isOpen() { return pageUri().equals(uri); }

    public void assertHealthy() {
        WebTarget target = CLIENT.target(uri).path("/health");
        Response response = target.request(APPLICATION_JSON_TYPE).get();
        response.bufferEntity();
        assertThat(response.getStatusInfo())
                .describedAs("response to GET of %s on %s: %s", target.getUri(), getClass().getSimpleName(),
                        response.readEntity(String.class))
                .isEqualTo(OK);
        JsonObject entity = readJsonObject(response);
        log.debug("health of {}: {}", target.getUri(), entity);
        assertThat(entity.getString("status")).isEqualTo("ok");
    }

    public abstract AbstractPageAsserts assertOpen();

    public abstract class AbstractPageAsserts<A extends AbstractPageAsserts<A>> {
        @SuppressWarnings("unchecked") private A self = (A) this;

        public AbstractPageAsserts() {
            assertThat(pageUri()).isEqualTo(uri);
            hasTitle();
        }

        protected abstract A hasTitle();

        public A hasTitle(String expected) {
            assertThat(driver.getTitle())
                    .describedAs(description("title"))
                    .isEqualTo(expected);
            return self;
        }
    }

    public String description(String what) { return what + " in:\n---------\n" + getPageSource() + "\n---------"; }
}
