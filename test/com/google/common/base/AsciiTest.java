/*
 * Copyright (C) 2010 The Guava Authors
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
import junit.framework.TestCase;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for {@link Ascii}.
 *
 * @author Craig Berry
 */
@GwtCompatible
public class AsciiTest extends TestCase {

  /**
   * The Unicode points {@code 00c1} and {@code 00e1} are the upper- and lowercase forms of
   * A-with-acute-accent, {@code Á} and {@code á}.
   */
  private static final String IGNORED = "`10-=~!@#$%^&*()_+[]\\{}|;':\",./<>?'\u00c1\u00e1\n";

  private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
  private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

  public void testToLowerCase() {
    assertThat(Ascii.toLowerCase(UPPER)).isEqualTo(LOWER);
    assertThat(Ascii.toLowerCase(LOWER)).isSameAs(LOWER);
    assertThat(Ascii.toLowerCase(IGNORED)).isEqualTo(IGNORED);
    assertThat(Ascii.toLowerCase("fOobaR")).isEqualTo("foobar");
  }

  public void testToUpperCase() {
    assertThat(Ascii.toUpperCase(LOWER)).isEqualTo(UPPER);
    assertThat(Ascii.toUpperCase(UPPER)).isSameAs(UPPER);
    assertThat(Ascii.toUpperCase(IGNORED)).isEqualTo(IGNORED);
    assertThat(Ascii.toUpperCase("FoOBAr")).isEqualTo("FOOBAR");
  }

  public void testCharsIgnored() {
    for (char c : IGNORED.toCharArray()) {
      String str = String.valueOf(c);
        assertThat(Ascii.toLowerCase(c)).isEqualTo(c);
        assertThat(Ascii.toUpperCase(c)).isEqualTo(c);
        assertThat(Ascii.isLowerCase(c)).isFalse();
        assertThat(Ascii.isUpperCase(c)).isFalse();
    }
  }

  public void testCharsLower() {
    for (char c : LOWER.toCharArray()) {
      String str = String.valueOf(c);
      assertThat(Ascii.toLowerCase(c)).isEqualTo(c);
      assertThat(Ascii.toUpperCase(c)).isNotEqualTo(c);
      assertThat(Ascii.isLowerCase(c)).isTrue();
      assertThat(Ascii.isUpperCase(c)).isFalse();
    }
  }

  public void testCharsUpper() {
    for (char c : UPPER.toCharArray()) {
      String str = String.valueOf(c);
        assertThat(Ascii.toLowerCase(c)).isNotEqualTo(c);
        assertThat(Ascii.toUpperCase(c)).isEqualTo(c);
        assertThat(Ascii.isLowerCase(c)).isFalse();
        assertThat(Ascii.isUpperCase(c)).isTrue();
    }
  }

  public void testTruncate() {
      assertThat(Ascii.truncate("foobar", 10, "...")).isEqualTo("foobar");
      assertThat(Ascii.truncate("foobar", 5, "...")).isEqualTo("fo...");
      assertThat(Ascii.truncate("foobar", 6, "...")).isEqualTo("foobar");
      assertThat(Ascii.truncate("foobar", 3, "...")).isEqualTo("...");
      assertThat(Ascii.truncate("foobar", 10, "…")).isEqualTo("foobar");
      assertThat(Ascii.truncate("foobar", 4, "…")).isEqualTo("foo…");
      assertThat(Ascii.truncate("foobar", 4, "--")).isEqualTo("fo--");
      assertThat(Ascii.truncate("foobar", 6, "…")).isEqualTo("foobar");
      assertThat(Ascii.truncate("foobar", 5, "…")).isEqualTo("foob…");
      assertThat(Ascii.truncate("foobar", 3, "")).isEqualTo("foo");
      assertThat(Ascii.truncate("", 5, "")).isEqualTo("");
      assertThat(Ascii.truncate("", 5, "...")).isEqualTo("");
      assertThat(Ascii.truncate("", 0, "")).isEqualTo("");
  }

  public void testTruncateIllegalArguments() {
    try {
      Ascii.truncate("foobar", 2, "...");
      fail();
    } catch (IllegalArgumentException expected) {
    }

    try {
      Ascii.truncate("foobar", 8, "1234567890");
      fail();
    } catch (IllegalArgumentException expected) {
    }

    try {
      Ascii.truncate("foobar", -1, "...");
      fail();
    } catch (IllegalArgumentException expected) {
    }

    try {
      Ascii.truncate("foobar", -1, "");
      fail();
    } catch (IllegalArgumentException expected) {
    }
  }

  public void testEqualsIgnoreCase() {
    assertThat(Ascii.equalsIgnoreCase("", "")).isTrue();
    assertThat(Ascii.equalsIgnoreCase("", "x")).isFalse();
    assertThat(Ascii.equalsIgnoreCase("x", "")).isFalse();
    assertThat(Ascii.equalsIgnoreCase(LOWER, UPPER)).isTrue();
    assertThat(Ascii.equalsIgnoreCase(UPPER, LOWER)).isTrue();
    // Create new strings here to avoid early-out logic.
    assertThat(Ascii.equalsIgnoreCase(new String(IGNORED), new String(IGNORED))).isTrue();
    // Compare to: "\u00c1".equalsIgnoreCase("\u00e1") == true
    assertThat(Ascii.equalsIgnoreCase("\u00c1", "\u00e1")).isFalse();
    // Test chars just outside the alphabetic range ('A'-1 vs 'a'-1, 'Z'+1 vs 'z'+1)
    assertThat(Ascii.equalsIgnoreCase("@", "`")).isFalse();
    assertThat(Ascii.equalsIgnoreCase("[", "{")).isFalse();
  }

  @GwtIncompatible // String.toUpperCase() has browser semantics
  public void testEqualsIgnoreCaseUnicodeEquivalence() {
    // Note that it's possible in future that the JDK's idea to toUpperCase() or equalsIgnoreCase()
    // may change and break assumptions in this test [*]. This is not a bug in the implementation of
    // Ascii.equalsIgnoreCase(), but it is a signal that its documentation may need updating as
    // regards edge cases.

    // The Unicode point {@code 00df} is the lowercase form of sharp-S (ß), whose uppercase is "SS".
    assertThat("pa\u00dfword".toUpperCase()).isEqualTo("PASSWORD"); // [*]
    assertThat("pa\u00dfword".equalsIgnoreCase("PASSWORD")).isFalse(); // [*]
    assertThat(Ascii.equalsIgnoreCase("pa\u00dfword", "PASSWORD")).isFalse();
  }
}
