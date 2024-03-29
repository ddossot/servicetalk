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
package io.servicetalk.http.api;

import org.junit.Test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;

import static io.servicetalk.http.api.CharSequences.contentEqualsIgnoreCase;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DefaultHttpCookiesTest {
    @Test
    public void decodeDuplicateNames() {
        final HttpHeaders headers = DefaultHttpHeadersFactory.INSTANCE.newHeaders();
        headers.add("set-cookie",
                "qwerty=12345; Domain=somecompany.co.uk; Path=/; Expires=Wed, 30 Aug 2019 00:00:00 GMT");
        headers.add("set-cookie",
                "qwerty=abcd; Domain=somecompany2.co.uk; Path=/2; Expires=Wed, 30 Aug 2019 00:00:00 GMT");
        decodeDuplicateNames(headers);
    }

    @Test
    public void decodeDuplicateNamesRO() {
        final HttpHeaders headers = new ReadOnlyHttpHeaders("set-cookie",
                "qwerty=12345; Domain=somecompany.co.uk; Path=/; Expires=Wed, 30 Aug 2019 00:00:00 GMT",
                "set-cookie",
                "qwerty=abcd; Domain=somecompany2.co.uk; Path=/2; Expires=Wed, 30 Aug 2019 00:00:00 GMT");
        decodeDuplicateNames(headers);
    }

    private static void decodeDuplicateNames(final HttpHeaders headers) {
        final Iterator<? extends HttpCookie> cookieItr = headers.getSetCookies("qwerty");
        assertTrue(cookieItr.hasNext());
        assertTrue(areCookiesEqual(new TestCookie("qwerty", "12345", "/", "somecompany.co.uk",
                "Wed, 30 Aug 2019 00:00:00 GMT", null, false, false, false), cookieItr.next()));
        assertTrue(cookieItr.hasNext());
        assertTrue(areCookiesEqual(new TestCookie("qwerty", "abcd", "/2", "somecompany2.co.uk",
                "Wed, 30 Aug 2019 00:00:00 GMT", null, false, false, false), cookieItr.next()));
        assertFalse(cookieItr.hasNext());
    }

    @Test
    public void decodeSecureCookieNames() {
        final HttpHeaders headers = DefaultHttpHeadersFactory.INSTANCE.newHeaders();
        headers.add("set-cookie", "__Secure-ID=123; Secure; Domain=example.com");
        decodeSecureCookieNames(headers);
    }

    @Test
    public void decodeSecureCookieNamesRO() {
        final HttpHeaders headers = new ReadOnlyHttpHeaders("set-cookie",
                "__Secure-ID=123; Secure; Domain=example.com");
        decodeSecureCookieNames(headers);
    }

    private static void decodeSecureCookieNames(final HttpHeaders headers) {
        final Iterator<? extends HttpCookie> cookieItr = headers.getSetCookies("__Secure-ID");
        assertTrue(cookieItr.hasNext());
        assertTrue(areCookiesEqual(new TestCookie("__Secure-ID", "123", null, "example.com", null,
                null, false, true, false), cookieItr.next()));
        assertFalse(cookieItr.hasNext());
    }

    @Test
    public void decodeDifferentCookieNames() {
        final HttpHeaders headers = new ReadOnlyHttpHeaders("set-cookie",
                "foo=12345; Domain=somecompany.co.uk; Path=/; HttpOnly",
                "set-cookie",
                "bar=abcd; Domain=somecompany.co.uk; Path=/2; Max-Age=3000");
        decodeDifferentCookieNames(headers);
    }

    @Test
    public void decodeDifferentCookieNamesRO() {
        final HttpHeaders headers = DefaultHttpHeadersFactory.INSTANCE.newHeaders();
        headers.add("set-cookie", "foo=12345; Domain=somecompany.co.uk; Path=/; HttpOnly");
        headers.add("set-cookie", "bar=abcd; Domain=somecompany.co.uk; Path=/2; Max-Age=3000");
        decodeDifferentCookieNames(headers);
    }

    private static void decodeDifferentCookieNames(final HttpHeaders headers) {
        Iterator<? extends HttpCookie> cookieItr = headers.getSetCookies("foo");
        assertTrue(cookieItr.hasNext());
        assertTrue(areCookiesEqual(new TestCookie("foo", "12345", "/", "somecompany.co.uk", null,
                null, false, false, true), cookieItr.next()));
        assertFalse(cookieItr.hasNext());
        cookieItr = headers.getSetCookies("bar");
        assertTrue(cookieItr.hasNext());
        assertTrue(areCookiesEqual(new TestCookie("bar", "abcd", "/2", "somecompany.co.uk", null,
                3000L, false, false, false), cookieItr.next()));
        assertFalse(cookieItr.hasNext());
    }

    @Test
    public void removeSingleCookie() {
        final HttpHeaders headers = DefaultHttpHeadersFactory.INSTANCE.newHeaders();
        headers.add("set-cookie", "foo=12345; Domain=somecompany.co.uk; Path=/; HttpOnly");
        headers.add("set-cookie", "bar=abcd; Domain=somecompany.co.uk; Path=/2; Max-Age=3000");
        assertTrue(headers.removeSetCookies("foo"));
        assertFalse(headers.getSetCookies("foo").hasNext());
        assertNull(headers.getSetCookie("foo"));
        Iterator<? extends HttpCookie> cookieItr = headers.getSetCookies("bar");
        assertTrue(cookieItr.hasNext());
        assertTrue(areCookiesEqual(new TestCookie("bar", "abcd", "/2", "somecompany.co.uk", null,
                3000L, false, false, false), cookieItr.next()));
        assertFalse(cookieItr.hasNext());
        final HttpCookie barCookie = headers.getSetCookie("bar");
        assertNotNull(barCookie);
        assertTrue(areCookiesEqual(new TestCookie("bar", "abcd", "/2", "somecompany.co.uk", null,
                3000L, false, false, false), barCookie));
    }

    @Test
    public void removeMultipleCookiesSameName() {
        final HttpHeaders headers = DefaultHttpHeadersFactory.INSTANCE.newHeaders();
        headers.add("set-cookie",
                "qwerty=12345; Domain=somecompany.co.uk; Path=/; Expires=Wed, 30 Aug 2019 00:00:00 GMT");
        headers.add("set-cookie",
                "qwerty=abcd; Domain=somecompany2.co.uk; Path=/2; Expires=Wed, 30 Aug 2019 00:00:00 GMT");
        assertTrue(headers.removeSetCookies("qwerty"));
        assertFalse(headers.getSetCookies("qwerty").hasNext());
        assertNull(headers.getSetCookie("qwerty"));
    }

    @Test
    public void addMultipleCookiesSameName() {
        final HttpHeaders headers = DefaultHttpHeadersFactory.INSTANCE.newHeaders();
        final TestCookie c1 = new TestCookie("qwerty", "12345", "/",
                "somecompany.co.uk", "Wed, 30 Aug 2019 00:00:00 GMT", null, false, false, false);
        headers.addSetCookie(c1);
        final TestCookie c2 = new TestCookie("qwerty", "abcd", "/2", "somecompany2.co.uk",
                "Wed, 30 Aug 2019 00:00:00 GMT", null, false, false, false);
        headers.addSetCookie(c2);
        final HttpCookie tmpCookie = headers.getSetCookie("qwerty");
        assertNotNull(tmpCookie);
        assertTrue(areCookiesEqual(c1, tmpCookie));
        decodeDuplicateNames(headers);
    }

    @Test
    public void addMultipleCookiesDifferentName() {
        final HttpHeaders headers = DefaultHttpHeadersFactory.INSTANCE.newHeaders();
        final TestCookie fooCookie = new TestCookie("foo", "12345", "/", "somecompany.co.uk", null,
                null, false, false, true);
        headers.addSetCookie(fooCookie);
        final TestCookie barCookie = new TestCookie("bar", "abcd", "/2", "somecompany.co.uk", null,
                3000L, false, false, false);
        headers.addSetCookie(barCookie);
        HttpCookie tmpCookie = headers.getSetCookie("foo");
        assertNotNull(tmpCookie);
        assertTrue(areCookiesEqual(fooCookie, tmpCookie));
        tmpCookie = headers.getSetCookie("bar");
        assertNotNull(tmpCookie);
        assertTrue(areCookiesEqual(barCookie, tmpCookie));
    }

    @Test
    public void getCookieNameDomainEmptyPath() {
        final HttpHeaders headers = DefaultHttpHeadersFactory.INSTANCE.newHeaders();
        headers.add("set-cookie", "qwerty=12345; Domain=somecompany.co.uk; Path=");
        getCookieNameDomainEmptyPath(headers);
    }

    @Test
    public void getCookieNameDomainEmptyPathRO() {
        final HttpHeaders headers = new ReadOnlyHttpHeaders("set-cookie",
                "qwerty=12345; Domain=somecompany.co.uk; Path=");
        getCookieNameDomainEmptyPath(headers);
    }

    private void getCookieNameDomainEmptyPath(HttpHeaders headers) {
        Iterator<? extends HttpCookie> cookieItr = headers.getSetCookies("qwerty", "somecompany.co.uk", "");
        assertFalse(cookieItr.hasNext());

        cookieItr = headers.getSetCookies("qwerty");
        assertTrue(cookieItr.hasNext());
        assertTrue(areCookiesEqual(new TestCookie("qwerty", "12345", null, "somecompany.co.uk", null,
                null, false, false, false), cookieItr.next()));
        assertFalse(cookieItr.hasNext());
    }

    @Test
    public void getAndRemoveCookiesNameDomainPath() {
        final HttpHeaders headers = DefaultHttpHeadersFactory.INSTANCE.newHeaders();
        headers.add("set-cookie",
                "qwerty=12345; Domain=somecompany.co.uk; Path=/; Expires=Wed, 30 Aug 2019 00:00:00 GMT");
        headers.add("set-cookie",
                "qwerty=abcd; Domain=somecompany2.co.uk; Path=/2; Expires=Wed, 30 Aug 2019 00:00:00 GMT");
        getCookiesNameDomainPath(headers);

        // Removal now.
        assertTrue(headers.removeSetCookies("qwerty", "somecompany2.co.uk", "/2"));
        Iterator<? extends HttpCookie> cookieItr = headers.getSetCookies("qwerty", "somecompany2.co.uk", "/2");
        assertFalse(cookieItr.hasNext());

        // Encode again
        cookieItr = headers.getSetCookies("qwerty", "somecompany.co.uk", "/");
        assertTrue(cookieItr.hasNext());
        assertTrue(areCookiesEqual(new TestCookie("qwerty", "12345", "/", "somecompany.co.uk",
                "Wed, 30 Aug 2019 00:00:00 GMT", null, false, false, false), cookieItr.next()));
        assertFalse(cookieItr.hasNext());
    }

    private static void getCookiesNameDomainPath(final HttpHeaders headers) {
        final Iterator<? extends HttpCookie> cookieItr = headers.getSetCookies("qwerty", "somecompany2.co.uk", "/2");
        assertTrue(cookieItr.hasNext());
        assertTrue(areCookiesEqual(new TestCookie("qwerty", "abcd", "/2", "somecompany2.co.uk", "Wed, 30 Aug 2019 00:00:00 GMT",
                null, false, false, false), cookieItr.next()));
        assertFalse(cookieItr.hasNext());
    }

    @Test
    public void getAndRemoveCookiesNameSubDomainPath() {
        final HttpHeaders headers = DefaultHttpHeadersFactory.INSTANCE.newHeaders();
        headers.add("set-cookie",
                "qwerty=12345; Domain=foo.somecompany.co.uk; Path=/2; Expires=Wed, 30 Aug 2019 00:00:00 GMT");
        headers.add("set-cookie", "qwerty=abcd; Domain=bar.somecompany.co.uk; Path=/2");
        headers.add("set-cookie", "qwerty=abxy; Domain=somecompany2.co.uk; Path=/2");
        headers.add("set-cookie", "qwerty=xyz; Domain=somecompany.co.uk; Path=/2");

        getAndRemoveCookiesNameSubDomainPath(headers, true);
    }

    @Test
    public void getAndRemoveCookiesNameSubDomainPathRO() {
        final HttpHeaders headers = new ReadOnlyHttpHeaders("set-cookie",
                "qwerty=12345; Domain=foo.somecompany.co.uk; Path=/2; Expires=Wed, 30 Aug 2019 00:00:00 GMT",
                "set-cookie", "qwerty=abcd; Domain=bar.somecompany.co.uk; Path=/2",
                "set-cookie", "qwerty=abxy; Domain=somecompany2.co.uk; Path=/2",
                "set-cookie", "qwerty=xyz; Domain=somecompany.co.uk; Path=/2");

        getAndRemoveCookiesNameSubDomainPath(headers, false);
    }

    private static void getAndRemoveCookiesNameSubDomainPath(HttpHeaders headers, boolean remove) {
        Iterator<? extends HttpCookie> cookieItr = headers.getSetCookies("qwerty", "somecompany.co.uk.", "/2");
        assertTrue(cookieItr.hasNext());
        assertTrue(areCookiesEqual(new TestCookie("qwerty", "12345", "/2", "foo.somecompany.co.uk",
                "Wed, 30 Aug 2019 00:00:00 GMT", null, false, false, false), cookieItr.next()));
        assertTrue(cookieItr.hasNext());
        assertTrue(areCookiesEqual(new TestCookie("qwerty", "abcd", "/2", "bar.somecompany.co.uk", null,
                null, false, false, false), cookieItr.next()));
        assertTrue(cookieItr.hasNext());
        assertTrue(areCookiesEqual(new TestCookie("qwerty", "xyz", "/2", "somecompany.co.uk", null,
                null, false, false, false), cookieItr.next()));
        assertFalse(cookieItr.hasNext());

        if (remove) {
            assertTrue(headers.removeSetCookies("qwerty", "somecompany.co.uk.", "/2"));
            cookieItr = headers.getSetCookies("qwerty");
            assertTrue(cookieItr.hasNext());
            assertTrue(areCookiesEqual(new TestCookie("qwerty", "abxy", "/2", "somecompany2.co.uk", null,
                    null, false, false, false), cookieItr.next()));
            assertFalse(cookieItr.hasNext());
        }
    }

    @Test
    public void getAllCookies() {
        final HttpHeaders headers = DefaultHttpHeadersFactory.INSTANCE.newHeaders();
        headers.add("set-cookie",
                "qwerty=12345; Domain=foo.somecompany.co.uk; Path=/2; Expires=Wed, 30 Aug 2019 00:00:00 GMT");
        headers.add("set-cookie", "qwerty=abcd; Domain=bar.somecompany.co.uk; Path=/2");
        headers.add("set-cookie", "qwerty=abxy; Domain=somecompany2.co.uk; Path=/2");
        headers.add("set-cookie", "qwerty=xyz; Domain=somecompany.co.uk; Path=/2");
        headers.add("cookie", "qwerty=xyz; Domain=foocompany.co.uk; Path=/2");

        getAllCookies(headers);
    }

    @Test
    public void getAllCookiesRO() {
        final HttpHeaders headers = new ReadOnlyHttpHeaders("set-cookie",
                "qwerty=12345; Domain=foo.somecompany.co.uk; Path=/2; Expires=Wed, 30 Aug 2019 00:00:00 GMT",
                "set-cookie", "qwerty=abcd; Domain=bar.somecompany.co.uk; Path=/2",
                "set-cookie", "qwerty=abxy; Domain=somecompany2.co.uk; Path=/2",
                "set-cookie", "qwerty=xyz; Domain=somecompany.co.uk; Path=/2",
                "cookie", "qwerty=xyz; Domain=foocompany.co.uk; Path=/2");

        getAllCookies(headers);
    }

    private static void getAllCookies(HttpHeaders headers) {
        Iterator<? extends HttpCookie> cookieItr = headers.getSetCookies();
        assertTrue(cookieItr.hasNext());
        assertTrue(areCookiesEqual(new TestCookie("qwerty", "12345", "/2", "foo.somecompany.co.uk",
                "Wed, 30 Aug 2019 00:00:00 GMT", null, false, false, false), cookieItr.next()));
        assertTrue(cookieItr.hasNext());
        assertTrue(areCookiesEqual(new TestCookie("qwerty", "abcd", "/2", "bar.somecompany.co.uk",
                null, null, false, false, false), cookieItr.next()));
        assertTrue(cookieItr.hasNext());
        assertTrue(areCookiesEqual(new TestCookie("qwerty", "abxy", "/2", "somecompany2.co.uk",
                null, null, false, false, false), cookieItr.next()));
        assertTrue(cookieItr.hasNext());
        assertTrue(areCookiesEqual(new TestCookie("qwerty", "xyz", "/2", "somecompany.co.uk",
                null, null, false, false, false), cookieItr.next()));
        assertFalse(cookieItr.hasNext());

        cookieItr = headers.getCookies();
        assertTrue(cookieItr.hasNext());
        assertTrue(areCookiesEqual(new TestCookie("qwerty", "xyz", "/2", "foocompany.co.uk",
                null, null, false, false, false), cookieItr.next()));
        assertFalse(cookieItr.hasNext());
    }

    @Test
    public void getAndRemoveCookiesNameDomainSubPath() {
        final HttpHeaders headers = DefaultHttpHeadersFactory.INSTANCE.newHeaders();
        headers.add("set-cookie",
                "qwerty=12345; Domain=somecompany.co.uk; Path=foo/bar; Expires=Wed, 30 Aug 2019 00:00:00 GMT");
        headers.add("set-cookie", "qwerty=abcd; Domain=somecompany.co.uk; Path=/foo/bar/");
        headers.add("set-cookie", "qwerty=xyz; Domain=somecompany.co.uk; Path=/foo/barnot");
        headers.add("set-cookie", "qwerty=abxy; Domain=somecompany.co.uk; Path=/foo/bar/baz");

        getAndRemoveCookiesNameDomainSubPath(headers, true);
    }

    @Test
    public void getAndRemoveCookiesNameDomainSubPathRO() {
        final HttpHeaders headers = new ReadOnlyHttpHeaders("set-cookie",
                "qwerty=12345; Domain=somecompany.co.uk; Path=foo/bar; Expires=Wed, 30 Aug 2019 00:00:00 GMT",
                "set-cookie", "qwerty=abcd; Domain=somecompany.co.uk; Path=/foo/bar/",
                "set-cookie", "qwerty=xyz; Domain=somecompany.co.uk; Path=/foo/barnot",
                "set-cookie", "qwerty=abxy; Domain=somecompany.co.uk; Path=/foo/bar/baz");

        getAndRemoveCookiesNameDomainSubPath(headers, false);
    }

    private static void getAndRemoveCookiesNameDomainSubPath(HttpHeaders headers, boolean remove) {
        Iterator<? extends HttpCookie> cookieItr = headers.getSetCookies("qwerty", "somecompany.co.uk", "/foo/bar");
        assertTrue(cookieItr.hasNext());
        assertTrue(areCookiesEqual(new TestCookie("qwerty", "12345", "foo/bar",
                "somecompany.co.uk", "Wed, 30 Aug 2019 00:00:00 GMT", null, false, false, false), cookieItr.next()));
        assertTrue(cookieItr.hasNext());
        assertTrue(areCookiesEqual(new TestCookie("qwerty", "abcd", "/foo/bar/", "somecompany.co.uk", null,
                null, false, false, false), cookieItr.next()));
        assertTrue(cookieItr.hasNext());
        assertTrue(areCookiesEqual(new TestCookie("qwerty", "abxy", "/foo/bar/baz", "somecompany.co.uk", null,
                null, false, false, false), cookieItr.next()));
        assertFalse(cookieItr.hasNext());

        if (remove) {
            assertTrue(headers.removeSetCookies("qwerty", "somecompany.co.uk", "/foo/bar"));
            cookieItr = headers.getSetCookies("qwerty");
            assertTrue(cookieItr.hasNext());
            assertTrue(areCookiesEqual(new TestCookie("qwerty", "xyz", "/foo/barnot", "somecompany.co.uk", null,
                    null, false, false, false), cookieItr.next()));
            assertFalse(cookieItr.hasNext());
        }
    }

    @Test
    public void percentEncodedValueCanBeDecoded() {
        final HttpHeaders headers = DefaultHttpHeadersFactory.INSTANCE.newHeaders();
        headers.add("set-cookie", "qwerty=%x21%x23");
        percentEncodedValueCanBeDecoded(headers);
    }

    @Test
    public void percentEncodedValueCanBeDecodedRO() {
        final HttpHeaders headers = new ReadOnlyHttpHeaders("set-cookie", "qwerty=%x21%x23");
        percentEncodedValueCanBeDecoded(headers);
    }

    private static void percentEncodedValueCanBeDecoded(HttpHeaders headers) {
        final HttpCookie cookie = headers.getSetCookie("qwerty");
        assertNotNull(cookie);
        assertTrue(areCookiesEqual(new TestCookie("qwerty", "%x21%x23", null, null, null,
                null, false, false, false), cookie));

        final Iterator<? extends HttpCookie> cookieItr = headers.getSetCookies("qwerty");
        assertTrue(cookieItr.hasNext());
        assertTrue(areCookiesEqual(new TestCookie("qwerty", "%x21%x23", null, null, null,
                null, false, false, false), cookieItr.next()));
        assertFalse(cookieItr.hasNext());
    }

    @Test
    public void percentEncodedNameCanBeDecoded() {
        final HttpHeaders headers = DefaultHttpHeadersFactory.INSTANCE.newHeaders();
        headers.add("set-cookie", "%x21=foo");
        percentEncodedNameCanBeDecoded(headers);
    }

    @Test
    public void percentEncodedNameCanBeDecodedRO() {
        final HttpHeaders headers = new ReadOnlyHttpHeaders("set-cookie", "%x21=foo");
        percentEncodedNameCanBeDecoded(headers);
    }

    private static void percentEncodedNameCanBeDecoded(HttpHeaders headers) {
        final HttpCookie cookie = headers.getSetCookie("%x21");
        assertNotNull(cookie);
        assertTrue(areCookiesEqual(new TestCookie("%x21", "foo", null, null, null,
                null, false, false, false), cookie));

        // Encode now
        final Iterator<? extends HttpCookie> cookieItr = headers.getSetCookies("%x21");
        assertTrue(cookieItr.hasNext());
        assertTrue(areCookiesEqual(new TestCookie("%x21", "foo", null, null, null,
                null, false, false, false), cookieItr.next()));
        assertFalse(cookieItr.hasNext());
    }

    @Test
    public void quotesInValuePreserved() {
        final HttpHeaders headers = DefaultHttpHeadersFactory.INSTANCE.newHeaders();
        headers.add("set-cookie",
                "qwerty=\"12345\"; Domain=somecompany.co.uk; Path=/; Expires=Wed, 30 Aug 2019 00:00:00 GMT");
        quotesInValuePreserved(headers);
    }

    @Test
    public void quotesInValuePreservedRO() {
        final HttpHeaders headers = new ReadOnlyHttpHeaders("set-cookie",
                "qwerty=\"12345\"; Domain=somecompany.co.uk; Path=/; Expires=Wed, 30 Aug 2019 00:00:00 GMT");
        quotesInValuePreserved(headers);
    }

    private static void quotesInValuePreserved(HttpHeaders headers) {
        final HttpCookie cookie = headers.getSetCookie("qwerty");
        assertNotNull(cookie);
        assertTrue(areCookiesEqual(new TestCookie("qwerty", "12345", "/",
                "somecompany.co.uk", "Wed, 30 Aug 2019 00:00:00 GMT", null, true, false, false), cookie));

        // Encode again
        final Iterator<? extends HttpCookie> cookieItr = headers.getSetCookies("qwerty");
        assertTrue(cookieItr.hasNext());
        assertTrue(areCookiesEqual(new TestCookie("qwerty", "12345", "/", "somecompany.co.uk", "Wed, 30 Aug 2019 00:00:00 GMT",
                null, true, false, false), cookieItr.next()));
        assertFalse(cookieItr.hasNext());

        final CharSequence value = headers.get("set-cookie");
        assertNotNull(value);
        assertTrue(value.toString().toLowerCase().contains("qwerty=\"12345\""));
    }

    @Test
    public void getCookiesIteratorRemove() {
        final HttpHeaders headers = DefaultHttpHeadersFactory.INSTANCE.newHeaders();
        headers.add("set-cookie", "foo=bar");
        headers.add("set-cookie",
                "qwerty=12345; Domain=somecompany.co.uk; Path=/; Expires=Wed, 30 Aug 2019 00:00:00 GMT");
        headers.add("set-cookie",
                "qwerty=abcd; Domain=somecompany2.co.uk; Path=/2; Expires=Wed, 30 Aug 2019 00:00:00 GMT");
        headers.add("set-cookie", "baz=xxx");

        Iterator<? extends HttpCookie> cookieItr = headers.getSetCookies("qwerty");
        assertTrue(cookieItr.hasNext());
        cookieItr.next();
        cookieItr.remove();
        assertTrue(cookieItr.hasNext());
        cookieItr.next();
        cookieItr.remove();
        assertFalse(cookieItr.hasNext());

        cookieItr = headers.getSetCookies("qwerty");
        assertFalse(cookieItr.hasNext());

        HttpCookie cookie = headers.getSetCookie("foo");
        assertNotNull(cookie);
        assertTrue(areCookiesEqual(new TestCookie("foo", "bar", null, null, null,
                null, false, false, false), cookie));
        cookie = headers.getSetCookie("baz");
        assertNotNull(cookie);
        assertTrue(areCookiesEqual(new TestCookie("baz", "xxx", null, null, null,
                null, false, false, false), cookie));
    }

    @Test
    public void overallIteratorRemoveFirstAndLast() {
        final HttpHeaders headers = DefaultHttpHeadersFactory.INSTANCE.newHeaders();
        headers.add("set-cookie", "foo=bar");
        headers.add("set-cookie", "qwerty=12345; Domain=somecompany.co.uk; Path=/; Expires=Wed, 30 Aug 2019 00:00:00 GMT");
        headers.add("set-cookie", "qwerty=abcd; Domain=somecompany2.co.uk; Path=/2; Expires=Wed, 30 Aug 2019 00:00:00 GMT");
        headers.add("set-cookie", "baz=xxx");

        // Overall iteration order isn't defined, so track which elements we don't expect to be present after removal.
        final Set<String> removedNameValue = new HashSet<>();

        Iterator<Entry<CharSequence, CharSequence>> cookieItr = headers.iterator();
        // Remove the first and last element
        assertTrue(cookieItr.hasNext());
        Entry<CharSequence, CharSequence> cookie = cookieItr.next();
        removedNameValue.add(calculateOverallRemoveIntermediateSetValue(cookie));
        cookieItr.remove();

        assertTrue(cookieItr.hasNext());
        cookieItr.next();

        assertTrue(cookieItr.hasNext());
        cookieItr.next();

        assertTrue(cookieItr.hasNext());
        cookie = cookieItr.next();
        removedNameValue.add(calculateOverallRemoveIntermediateSetValue(cookie));
        cookieItr.remove();
        assertFalse(cookieItr.hasNext());

        // Encode now
        cookieItr = headers.iterator();
        assertTrue(cookieItr.hasNext());
        cookie = cookieItr.next();
        assertFalse(removedNameValue.contains(calculateOverallRemoveIntermediateSetValue(cookie)));

        assertTrue(cookieItr.hasNext());
        cookie = cookieItr.next();
        assertFalse(removedNameValue.contains(calculateOverallRemoveIntermediateSetValue(cookie)));

        assertFalse(cookieItr.hasNext());
    }

    private static String calculateOverallRemoveIntermediateSetValue(Entry<CharSequence, CharSequence> cookie) {
        int i = cookie.getValue().toString().indexOf(';');
        return i >= 0 ? cookie.getValue().subSequence(0, i).toString() : cookie.getValue().toString();
    }

    @Test
    public void overallIteratorRemoveMiddle() {
        final HttpHeaders headers = DefaultHttpHeadersFactory.INSTANCE.newHeaders();
        headers.add("cookie", "foo=bar");
        headers.add("cookie", "qwerty=12345; Domain=somecompany.co.uk; Path=/; Expires=Wed, 30 Aug 2019 00:00:00 GMT");
        headers.add("cookie", "qwerty=abcd; Domain=somecompany2.co.uk; Path=/2; Expires=Wed, 30 Aug 2019 00:00:00 GMT");
        headers.add("cookie", "baz=xxx");

        // Overall iteration order isn't defined, so track which elements we don't expect to be present after removal.
        final Set<String> removedNameValue = new HashSet<>();

        Iterator<Entry<CharSequence, CharSequence>> cookieItr = headers.iterator();
        // Remove the first and last element
        assertTrue(cookieItr.hasNext());
        cookieItr.next();

        assertTrue(cookieItr.hasNext());
        Entry<CharSequence, CharSequence> cookie = cookieItr.next();
        removedNameValue.add(calculateOverallRemoveIntermediateSetValue(cookie));
        cookieItr.remove();

        assertTrue(cookieItr.hasNext());
        cookie = cookieItr.next();
        removedNameValue.add(calculateOverallRemoveIntermediateSetValue(cookie));
        cookieItr.remove();

        assertTrue(cookieItr.hasNext());
        cookieItr.next();
        assertFalse(cookieItr.hasNext());

        // Encode now
        cookieItr = headers.iterator();
        assertTrue(cookieItr.hasNext());
        cookie = cookieItr.next();
        assertFalse(removedNameValue.contains(calculateOverallRemoveIntermediateSetValue(cookie)));

        assertTrue(cookieItr.hasNext());
        cookie = cookieItr.next();
        assertFalse(removedNameValue.contains(calculateOverallRemoveIntermediateSetValue(cookie)));

        assertFalse(cookieItr.hasNext());
    }

    @Test
    public void overallIteratorRemoveAll() {
        final HttpHeaders headers = DefaultHttpHeadersFactory.INSTANCE.newHeaders();
        headers.add("set-cookie", "foo=bar");
        headers.add("set-cookie", "qwerty=12345; Domain=somecompany.co.uk; Path=/; Expires=Wed, 30 Aug 2019 00:00:00 GMT");
        headers.add("set-cookie", "qwerty=abcd; Domain=somecompany2.co.uk; Path=/2; Expires=Wed, 30 Aug 2019 00:00:00 GMT");
        headers.add("set-cookie", "baz=xxx");

        Iterator<Entry<CharSequence, CharSequence>> cookieItr = headers.iterator();
        // Remove the first and last element
        assertTrue(cookieItr.hasNext());
        cookieItr.next();
        cookieItr.remove();

        assertTrue(cookieItr.hasNext());
        cookieItr.next();
        cookieItr.remove();

        assertTrue(cookieItr.hasNext());
        cookieItr.next();
        cookieItr.remove();

        assertTrue(cookieItr.hasNext());
        cookieItr.next();
        cookieItr.remove();
        assertFalse(cookieItr.hasNext());

        // Encode now
        cookieItr = headers.iterator();
        assertFalse(cookieItr.hasNext());
    }

    @Test(expected = IllegalArgumentException.class)
    public void noEqualsButQuotedValueThrows() {
        final HttpHeaders headers = DefaultHttpHeadersFactory.INSTANCE.newHeaders();
        headers.add("set-cookie",
                "qwerty\"12345\"; Domain=somecompany.co.uk; Path=/; Expires=Wed, 30 Aug 2019 00:00:00 GMT");
        headers.getSetCookie("qwerty\"12345\"");
    }

    @Test(expected = IllegalArgumentException.class)
    public void noEqualsButQuotedValueThrowsRO() {
        final HttpHeaders headers = new ReadOnlyHttpHeaders("set-cookie",
                "qwerty\"12345\"; Domain=somecompany.co.uk; Path=/; Expires=Wed, 30 Aug 2019 00:00:00 GMT");
        headers.getSetCookie("qwerty\"12345\"");
    }

    @Test(expected = IllegalArgumentException.class)
    public void noEqualsValueWithAttributesThrows() {
        final HttpHeaders headers = DefaultHttpHeadersFactory.INSTANCE.newHeaders();
        headers.add("set-cookie",
                "qwerty12345; Domain=somecompany.co.uk; Path=/; Expires=Wed, 30 Aug 2019 00:00:00 GMT");
        headers.getSetCookie("qwerty12345");
    }

    @Test(expected = IllegalArgumentException.class)
    public void noEqualsValueWithAttributesThrowsRO() {
        final HttpHeaders headers = new ReadOnlyHttpHeaders("set-cookie",
                "qwerty12345; Domain=somecompany.co.uk; Path=/; Expires=Wed, 30 Aug 2019 00:00:00 GMT");
        headers.getSetCookie("qwerty12345");
    }

    @Test(expected = IllegalArgumentException.class)
    public void noEqualsValueThrows() {
        final HttpHeaders headers = DefaultHttpHeadersFactory.INSTANCE.newHeaders();
        headers.add("set-cookie", "qwerty12345");
        headers.getSetCookie("qwerty12345");
    }

    @Test(expected = IllegalArgumentException.class)
    public void noEqualsValueThrowsRO() {
        final HttpHeaders headers = new ReadOnlyHttpHeaders("set-cookie", "qwerty12345");
        headers.getSetCookie("qwerty12345");
    }

    @Test(expected = IllegalArgumentException.class)
    public void trailingSemiColon() {
        final HttpHeaders headers = DefaultHttpHeadersFactory.INSTANCE.newHeaders();
        headers.add("set-cookie", "qwerty=12345;");
        headers.getSetCookie("qwerty");
    }

    @Test(expected = IllegalArgumentException.class)
    public void trailingSemiColonRO() {
        final HttpHeaders headers = new ReadOnlyHttpHeaders("set-cookie", "qwerty=12345;");
        headers.getSetCookie("qwerty");
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidCookieName() {
        final HttpHeaders headers = DefaultHttpHeadersFactory.INSTANCE.newHeaders();
        headers.add("set-cookie", "q@werty=12345");
        headers.getSetCookie("q@werty");
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidCookieNameRO() {
        final HttpHeaders headers = new ReadOnlyHttpHeaders("set-cookie", "q@werty=12345");
        headers.getSetCookie("q@werty");
    }

    @Test
    public void invalidCookieNameNoThowIfNoValidate() {
        final HttpHeaders headers = new DefaultHttpHeadersFactory(false, false).newHeaders();
        headers.add("set-cookie", "q@werty=12345");
        headers.getSetCookie("q@werty");
    }

    @Test
    public void invalidCookieNameNoThowIfNoValidateRO() {
        final HttpHeaders headers = new ReadOnlyHttpHeaders(false, "set-cookie", "q@werty=12345");
        headers.getSetCookie("q@werty");
    }

    @Test(expected = IllegalStateException.class)
    public void valueIteratorThrowsIfNoNextCall() {
        final HttpHeaders headers = DefaultHttpHeadersFactory.INSTANCE.newHeaders();
        headers.add("set-cookie", "qwerty=12345");
        valueIteratorThrowsIfNoNextCall(headers);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void valueIteratorThrowsIfNoNextCallRO() {
        final HttpHeaders headers = new ReadOnlyHttpHeaders("set-cookie", "qwerty=12345");
        valueIteratorThrowsIfNoNextCall(headers);
    }

    private static void valueIteratorThrowsIfNoNextCall(HttpHeaders headers) {
        final Iterator<? extends HttpCookie> itr = headers.getSetCookies("qwerty");
        assertTrue(itr.hasNext());
        itr.remove();
    }

    @Test(expected = IllegalStateException.class)
    public void entryIteratorThrowsIfDoubleRemove() {
        final HttpHeaders headers = DefaultHttpHeadersFactory.INSTANCE.newHeaders();
        headers.add("set-cookie", "qwerty=12345");
        entryIteratorThrowsIfDoubleRemove(headers);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void entryIteratorThrowsIfDoubleRemoveRO() {
        final HttpHeaders headers = new ReadOnlyHttpHeaders("set-cookie", "qwerty=12345");
        entryIteratorThrowsIfDoubleRemove(headers);
    }

    private static void entryIteratorThrowsIfDoubleRemove(HttpHeaders headers) {
        final Iterator<? extends HttpCookie> itr = headers.getSetCookies("qwerty");
        assertTrue(itr.hasNext());
        assertTrue(areCookiesEqual(new TestCookie("qwerty", "12345", null, null, null,
                null, false, false, false), itr.next()));
        itr.remove();
        itr.remove();
    }

    private static boolean areCookiesEqual(final HttpCookie cookie1, final HttpCookie cookie2) {
        return contentEqualsIgnoreCase(cookie1.getName(), cookie2.getName()) &&
                cookie1.getValue().equals(cookie2.getValue()) &&
                Objects.equals(cookie1.getDomain(), cookie2.getDomain()) &&
                Objects.equals(cookie1.getPath(), cookie2.getPath()) &&
                Objects.equals(cookie1.getExpires(), cookie2.getExpires()) &&
                Objects.equals(cookie1.getValue(), cookie2.getValue()) &&
                cookie1.isHttpOnly() == cookie2.isHttpOnly() &&
                cookie1.isSecure() == cookie2.isSecure() &&
                cookie1.isWrapped() == cookie2.isWrapped();
    }

    private static final class TestCookie implements HttpCookie {
        private final String name;
        private final String value;
        @Nullable
        private final String path;
        @Nullable
        private final String domain;
        @Nullable
        private final String expires;
        @Nullable
        private final Long maxAge;
        private final boolean isWrapped;
        private final boolean isSecure;
        private final boolean isHttpOnly;

        TestCookie(final String name, final String value, @Nullable final String path,
                   @Nullable final String domain, @Nullable final String expires,
                   @Nullable final Long maxAge, final boolean isWrapped, final boolean isSecure, final boolean isHttpOnly) {
            this.name = name;
            this.value = value;
            this.path = path;
            this.domain = domain;
            this.expires = expires;
            this.maxAge = maxAge;
            this.isWrapped = isWrapped;
            this.isSecure = isSecure;
            this.isHttpOnly = isHttpOnly;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public boolean isWrapped() {
            return isWrapped;
        }

        @Nullable
        @Override
        public String getDomain() {
            return domain;
        }

        @Nullable
        @Override
        public String getPath() {
            return path;
        }

        @Nullable
        @Override
        public Long getMaxAge() {
            return maxAge;
        }

        @Nullable
        @Override
        public String getExpires() {
            return expires;
        }

        @Override
        public boolean isSecure() {
            return isSecure;
        }

        @Override
        public boolean isHttpOnly() {
            return isHttpOnly;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + "[" + name + "]";
        }
    }
}
