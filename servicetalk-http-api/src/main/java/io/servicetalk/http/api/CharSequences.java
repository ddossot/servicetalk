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

import io.servicetalk.buffer.api.Buffer;

import javax.annotation.Nullable;

import static io.servicetalk.buffer.api.ReadOnlyBufferAllocators.DEFAULT_RO_ALLOCATOR;
import static io.servicetalk.http.api.AsciiBuffer.EMPTY_ASCII_BUFFER;
import static io.servicetalk.http.api.AsciiBuffer.hashCodeAscii;
import static java.lang.Character.toUpperCase;

/**
 * Provides factory methods for creating {@link CharSequence} implementations.
 */
public final class CharSequences {
    private CharSequences() {
        // No instances
    }

    /**
     * Create a new {@link CharSequence} from the specified {@code input}, supporting only 8-bit ASCII characters, and
     * with a case-insensitive {@code hashCode}.
     * <p>
     * Supporting only 8-bit ASCII and providing a case-insensitive {@code hashCode} allows for optimizations when the
     * {@link CharSequence} returned is used as {@link HttpHeaders} names or values.
     *
     * @param input a string containing only 8-bit ASCII characters.
     * @return a {@link CharSequence}
     */
    public static CharSequence newAsciiString(final String input) {
        return newAsciiString(DEFAULT_RO_ALLOCATOR.fromAscii(input));
    }

    /**
     * Create a new {@link CharSequence} from the specified {@code input}, supporting only 8-bit ASCII characters, and
     * with a case-insensitive {@code hashCode}.
     * <p>
     * Supporting only 8-bit ASCII and providing a case-insensitive {@code hashCode} allows for optimizations when the
     * {@link CharSequence} returned is used as {@link HttpHeaders} names or values.
     * @param input a {@link Buffer} containing
     * @return a {@link CharSequence}.
     */
    public static CharSequence newAsciiString(final Buffer input) {
        return new AsciiBuffer(input);
    }

    /**
     * Get a reference to an unmodifiable empty {@link CharSequence} with the same properties as
     * {@link #newAsciiString(Buffer)}.
     * @return a reference to an unmodifiable empty {@link CharSequence} with the same properties as
     *      {@link #newAsciiString(Buffer)}.
     */
    public static CharSequence emptyAsciiString() {
        return EMPTY_ASCII_BUFFER;
    }

    /**
     * Attempt to unwrap a {@link CharSequence} and obtain the underlying {@link Buffer} if possible.
     * @param cs the {@link CharSequence} to unwrap.
     * @return the underlying {@link Buffer} or {@code null}.
     */
    @Nullable
    public static Buffer unwrapBuffer(CharSequence cs) {
        return cs.getClass() == AsciiBuffer.class ? ((AsciiBuffer) cs).unwrap() : null;
    }

    /**
     * Perform a case-insensitive comparison of two {@link CharSequence}s.
     * <p>
     * NOTE: This only supports 8-bit ASCII.
     *
     * @param a first {@link CharSequence} to compare.
     * @param b second {@link CharSequence} to compare.
     * @return {@code true} if both {@link CharSequence}'s are equals when ignoring the case.
     */
    static boolean contentEqualsIgnoreCase(@Nullable final CharSequence a, @Nullable final CharSequence b) {
        if (a == null || b == null) {
            return a == b;
        }
        if (a == b) {
            return true;
        }
        if (a.length() != b.length()) {
            return false;
        }
        if (a.getClass() == AsciiBuffer.class) {
            return ((AsciiBuffer) a).contentEqualsIgnoreCase(b);
        }
        return contentEqualsIgnoreCaseUnknownTypes(a, b);
    }

    static boolean contentEqualsIgnoreCaseUnknownTypes(final CharSequence a, final CharSequence b) {
        for (int i = 0; i < a.length(); ++i) {
            if (!equalsIgnoreCase(a.charAt(i), b.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static boolean equalsIgnoreCase(final char a, final char b) {
        return a == b || toLowerCase(a) == toLowerCase(b);
    }

    private static char toLowerCase(final char c) {
        return isUpperCase(c) ? (char) (c + 32) : c;
    }

    private static boolean isUpperCase(final char value) {
        return value >= 'A' && value <= 'Z';
    }

    /**
     * This methods make regionMatches operation correctly for any chars in strings.
     *
     * @param cs         the {@code CharSequence} to be processed
     * @param ignoreCase specifies if case should be ignored.
     * @param csStart    the starting offset in the {@code cs} CharSequence
     * @param string     the {@code CharSequence} to compare.
     * @param start      the starting offset in the specified {@code string}.
     * @param length     the number of characters to compare.
     * @return {@code true} if the ranges of characters are equal, {@code false} otherwise.
     */
    static boolean regionMatches(final CharSequence cs, final boolean ignoreCase, final int csStart,
                                 final CharSequence string, final int start, final int length) {
        if (cs instanceof String && string instanceof String) {
            return ((String) cs).regionMatches(ignoreCase, csStart, (String) string, start, length);
        }

        return regionMatchesCharSequences(cs, csStart, string, start, length,
                ignoreCase ? GeneralCaseInsensitiveCharEqualityComparator.INSTANCE :
                        DefaultCharEqualityComparator.INSTANCE);
    }

    private static boolean regionMatchesCharSequences(final CharSequence cs, final int csStart,
                                                      final CharSequence string, final int start, final int length,
                                                      final CharEqualityComparator charEqualityComparator) {
        if (csStart < 0 || length > cs.length() - csStart) {
            return false;
        }
        if (start < 0 || length > string.length() - start) {
            return false;
        }

        int csIndex = csStart;
        final int csEnd = csIndex + length;
        int stringIndex = start;

        while (csIndex < csEnd) {
            final char c1 = cs.charAt(csIndex++);
            final char c2 = string.charAt(stringIndex++);

            if (!charEqualityComparator.equals(c1, c2)) {
                return false;
            }
        }
        return true;
    }

    private interface CharEqualityComparator {
        boolean equals(char a, char b);
    }

    private static final class DefaultCharEqualityComparator implements CharEqualityComparator {
        static final DefaultCharEqualityComparator INSTANCE = new DefaultCharEqualityComparator();

        private DefaultCharEqualityComparator() {
            // No instances
        }

        @Override
        public boolean equals(final char a, final char b) {
            return a == b;
        }
    }

    private static final class GeneralCaseInsensitiveCharEqualityComparator implements CharEqualityComparator {
        static final GeneralCaseInsensitiveCharEqualityComparator
                INSTANCE = new GeneralCaseInsensitiveCharEqualityComparator();

        private GeneralCaseInsensitiveCharEqualityComparator() {
            // No instances
        }

        @Override
        public boolean equals(final char a, final char b) {
            //For motivation, why we need two checks, see comment in String#regionMatches
            return toUpperCase(a) == toUpperCase(b) || Character.toLowerCase(a) == Character.toLowerCase(b);
        }
    }

    /**
     * Returns the case-insensitive hash code of the specified string. Note that this method uses the same hashing
     * algorithm with {@link #hashCode()} so that you can put arbitrary {@link CharSequence}s into the same headers.
     */
    static int caseInsensitiveHashCode(CharSequence seq) {
        return seq.getClass() == AsciiBuffer.class ? seq.hashCode() : hashCodeAscii(seq);
    }

    /**
     * Returns {@code true} if the content of both {@link CharSequence}'s are equals. This only supports 8-bit ASCII.
     */
    static boolean contentEquals(final CharSequence a, final CharSequence b) {
        if (a == b) {
            return true;
        }
        if (a.length() != b.length()) {
            return false;
        }
        if (a.getClass() == AsciiBuffer.class) {
            return ((AsciiBuffer) a).contentEquals(b);
        }
        return contentEqualsUnknownTypes(a, b);
    }

    static boolean contentEqualsUnknownTypes(final CharSequence a, final CharSequence b) {
        for (int i = 0; i < a.length(); ++i) {
            if (a.charAt(i) != b.charAt(i)) {
                return false;
            }
        }
        return true;
    }
}
