/*
 * Copyright (C) 2011 The Guava Authors
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
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.testing.EqualsTester;
import com.google.common.testing.NullPointerTester;
import junit.framework.TestCase;

import java.util.Collections;
import java.util.Set;

import static com.google.common.testing.SerializableTester.reserialize;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for {@link Optional}.
 *
 * @author Kurt Alfred Kluever
 */
@GwtCompatible(emulated = true)
public final class OptionalTest extends TestCase {
    public void testToJavaUtil_static() {
        assertThat(Optional.toJavaUtil(null)).isNull();
        assertThat(Optional.toJavaUtil(Optional.absent())).isEmpty();
        assertThat(Optional.toJavaUtil(Optional.of("abc"))).hasValue("abc");
    }

    public void testToJavaUtil_instance() {
        assertThat(Optional.absent().toJavaUtil()).isEmpty();
        assertThat(Optional.of("abc").toJavaUtil()).hasValue("abc");
    }

    public void testFromJavaUtil() {
        assertThat(Optional.fromJavaUtil(null)).isNull();
        assertThat(Optional.fromJavaUtil(java.util.Optional.empty())).isEqualTo(Optional.absent());
        assertThat(Optional.fromJavaUtil(java.util.Optional.of("abc"))).isEqualTo(Optional.of("abc"));
    }

    public void testAbsent() {
        Optional<String> optionalName = Optional.absent();
        assertThat(optionalName.isPresent()).isFalse();
    }

    public void testOf() {
        assertThat(Optional.of("training").get()).isEqualTo("training");
    }

    public void testOf_null() {
        try {
            Optional.of(null);
            fail();
        } catch (NullPointerException expected) {
        }
    }

    public void testFromNullable() {
        Optional<String> optionalName = Optional.fromNullable("bob");
        assertThat(optionalName.get()).isEqualTo("bob");
    }

    public void testFromNullable_null() {
        // not promised by spec, but easier to test
        assertThat(Optional.fromNullable(null)).isSameAs(Optional.absent());
    }

    public void testIsPresent_no() {
        assertThat(Optional.absent().isPresent()).isFalse();
    }

    @SuppressWarnings("OptionalOfRedundantMethod") // Unit tests for Optional
    public void testIsPresent_yes() {
        assertThat(Optional.of("training").isPresent()).isTrue();
    }

    public void testGet_absent() {
        Optional<String> optional = Optional.absent();
        try {
            optional.get();
            fail();
        } catch (IllegalStateException expected) {
        }
    }

    public void testGet_present() {
        assertThat(Optional.of("training").get()).isEqualTo("training");
    }

    @SuppressWarnings("OptionalOfRedundantMethod") // Unit tests for Optional
    public void testOr_T_present() {
        assertThat(Optional.of("a").or("default")).isEqualTo("a");
    }

    public void testOr_T_absent() {
        assertThat(Optional.absent().or("default")).isEqualTo("default");
    }

    @SuppressWarnings("OptionalOfRedundantMethod") // Unit tests for Optional
    public void testOr_supplier_present() {
        assertThat(Optional.of("a").or(Suppliers.ofInstance("fallback"))).isEqualTo("a");
    }

    public void testOr_supplier_absent() {
        assertThat(Optional.absent().or(Suppliers.ofInstance("fallback"))).isEqualTo("fallback");
    }

    public void testOr_nullSupplier_absent() {
        Supplier<Object> nullSupplier = Suppliers.ofInstance(null);
        Optional<Object> absentOptional = Optional.absent();
        try {
            absentOptional.or(nullSupplier);
            fail();
        } catch (NullPointerException expected) {
        }
    }

    @SuppressWarnings("OptionalOfRedundantMethod") // Unit tests for Optional
    public void testOr_nullSupplier_present() {
        Supplier<String> nullSupplier = Suppliers.ofInstance(null);
        assertThat(Optional.of("a").or(nullSupplier)).isEqualTo("a");
    }

    @SuppressWarnings("OptionalOfRedundantMethod") // Unit tests for Optional
    public void testOr_Optional_present() {
        assertThat(Optional.of("a").or(Optional.of("fallback"))).isEqualTo(Optional.of("a"));
    }

    public void testOr_Optional_absent() {
        assertThat(Optional.absent().or(Optional.of("fallback"))).isEqualTo(Optional.of("fallback"));
    }

    @SuppressWarnings("OptionalOfRedundantMethod") // Unit tests for Optional
    public void testOrNull_present() {
        assertThat(Optional.of("a").orNull()).isEqualTo("a");
    }

    public void testOrNull_absent() {
        assertThat(Optional.absent().orNull()).isNull();
    }

    public void testAsSet_present() {
        Set<String> expected = Collections.singleton("a");
        assertThat(Optional.of("a").asSet()).isEqualTo(expected);
    }

    public void testAsSet_absent() {
        assertThat(Optional.absent().asSet().isEmpty()).as("Returned set should be empty").isTrue();
    }

    public void testAsSet_presentIsImmutable() {
        Set<String> presentAsSet = Optional.of("a").asSet();
        try {
            presentAsSet.add("b");
            fail();
        } catch (UnsupportedOperationException expected) {
        }
    }

    public void testAsSet_absentIsImmutable() {
        Set<Object> absentAsSet = Optional.absent().asSet();
        try {
            absentAsSet.add("foo");
            fail();
        } catch (UnsupportedOperationException expected) {
        }
    }

    public void testTransform_absent() {
        assertThat(Optional.absent().transform(Functions.identity())).isEqualTo(Optional.absent());
        assertThat(Optional.absent().transform(Functions.toStringFunction())).isEqualTo(Optional.absent());
    }

    public void testTransform_presentIdentity() {
        assertThat(Optional.of("a").transform(Functions.identity())).isEqualTo(Optional.of("a"));
    }

    public void testTransform_presentToString() {
        assertThat(Optional.of(42).transform(Functions.toStringFunction())).isEqualTo(Optional.of("42"));
    }

    public void testTransform_present_functionReturnsNull() {
        try {
            Optional<String> unused =
                    Optional.of("a")
                            .transform(
                                    new Function<String, String>() {
                                        @Override
                                        public String apply(String input) {
                                            return null;
                                        }
                                    });
            fail("Should throw if Function returns null.");
        } catch (NullPointerException expected) {
        }
    }

    public void testTransform_absent_functionReturnsNull() {
        assertEquals(
                Optional.absent(),
                Optional.absent()
                        .transform(
                                new Function<Object, Object>() {
                                    @Override
                                    public Object apply(Object input) {
                                        return null;
                                    }
                                }));
    }

    public void testEqualsAndHashCode() {
        new EqualsTester()
                .addEqualityGroup(Optional.absent(), reserialize(Optional.absent()))
                .addEqualityGroup(Optional.of(new Long(5)), reserialize(Optional.of(new Long(5))))
                .addEqualityGroup(Optional.of(new Long(42)), reserialize(Optional.of(new Long(42))))
                .testEquals();
    }

    public void testToString_absent() {
        assertThat(Optional.absent().toString()).isEqualTo("Optional.absent()");
    }

    public void testToString_present() {
        assertThat(Optional.of("training").toString()).isEqualTo("Optional.of(training)");
    }

    private static Optional<Integer> getSomeOptionalInt() {
        return Optional.of(1);
    }

    private static FluentIterable<? extends Number> getSomeNumbers() {
        return FluentIterable.from(ImmutableList.<Number>of());
    }

    /*
     * The following tests demonstrate the shortcomings of or() and test that the casting workaround
     * mentioned in the method Javadoc does in fact compile.
     */

    @SuppressWarnings("unused") // compilation test
    public void testSampleCodeError1() {
        Optional<Integer> optionalInt = getSomeOptionalInt();
        // Number value = optionalInt.or(0.5); // error
    }

    @SuppressWarnings("unused") // compilation test
    public void testSampleCodeError2() {
        FluentIterable<? extends Number> numbers = getSomeNumbers();
        Optional<? extends Number> first = numbers.first();
        // Number value = first.or(0.5); // error
    }

    @SuppressWarnings("unused") // compilation test
    public void testSampleCodeFine1() {
        Optional<Number> optionalInt = Optional.of((Number) 1);
        Number value = optionalInt.or(0.5); // fine
    }

    @SuppressWarnings("unused") // compilation test
    public void testSampleCodeFine2() {
        FluentIterable<? extends Number> numbers = getSomeNumbers();

        // Sadly, the following is what users will have to do in some circumstances.

        @SuppressWarnings("unchecked") // safe covariant cast
        Optional<Number> first = (Optional<Number>) numbers.first();
        Number value = first.or(0.5); // fine
    }

    @GwtIncompatible // NullPointerTester
    public void testNullPointers() {
        NullPointerTester npTester = new NullPointerTester();
        npTester.testAllPublicConstructors(Optional.class);
        npTester.testAllPublicStaticMethods(Optional.class);
        npTester.testAllPublicInstanceMethods(Optional.absent());
        npTester.testAllPublicInstanceMethods(Optional.of("training"));
    }
}
