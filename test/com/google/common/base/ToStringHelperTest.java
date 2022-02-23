/*
 * Copyright (C) 2009 The Guava Authors
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
import junit.framework.TestCase;

import java.util.Arrays;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link MoreObjects#toStringHelper(Object)}.
 *
 * @author Jason Lee
 */
@GwtCompatible
public class ToStringHelperTest extends TestCase {

    @GwtIncompatible // Class names are obfuscated in GWT
    public void testConstructor_instance() {
        String toTest = MoreObjects.toStringHelper(this).toString();
        assertThat(toTest).isEqualTo("ToStringHelperTest{}");
    }

    public void testConstructorLenient_instance() {
        String toTest = MoreObjects.toStringHelper(this).toString();
        assertThat(toTest.matches(".*\\{\\}")).isTrue();
    }

    @GwtIncompatible // Class names are obfuscated in GWT
    public void testConstructor_innerClass() {
        String toTest = MoreObjects.toStringHelper(new TestClass()).toString();
        assertThat(toTest).isEqualTo("TestClass{}");
    }

    public void testConstructorLenient_innerClass() {
        String toTest = MoreObjects.toStringHelper(new TestClass()).toString();
        assertThat(toTest.matches(".*\\{\\}")).isTrue();
    }

    @GwtIncompatible // Class names are obfuscated in GWT
    public void testConstructor_anonymousClass() {
        String toTest = MoreObjects.toStringHelper(new Object() {
        }).toString();
        assertThat(toTest).isEqualTo("{}");
    }

    public void testConstructorLenient_anonymousClass() {
        String toTest = MoreObjects.toStringHelper(new Object() {
        }).toString();
        assertThat(toTest.matches(".*\\{\\}")).isTrue();
    }

    @GwtIncompatible // Class names are obfuscated in GWT
    public void testConstructor_classObject() {
        String toTest = MoreObjects.toStringHelper(TestClass.class).toString();
        assertThat(toTest).isEqualTo("TestClass{}");
    }

    public void testConstructorLenient_classObject() {
        String toTest = MoreObjects.toStringHelper(TestClass.class).toString();
        assertThat(toTest.matches(".*\\{\\}")).isTrue();
    }

    public void testConstructor_stringObject() {
        String toTest = MoreObjects.toStringHelper("FooBar").toString();
        assertThat(toTest).isEqualTo("FooBar{}");
    }

    @GwtIncompatible // Class names are obfuscated in GWT
    public void testToStringHelper_localInnerClass() {
        // Local inner classes have names ending like "Outer.$1Inner"
        class LocalInnerClass {
        }
        String toTest = MoreObjects.toStringHelper(new LocalInnerClass()).toString();
        assertThat(toTest).isEqualTo("LocalInnerClass{}");
    }

    public void testToStringHelperLenient_localInnerClass() {
        class LocalInnerClass {
        }
        String toTest = MoreObjects.toStringHelper(new LocalInnerClass()).toString();
        assertThat(toTest.matches(".*\\{\\}")).isTrue();
    }

    @GwtIncompatible // Class names are obfuscated in GWT
    public void testToStringHelper_localInnerNestedClass() {
        class LocalInnerClass {
            class LocalInnerNestedClass {
            }
        }
        String toTest =
                MoreObjects.toStringHelper(new LocalInnerClass().new LocalInnerNestedClass()).toString();
        assertThat(toTest).isEqualTo("LocalInnerNestedClass{}");
    }

    public void testToStringHelperLenient_localInnerNestedClass() {
        class LocalInnerClass {
            class LocalInnerNestedClass {
            }
        }
        String toTest =
                MoreObjects.toStringHelper(new LocalInnerClass().new LocalInnerNestedClass()).toString();
        assertThat(toTest.matches(".*\\{\\}")).isTrue();
    }

    @GwtIncompatible // Class names are obfuscated in GWT
    public void testToStringHelper_moreThanNineAnonymousClasses() {
        // The nth anonymous class has a name ending like "Outer.$n"
        Object unused1 = new Object() {
        };
        Object unused2 = new Object() {
        };
        Object unused3 = new Object() {
        };
        Object unused4 = new Object() {
        };
        Object unused5 = new Object() {
        };
        Object unused6 = new Object() {
        };
        Object unused7 = new Object() {
        };
        Object unused8 = new Object() {
        };
        Object unused9 = new Object() {
        };
        Object o10 = new Object() {
        };
        String toTest = MoreObjects.toStringHelper(o10).toString();
        assertThat(toTest).isEqualTo("{}");
    }

    public void testToStringHelperLenient_moreThanNineAnonymousClasses() {
        // The nth anonymous class has a name ending like "Outer.$n"
        Object unused1 = new Object() {
        };
        Object unused2 = new Object() {
        };
        Object unused3 = new Object() {
        };
        Object unused4 = new Object() {
        };
        Object unused5 = new Object() {
        };
        Object unused6 = new Object() {
        };
        Object unused7 = new Object() {
        };
        Object unused8 = new Object() {
        };
        Object unused9 = new Object() {
        };
        Object o10 = new Object() {
        };
        String toTest = MoreObjects.toStringHelper(o10).toString();
        assertThat(toTest.matches(".*\\{\\}")).isTrue();
    }

    // all remaining test are on an inner class with various fields
    @GwtIncompatible // Class names are obfuscated in GWT
    public void testToString_oneField() {
        String toTest = MoreObjects.toStringHelper(new TestClass()).add("field1", "Hello").toString();
        assertThat(toTest).isEqualTo("TestClass{field1=Hello}");
    }

    @GwtIncompatible // Class names are obfuscated in GWT
    public void testToString_oneIntegerField() {
        String toTest =
                MoreObjects.toStringHelper(new TestClass()).add("field1", new Integer(42)).toString();
        assertThat(toTest).isEqualTo("TestClass{field1=42}");
    }

    @GwtIncompatible // Class names are obfuscated in GWT
    public void testToString_nullInteger() {
        String toTest =
                MoreObjects.toStringHelper(new TestClass()).add("field1", null).toString();
        assertThat(toTest).isEqualTo("TestClass{field1=null}");
    }

    public void testToStringLenient_oneField() {
        String toTest = MoreObjects.toStringHelper(new TestClass()).add("field1", "Hello").toString();
        assertThat(toTest.matches(".*\\{field1\\=Hello\\}")).isTrue();
    }

    public void testToStringLenient_oneIntegerField() {
        String toTest =
                MoreObjects.toStringHelper(new TestClass()).add("field1", new Integer(42)).toString();
        assertThat(toTest.matches(".*\\{field1\\=42\\}")).isTrue();
    }

    public void testToStringLenient_nullInteger() {
        String toTest =
                MoreObjects.toStringHelper(new TestClass()).add("field1", null).toString();
        assertThat(toTest.matches(".*\\{field1\\=null\\}")).isTrue();
    }

    @GwtIncompatible // Class names are obfuscated in GWT
    public void testToString_complexFields() {

        Map<String, Integer> map =
                ImmutableMap.<String, Integer>builder().put("abc", 1).put("def", 2).put("ghi", 3).build();
        String toTest =
                MoreObjects.toStringHelper(new TestClass())
                        .add("field1", "This is string.")
                        .add("field2", Arrays.asList("abc", "def", "ghi"))
                        .add("field3", map)
                        .toString();
        final String expected =
                "TestClass{"
                        + "field1=This is string., field2=[abc, def, ghi], field3={abc=1, def=2, ghi=3}}";

        assertThat(toTest).isEqualTo(expected);
    }

    public void testToStringLenient_complexFields() {

        Map<String, Integer> map =
                ImmutableMap.<String, Integer>builder().put("abc", 1).put("def", 2).put("ghi", 3).build();
        String toTest =
                MoreObjects.toStringHelper(new TestClass())
                        .add("field1", "This is string.")
                        .add("field2", Arrays.asList("abc", "def", "ghi"))
                        .add("field3", map)
                        .toString();
        final String expectedRegex =
                ".*\\{"
                        + "field1\\=This is string\\., "
                        + "field2\\=\\[abc, def, ghi\\], "
                        + "field3=\\{abc\\=1, def\\=2, ghi\\=3\\}\\}";

        assertThat(toTest.matches(expectedRegex)).isTrue();
    }

    public void testToString_addWithNullName() {
        MoreObjects.ToStringHelper helper = MoreObjects.toStringHelper(new TestClass());
        try {
            helper.add(null, "Hello");
            fail("No exception was thrown.");
        } catch (NullPointerException expected) {
        }
    }

    @GwtIncompatible // Class names are obfuscated in GWT
    public void testToString_addWithNullValue() {
        final String result = MoreObjects.toStringHelper(new TestClass()).add("Hello", null).toString();

        assertThat(result).isEqualTo("TestClass{Hello=null}");
    }

    public void testToStringLenient_addWithNullValue() {
        final String result = MoreObjects.toStringHelper(new TestClass()).add("Hello", null).toString();
        assertThat(result.matches(".*\\{Hello\\=null\\}")).isTrue();
    }

    @GwtIncompatible // Class names are obfuscated in GWT
    public void testToString_ToStringTwice() {
        MoreObjects.ToStringHelper helper =
                MoreObjects.toStringHelper(new TestClass())
                        .add("field1", 1)
                        .addValue("value1")
                        .add("field2", "value2");
        final String expected = "TestClass{field1=1, value1, field2=value2}";

        assertThat(helper.toString()).isEqualTo(expected);
        // Call toString again
        assertThat(helper.toString()).isEqualTo(expected);

        // Make sure the cached value is reset when we modify the helper at all
        final String expected2 = "TestClass{field1=1, value1, field2=value2, 2}";
        helper.addValue(2);
        assertThat(helper.toString()).isEqualTo(expected2);
    }

    @GwtIncompatible // Class names are obfuscated in GWT
    public void testToString_addValue() {
        String toTest =
                MoreObjects.toStringHelper(new TestClass())
                        .add("field1", 1)
                        .addValue("value1")
                        .add("field2", "value2")
                        .addValue(2)
                        .toString();
        final String expected = "TestClass{field1=1, value1, field2=value2, 2}";

        assertThat(toTest).isEqualTo(expected);
    }

    public void testToStringLenient_addValue() {
        String toTest =
                MoreObjects.toStringHelper(new TestClass())
                        .add("field1", 1)
                        .addValue("value1")
                        .add("field2", "value2")
                        .addValue(2)
                        .toString();
        final String expected = ".*\\{field1\\=1, value1, field2\\=value2, 2\\}";

        assertThat(toTest.matches(expected)).isTrue();
    }

    @GwtIncompatible // Class names are obfuscated in GWT
    public void testToString_addValueWithNullValue() {
        final String result =
                MoreObjects.toStringHelper(new TestClass())
                        .addValue(null)
                        .addValue("Hello")
                        .addValue(null)
                        .toString();
        final String expected = "TestClass{null, Hello, null}";

        assertThat(result).isEqualTo(expected);
    }

    public void testToStringLenient_addValueWithNullValue() {
        final String result =
                MoreObjects.toStringHelper(new TestClass())
                        .addValue(null)
                        .addValue("Hello")
                        .addValue(null)
                        .toString();
        final String expected = ".*\\{null, Hello, null\\}";

        assertThat(result.matches(expected)).isTrue();
    }

    @GwtIncompatible // Class names are obfuscated in GWT
    public void testToStringOmitNullValues_oneField() {
        String toTest =
                MoreObjects.toStringHelper(new TestClass()).omitNullValues().add("field1", null).toString();
        assertThat(toTest).isEqualTo("TestClass{}");
    }

    @GwtIncompatible // Class names are obfuscated in GWT
    public void testToStringOmitNullValues_manyFieldsFirstNull() {
        String toTest =
                MoreObjects.toStringHelper(new TestClass())
                        .omitNullValues()
                        .add("field1", null)
                        .add("field2", "Googley")
                        .add("field3", "World")
                        .toString();
        assertThat(toTest).isEqualTo("TestClass{field2=Googley, field3=World}");
    }

    @GwtIncompatible // Class names are obfuscated in GWT
    public void testToStringOmitNullValues_manyFieldsOmitAfterNull() {
        String toTest =
                MoreObjects.toStringHelper(new TestClass())
                        .add("field1", null)
                        .add("field2", "Googley")
                        .add("field3", "World")
                        .omitNullValues()
                        .toString();
        assertThat(toTest).isEqualTo("TestClass{field2=Googley, field3=World}");
    }

    @GwtIncompatible // Class names are obfuscated in GWT
    public void testToStringOmitNullValues_manyFieldsLastNull() {
        String toTest =
                MoreObjects.toStringHelper(new TestClass())
                        .omitNullValues()
                        .add("field1", "Hello")
                        .add("field2", "Googley")
                        .add("field3", null)
                        .toString();
        assertThat(toTest).isEqualTo("TestClass{field1=Hello, field2=Googley}");
    }

    @GwtIncompatible // Class names are obfuscated in GWT
    public void testToStringOmitEmptyValues_oneValue() {
        String toTest =
                MoreObjects.toStringHelper(new TestClass()).omitNullValues().addValue(null).toString();
        assertThat(toTest).isEqualTo("TestClass{}");
    }

    @GwtIncompatible // Class names are obfuscated in GWT
    public void testToStringOmitNullValues_manyValuesFirstNull() {
        String toTest =
                MoreObjects.toStringHelper(new TestClass())
                        .omitNullValues()
                        .addValue(null)
                        .addValue("Googley")
                        .addValue("World")
                        .toString();
        assertThat(toTest).isEqualTo("TestClass{Googley, World}");
    }

    @GwtIncompatible // Class names are obfuscated in GWT
    public void testToStringOmitNullValues_manyValuesLastNull() {
        String toTest =
                MoreObjects.toStringHelper(new TestClass())
                        .omitNullValues()
                        .addValue("Hello")
                        .addValue("Googley")
                        .addValue(null)
                        .toString();
        assertThat(toTest).isEqualTo("TestClass{Hello, Googley}");
    }

    @GwtIncompatible // Class names are obfuscated in GWT
    public void testToStringOmitNullValues_differentOrder() {
        String expected = "TestClass{field1=Hello, field2=Googley, field3=World}";
        String toTest1 =
                MoreObjects.toStringHelper(new TestClass())
                        .omitNullValues()
                        .add("field1", "Hello")
                        .add("field2", "Googley")
                        .add("field3", "World")
                        .toString();
        String toTest2 =
                MoreObjects.toStringHelper(new TestClass())
                        .add("field1", "Hello")
                        .add("field2", "Googley")
                        .omitNullValues()
                        .add("field3", "World")
                        .toString();
        assertThat(toTest1).isEqualTo(expected);
        assertThat(toTest2).isEqualTo(expected);
    }

    @GwtIncompatible // Class names are obfuscated in GWT
    public void testToStringOmitNullValues_canBeCalledManyTimes() {
        String toTest =
                MoreObjects.toStringHelper(new TestClass())
                        .omitNullValues()
                        .omitNullValues()
                        .add("field1", "Hello")
                        .omitNullValues()
                        .add("field2", "Googley")
                        .omitNullValues()
                        .add("field3", "World")
                        .toString();
        assertThat(toTest).isEqualTo("TestClass{field1=Hello, field2=Googley, field3=World}");
    }

    /**
     * Test class for testing formatting of inner classes.
     */
    private static class TestClass {
    }
}
