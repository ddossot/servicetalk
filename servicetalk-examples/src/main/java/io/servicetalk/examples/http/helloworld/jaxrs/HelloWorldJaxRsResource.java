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
package io.servicetalk.examples.http.helloworld.jaxrs;

import io.servicetalk.buffer.api.Buffer;
import io.servicetalk.buffer.api.BufferAllocator;
import io.servicetalk.concurrent.api.Publisher;
import io.servicetalk.concurrent.api.Single;
import io.servicetalk.http.api.HttpPayloadChunk;
import io.servicetalk.transport.api.ConnectionContext;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

import static io.servicetalk.concurrent.api.Publisher.just;
import static io.servicetalk.http.api.HttpPayloadChunks.newPayloadChunk;
import static java.lang.Math.random;
import static java.util.Collections.singletonMap;
import static java.util.concurrent.TimeUnit.SECONDS;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.accepted;
import static javax.ws.rs.core.Response.ok;

/**
 * JAX-RS resource class that demonstrates some of the features supported by ServiceTalk's Jersey HTTP router.
 */
@Path("greetings")
public class HelloWorldJaxRsResource {
    /**
     * Resource that relies on the Publisher/OIO adapters to produce text responses.
     * <p>
     * Test with:
     * <pre>
     * curl http://localhost:8080/greetings/hello
     * curl http://localhost:8080/greetings/hello?who=turnip
     * </pre>
     *
     * @param who the recipient of the greetings.
     * @return greetings as a {@link String}.
     */
    @GET
    @Path("hello")
    @Produces(TEXT_PLAIN)
    public String hello(@DefaultValue("world") @QueryParam("who") final String who) {
        return "hello " + who;
    }

    /**
     * Resource that relies on the Publisher/OIO adapters and Jackson to consume and produce JSON entities.
     * This project uses ServiceTalk's Jackson provider for Jersey hence no OIO adaptation is involved.
     * <p>
     * Test with:
     * <pre>
     * curl -H 'content-type: application/json' -d '{}' http://localhost:8080/greetings/hello
     * curl -H 'content-type: application/json' -d '{"who":"turnip"}' http://localhost:8080/greetings/hello
     * </pre>
     *
     * @param salutation a {@link Map} that provides salutation data.
     * @return greetings as a {@link Map}.
     */
    @POST
    @Path("hello")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public Map<String, String> hello(final Map<String, String> salutation) {
        return singletonMap("hello", salutation.getOrDefault("who", "world"));
    }

    /**
     * Resource that uses Java's CompletionStage to produce an async response.
     * Note that the {@link ConnectionContext} could also be injected into a class-level {@code @Context} field.
     * <p>
     * Test with:
     * <pre>
     * curl http://localhost:8080/greetings/slow-hello
     * curl http://localhost:8080/greetings/slow-hello?who=doctor
     * </pre>
     *
     * @param who the recipient of the greetings.
     * @param ctx the {@link ConnectionContext}.
     * @return future greetings as a {@link CompletionStage} of {@link String}.
     */
    @GET
    @Path("slow-hello")
    @Produces(TEXT_PLAIN)
    public CompletionStage<String> slowHello(@DefaultValue("world") @QueryParam("who") final String who,
                                             @Context final ConnectionContext ctx) {
        final CompletableFuture<String> delayedResponse = new CompletableFuture<>();
        ctx.getExecutionContext().getExecutor().timer(1, SECONDS)
                .doAfterComplete(() -> delayedResponse.complete("well, hello " + who))
                .subscribe();
        return delayedResponse;
    }

    /**
     * Resource that only relies on {@link Single}s for consuming and producing data, and operators for processing it.
     * Behind the scene, ServiceTalk's aggregation mechanism is used to provide the resource with a
     * {@link Single Single&lt;Buffer&gt;} that contains the whole request entity as a {@link Buffer}.
     * Note that the {@link ConnectionContext} could also be injected into a class-level {@code @Context} field.
     * <p>
     * Test with:
     * <pre>
     * curl -H 'content-type: text/plain' -d 'dolly' http://localhost:8080/greetings/hello
     * </pre>
     *
     * @param who the recipient of the greetings.
     * @param ctx the {@link ConnectionContext}.
     * @return greetings as a {@link Publisher} of {@link HttpPayloadChunk}.
     */
    @POST
    @Path("hello")
    @Consumes(TEXT_PLAIN)
    @Produces(TEXT_PLAIN)
    public Single<Buffer> hello(final Single<Buffer> who,
                                @Context final ConnectionContext ctx) {
        final BufferAllocator allocator = ctx.getExecutionContext().getBufferAllocator();
        return who.map(b -> allocator.newCompositeBuffer()
                .addBuffer(allocator.fromAscii("hello, "))
                .addBuffer(b)
                .addBuffer(allocator.fromAscii("!")));
    }

    /**
     * Resource that only relies on {@link Publisher}s for consuming and producing data,
     * and returns a JAX-RS {@link Response} in order to set its status.
     * No OIO adaptation is involved when requests are dispatched to it,
     * allowing it to fully benefit from ReactiveStream's features like flow control.
     * Note that the {@link ConnectionContext} could also be injected into a class-level {@code @Context} field.
     * <p>
     * Test with:
     * <pre>
     * curl -i -H 'content-type: text/plain' -d 'kitty' http://localhost:8080/greetings/random-hello
     * </pre>
     *
     * @param who the recipient of the greetings.
     * @param ctx the {@link ConnectionContext}.
     * @return greetings as a JAX-RS {@link Response}.
     */
    @POST
    @Path("random-hello")
    @Consumes(TEXT_PLAIN)
    @Produces(TEXT_PLAIN)
    public Response randomHello(final Publisher<HttpPayloadChunk> who,
                                @Context final ConnectionContext ctx) {
        if (random() < .5) {
            return accepted("greetings accepted, call again for a response").build();
        }

        final BufferAllocator allocator = ctx.getExecutionContext().getBufferAllocator();
        final Publisher<HttpPayloadChunk> payload =
                just(newPayloadChunk(allocator.fromAscii("hello "))).concatWith(who);

        // Wrap content Publisher to capture its generic type (i.e. HttpPayloadChunk) so it is handled correctly
        final GenericEntity<Publisher<HttpPayloadChunk>> entity =
                new GenericEntity<Publisher<HttpPayloadChunk>>(payload) { };

        return ok(entity).build();
    }

    /**
     * Resource that only relies on {@link Single}s for consuming and producing data, and operators for processing it.
     * No OIO adaptation is involved when requests are dispatched to it, as it relies on ServiceTalk/Jackson
     * non-blocking (de)serialization.
     * <p>
     * Test with:
     * <pre>
     * curl -H 'content-type: application/json' -d '{}' http://localhost:8080/greetings/single-hello
     * curl -H 'content-type: application/json' -d '{"who":"turnip"}' http://localhost:8080/greetings/single-hello
     * </pre>
     *
     * @param salutation a {@link Single Single&lt;Map&gt;} that provides salutation data.
     * @return greetings as a {@link Single Single&lt;Map&gt;}.
     */
    @POST
    @Path("single-hello")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public Single<Map<String, String>> singleHello(final Single<Map<String, String>> salutation) {
        return salutation.map(m -> singletonMap("single hello", m.getOrDefault("who", "world")));
    }
}
