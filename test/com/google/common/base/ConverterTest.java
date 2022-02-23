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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.primitives.Longs;
import com.google.common.testing.EqualsTester;
import com.google.common.testing.SerializableTester;
import junit.framework.TestCase;

import java.util.Iterator;
import java.util.List;

import static com.google.common.base.Functions.toStringFunction;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link Converter}.
 */
@GwtCompatible
public class ConverterTest extends TestCase {

    private static final Converter<String, Long> STR_TO_LONG =
            new Converter<String, Long>() {
                @Override
                protected Long doForward(String object) {
                    return Long.valueOf(object);
                }

                @Override
                protected String doBackward(Long object) {
                    return String.valueOf(object);
                }

                @Override
                public String toString() {
                    return "string2long";
                }
            };

    private static final Long LONG_VAL = 12345L;
    private static final String STR_VAL = "12345";

    private static final ImmutableList<String> STRINGS = ImmutableList.of("123", "456");
    private static final ImmutableList<Long> LONGS = ImmutableList.of(123L, 456L);

    public void testConverter() {
        assertThat(STR_TO_LONG.convert(STR_VAL)).isEqualTo(LONG_VAL);
        assertThat(STR_TO_LONG.reverse().convert(LONG_VAL)).isEqualTo(STR_VAL);

        Iterable<Long> convertedValues = STR_TO_LONG.convertAll(STRINGS);
        assertThat(ImmutableList.copyOf(convertedValues)).isEqualTo(LONGS);
    }

    public void testConvertAllIsView() {
        List<String> mutableList = Lists.newArrayList("789", "123");
        Iterable<Long> convertedValues = STR_TO_LONG.convertAll(mutableList);
        assertThat(ImmutableList.copyOf(convertedValues)).isEqualTo(ImmutableList.of(789L, 123L));

        Iterator<Long> iterator = convertedValues.iterator();
        iterator.next();
        iterator.remove();
        assertThat(mutableList).isEqualTo(ImmutableList.of("123"));
    }

    public void testReverse() {
        Converter<Long, String> reverseConverter = STR_TO_LONG.reverse();

        assertThat(reverseConverter.convert(LONG_VAL)).isEqualTo(STR_VAL);
        assertThat(reverseConverter.reverse().convert(STR_VAL)).isEqualTo(LONG_VAL);

        Iterable<String> convertedValues = reverseConverter.convertAll(LONGS);
        assertThat(ImmutableList.copyOf(convertedValues)).isEqualTo(STRINGS);

        assertThat(reverseConverter.reverse()).isSameAs(STR_TO_LONG);

        assertThat(reverseConverter.toString()).isEqualTo("string2long.reverse()");

        new EqualsTester()
                .addEqualityGroup(STR_TO_LONG, STR_TO_LONG.reverse().reverse())
                .addEqualityGroup(STR_TO_LONG.reverse(), STR_TO_LONG.reverse())
                .testEquals();
    }

    public void testReverseReverse() {
        Converter<String, Long> converter = STR_TO_LONG;
        assertThat(converter.reverse().reverse()).isEqualTo(converter);
    }

    public void testApply() {
        assertThat(STR_TO_LONG.apply(STR_VAL)).isEqualTo(LONG_VAL);
    }

    private static class StringWrapper {
        private final String value;

        public StringWrapper(String value) {
            this.value = value;
        }
    }

    public void testAndThen() {
        Converter<StringWrapper, String> first =
                new Converter<StringWrapper, String>() {
                    @Override
                    protected String doForward(StringWrapper object) {
                        return object.value;
                    }

                    @Override
                    protected StringWrapper doBackward(String object) {
                        return new StringWrapper(object);
                    }

                    @Override
                    public String toString() {
                        return "StringWrapper";
                    }
                };

        Converter<StringWrapper, Long> converter = first.andThen(STR_TO_LONG);

        assertThat(converter.convert(new StringWrapper(STR_VAL))).isEqualTo(LONG_VAL);
        assertThat(converter.reverse().convert(LONG_VAL).value).isEqualTo(STR_VAL);

        assertThat(converter.toString()).isEqualTo("StringWrapper.andThen(string2long)");

        assertThat(first.andThen(STR_TO_LONG)).isEqualTo(first.andThen(STR_TO_LONG));
    }

    public void testIdentityConverter() {
        Converter<String, String> stringIdentityConverter = Converter.identity();

        assertThat(stringIdentityConverter.reverse()).isSameAs(stringIdentityConverter);
        assertThat(stringIdentityConverter.andThen(STR_TO_LONG)).isSameAs(STR_TO_LONG);

        assertThat(stringIdentityConverter.convert(STR_VAL)).isSameAs(STR_VAL);
        assertThat(stringIdentityConverter.reverse().convert(STR_VAL)).isSameAs(STR_VAL);

        assertThat(stringIdentityConverter.toString()).isEqualTo("Converter.identity()");

        assertThat(Converter.identity()).isSameAs(Converter.identity());
    }

    public void testFrom() {
        Function<String, Integer> forward =
                new Function<String, Integer>() {
                    @Override
                    public Integer apply(String input) {
                        return Integer.parseInt(input);
                    }
                };
        Function<Object, String> backward = toStringFunction();

        Converter<String, Number> converter = Converter.from(forward, backward);

        assertThat(converter.convert(null)).isNull();
        assertThat(converter.reverse().convert(null)).isNull();

        assertThat(converter.convert("5")).isEqualTo(5);
        assertThat(converter.reverse().convert(5)).isEqualTo("5");
    }

    public void testNullIsPassedThrough() {
        Converter<String, String> nullsArePassed = sillyConverter(false);
        assertThat(nullsArePassed.convert("foo")).isEqualTo("forward");
        assertThat(nullsArePassed.convert(null)).isEqualTo("forward");
        assertThat(nullsArePassed.reverse().convert("foo")).isEqualTo("backward");
        assertThat(nullsArePassed.reverse().convert(null)).isEqualTo("backward");
    }

    public void testNullIsNotPassedThrough() {
        Converter<String, String> nullsAreHandled = sillyConverter(true);
        assertThat(nullsAreHandled.convert("foo")).isEqualTo("forward");
        assertThat(nullsAreHandled.convert(null)).isEqualTo(null);
        assertThat(nullsAreHandled.reverse().convert("foo")).isEqualTo("backward");
        assertThat(nullsAreHandled.reverse().convert(null)).isEqualTo(null);
    }

    private static Converter<String, String> sillyConverter(final boolean handleNullAutomatically) {
        return new Converter<String, String>(handleNullAutomatically) {
            @Override
            protected String doForward(String string) {
                return "forward";
            }

            @Override
            protected String doBackward(String string) {
                return "backward";
            }
        };
    }

    public void testSerialization_identity() {
        Converter<String, String> identityConverter = Converter.identity();
        SerializableTester.reserializeAndAssert(identityConverter);
    }

    public void testSerialization_reverse() {
        Converter<Long, String> reverseConverter = Longs.stringConverter().reverse();
        SerializableTester.reserializeAndAssert(reverseConverter);
    }

    public void testSerialization_andThen() {
        Converter<String, Long> converterA = Longs.stringConverter();
        Converter<Long, String> reverseConverter = Longs.stringConverter().reverse();
        Converter<String, String> composedConverter = converterA.andThen(reverseConverter);
        SerializableTester.reserializeAndAssert(composedConverter);
    }

    public void testSerialization_from() {
        Converter<String, String> dumb = Converter.from(toStringFunction(), toStringFunction());
        SerializableTester.reserializeAndAssert(dumb);
    }
}
