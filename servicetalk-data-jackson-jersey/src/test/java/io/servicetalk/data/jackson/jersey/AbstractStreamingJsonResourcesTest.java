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
package io.servicetalk.data.jackson.jersey;

import io.servicetalk.data.jackson.jersey.resources.PublisherJsonResources;
import io.servicetalk.data.jackson.jersey.resources.SingleJsonResources;
import io.servicetalk.http.api.HttpResponseStatus;
import io.servicetalk.http.router.jersey.AbstractJerseyStreamingHttpServiceTest;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.Application;

import static io.servicetalk.http.api.HttpHeaderValues.APPLICATION_JSON;
import static io.servicetalk.http.api.HttpResponseStatuses.ACCEPTED;
import static io.servicetalk.http.api.HttpResponseStatuses.BAD_REQUEST;
import static io.servicetalk.http.api.HttpResponseStatuses.INTERNAL_SERVER_ERROR;
import static io.servicetalk.http.api.HttpResponseStatuses.OK;
import static java.util.Arrays.asList;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;

public abstract class AbstractStreamingJsonResourcesTest extends AbstractJerseyStreamingHttpServiceTest {
    public static class TestApplication extends Application {
        @Override
        public Set<Class<?>> getClasses() {
            return new HashSet<>(asList(
                    SingleJsonResources.class,
                    PublisherJsonResources.class
            ));
        }
    }

    @Override
    protected Application getApplication() {
        return new TestApplication();
    }

    @Test
    public void postJsonMap() {
        testPostJsonMap("/map", OK);
    }

    @Test
    public void postJsonMapResponse() {
        testPostJsonMap("/map-response", ACCEPTED);
    }

    private void testPostJsonMap(final String path, final HttpResponseStatus expectedStatus) {
        sendAndAssertResponse(post(path, "{\"foo\":\"bar\"}", APPLICATION_JSON),
                expectedStatus, APPLICATION_JSON, jsonEquals("{\"got\":{\"foo\":\"bar\"}}"), __ -> null);
    }

    @Test
    public void postJsonMapFailure() {
        testPostJsonMapFailure("/map");
    }

    @Test
    public void postJsonMapResponseFailure() {
        testPostJsonMapFailure("/map-response");
    }

    private void testPostJsonMapFailure(final String path) {
        sendAndAssertNoResponse(post(path + "?fail=true", "{\"foo\":\"bar\"}", APPLICATION_JSON),
                INTERNAL_SERVER_ERROR);
    }

    @Test
    public void postBrokenJsonMap() {
        testPostBrokenJsonMap("/map");
    }

    @Test
    public void postBrokenJsonMapResponse() {
        testPostBrokenJsonMap("/map-response");
    }

    private void testPostBrokenJsonMap(final String path) {
        sendAndAssertStatusOnly(post(path, "{key:789}", APPLICATION_JSON), BAD_REQUEST);
    }

    @Test
    public void postJsonPojo() {
        testPostJsonPojo("/pojo", OK);
    }

    @Test
    public void postJsonPojoResponse() {
        testPostJsonPojo("/pojo-response", ACCEPTED);
    }

    private void testPostJsonPojo(final String path, final HttpResponseStatus expectedStatus) {
        sendAndAssertResponse(post(path, "{\"aString\":\"foo\",\"anInt\":123}", APPLICATION_JSON),
                expectedStatus, APPLICATION_JSON, jsonEquals("{\"aString\":\"foox\",\"anInt\":124}"), __ -> null);
    }

    @Test
    public void postJsonPojoFailure() {
        testPostJsonPojoFailure("/pojo");
    }

    @Test
    public void postJsonPojoResponseFailure() {
        testPostJsonPojoFailure("/pojo-response");
    }

    private void testPostJsonPojoFailure(final String path) {
        sendAndAssertNoResponse(post(path + "?fail=true", "{\"aString\":\"foo\",\"anInt\":123}", APPLICATION_JSON),
                INTERNAL_SERVER_ERROR);
    }

    @Test
    public void postBrokenJsonPojo() {
        testPostBrokenJsonPojo("/pojo");
    }

    @Test
    public void postBrokenJsonPojoResponse() {
        testPostBrokenJsonPojo("/pojo-response");
    }

    private void testPostBrokenJsonPojo(final String path) {
        sendAndAssertStatusOnly(post(path, "{key:789}", APPLICATION_JSON), BAD_REQUEST);
    }

    @Test
    public void postInvalidJsonPojo() {
        testPostInvalidJsonPojo("/pojo");
    }

    @Test
    public void postInvalidJsonPojoResponse() {
        testPostInvalidJsonPojo("/pojo-response");
    }

    private void testPostInvalidJsonPojo(final String path) {
        sendAndAssertStatusOnly(post(path, "{\"foo\":\"bar\"}", APPLICATION_JSON), BAD_REQUEST);
    }
}
