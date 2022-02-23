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
import com.google.common.collect.Sets;
import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import java.util.*;

import static com.google.common.base.CharMatcher.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for {@link CharMatcher}.
 *
 * @author Kevin Bourrillion
 */
@GwtCompatible(emulated = true)
public class CharMatcherTest extends TestCase {

    private static final CharMatcher WHATEVER =
            new CharMatcher() {
                @Override
                public boolean matches(char c) {
                    throw new AssertionFailedError("You weren't supposed to actually invoke me!");
                }
            };

    public void testAnyAndNone_logicalOps() throws Exception {
        // These are testing behavior that's never promised by the API, but since
        // we're lucky enough that these do pass, it saves us from having to write
        // more excruciating tests! Hooray!

        assertThat(CharMatcher.none().negate()).isSameAs(CharMatcher.any());
        assertThat(CharMatcher.any().negate()).isSameAs(CharMatcher.none());

        assertThat(CharMatcher.any().and(WHATEVER)).isSameAs(WHATEVER);
        assertThat(CharMatcher.any().or(WHATEVER)).isSameAs(CharMatcher.any());

        assertThat(CharMatcher.none().and(WHATEVER)).isSameAs(CharMatcher.none());
        assertThat(CharMatcher.none().or(WHATEVER)).isSameAs(WHATEVER);
    }

    // The rest of the behavior of ANY and DEFAULT will be covered in the tests for
    // the text processing methods below.

    public void testWhitespaceBreakingWhitespaceSubset() throws Exception {
        for (int c = 0; c <= Character.MAX_VALUE; c++) {
            if (breakingWhitespace().matches((char) c)) {
                assertThat(whitespace().matches((char) c)).isTrue();
            }
        }
    }

    // The next tests require ICU4J and have, at least for now, been sliced out
    // of the open-source view of the tests.

    @GwtIncompatible // Character.isISOControl
    public void testJavaIsoControl() {
        for (int c = 0; c <= Character.MAX_VALUE; c++) {
            assertThat(CharMatcher.javaIsoControl().matches((char) c)).isEqualTo(Character.isISOControl(c));
        }
    }

    // Omitting tests for the rest of the JAVA_* constants as these are defined
    // as extremely straightforward pass-throughs to the JDK methods.

    // We're testing the is(), isNot(), anyOf(), noneOf() and inRange() methods
    // below by testing their text-processing methods.

    // The organization of this test class is unusual, as it's not done by
    // method, but by overall "scenario". Also, the variety of actual tests we
    // do borders on absurd overkill. Better safe than sorry, though?

    @GwtIncompatible // java.util.BitSet
    public void testSetBits() {
        doTestSetBits(CharMatcher.any());
        doTestSetBits(CharMatcher.none());
        doTestSetBits(is('a'));
        doTestSetBits(isNot('a'));
        doTestSetBits(anyOf(""));
        doTestSetBits(anyOf("x"));
        doTestSetBits(anyOf("xy"));
        doTestSetBits(anyOf("CharMatcher"));
        doTestSetBits(noneOf("CharMatcher"));
        doTestSetBits(inRange('n', 'q'));
        doTestSetBits(forPredicate(Predicates.equalTo('c')));
        doTestSetBits(CharMatcher.ascii());
        doTestSetBits(CharMatcher.digit());
        doTestSetBits(CharMatcher.invisible());
        doTestSetBits(CharMatcher.whitespace());
        doTestSetBits(inRange('A', 'Z').and(inRange('F', 'K').negate()));
    }

    @GwtIncompatible // java.util.BitSet
    private void doTestSetBits(CharMatcher matcher) {
        BitSet bitset = new BitSet();
        matcher.setBits(bitset);
        for (int i = Character.MIN_VALUE; i <= Character.MAX_VALUE; i++) {
            assertThat(bitset.get(i)).isEqualTo(matcher.matches((char) i));
        }
    }

    public void testEmpty() throws Exception {
        doTestEmpty(CharMatcher.any());
        doTestEmpty(CharMatcher.none());
        doTestEmpty(is('a'));
        doTestEmpty(isNot('a'));
        doTestEmpty(anyOf(""));
        doTestEmpty(anyOf("x"));
        doTestEmpty(anyOf("xy"));
        doTestEmpty(anyOf("CharMatcher"));
        doTestEmpty(noneOf("CharMatcher"));
        doTestEmpty(inRange('n', 'q'));
        doTestEmpty(forPredicate(Predicates.equalTo('c')));
    }

    private void doTestEmpty(CharMatcher matcher) throws Exception {
        reallyTestEmpty(matcher);
        reallyTestEmpty(matcher.negate());
        reallyTestEmpty(matcher.precomputed());
    }

    private void reallyTestEmpty(CharMatcher matcher) throws Exception {
        assertThat(matcher.indexIn("")).isEqualTo(-1);
        assertThat(matcher.indexIn("", 0)).isEqualTo(-1);
        try {
            matcher.indexIn("", 1);
            fail();
        } catch (IndexOutOfBoundsException expected) {
        }
        try {
            matcher.indexIn("", -1);
            fail();
        } catch (IndexOutOfBoundsException expected) {
        }
        assertThat(matcher.lastIndexIn("")).isEqualTo(-1);
        assertThat(matcher.matchesAnyOf("")).isFalse();
        assertThat(matcher.matchesAllOf("")).isTrue();
        assertThat(matcher.matchesNoneOf("")).isTrue();
        assertThat(matcher.removeFrom("")).isEqualTo("");
        assertThat(matcher.replaceFrom("", 'z')).isEqualTo("");
        assertThat(matcher.replaceFrom("", "ZZ")).isEqualTo("");
        assertThat(matcher.trimFrom("")).isEqualTo("");
        assertThat(matcher.countIn("")).isEqualTo(0);
    }

    public void testNoMatches() {
        doTestNoMatches(CharMatcher.none(), "blah");
        doTestNoMatches(is('a'), "bcde");
        doTestNoMatches(isNot('a'), "aaaa");
        doTestNoMatches(anyOf(""), "abcd");
        doTestNoMatches(anyOf("x"), "abcd");
        doTestNoMatches(anyOf("xy"), "abcd");
        doTestNoMatches(anyOf("CharMatcher"), "zxqy");
        doTestNoMatches(noneOf("CharMatcher"), "ChMa");
        doTestNoMatches(inRange('p', 'x'), "mom");
        doTestNoMatches(forPredicate(Predicates.equalTo('c')), "abe");
        doTestNoMatches(inRange('A', 'Z').and(inRange('F', 'K').negate()), "F1a");
        doTestNoMatches(CharMatcher.digit(), "\tAz()");
        doTestNoMatches(CharMatcher.javaDigit(), "\tAz()");
        doTestNoMatches(CharMatcher.digit().and(CharMatcher.ascii()), "\tAz()");
        doTestNoMatches(CharMatcher.singleWidth(), "\u05bf\u3000");
    }

    private void doTestNoMatches(CharMatcher matcher, String s) {
        reallyTestNoMatches(matcher, s);
        reallyTestAllMatches(matcher.negate(), s);
        reallyTestNoMatches(matcher.precomputed(), s);
        reallyTestAllMatches(matcher.negate().precomputed(), s);
        reallyTestAllMatches(matcher.precomputed().negate(), s);
        reallyTestNoMatches(forPredicate(matcher), s);

        reallyTestNoMatches(matcher, new StringBuilder(s));
    }

    public void testAllMatches() {
        doTestAllMatches(CharMatcher.any(), "blah");
        doTestAllMatches(isNot('a'), "bcde");
        doTestAllMatches(is('a'), "aaaa");
        doTestAllMatches(noneOf("CharMatcher"), "zxqy");
        doTestAllMatches(anyOf("x"), "xxxx");
        doTestAllMatches(anyOf("xy"), "xyyx");
        doTestAllMatches(anyOf("CharMatcher"), "ChMa");
        doTestAllMatches(inRange('m', 'p'), "mom");
        doTestAllMatches(forPredicate(Predicates.equalTo('c')), "ccc");
        doTestAllMatches(CharMatcher.digit(), "0123456789\u0ED0\u1B59");
        doTestAllMatches(CharMatcher.javaDigit(), "0123456789");
        doTestAllMatches(CharMatcher.digit().and(CharMatcher.ascii()), "0123456789");
        doTestAllMatches(CharMatcher.singleWidth(), "\t0123ABCdef~\u00A0\u2111");
    }

    private void doTestAllMatches(CharMatcher matcher, String s) {
        reallyTestAllMatches(matcher, s);
        reallyTestNoMatches(matcher.negate(), s);
        reallyTestAllMatches(matcher.precomputed(), s);
        reallyTestNoMatches(matcher.negate().precomputed(), s);
        reallyTestNoMatches(matcher.precomputed().negate(), s);
        reallyTestAllMatches(forPredicate(matcher), s);

        reallyTestAllMatches(matcher, new StringBuilder(s));
    }

    private void reallyTestNoMatches(CharMatcher matcher, CharSequence s) {
        assertThat(matcher.matches(s.charAt(0))).isFalse();
        assertThat(matcher.indexIn(s)).isEqualTo(-1);
        assertThat(matcher.indexIn(s, 0)).isEqualTo(-1);
        assertThat(matcher.indexIn(s, 1)).isEqualTo(-1);
        assertThat(matcher.indexIn(s, s.length())).isEqualTo(-1);
        try {
            matcher.indexIn(s, s.length() + 1);
            fail();
        } catch (IndexOutOfBoundsException expected) {
        }
        try {
            matcher.indexIn(s, -1);
            fail();
        } catch (IndexOutOfBoundsException expected) {
        }
        assertThat(matcher.lastIndexIn(s)).isEqualTo(-1);
        assertThat(matcher.matchesAnyOf(s)).isFalse();
        assertThat(matcher.matchesAllOf(s)).isFalse();
        assertThat(matcher.matchesNoneOf(s)).isTrue();

        assertThat(matcher.removeFrom(s)).isEqualTo(s.toString());
        assertThat(matcher.replaceFrom(s, 'z')).isEqualTo(s.toString());
        assertThat(matcher.replaceFrom(s, "ZZ")).isEqualTo(s.toString());
        assertThat(matcher.trimFrom(s)).isEqualTo(s.toString());
        assertThat(matcher.countIn(s)).isEqualTo(0);
    }

    private void reallyTestAllMatches(CharMatcher matcher, CharSequence s) {
        assertThat(matcher.matches(s.charAt(0))).isTrue();
        assertThat(matcher.indexIn(s)).isEqualTo(0);
        assertThat(matcher.indexIn(s, 0)).isEqualTo(0);
        assertThat(matcher.indexIn(s, 1)).isEqualTo(1);
        assertThat(matcher.indexIn(s, s.length())).isEqualTo(-1);
        assertThat(matcher.lastIndexIn(s)).isEqualTo(s.length() - 1);
        assertThat(matcher.matchesAnyOf(s)).isTrue();
        assertThat(matcher.matchesAllOf(s)).isTrue();
        assertThat(matcher.matchesNoneOf(s)).isFalse();
        assertThat(matcher.removeFrom(s)).isEqualTo("");
        assertThat(matcher.replaceFrom(s, 'z')).isEqualTo(Strings.repeat("z", s.length()));
        assertThat(matcher.replaceFrom(s, "ZZ")).isEqualTo(Strings.repeat("ZZ", s.length()));
        assertEquals("", matcher.trimFrom(s));
        assertThat(matcher.countIn(s)).isEqualTo(s.length());
    }

    /**
     * Checks that expected is equals to out, and further, if in is equals to expected, then out is
     * successfully optimized to be identical to in, i.e. that "in" is simply returned.
     */
    private void assertEqualsSame(String expected, String in, String out) {
        if (expected.equals(in)) {
            assertThat(out).isSameAs(in);
        } else {
            assertThat(out).isEqualTo(expected);
        }
    }

    // Test collapse() a little differently than the rest, as we really want to
    // cover lots of different configurations of input text
    public void testCollapse() {
        // collapsing groups of '-' into '_' or '-'
        doTestCollapse("-", "_");
        doTestCollapse("x-", "x_");
        doTestCollapse("-x", "_x");
        doTestCollapse("--", "_");
        doTestCollapse("x--", "x_");
        doTestCollapse("--x", "_x");
        doTestCollapse("-x-", "_x_");
        doTestCollapse("x-x", "x_x");
        doTestCollapse("---", "_");
        doTestCollapse("--x-", "_x_");
        doTestCollapse("--xx", "_xx");
        doTestCollapse("-x--", "_x_");
        doTestCollapse("-x-x", "_x_x");
        doTestCollapse("-xx-", "_xx_");
        doTestCollapse("x--x", "x_x");
        doTestCollapse("x-x-", "x_x_");
        doTestCollapse("x-xx", "x_xx");
        doTestCollapse("x-x--xx---x----x", "x_x_xx_x_x");

        doTestCollapseWithNoChange("");
        doTestCollapseWithNoChange("x");
        doTestCollapseWithNoChange("xx");
    }

    private void doTestCollapse(String in, String out) {
        // Try a few different matchers which all match '-' and not 'x'
        // Try replacement chars that both do and do not change the value.
        for (char replacement : new char[]{'_', '-'}) {
            String expected = out.replace('_', replacement);
            assertEqualsSame(expected, in, is('-').collapseFrom(in, replacement));
            assertEqualsSame(expected, in, is('-').collapseFrom(in, replacement));
            assertEqualsSame(expected, in, is('-').or(is('#')).collapseFrom(in, replacement));
            assertEqualsSame(expected, in, isNot('x').collapseFrom(in, replacement));
            assertEqualsSame(expected, in, is('x').negate().collapseFrom(in, replacement));
            assertEqualsSame(expected, in, anyOf("-").collapseFrom(in, replacement));
            assertEqualsSame(expected, in, anyOf("-#").collapseFrom(in, replacement));
            assertEqualsSame(expected, in, anyOf("-#123").collapseFrom(in, replacement));
        }
    }

    private void doTestCollapseWithNoChange(String inout) {
        assertThat(is('-').collapseFrom(inout, '_')).isSameAs(inout);
        assertThat(is('-').or(is('#')).collapseFrom(inout, '_')).isSameAs(inout);
        assertThat(isNot('x').collapseFrom(inout, '_')).isSameAs(inout);
        assertThat(is('x').negate().collapseFrom(inout, '_')).isSameAs(inout);
        assertThat(anyOf("-").collapseFrom(inout, '_')).isSameAs(inout);
        assertThat(anyOf("-#").collapseFrom(inout, '_')).isSameAs(inout);
        assertThat(anyOf("-#123").collapseFrom(inout, '_')).isSameAs(inout);
        assertThat(CharMatcher.none().collapseFrom(inout, '_')).isSameAs(inout);
    }

    public void testCollapse_any() {
        assertThat(CharMatcher.any().collapseFrom("", '_')).isEqualTo("");
        assertThat(CharMatcher.any().collapseFrom("a", '_')).isEqualTo("_");
        assertThat(CharMatcher.any().collapseFrom("ab", '_')).isEqualTo("_");
        assertThat(CharMatcher.any().collapseFrom("abcd", '_')).isEqualTo("_");
    }

    public void testTrimFrom() {
        // trimming -
        doTestTrimFrom("-", "");
        doTestTrimFrom("x-", "x");
        doTestTrimFrom("-x", "x");
        doTestTrimFrom("--", "");
        doTestTrimFrom("x--", "x");
        doTestTrimFrom("--x", "x");
        doTestTrimFrom("-x-", "x");
        doTestTrimFrom("x-x", "x-x");
        doTestTrimFrom("---", "");
        doTestTrimFrom("--x-", "x");
        doTestTrimFrom("--xx", "xx");
        doTestTrimFrom("-x--", "x");
        doTestTrimFrom("-x-x", "x-x");
        doTestTrimFrom("-xx-", "xx");
        doTestTrimFrom("x--x", "x--x");
        doTestTrimFrom("x-x-", "x-x");
        doTestTrimFrom("x-xx", "x-xx");
        doTestTrimFrom("x-x--xx---x----x", "x-x--xx---x----x");
        // additional testing using the doc example
        assertThat(anyOf("ab").trimFrom("abacatbab")).isEqualTo("cat");
    }

    private void doTestTrimFrom(String in, String out) {
        // Try a few different matchers which all match '-' and not 'x'
        assertThat(is('-').trimFrom(in)).isEqualTo(out);
        assertThat(is('-').or(is('#')).trimFrom(in)).isEqualTo(out);
        assertThat(isNot('x').trimFrom(in)).isEqualTo(out);
        assertThat(is('x').negate().trimFrom(in)).isEqualTo(out);
        assertThat(anyOf("-").trimFrom(in)).isEqualTo(out);
        assertThat(anyOf("-#").trimFrom(in)).isEqualTo(out);
        assertThat(anyOf("-#123").trimFrom(in)).isEqualTo(out);
    }

    public void testTrimLeadingFrom() {
        // trimming -
        doTestTrimLeadingFrom("-", "");
        doTestTrimLeadingFrom("x-", "x-");
        doTestTrimLeadingFrom("-x", "x");
        doTestTrimLeadingFrom("--", "");
        doTestTrimLeadingFrom("x--", "x--");
        doTestTrimLeadingFrom("--x", "x");
        doTestTrimLeadingFrom("-x-", "x-");
        doTestTrimLeadingFrom("x-x", "x-x");
        doTestTrimLeadingFrom("---", "");
        doTestTrimLeadingFrom("--x-", "x-");
        doTestTrimLeadingFrom("--xx", "xx");
        doTestTrimLeadingFrom("-x--", "x--");
        doTestTrimLeadingFrom("-x-x", "x-x");
        doTestTrimLeadingFrom("-xx-", "xx-");
        doTestTrimLeadingFrom("x--x", "x--x");
        doTestTrimLeadingFrom("x-x-", "x-x-");
        doTestTrimLeadingFrom("x-xx", "x-xx");
        doTestTrimLeadingFrom("x-x--xx---x----x", "x-x--xx---x----x");
        // additional testing using the doc example
        assertThat(anyOf("ab").trimLeadingFrom("abacatbab")).isEqualTo("catbab");
    }

    private void doTestTrimLeadingFrom(String in, String out) {
        // Try a few different matchers which all match '-' and not 'x'
        assertThat(is('-').trimLeadingFrom(in)).isEqualTo(out);
        assertThat(is('-').or(is('#')).trimLeadingFrom(in)).isEqualTo(out);
        assertThat(isNot('x').trimLeadingFrom(in)).isEqualTo(out);
        assertThat(is('x').negate().trimLeadingFrom(in)).isEqualTo(out);
        assertThat(anyOf("-#").trimLeadingFrom(in)).isEqualTo(out);
        assertThat(anyOf("-#123").trimLeadingFrom(in)).isEqualTo(out);
    }

    public void testTrimTrailingFrom() {
        // trimming -
        doTestTrimTrailingFrom("-", "");
        doTestTrimTrailingFrom("x-", "x");
        doTestTrimTrailingFrom("-x", "-x");
        doTestTrimTrailingFrom("--", "");
        doTestTrimTrailingFrom("x--", "x");
        doTestTrimTrailingFrom("--x", "--x");
        doTestTrimTrailingFrom("-x-", "-x");
        doTestTrimTrailingFrom("x-x", "x-x");
        doTestTrimTrailingFrom("---", "");
        doTestTrimTrailingFrom("--x-", "--x");
        doTestTrimTrailingFrom("--xx", "--xx");
        doTestTrimTrailingFrom("-x--", "-x");
        doTestTrimTrailingFrom("-x-x", "-x-x");
        doTestTrimTrailingFrom("-xx-", "-xx");
        doTestTrimTrailingFrom("x--x", "x--x");
        doTestTrimTrailingFrom("x-x-", "x-x");
        doTestTrimTrailingFrom("x-xx", "x-xx");
        doTestTrimTrailingFrom("x-x--xx---x----x", "x-x--xx---x----x");
        // additional testing using the doc example
        assertThat(anyOf("ab").trimTrailingFrom("abacatbab")).isEqualTo("abacat");
    }

    private void doTestTrimTrailingFrom(String in, String out) {
        // Try a few different matchers which all match '-' and not 'x'
        assertThat(is('-').trimTrailingFrom(in)).isEqualTo(out);
        assertThat(is('-').or(is('#')).trimTrailingFrom(in)).isEqualTo(out);
        assertThat(isNot('x').trimTrailingFrom(in)).isEqualTo(out);
        assertThat(is('x').negate().trimTrailingFrom(in)).isEqualTo(out);
        assertThat(anyOf("-#").trimTrailingFrom(in)).isEqualTo(out);
        assertThat(anyOf("-#123").trimTrailingFrom(in)).isEqualTo(out);
    }

    public void testTrimAndCollapse() {
        // collapsing groups of '-' into '_' or '-'
        doTestTrimAndCollapse("", "");
        doTestTrimAndCollapse("x", "x");
        doTestTrimAndCollapse("-", "");
        doTestTrimAndCollapse("x-", "x");
        doTestTrimAndCollapse("-x", "x");
        doTestTrimAndCollapse("--", "");
        doTestTrimAndCollapse("x--", "x");
        doTestTrimAndCollapse("--x", "x");
        doTestTrimAndCollapse("-x-", "x");
        doTestTrimAndCollapse("x-x", "x_x");
        doTestTrimAndCollapse("---", "");
        doTestTrimAndCollapse("--x-", "x");
        doTestTrimAndCollapse("--xx", "xx");
        doTestTrimAndCollapse("-x--", "x");
        doTestTrimAndCollapse("-x-x", "x_x");
        doTestTrimAndCollapse("-xx-", "xx");
        doTestTrimAndCollapse("x--x", "x_x");
        doTestTrimAndCollapse("x-x-", "x_x");
        doTestTrimAndCollapse("x-xx", "x_xx");
        doTestTrimAndCollapse("x-x--xx---x----x", "x_x_xx_x_x");
    }

    private void doTestTrimAndCollapse(String in, String out) {
        // Try a few different matchers which all match '-' and not 'x'
        for (char replacement : new char[]{'_', '-'}) {
            String expected = out.replace('_', replacement);
            assertEqualsSame(expected, in, is('-').trimAndCollapseFrom(in, replacement));
            assertEqualsSame(expected, in, is('-').or(is('#')).trimAndCollapseFrom(in, replacement));
            assertEqualsSame(expected, in, isNot('x').trimAndCollapseFrom(in, replacement));
            assertEqualsSame(expected, in, is('x').negate().trimAndCollapseFrom(in, replacement));
            assertEqualsSame(expected, in, anyOf("-").trimAndCollapseFrom(in, replacement));
            assertEqualsSame(expected, in, anyOf("-#").trimAndCollapseFrom(in, replacement));
            assertEqualsSame(expected, in, anyOf("-#123").trimAndCollapseFrom(in, replacement));
        }
    }

    public void testReplaceFrom() {
        assertThat(is('a').replaceFrom("yaha", 'o')).isEqualTo("yoho");
        assertThat(is('a').replaceFrom("yaha", "")).isEqualTo("yh");
        assertThat(is('a').replaceFrom("yaha", "o")).isEqualTo("yoho");
        assertThat(is('a').replaceFrom("yaha", "oo")).isEqualTo("yoohoo");
        assertThat(is('>').replaceFrom("12 > 5", "&gt;")).isEqualTo("12 &gt; 5");
    }

    public void testPrecomputedOptimizations() {
        // These are testing behavior that's never promised by the API.
        // Some matchers are so efficient that it is a waste of effort to
        // build a precomputed version.
        CharMatcher m1 = is('x');
        assertThat(m1.precomputed()).isSameAs(m1);
        assertThat(m1.precomputed().toString()).isEqualTo(m1.toString());

        CharMatcher m2 = anyOf("Az");
        assertThat(m2.precomputed()).isSameAs(m2);
        assertThat(m2.precomputed().toString()).isEqualTo(m2.toString());

        CharMatcher m3 = inRange('A', 'Z');
        assertThat(m3.precomputed()).isSameAs(m3);
        assertThat(m3.precomputed().toString()).isEqualTo(m3.toString());

        assertThat(CharMatcher.none().precomputed()).isSameAs(CharMatcher.none());
        assertThat(CharMatcher.any().precomputed()).isSameAs(CharMatcher.any());
    }

    @GwtIncompatible // java.util.BitSet
    private static BitSet bitSet(String chars) {
        return bitSet(chars.toCharArray());
    }

    @GwtIncompatible // java.util.BitSet
    private static BitSet bitSet(char[] chars) {
        BitSet tmp = new BitSet();
        for (char c : chars) {
            tmp.set(c);
        }
        return tmp;
    }

    @GwtIncompatible // java.util.Random, java.util.BitSet
    public void testSmallCharMatcher() {
        CharMatcher len1 = SmallCharMatcher.from(bitSet("#"), "#");
        CharMatcher len2 = SmallCharMatcher.from(bitSet("ab"), "ab");
        CharMatcher len3 = SmallCharMatcher.from(bitSet("abc"), "abc");
        CharMatcher len4 = SmallCharMatcher.from(bitSet("abcd"), "abcd");
        assertThat(len1.matches('#')).isTrue();
        assertThat(len1.matches('!')).isFalse();
        assertThat(len2.matches('a')).isTrue();
        assertThat(len2.matches('b')).isTrue();
        for (char c = 'c'; c < 'z'; c++) {
            assertThat(len2.matches(c)).isFalse();
        }
        assertThat(len3.matches('a')).isTrue();
        assertThat(len3.matches('b')).isTrue();
        assertThat(len3.matches('c')).isTrue();
        for (char c = 'd'; c < 'z'; c++) {
            assertThat(len3.matches(c)).isFalse();
        }
        assertThat(len4.matches('a')).isTrue();
        assertThat(len4.matches('b')).isTrue();
        assertThat(len4.matches('c')).isTrue();
        assertThat(len4.matches('d')).isTrue();
        for (char c = 'e'; c < 'z'; c++) {
            assertThat(len4.matches(c)).isFalse();
        }

        Random rand = new Random(1234);
        for (int testCase = 0; testCase < 100; testCase++) {
            char[] chars = randomChars(rand, rand.nextInt(63) + 1);
            CharMatcher m = SmallCharMatcher.from(bitSet(chars), new String(chars));
            checkExactMatches(m, chars);
        }
    }

    static void checkExactMatches(CharMatcher m, char[] chars) {
        Set<Character> positive = Sets.newHashSetWithExpectedSize(chars.length);
        for (char c : chars) {
            positive.add(c);
        }
        for (int c = 0; c <= Character.MAX_VALUE; c++) {
            assertThat(positive.contains(new Character((char) c)) ^ m.matches((char) c)).isFalse();
        }
    }

    static char[] randomChars(Random rand, int size) {
        Set<Character> chars = new HashSet<>(size);
        for (int i = 0; i < size; i++) {
            char c;
            do {
                c = (char) rand.nextInt(Character.MAX_VALUE - Character.MIN_VALUE + 1);
            } while (chars.contains(c));
            chars.add(c);
        }
        char[] retValue = new char[chars.size()];
        int i = 0;
        for (char c : chars) {
            retValue[i++] = c;
        }
        Arrays.sort(retValue);
        return retValue;
    }

    public void testToString() {
        assertToStringWorks("CharMatcher.none()", CharMatcher.anyOf(""));
        assertToStringWorks("CharMatcher.is('\\u0031')", CharMatcher.anyOf("1"));
        assertToStringWorks("CharMatcher.isNot('\\u0031')", CharMatcher.isNot('1'));
        assertToStringWorks("CharMatcher.anyOf(\"\\u0031\\u0032\")", CharMatcher.anyOf("12"));
        assertToStringWorks("CharMatcher.anyOf(\"\\u0031\\u0032\\u0033\")", CharMatcher.anyOf("321"));
        assertToStringWorks("CharMatcher.inRange('\\u0031', '\\u0033')", CharMatcher.inRange('1', '3'));
    }

    private static void assertToStringWorks(String expected, CharMatcher matcher) {
        assertThat(matcher.toString()).isEqualTo(expected);
        assertThat(matcher.precomputed().toString()).isEqualTo(expected);
        assertThat(matcher.negate().negate().toString()).isEqualTo(expected);
        assertThat(matcher.negate().precomputed().negate().toString()).isEqualTo(expected);
        assertThat(matcher.negate().precomputed().negate().precomputed().toString()).isEqualTo(expected);
    }
}
