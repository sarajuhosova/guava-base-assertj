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

import static com.google.common.base.StandardSystemProperty.JAVA_CLASS_PATH;
import static com.google.common.base.StandardSystemProperty.PATH_SEPARATOR;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.testing.GcFinalization;
import com.google.common.testing.NullPointerTester;
import com.google.common.testing.SerializableTester;
import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;
import junit.framework.TestCase;

/**
 * Tests for {@link Enums}.
 *
 * @author Steve McKay
 */
@GwtCompatible(emulated = true)
public class EnumsTest extends TestCase {

  private enum TestEnum {
    CHEETO,
    HONDA,
    POODLE,
  }

  private enum OtherEnum {}


  @GwtIncompatible // weak references
  public void testGetIfPresent_doesNotPreventClassUnloading() throws Exception {
    WeakReference<?> shadowLoaderReference = doTestClassUnloading();
    GcFinalization.awaitClear(shadowLoaderReference);
  }

  // Create a second ClassLoader and use it to get a second version of the TestEnum class.
  // Run Enums.getIfPresent on that other TestEnum and then return a WeakReference containing the
  // new ClassLoader. If Enums.getIfPresent does caching that prevents the shadow TestEnum
  // (and therefore its ClassLoader) from being unloaded, then this WeakReference will never be
  // cleared.
  @GwtIncompatible // weak references
  private WeakReference<?> doTestClassUnloading() throws Exception {
    URLClassLoader shadowLoader = new URLClassLoader(getClassPathUrls(), null);
    @SuppressWarnings("unchecked")
    Class<TestEnum> shadowTestEnum =
        (Class<TestEnum>) Class.forName(TestEnum.class.getName(), false, shadowLoader);
    assertThat(TestEnum.class).isNotSameAs(shadowTestEnum);
    // We can't write Set<TestEnum> because that is a Set of the TestEnum from the original
    // ClassLoader.
    Set<Object> shadowConstants = new HashSet<>();
    for (TestEnum constant : TestEnum.values()) {
      Optional<TestEnum> result = Enums.getIfPresent(shadowTestEnum, constant.name());
      shadowConstants.add(result.get());
    }
    assertThat(shadowConstants).isEqualTo(ImmutableSet.<Object>copyOf(shadowTestEnum.getEnumConstants()));
    Optional<TestEnum> result = Enums.getIfPresent(shadowTestEnum, "blibby");
    return new WeakReference<>(shadowLoader);
  }

  public void testStringConverter_convert() {
    Converter<String, TestEnum> converter = Enums.stringConverter(TestEnum.class);
    assertThat(converter.convert("CHEETO")).isEqualTo(TestEnum.CHEETO);
    assertThat(converter.convert("HONDA")).isEqualTo(TestEnum.HONDA);
    assertThat(converter.convert("POODLE")).isEqualTo(TestEnum.POODLE);
    assertThat(converter.convert(null)).isNull();
    assertThat(converter.reverse().convert(null)).isNull();
  }

  public void testStringConverter_convertError() {
    Converter<String, TestEnum> converter = Enums.stringConverter(TestEnum.class);
    try {
      converter.convert("xxx");
      fail();
    } catch (IllegalArgumentException expected) {
    }
  }

  public void testStringConverter_reverse() {
    Converter<String, TestEnum> converter = Enums.stringConverter(TestEnum.class);
    assertThat(converter.reverse().convert(TestEnum.CHEETO)).isEqualTo("CHEETO");
    assertThat(converter.reverse().convert(TestEnum.HONDA)).isEqualTo("HONDA");
    assertThat(converter.reverse().convert(TestEnum.POODLE)).isEqualTo("POODLE");
  }

  @GwtIncompatible // NullPointerTester
  public void testStringConverter_nullPointerTester() throws Exception {
    Converter<String, TestEnum> converter = Enums.stringConverter(TestEnum.class);
    NullPointerTester tester = new NullPointerTester();
    tester.testAllPublicInstanceMethods(converter);
  }

  public void testStringConverter_nullConversions() {
    Converter<String, TestEnum> converter = Enums.stringConverter(TestEnum.class);
    assertThat(converter.convert(null)).isNull();
    assertThat(converter.reverse().convert(null)).isNull();
  }

  @GwtIncompatible // Class.getName()
  public void testStringConverter_toString() {
    assertEquals(
        "Enums.stringConverter(com.google.common.base.EnumsTest$TestEnum.class)",
        Enums.stringConverter(TestEnum.class).toString());
  }

  public void testStringConverter_serialization() {
    SerializableTester.reserializeAndAssert(Enums.stringConverter(TestEnum.class));
  }

  @GwtIncompatible // NullPointerTester
  public void testNullPointerExceptions() {
    NullPointerTester tester = new NullPointerTester();
    tester.testAllPublicStaticMethods(Enums.class);
  }

  @Retention(RetentionPolicy.RUNTIME)
  private @interface ExampleAnnotation {}

  private enum AnEnum {
    @ExampleAnnotation
    FOO,
    BAR
  }

  @GwtIncompatible // reflection
  public void testGetField() {
    Field foo = Enums.getField(AnEnum.FOO);
    assertThat(foo.getName()).isEqualTo("FOO");
    assertThat(foo.isAnnotationPresent(ExampleAnnotation.class)).isTrue();

    Field bar = Enums.getField(AnEnum.BAR);
    assertThat(bar.getName()).isEqualTo("BAR");
    assertThat(bar.isAnnotationPresent(ExampleAnnotation.class)).isFalse();
  }

  @GwtIncompatible // Class.getClassLoader()
  private URL[] getClassPathUrls() {
    ClassLoader classLoader = getClass().getClassLoader();
    return classLoader instanceof URLClassLoader
        ? ((URLClassLoader) classLoader).getURLs()
        : parseJavaClassPath().toArray(new URL[0]);
  }

  /**
   * Returns the URLs in the class path specified by the {@code java.class.path} {@linkplain
   * System#getProperty system property}.
   */
  // TODO(b/65488446): Make this a public API.
  @GwtIncompatible
  private static ImmutableList<URL> parseJavaClassPath() {
    ImmutableList.Builder<URL> urls = ImmutableList.builder();
    for (String entry : Splitter.on(PATH_SEPARATOR.value()).split(JAVA_CLASS_PATH.value())) {
      try {
        try {
          urls.add(new File(entry).toURI().toURL());
        } catch (SecurityException e) { // File.toURI checks to see if the file is a directory
          urls.add(new URL("file", null, new File(entry).getAbsolutePath()));
        }
      } catch (MalformedURLException e) {
        AssertionError error = new AssertionError("malformed class path entry: " + entry);
        error.initCause(e);
        throw error;
      }
    }
    return urls.build();
  }
}
