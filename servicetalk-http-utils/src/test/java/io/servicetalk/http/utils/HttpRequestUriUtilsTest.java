/*
 * Copyright © 2018 Apple Inc. and the ServiceTalk project authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.servicetalk.http.utils;

import io.servicetalk.http.api.StreamingHttpRequest;
import io.servicetalk.transport.api.ConnectionContext;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import javax.net.ssl.SSLSession;

import static io.servicetalk.http.api.HttpHeaderNames.HOST;
import static io.servicetalk.http.api.HttpRequestMethods.CONNECT;
import static io.servicetalk.http.api.HttpRequestMethods.GET;
import static io.servicetalk.http.api.HttpRequestMethods.OPTIONS;
import static io.servicetalk.http.api.StreamingHttpRequests.newRequest;
import static io.servicetalk.http.utils.HttpRequestUriUtils.getBaseRequestUri;
import static io.servicetalk.http.utils.HttpRequestUriUtils.getEffectiveRequestUri;
import static java.net.InetSocketAddress.createUnresolved;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpRequestUriUtilsTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private ConnectionContext ctx;

    @Test
    public void originForm() {
        when(ctx.getLocalAddress()).thenReturn(createUnresolved("my.site.com", 80));

        final StreamingHttpRequest<Object> req = newRequest(GET, "/some/path.html?query");

        assertThat(getEffectiveRequestUri(ctx, req, null, null, false),
                is("http://my.site.com/some/path.html?query"));
        assertThat(getEffectiveRequestUri(ctx, req, "https", null, false),
                is("https://my.site.com:80/some/path.html?query"));
        assertThat(getEffectiveRequestUri(ctx, req, null, "other.site.com:1234", false),
                is("http://other.site.com:1234/some/path.html?query"));
        assertThat(getEffectiveRequestUri(ctx, req, "https", "other.site.com:1234", false),
                is("https://other.site.com:1234/some/path.html?query"));
        assertThat(getEffectiveRequestUri(ctx, req, "https", "jdoe@other.site.com:1234", false),
                is("https://jdoe@other.site.com:1234/some/path.html?query"));
        assertThat(getEffectiveRequestUri(ctx, req, "https", "jdoe@other.site.com:1234", true),
                is("https://jdoe@other.site.com:1234/some/path.html?query"));
        assertThat(getEffectiveRequestUri(ctx, req, null, "jdoe@other.site.com:1234", false),
                is("http://jdoe@other.site.com:1234/some/path.html?query"));
        assertThat(getEffectiveRequestUri(ctx, req, null, "jdoe@other.site.com:1234", true),
                is("http://jdoe@other.site.com:1234/some/path.html?query"));

        assertThat(getBaseRequestUri(ctx, req, null, null, false),
                is("http://my.site.com/"));
        assertThat(getBaseRequestUri(ctx, req, "https", null, false),
                is("https://my.site.com:80/"));
        assertThat(getBaseRequestUri(ctx, req, null, "other.site.com:1234", false),
                is("http://other.site.com:1234/"));
        assertThat(getBaseRequestUri(ctx, req, "https", "other.site.com:1234", false),
                is("https://other.site.com:1234/"));
        assertThat(getBaseRequestUri(ctx, req, "https", "jdoe@other.site.com:1234", false),
                is("https://jdoe@other.site.com:1234/"));
        assertThat(getBaseRequestUri(ctx, req, "https", "jdoe@other.site.com:1234", true),
                is("https://jdoe@other.site.com:1234/"));
        assertThat(getBaseRequestUri(ctx, req, null, "jdoe@other.site.com:1234", false),
                is("http://jdoe@other.site.com:1234/"));
        assertThat(getBaseRequestUri(ctx, req, null, "jdoe@other.site.com:1234", true),
                is("http://jdoe@other.site.com:1234/"));
    }

    @Test
    public void originFormWithHostHeader() {
        final StreamingHttpRequest<Object> req = newRequest(GET, "/some/path.html?query");
        req.getHeaders().set(HOST, "my.site.com:8080");

        assertThat(getEffectiveRequestUri(ctx, req, null, null, false),
                is("http://my.site.com:8080/some/path.html?query"));
        assertThat(getEffectiveRequestUri(ctx, req, "https", null, false),
                is("https://my.site.com:8080/some/path.html?query"));
        assertThat(getEffectiveRequestUri(ctx, req, null, "other.site.com:1234", false),
                is("http://other.site.com:1234/some/path.html?query"));
        assertThat(getEffectiveRequestUri(ctx, req, "https", "other.site.com:1234", false),
                is("https://other.site.com:1234/some/path.html?query"));

        assertThat(getBaseRequestUri(ctx, req, null, null, false),
                is("http://my.site.com:8080/"));
        assertThat(getBaseRequestUri(ctx, req, "https", null, false),
                is("https://my.site.com:8080/"));
        assertThat(getBaseRequestUri(ctx, req, null, "other.site.com:1234", false),
                is("http://other.site.com:1234/"));
        assertThat(getBaseRequestUri(ctx, req, "https", "other.site.com:1234", false),
                is("https://other.site.com:1234/"));
    }

    @Test
    public void absoluteForm() {
        final StreamingHttpRequest<Object> req = newRequest(GET, "https://my.site.com/some/path.html?query");

        assertThat(getEffectiveRequestUri(ctx, req, null, null, false),
                is("https://my.site.com/some/path.html?query"));
        assertThat(getEffectiveRequestUri(ctx, req, "https", null, false),
                is("https://my.site.com/some/path.html?query"));
        assertThat(getEffectiveRequestUri(ctx, req, null, "other.site.com:1234", false),
                is("https://my.site.com/some/path.html?query"));
        assertThat(getEffectiveRequestUri(ctx, req, "https", "other.site.com:1234", false),
                is("https://my.site.com/some/path.html?query"));
        assertThat(getEffectiveRequestUri(ctx, req, "http", "jdoe@other.site.com:1234", false),
                is("https://my.site.com/some/path.html?query"));
        assertThat(getEffectiveRequestUri(ctx, req, "http", "jdoe@other.site.com:1234", true),
                is("https://my.site.com/some/path.html?query"));
        assertThat(getEffectiveRequestUri(ctx, req, null, "jdoe@other.site.com:1234", false),
                is("https://my.site.com/some/path.html?query"));
        assertThat(getEffectiveRequestUri(ctx, req, null, "jdoe@other.site.com:1234", true),
                is("https://my.site.com/some/path.html?query"));

        assertThat(getBaseRequestUri(ctx, req, null, null, false),
                is("https://my.site.com/"));
        assertThat(getBaseRequestUri(ctx, req, "https", null, false),
                is("https://my.site.com/"));
        assertThat(getBaseRequestUri(ctx, req, null, "other.site.com:1234", false),
                is("https://my.site.com/"));
        assertThat(getBaseRequestUri(ctx, req, "https", "other.site.com:1234", false),
                is("https://my.site.com/"));
        assertThat(getBaseRequestUri(ctx, req, "http", "jdoe@other.site.com:1234", false),
                is("https://my.site.com/"));
        assertThat(getBaseRequestUri(ctx, req, "http", "jdoe@other.site.com:1234", true),
                is("https://my.site.com/"));
        assertThat(getBaseRequestUri(ctx, req, null, "jdoe@other.site.com:1234", false),
                is("https://my.site.com/"));
        assertThat(getBaseRequestUri(ctx, req, null, "jdoe@other.site.com:1234", true),
                is("https://my.site.com/"));
    }

    @Test
    public void absoluteFormWithUserInfo() {
        final StreamingHttpRequest<Object> req = newRequest(GET, "https://jdoe@my.site.com/some/path.html?query");

        assertThat(getEffectiveRequestUri(ctx, req, null, null, true),
                is("https://jdoe@my.site.com/some/path.html?query"));
        assertThat(getEffectiveRequestUri(ctx, req, null, null, false),
                is("https://my.site.com/some/path.html?query"));
        assertThat(getEffectiveRequestUri(ctx, req, "https", null, false),
                is("https://my.site.com/some/path.html?query"));
        assertThat(getEffectiveRequestUri(ctx, req, null, "other.site.com:1234", false),
                is("https://my.site.com/some/path.html?query"));
        assertThat(getEffectiveRequestUri(ctx, req, "https", "other.site.com:1234", false),
                is("https://my.site.com/some/path.html?query"));
        assertThat(getEffectiveRequestUri(ctx, req, "http", "jsmith@other.site.com:1234", true),
                is("https://jdoe@my.site.com/some/path.html?query"));
        assertThat(getEffectiveRequestUri(ctx, req, "http", "jsmith@other.site.com:1234", false),
                is("https://my.site.com/some/path.html?query"));
        assertThat(getEffectiveRequestUri(ctx, req, null, "jsmith@other.site.com:1234", false),
                is("https://my.site.com/some/path.html?query"));
        assertThat(getEffectiveRequestUri(ctx, req, null, "jsmith@other.site.com:1234", true),
                is("https://jdoe@my.site.com/some/path.html?query"));

        assertThat(getBaseRequestUri(ctx, req, null, null, true),
                is("https://jdoe@my.site.com/"));
        assertThat(getBaseRequestUri(ctx, req, null, null, false),
                is("https://my.site.com/"));
        assertThat(getBaseRequestUri(ctx, req, "https", null, false),
                is("https://my.site.com/"));
        assertThat(getBaseRequestUri(ctx, req, null, "other.site.com:1234", false),
                is("https://my.site.com/"));
        assertThat(getBaseRequestUri(ctx, req, "https", "other.site.com:1234", false),
                is("https://my.site.com/"));
        assertThat(getBaseRequestUri(ctx, req, "http", "jsmith@other.site.com:1234", true),
                is("https://jdoe@my.site.com/"));
        assertThat(getBaseRequestUri(ctx, req, "http", "jsmith@other.site.com:1234", false),
                is("https://my.site.com/"));
        assertThat(getBaseRequestUri(ctx, req, null, "jsmith@other.site.com:1234", false),
                is("https://my.site.com/"));
        assertThat(getBaseRequestUri(ctx, req, null, "jsmith@other.site.com:1234", true),
                is("https://jdoe@my.site.com/"));
    }

    @Test
    public void authorityForm() {
        when(ctx.getSslSession()).thenReturn(mock(SSLSession.class));

        final StreamingHttpRequest<Object> req = newRequest(CONNECT, "my.site.com:9876");

        assertThat(getEffectiveRequestUri(ctx, req, null, null, false),
                is("https://my.site.com:9876"));
        assertThat(getEffectiveRequestUri(ctx, req, "http", null, false),
                is("http://my.site.com:9876"));
        assertThat(getEffectiveRequestUri(ctx, req, null, "other.site.com:1234", false),
                is("https://other.site.com:1234"));
        assertThat(getEffectiveRequestUri(ctx, req, "http", "other.site.com:1234", false),
                is("http://other.site.com:1234"));
        assertThat(getEffectiveRequestUri(ctx, req, "http", "jsmith@other.site.com:1234", true),
                is("http://jsmith@other.site.com:1234"));
        assertThat(getEffectiveRequestUri(ctx, req, "http", "jsmith@other.site.com:1234", false),
                is("http://jsmith@other.site.com:1234"));
        assertThat(getEffectiveRequestUri(ctx, req, null, "jsmith@other.site.com:1234", false),
                is("https://jsmith@other.site.com:1234"));
        assertThat(getEffectiveRequestUri(ctx, req, null, "jsmith@other.site.com:1234", true),
                is("https://jsmith@other.site.com:1234"));

        assertThat(getBaseRequestUri(ctx, req, null, null, false),
                is("https://my.site.com:9876/"));
        assertThat(getBaseRequestUri(ctx, req, "http", null, false),
                is("http://my.site.com:9876/"));
        assertThat(getBaseRequestUri(ctx, req, null, "other.site.com:1234", false),
                is("https://other.site.com:1234/"));
        assertThat(getBaseRequestUri(ctx, req, "http", "other.site.com:1234", false),
                is("http://other.site.com:1234/"));
        assertThat(getBaseRequestUri(ctx, req, "http", "jsmith@other.site.com:1234", true),
                is("http://jsmith@other.site.com:1234/"));
        assertThat(getBaseRequestUri(ctx, req, "http", "jsmith@other.site.com:1234", false),
                is("http://jsmith@other.site.com:1234/"));
        assertThat(getBaseRequestUri(ctx, req, null, "jsmith@other.site.com:1234", false),
                is("https://jsmith@other.site.com:1234/"));
        assertThat(getBaseRequestUri(ctx, req, null, "jsmith@other.site.com:1234", true),
                is("https://jsmith@other.site.com:1234/"));
    }

    @Test
    public void asteriskForm() {
        when(ctx.getLocalAddress()).thenReturn(createUnresolved("my.site.com", 443));
        when(ctx.getSslSession()).thenReturn(mock(SSLSession.class));

        final StreamingHttpRequest<Object> req = newRequest(OPTIONS, "*");

        assertThat(getEffectiveRequestUri(ctx, req, null, null, false),
                is("https://my.site.com"));
        assertThat(getEffectiveRequestUri(ctx, req, "http", null, false),
                is("http://my.site.com:443"));
        assertThat(getEffectiveRequestUri(ctx, req, null, "other.site.com:1234", false),
                is("https://other.site.com:1234"));
        assertThat(getEffectiveRequestUri(ctx, req, "http", "other.site.com:1234", false),
                is("http://other.site.com:1234"));

        assertThat(getBaseRequestUri(ctx, req, null, null, false),
                is("https://my.site.com/"));
        assertThat(getBaseRequestUri(ctx, req, "http", null, false),
                is("http://my.site.com:443/"));
        assertThat(getBaseRequestUri(ctx, req, null, "other.site.com:1234", false),
                is("https://other.site.com:1234/"));
        assertThat(getBaseRequestUri(ctx, req, "http", "other.site.com:1234", false),
                is("http://other.site.com:1234/"));
    }

    @Test
    public void asteriskFormWithHostHeader() {
        final StreamingHttpRequest<Object> req = newRequest(OPTIONS, "*");
        req.getHeaders().set(HOST, "my.site.com:8080");

        assertThat(getEffectiveRequestUri(ctx, req, null, null, false),
                is("http://my.site.com:8080"));
        assertThat(getEffectiveRequestUri(ctx, req, "https", null, false),
                is("https://my.site.com:8080"));
        assertThat(getEffectiveRequestUri(ctx, req, null, "other.site.com:1234", false),
                is("http://other.site.com:1234"));
        assertThat(getEffectiveRequestUri(ctx, req, "https", "other.site.com:1234", false),
                is("https://other.site.com:1234"));
        assertThat(getEffectiveRequestUri(ctx, req, "http", "jsmith@other.site.com:1234", true),
                is("http://jsmith@other.site.com:1234"));
        assertThat(getEffectiveRequestUri(ctx, req, "http", "jsmith@other.site.com:1234", false),
                is("http://jsmith@other.site.com:1234"));
        assertThat(getEffectiveRequestUri(ctx, req, null, "jsmith@other.site.com:1234", false),
                is("http://jsmith@other.site.com:1234"));
        assertThat(getEffectiveRequestUri(ctx, req, null, "jsmith@other.site.com:1234", true),
                is("http://jsmith@other.site.com:1234"));

        assertThat(getBaseRequestUri(ctx, req, null, null, false),
                is("http://my.site.com:8080/"));
        assertThat(getBaseRequestUri(ctx, req, "https", null, false),
                is("https://my.site.com:8080/"));
        assertThat(getBaseRequestUri(ctx, req, null, "other.site.com:1234", false),
                is("http://other.site.com:1234/"));
        assertThat(getBaseRequestUri(ctx, req, "https", "other.site.com:1234", false),
                is("https://other.site.com:1234/"));
        assertThat(getBaseRequestUri(ctx, req, "http", "jsmith@other.site.com:1234", true),
                is("http://jsmith@other.site.com:1234/"));
        assertThat(getBaseRequestUri(ctx, req, "http", "jsmith@other.site.com:1234", false),
                is("http://jsmith@other.site.com:1234/"));
        assertThat(getBaseRequestUri(ctx, req, null, "jsmith@other.site.com:1234", false),
                is("http://jsmith@other.site.com:1234/"));
        assertThat(getBaseRequestUri(ctx, req, null, "jsmith@other.site.com:1234", true),
                is("http://jsmith@other.site.com:1234/"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void unsupportedFixedSchemeEffectiveRequestUri() {
        final StreamingHttpRequest<Object> req = newRequest(GET, "/some/path.html?query");
        getEffectiveRequestUri(ctx, req, "ftp", null, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void unsupportedFixedSchemeBaseUri() {
        final StreamingHttpRequest<Object> req = newRequest(GET, "/some/path.html?query");
        getBaseRequestUri(ctx, req, "ftp", null, false);
    }
}
