/*
 * Copyright (C) 2005 The Guava Authors
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
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import junit.framework.TestCase;

import java.io.Serializable;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link Functions}.
 *
 * @author Mike Bostock
 * @author Vlad Patryshev
 */
@GwtCompatible(emulated = true)
public class FunctionsTest extends TestCase {

    public void testIdentity_same() {
        Function<String, String> identity = Functions.identity();
        assertThat(identity.apply(null)).isNull();
        assertThat(identity.apply("foo")).isSameAs("foo");
    }

    public void testIdentity_notSame() {
        Function<Long, Long> identity = Functions.identity();
        assertThat(identity.apply(new Long(135135L))).isNotSameAs(new Long(135135L));
    }

    public void testToStringFunction_apply() {
        assertThat(Functions.toStringFunction().apply(3)).isEqualTo("3");
        assertThat(Functions.toStringFunction().apply("hiya")).isEqualTo("hiya");
        assertEquals(
                "I'm a string",
                Functions.toStringFunction()
                        .apply(
                                new Object() {
                                    @Override
                                    public String toString() {
                                        return "I'm a string";
                                    }
                                }));
        try {
            Functions.toStringFunction().apply(null);
            fail("expected NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    public void testForMapWithoutDefault() {
        Map<String, Integer> map = Maps.newHashMap();
        map.put("One", 1);
        map.put("Three", 3);
        map.put("Null", null);
        Function<String, Integer> function = Functions.forMap(map);

        assertThat(function.apply("One").intValue()).isEqualTo(1);
        assertThat(function.apply("Three").intValue()).isEqualTo(3);
        assertThat(function.apply("Null")).isNull();

        try {
            function.apply("Two");
            fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    public void testForMapWithDefault() {
        Map<String, Integer> map = Maps.newHashMap();
        map.put("One", 1);
        map.put("Three", 3);
        map.put("Null", null);
        Function<String, Integer> function = Functions.forMap(map, 42);

        assertThat(function.apply("One").intValue()).isEqualTo(1);
        assertThat(function.apply("Two").intValue()).isEqualTo(42);
        assertThat(function.apply("Three").intValue()).isEqualTo(3);
        assertThat(function.apply("Null")).isNull();
    }

    @GwtIncompatible // SerializableTester
    public void testForMapWithDefault_includeSerializable() {
        Map<String, Integer> map = Maps.newHashMap();
        map.put("One", 1);
        map.put("Three", 3);
        Function<String, Integer> function = Functions.forMap(map, 42);

        assertThat(function.apply("One").intValue()).isEqualTo(1);
        assertThat(function.apply("Two").intValue()).isEqualTo(42);
        assertThat(function.apply("Three").intValue()).isEqualTo(3);
    }

    public void testForMapWithDefault_null() {
        ImmutableMap<String, Integer> map = ImmutableMap.of("One", 1);
        Function<String, Integer> function = Functions.forMap(map, null);

        assertThat(function.apply("One")).isEqualTo((Integer) 1);
        assertThat(function.apply("Two")).isNull();
    }

    @GwtIncompatible // SerializableTester
    public void testForMapWithDefault_null_compareWithSerializable() {
        ImmutableMap<String, Integer> map = ImmutableMap.of("One", 1);
        Function<String, Integer> function = Functions.forMap(map, null);

        assertThat(function.apply("One")).isEqualTo((Integer) 1);
        assertThat(function.apply("Two")).isNull();
    }

    public void testForMapWildCardWithDefault() {
        Map<String, Integer> map = Maps.newHashMap();
        map.put("One", 1);
        map.put("Three", 3);
        Number number = Double.valueOf(42);
        Function<String, Number> function = Functions.forMap(map, number);

        assertThat(function.apply("One").intValue()).isEqualTo(1);
        assertThat(function.apply("Two")).isEqualTo(number);
        assertThat(function.apply("Three").longValue()).isEqualTo(3L);
    }

    public void testComposition() {
        Map<String, Integer> mJapaneseToInteger = Maps.newHashMap();
        mJapaneseToInteger.put("Ichi", 1);
        mJapaneseToInteger.put("Ni", 2);
        mJapaneseToInteger.put("San", 3);
        Function<String, Integer> japaneseToInteger = Functions.forMap(mJapaneseToInteger);

        Map<Integer, String> mIntegerToSpanish = Maps.newHashMap();
        mIntegerToSpanish.put(1, "Uno");
        mIntegerToSpanish.put(3, "Tres");
        mIntegerToSpanish.put(4, "Cuatro");
        Function<Integer, String> integerToSpanish = Functions.forMap(mIntegerToSpanish);

        Function<String, String> japaneseToSpanish =
                Functions.compose(integerToSpanish, japaneseToInteger);

        assertThat(japaneseToSpanish.apply("Ichi")).isEqualTo("Uno");
        try {
            japaneseToSpanish.apply("Ni");
            fail();
        } catch (IllegalArgumentException e) {
        }
        assertThat(japaneseToSpanish.apply("San")).isEqualTo("Tres");
        try {
            japaneseToSpanish.apply("Shi");
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

    @GwtIncompatible // SerializableTester
    public void testComposition_includeReserializabled() {
        Map<String, Integer> mJapaneseToInteger = Maps.newHashMap();
        mJapaneseToInteger.put("Ichi", 1);
        mJapaneseToInteger.put("Ni", 2);
        mJapaneseToInteger.put("San", 3);
        Function<String, Integer> japaneseToInteger = Functions.forMap(mJapaneseToInteger);

        Map<Integer, String> mIntegerToSpanish = Maps.newHashMap();
        mIntegerToSpanish.put(1, "Uno");
        mIntegerToSpanish.put(3, "Tres");
        mIntegerToSpanish.put(4, "Cuatro");
        Function<Integer, String> integerToSpanish = Functions.forMap(mIntegerToSpanish);

        Function<String, String> japaneseToSpanish =
                Functions.compose(integerToSpanish, japaneseToInteger);
    }

    public void testCompositionWildcard() {
        Map<String, Integer> mapJapaneseToInteger = Maps.newHashMap();
        Function<String, Integer> japaneseToInteger = Functions.forMap(mapJapaneseToInteger);

        Function<Object, String> numberToSpanish = Functions.constant("Yo no se");

        Function<String, String> japaneseToSpanish =
                Functions.compose(numberToSpanish, japaneseToInteger);
    }

    private static class HashCodeFunction implements Function<Object, Integer> {
        @Override
        public Integer apply(Object o) {
            return (o == null) ? 0 : o.hashCode();
        }
    }

    public void testComposeOfFunctionsIsAssociative() {
        Map<Float, String> m = ImmutableMap.of(4.0f, "A", 3.0f, "B", 2.0f, "C", 1.0f, "D");
        Function<? super Integer, Boolean> h = Functions.constant(Boolean.TRUE);
        Function<? super String, Integer> g = new HashCodeFunction();
        Function<Float, String> f = Functions.forMap(m, "F");

        Function<Float, Boolean> c1 = Functions.compose(Functions.compose(h, g), f);
        Function<Float, Boolean> c2 = Functions.compose(h, Functions.compose(g, f));

        // Might be nice (eventually) to have:
        //     assertThat(c2).isEqualTo(c1);

        // But for now, settle for this:
        assertThat(c2.hashCode()).isEqualTo(c1.hashCode());

        assertThat(c2.apply(1.0f)).isEqualTo(c1.apply(1.0f));
        assertThat(c2.apply(5.0f)).isEqualTo(c1.apply(5.0f));
    }

    public void testComposeOfPredicateAndFunctionIsAssociative() {
        Map<Float, String> m = ImmutableMap.of(4.0f, "A", 3.0f, "B", 2.0f, "C", 1.0f, "D");
        Predicate<? super Integer> h = Predicates.equalTo(42);
        Function<? super String, Integer> g = new HashCodeFunction();
        Function<Float, String> f = Functions.forMap(m, "F");

        Predicate<Float> p1 = Predicates.compose(Predicates.compose(h, g), f);
        Predicate<Float> p2 = Predicates.compose(h, Functions.compose(g, f));

        // Might be nice (eventually) to have:
        //     assertThat(p2).isEqualTo(p1);

        // But for now, settle for this:
        assertThat(p2.hashCode()).isEqualTo(p1.hashCode());

        assertThat(p2.apply(1.0f)).isEqualTo(p1.apply(1.0f));
        assertThat(p2.apply(5.0f)).isEqualTo(p1.apply(5.0f));
    }

    public void testForPredicate() {
        Function<Object, Boolean> alwaysTrue = Functions.forPredicate(Predicates.alwaysTrue());
        Function<Object, Boolean> alwaysFalse = Functions.forPredicate(Predicates.alwaysFalse());

        assertThat(alwaysTrue.apply(0)).isTrue();
        assertThat(alwaysFalse.apply(0)).isFalse();
    }

    public void testConstant() {
        Function<Object, Object> f = Functions.constant("correct");
        assertThat(f.apply(new Object())).isEqualTo("correct");
        assertThat(f.apply(null)).isEqualTo("correct");

        Function<Object, String> g = Functions.constant(null);
        assertThat(g.apply(2)).isEqualTo(null);
        assertThat(g.apply(null)).isEqualTo(null);
    }

    private static class CountingSupplier implements Supplier<Integer>, Serializable {

        private static final long serialVersionUID = 0;

        private int value;

        @Override
        public Integer get() {
            return ++value;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof CountingSupplier) {
                return this.value == ((CountingSupplier) obj).value;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return value;
        }
    }

    public void testForSupplier() {
        Supplier<Integer> supplier = new CountingSupplier();
        Function<Object, Integer> function = Functions.forSupplier(supplier);

        assertThat((int) function.apply(null)).isEqualTo(1);
        assertThat((int) function.apply("foo")).isEqualTo(2);
    }
}
