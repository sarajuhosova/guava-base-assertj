/*
 * Copyright (C) 2006 The Guava Authors
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
import junit.framework.TestCase;

import static com.google.common.base.CaseFormat.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for {@link CaseFormat}.
 *
 * @author Mike Bostock
 */
@GwtCompatible(emulated = true)
public class CaseFormatTest extends TestCase {

    public void testIdentity() {
        for (CaseFormat from : CaseFormat.values()) {
            assertThat(from.to(from, "foo")).isSameAs("foo");
            for (CaseFormat to : CaseFormat.values()) {
                assertThat(from.to(to, "")).isEqualTo("");
                assertThat(from.to(to, " ")).isEqualTo(" ");
            }
        }
    }

//    public void testLowerHyphenToLowerHyphen() {
//        assertThat().isEqualTo();("foo", LOWER_HYPHEN.to(LOWER_HYPHEN, "foo"));
//        assertThat().isEqualTo();("foo-bar", LOWER_HYPHEN.to(LOWER_HYPHEN, "foo-bar"));
//    }
//
//    public void testLowerHyphenToLowerUnderscore() {
//        assertThat().isEqualTo();("foo", LOWER_HYPHEN.to(LOWER_UNDERSCORE, "foo"));
//        assertThat().isEqualTo();("foo_bar", LOWER_HYPHEN.to(LOWER_UNDERSCORE, "foo-bar"));
//    }
//
//    public void testLowerHyphenToLowerCamel() {
//        assertThat().isEqualTo();("foo", LOWER_HYPHEN.to(LOWER_CAMEL, "foo"));
//        assertThat().isEqualTo();("fooBar", LOWER_HYPHEN.to(LOWER_CAMEL, "foo-bar"));
//    }
//
//    public void testLowerHyphenToUpperCamel() {
//        assertThat().isEqualTo();("Foo", LOWER_HYPHEN.to(UPPER_CAMEL, "foo"));
//        assertThat().isEqualTo();("FooBar", LOWER_HYPHEN.to(UPPER_CAMEL, "foo-bar"));
//    }
//
//    public void testLowerHyphenToUpperUnderscore() {
//        assertThat().isEqualTo();("FOO", LOWER_HYPHEN.to(UPPER_UNDERSCORE, "foo"));
//        assertThat().isEqualTo();("FOO_BAR", LOWER_HYPHEN.to(UPPER_UNDERSCORE, "foo-bar"));
//    }
//
//    public void testLowerUnderscoreToLowerHyphen() {
//        assertThat().isEqualTo();("foo", LOWER_UNDERSCORE.to(LOWER_HYPHEN, "foo"));
//        assertThat().isEqualTo();("foo-bar", LOWER_UNDERSCORE.to(LOWER_HYPHEN, "foo_bar"));
//    }
//
//    public void testLowerUnderscoreToLowerUnderscore() {
//        assertThat().isEqualTo();("foo", LOWER_UNDERSCORE.to(LOWER_UNDERSCORE, "foo"));
//        assertThat().isEqualTo();("foo_bar", LOWER_UNDERSCORE.to(LOWER_UNDERSCORE, "foo_bar"));
//    }
//
//    public void testLowerUnderscoreToLowerCamel() {
//        assertThat().isEqualTo();("foo", LOWER_UNDERSCORE.to(LOWER_CAMEL, "foo"));
//        assertThat().isEqualTo();("fooBar", LOWER_UNDERSCORE.to(LOWER_CAMEL, "foo_bar"));
//    }
//
//    public void testLowerUnderscoreToUpperCamel() {
//        assertThat().isEqualTo();("Foo", LOWER_UNDERSCORE.to(UPPER_CAMEL, "foo"));
//        assertThat().isEqualTo();("FooBar", LOWER_UNDERSCORE.to(UPPER_CAMEL, "foo_bar"));
//    }
//
//    public void testLowerUnderscoreToUpperUnderscore() {
//        assertThat().isEqualTo();("FOO", LOWER_UNDERSCORE.to(UPPER_UNDERSCORE, "foo"));
//        assertThat().isEqualTo();("FOO_BAR", LOWER_UNDERSCORE.to(UPPER_UNDERSCORE, "foo_bar"));
//    }
//
//    public void testLowerCamelToLowerHyphen() {
//        assertThat().isEqualTo();("foo", LOWER_CAMEL.to(LOWER_HYPHEN, "foo"));
//        assertThat().isEqualTo();("foo-bar", LOWER_CAMEL.to(LOWER_HYPHEN, "fooBar"));
//        assertThat().isEqualTo();("h-t-t-p", LOWER_CAMEL.to(LOWER_HYPHEN, "HTTP"));
//    }
//
//    public void testLowerCamelToLowerUnderscore() {
//        assertThat().isEqualTo();("foo", LOWER_CAMEL.to(LOWER_UNDERSCORE, "foo"));
//        assertThat().isEqualTo();("foo_bar", LOWER_CAMEL.to(LOWER_UNDERSCORE, "fooBar"));
//        assertThat().isEqualTo();("h_t_t_p", LOWER_CAMEL.to(LOWER_UNDERSCORE, "hTTP"));
//    }
//
//    public void testLowerCamelToLowerCamel() {
//        assertThat().isEqualTo();("foo", LOWER_CAMEL.to(LOWER_CAMEL, "foo"));
//        assertThat().isEqualTo();("fooBar", LOWER_CAMEL.to(LOWER_CAMEL, "fooBar"));
//    }
//
//    public void testLowerCamelToUpperCamel() {
//        assertThat().isEqualTo();("Foo", LOWER_CAMEL.to(UPPER_CAMEL, "foo"));
//        assertThat().isEqualTo();("FooBar", LOWER_CAMEL.to(UPPER_CAMEL, "fooBar"));
//        assertThat().isEqualTo();("HTTP", LOWER_CAMEL.to(UPPER_CAMEL, "hTTP"));
//    }
//
//    public void testLowerCamelToUpperUnderscore() {
//        assertThat().isEqualTo();("FOO", LOWER_CAMEL.to(UPPER_UNDERSCORE, "foo"));
//        assertThat().isEqualTo();("FOO_BAR", LOWER_CAMEL.to(UPPER_UNDERSCORE, "fooBar"));
//    }
//
//    public void testUpperCamelToLowerHyphen() {
//        assertThat(UPPER_CAMEL.to(LOWER_HYPHEN, "Foo")).isEqualTo("foo");
//        assertThat(UPPER_CAMEL.to(LOWER_HYPHEN, "FooBar")).isEqualTo("foo-bar");
//    }
//
//    public void testUpperCamelToLowerUnderscore() {
//        assertThat().isEqualTo();("foo", UPPER_CAMEL.to(LOWER_UNDERSCORE, "Foo"));
//        assertThat().isEqualTo();("foo_bar", UPPER_CAMEL.to(LOWER_UNDERSCORE, "FooBar"));
//    }

    public void testUpperCamelToLowerCamel() {
        assertThat(UPPER_CAMEL.to(LOWER_CAMEL, "Foo")).isEqualTo("foo");
        assertThat(UPPER_CAMEL.to(LOWER_CAMEL, "FooBar")).isEqualTo("fooBar");
        assertThat(UPPER_CAMEL.to(LOWER_CAMEL, "HTTP")).isEqualTo("hTTP");
    }

    public void testUpperCamelToUpperCamel() {
        assertThat(UPPER_CAMEL.to(UPPER_CAMEL, "Foo")).isEqualTo("Foo");
        assertThat(UPPER_CAMEL.to(UPPER_CAMEL, "FooBar")).isEqualTo("FooBar");
    }

    public void testUpperCamelToUpperUnderscore() {
        assertThat(UPPER_CAMEL.to(UPPER_UNDERSCORE, "Foo")).isEqualTo("FOO");
        assertThat(UPPER_CAMEL.to(UPPER_UNDERSCORE, "FooBar")).isEqualTo("FOO_BAR");
        assertThat(UPPER_CAMEL.to(UPPER_UNDERSCORE, "HTTP")).isEqualTo("H_T_T_P");
        assertThat(UPPER_CAMEL.to(UPPER_UNDERSCORE, "H_T_T_P")).isEqualTo("H__T__T__P");
    }

    public void testUpperUnderscoreToLowerHyphen() {
        assertThat(UPPER_UNDERSCORE.to(LOWER_HYPHEN, "FOO")).isEqualTo("foo");
        assertThat(UPPER_UNDERSCORE.to(LOWER_HYPHEN, "FOO_BAR")).isEqualTo("foo-bar");
    }

    public void testUpperUnderscoreToLowerUnderscore() {
        assertThat(UPPER_UNDERSCORE.to(LOWER_UNDERSCORE, "FOO")).isEqualTo("foo");
        assertThat(UPPER_UNDERSCORE.to(LOWER_UNDERSCORE, "FOO_BAR")).isEqualTo("foo_bar");
    }

    public void testUpperUnderscoreToLowerCamel() {
        assertThat(UPPER_UNDERSCORE.to(LOWER_CAMEL, "FOO")).isEqualTo("foo");
        assertThat(UPPER_UNDERSCORE.to(LOWER_CAMEL, "FOO_BAR")).isEqualTo("fooBar");
    }

    public void testUpperUnderscoreToUpperCamel() {
        assertThat(UPPER_UNDERSCORE.to(UPPER_CAMEL, "FOO")).isEqualTo("Foo");
        assertThat(UPPER_UNDERSCORE.to(UPPER_CAMEL, "FOO_BAR")).isEqualTo("FooBar");
        assertThat(UPPER_UNDERSCORE.to(UPPER_CAMEL, "H_T_T_P")).isEqualTo("HTTP");
    }

    public void testUpperUnderscoreToUpperUnderscore() {
        assertThat(UPPER_UNDERSCORE.to(UPPER_UNDERSCORE, "FOO")).isEqualTo("FOO");
        assertThat(UPPER_UNDERSCORE.to(UPPER_UNDERSCORE, "FOO_BAR")).isEqualTo("FOO_BAR");
    }

    public void testConverterToForward() {
        assertThat(UPPER_UNDERSCORE.converterTo(UPPER_CAMEL).convert("FOO_BAR")).isEqualTo("FooBar");
        assertThat(UPPER_UNDERSCORE.converterTo(LOWER_CAMEL).convert("FOO_BAR")).isEqualTo("fooBar");
        assertThat(UPPER_CAMEL.converterTo(UPPER_UNDERSCORE).convert("FooBar")).isEqualTo("FOO_BAR");
        assertThat(LOWER_CAMEL.converterTo(UPPER_UNDERSCORE).convert("fooBar")).isEqualTo("FOO_BAR");
    }

    public void testConverterToBackward() {
        assertThat(UPPER_UNDERSCORE.converterTo(UPPER_CAMEL).reverse().convert("FooBar")).isEqualTo("FOO_BAR");
        assertThat(UPPER_UNDERSCORE.converterTo(LOWER_CAMEL).reverse().convert("fooBar")).isEqualTo("FOO_BAR");
        assertThat(UPPER_CAMEL.converterTo(UPPER_UNDERSCORE).reverse().convert("FOO_BAR")).isEqualTo("FooBar");
        assertThat(LOWER_CAMEL.converterTo(UPPER_UNDERSCORE).reverse().convert("FOO_BAR")).isEqualTo("fooBar");
    }

    public void testConverter_nullConversions() {
        for (CaseFormat outer : CaseFormat.values()) {
            for (CaseFormat inner : CaseFormat.values()) {
                assertThat(outer.converterTo(inner).convert(null)).isNull();
                assertThat(outer.converterTo(inner).reverse().convert(null)).isNull();
            }
        }
    }

    public void testConverter_toString() {
        assertThat(LOWER_HYPHEN.converterTo(UPPER_CAMEL).toString())
                .isEqualTo("LOWER_HYPHEN.converterTo(UPPER_CAMEL)");
    }
}
