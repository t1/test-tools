package com.github.t1.testtools;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.net.URI;

import javax.ws.rs.client.*;

import org.mockito.Mockito;

public class WebTargetMockBuilder {
    private final Client client = mock(Client.class);

    public WebTargetResponseBuilder when(URI uri) {
        return new WebTargetResponseBuilder(uri);
    }

    public Client build() {
        return client;
    }

    public class WebTargetResponseBuilder {
        private final URI uri;
        private final WebTarget webTarget = mock(WebTarget.class);

        private WebTargetResponseBuilder(URI uri) {
            this.uri = uri;
            Mockito.when(client.target(uri)).thenReturn(webTarget);
        }

        public WebTargetResponseBuilder at(String path) {
            Mockito.when(webTarget.path(path)).thenReturn(webTarget);
            return this;
        }

        public <T> WebTargetMockBuilder thenReturn(Class<T> responseType, T response) {
            Invocation.Builder invocationBuilder = mock(Invocation.Builder.class);
            Mockito.when(webTarget.request()).thenReturn(invocationBuilder);
            Mockito.when(webTarget.request(anyString())).thenReturn(invocationBuilder);
            Mockito.when(invocationBuilder.get(responseType)).thenReturn(response);
            return WebTargetMockBuilder.this;
        }
    }
}
