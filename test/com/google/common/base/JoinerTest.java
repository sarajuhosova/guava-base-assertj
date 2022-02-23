/*
 * Copyright (C) 2008 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Joiner.MapJoiner;
import com.google.common.collect.*;
import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for {@link Joiner}.
 *
 * @author Kevin Bourrillion
 */
@GwtCompatible(emulated = true)
public class JoinerTest extends TestCase {
    private static final Joiner J = Joiner.on("-");

    // <Integer> needed to prevent warning :(
    private static final Iterable<Integer> ITERABLE_ = Arrays.asList();
    private static final Iterable<Integer> ITERABLE_1 = Arrays.asList(1);
    private static final Iterable<Integer> ITERABLE_12 = Arrays.asList(1, 2);
    private static final Iterable<Integer> ITERABLE_123 = Arrays.asList(1, 2, 3);
    private static final Iterable<Integer> ITERABLE_NULL = Arrays.asList((Integer) null);
    private static final Iterable<Integer> ITERABLE_NULL_NULL = Arrays.asList(null, null);
    private static final Iterable<Integer> ITERABLE_NULL_1 = Arrays.asList(null, 1);
    private static final Iterable<Integer> ITERABLE_1_NULL = Arrays.asList(1, null);
    private static final Iterable<Integer> ITERABLE_1_NULL_2 = Arrays.asList(1, null, 2);
    private static final Iterable<Integer> ITERABLE_FOUR_NULLS =
            Arrays.asList(null, null, null, null);

    public void testNoSpecialNullBehavior() {
        checkNoOutput(J, ITERABLE_);
        checkResult(J, ITERABLE_1, "1");
        checkResult(J, ITERABLE_12, "1-2");
        checkResult(J, ITERABLE_123, "1-2-3");

        try {
            J.join(ITERABLE_NULL);
            fail();
        } catch (NullPointerException expected) {
        }
        try {
            J.join(ITERABLE_1_NULL_2);
            fail();
        } catch (NullPointerException expected) {
        }

        try {
            J.join(ITERABLE_NULL.iterator());
            fail();
        } catch (NullPointerException expected) {
        }
        try {
            J.join(ITERABLE_1_NULL_2.iterator());
            fail();
        } catch (NullPointerException expected) {
        }
    }

    public void testOnCharOverride() {
        Joiner onChar = Joiner.on('-');
        checkNoOutput(onChar, ITERABLE_);
        checkResult(onChar, ITERABLE_1, "1");
        checkResult(onChar, ITERABLE_12, "1-2");
        checkResult(onChar, ITERABLE_123, "1-2-3");
    }

    public void testSkipNulls() {
        Joiner skipNulls = J.skipNulls();
        checkNoOutput(skipNulls, ITERABLE_);
        checkNoOutput(skipNulls, ITERABLE_NULL);
        checkNoOutput(skipNulls, ITERABLE_NULL_NULL);
        checkNoOutput(skipNulls, ITERABLE_FOUR_NULLS);
        checkResult(skipNulls, ITERABLE_1, "1");
        checkResult(skipNulls, ITERABLE_12, "1-2");
        checkResult(skipNulls, ITERABLE_123, "1-2-3");
        checkResult(skipNulls, ITERABLE_NULL_1, "1");
        checkResult(skipNulls, ITERABLE_1_NULL, "1");
        checkResult(skipNulls, ITERABLE_1_NULL_2, "1-2");
    }

    public void testUseForNull() {
        Joiner zeroForNull = J.useForNull("0");
        checkNoOutput(zeroForNull, ITERABLE_);
        checkResult(zeroForNull, ITERABLE_1, "1");
        checkResult(zeroForNull, ITERABLE_12, "1-2");
        checkResult(zeroForNull, ITERABLE_123, "1-2-3");
        checkResult(zeroForNull, ITERABLE_NULL, "0");
        checkResult(zeroForNull, ITERABLE_NULL_NULL, "0-0");
        checkResult(zeroForNull, ITERABLE_NULL_1, "0-1");
        checkResult(zeroForNull, ITERABLE_1_NULL, "1-0");
        checkResult(zeroForNull, ITERABLE_1_NULL_2, "1-0-2");
        checkResult(zeroForNull, ITERABLE_FOUR_NULLS, "0-0-0-0");
    }

    private static void checkNoOutput(Joiner joiner, Iterable<Integer> set) {
        assertEquals("", joiner.join(set));
        assertEquals("", joiner.join(set.iterator()));

        Object[] array = Lists.newArrayList(set).toArray(new Integer[0]);
        assertEquals("", joiner.join(array));

        StringBuilder sb1FromIterable = new StringBuilder();
        assertThat(joiner.appendTo(sb1FromIterable, set)).isSameAs(sb1FromIterable);
        assertThat(sb1FromIterable.length()).isEqualTo(0);

        StringBuilder sb1FromIterator = new StringBuilder();
        assertThat(joiner.appendTo(sb1FromIterator, set)).isSameAs(sb1FromIterator);
        assertThat(sb1FromIterator.length()).isEqualTo(0);

        StringBuilder sb2 = new StringBuilder();
        assertThat(joiner.appendTo(sb2, array)).isSameAs(sb2);
        assertThat(sb2.length()).isEqualTo(0);

        try {
            joiner.appendTo(NASTY_APPENDABLE, set);
        } catch (IOException e) {
            throw new AssertionError(e);
        }

        try {
            joiner.appendTo(NASTY_APPENDABLE, set.iterator());
        } catch (IOException e) {
            throw new AssertionError(e);
        }

        try {
            joiner.appendTo(NASTY_APPENDABLE, array);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    private static final Appendable NASTY_APPENDABLE =
            new Appendable() {
                @Override
                public Appendable append(CharSequence csq) throws IOException {
                    throw new IOException();
                }

                @Override
                public Appendable append(CharSequence csq, int start, int end) throws IOException {
                    throw new IOException();
                }

                @Override
                public Appendable append(char c) throws IOException {
                    throw new IOException();
                }
            };

    private static void checkResult(Joiner joiner, Iterable<Integer> parts, String expected) {
        assertThat(joiner.join(parts)).isEqualTo(expected);
        assertThat(joiner.join(parts.iterator())).isEqualTo(expected);

        StringBuilder sb1FromIterable = new StringBuilder().append('x');
        joiner.appendTo(sb1FromIterable, parts);
        assertEquals("x" + expected, sb1FromIterable.toString());

        StringBuilder sb1FromIterator = new StringBuilder().append('x');
        joiner.appendTo(sb1FromIterator, parts.iterator());
        assertEquals("x" + expected, sb1FromIterator.toString());

        Integer[] partsArray = Lists.newArrayList(parts).toArray(new Integer[0]);
        assertThat(joiner.join(partsArray)).isEqualTo(expected);

        StringBuilder sb2 = new StringBuilder().append('x');
        joiner.appendTo(sb2, partsArray);
        assertEquals("x" + expected, sb2.toString());

        int num = partsArray.length - 2;
        if (num >= 0) {
            Object[] rest = new Integer[num];
            for (int i = 0; i < num; i++) {
                rest[i] = partsArray[i + 2];
            }

            assertThat(joiner.join(partsArray[0], partsArray[1], rest)).isEqualTo(expected);

            StringBuilder sb3 = new StringBuilder().append('x');
            joiner.appendTo(sb3, partsArray[0], partsArray[1], rest);
            assertEquals("x" + expected, sb3.toString());
        }
    }

    public void test_useForNull_skipNulls() {
        Joiner j = Joiner.on("x").useForNull("y");
        try {
            j = j.skipNulls();
            fail();
        } catch (UnsupportedOperationException expected) {
        }
    }

    public void test_skipNulls_useForNull() {
        Joiner j = Joiner.on("x").skipNulls();
        try {
            j = j.useForNull("y");
            fail();
        } catch (UnsupportedOperationException expected) {
        }
    }

    public void test_useForNull_twice() {
        Joiner j = Joiner.on("x").useForNull("y");
        try {
            j = j.useForNull("y");
            fail();
        } catch (UnsupportedOperationException expected) {
        }
    }

    public void testMap() {
        MapJoiner j = Joiner.on(';').withKeyValueSeparator(':');
        assertEquals("", j.join(ImmutableMap.of()));
        assertThat(j.join(ImmutableMap.of("", ""))).isEqualTo(":");

        Map<String, String> mapWithNulls = Maps.newLinkedHashMap();
        mapWithNulls.put("a", null);
        mapWithNulls.put(null, "b");

        try {
            j.join(mapWithNulls);
            fail();
        } catch (NullPointerException expected) {
        }

        assertThat(j.useForNull("00").join(mapWithNulls)).isEqualTo("a:00;00:b");

        StringBuilder sb = new StringBuilder();
        j.appendTo(sb, ImmutableMap.of(1, 2, 3, 4, 5, 6));
        assertThat(sb.toString()).isEqualTo("1:2;3:4;5:6");
    }

    public void testEntries() {
        MapJoiner j = Joiner.on(";").withKeyValueSeparator(":");
        assertThat(j.join(ImmutableMultimap.of().entries())).isEqualTo("");
        assertThat(j.join(ImmutableMultimap.of().entries().iterator())).isEqualTo("");
        assertThat(j.join(ImmutableMultimap.of("", "").entries())).isEqualTo(":");
        assertThat(j.join(ImmutableMultimap.of("", "").entries().iterator())).isEqualTo(":");
        assertThat(j.join(ImmutableMultimap.of("1", "a", "1", "b").entries())).isEqualTo("1:a;1:b");
        assertThat(j.join(ImmutableMultimap.of("1", "a", "1", "b").entries().iterator())).isEqualTo("1:a;1:b");

        Map<String, String> mapWithNulls = Maps.newLinkedHashMap();
        mapWithNulls.put("a", null);
        mapWithNulls.put(null, "b");
        Set<Entry<String, String>> entriesWithNulls = mapWithNulls.entrySet();

        try {
            j.join(entriesWithNulls);
            fail();
        } catch (NullPointerException expected) {
        }

        try {
            j.join(entriesWithNulls.iterator());
            fail();
        } catch (NullPointerException expected) {
        }

        assertThat(j.useForNull("00").join(entriesWithNulls)).isEqualTo("a:00;00:b");
        assertThat(j.useForNull("00").join(entriesWithNulls.iterator())).isEqualTo("a:00;00:b");

        StringBuilder sb1 = new StringBuilder();
        j.appendTo(sb1, ImmutableMultimap.of(1, 2, 3, 4, 5, 6, 1, 3, 5, 10).entries());
        assertThat(sb1.toString()).isEqualTo("1:2;1:3;3:4;5:6;5:10");

        StringBuilder sb2 = new StringBuilder();
        j.appendTo(sb2, ImmutableMultimap.of(1, 2, 3, 4, 5, 6, 1, 3, 5, 10).entries().iterator());
        assertThat(sb2.toString()).isEqualTo("1:2;1:3;3:4;5:6;5:10");
    }

    public void test_skipNulls_onMap() {
        Joiner j = Joiner.on(",").skipNulls();
        try {
            j.withKeyValueSeparator("/");
            fail();
        } catch (UnsupportedOperationException expected) {
        }
    }

    private static class DontStringMeBro implements CharSequence {
        @Override
        public int length() {
            return 3;
        }

        @Override
        public char charAt(int index) {
            return "foo".charAt(index);
        }

        @Override
        public CharSequence subSequence(int start, int end) {
            return "foo".subSequence(start, end);
        }

        @Override
        public String toString() {
            throw new AssertionFailedError("shouldn't be invoked");
        }
    }

    // Don't do this.
    private static class IterableIterator implements Iterable<Integer>, Iterator<Integer> {
        private static final ImmutableSet<Integer> INTEGERS = ImmutableSet.of(1, 2, 3, 4);
        private final Iterator<Integer> iterator;

        public IterableIterator() {
            this.iterator = iterator();
        }

        @Override
        public Iterator<Integer> iterator() {
            return INTEGERS.iterator();
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Integer next() {
            return iterator.next();
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }

    @GwtIncompatible // StringBuilder.append in GWT invokes Object.toString(), unlike the JRE version.
    public void testDontConvertCharSequenceToString() {
        assertThat(Joiner.on(",").join(new DontStringMeBro(), new DontStringMeBro())).isEqualTo("foo,foo");
        assertThat(Joiner.on(",").useForNull("bar").join(new DontStringMeBro(), null, new DontStringMeBro()))
                .isEqualTo("foo,bar,foo");
    }
}
