/*
 * Copyright 2010-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.huyu.benchmark.method;

import com.huyu.field.ReflectFieldInvokerUtils;
import com.huyu.field.invoker.FieldReflectInvoker;
import com.huyu.method.ReflectMethodInvokerUtils;
import com.huyu.method.invoker.MethodReflectInvoker;
import com.huyu.service.XxService;
import com.huyu.service.impl.XxServiceImpl;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

@BenchmarkMode({Mode.AverageTime})
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Fork(value = 2, jvmArgs = {"-Xms4G", "-Xmx4G"})
@Warmup(iterations = 3, time = 2)
@Measurement(iterations = 3, time = 2)
public class MethodBenchmark {

  private XxService target;

  MethodReflectInvoker hasParamMethodReflectInvoker;
  MethodReflectInvoker hasParamDirectInvoker;
  MethodReflectInvoker hasParamStaticLambdaInvoker;
  MethodReflectInvoker hasParamLambdaInvoker;

  MethodReflectInvoker emptyParamMethodReflectInvoker;
  MethodReflectInvoker emptyParamDirectInvoker;
  MethodReflectInvoker emptyParamStaticLambdaInvoker;
  MethodReflectInvoker emptyParamLambdaInvoker;

  FieldReflectInvoker unsafeInvoker;
  FieldReflectInvoker varHandleInvoker;
  FieldReflectInvoker directInvoker;
  FieldReflectInvoker fieldInvoker;


  @Setup
  public void setup() throws Throwable {
    target = new XxServiceImpl();

    Method hasParamMethod = target.getClass()
        .getMethod("add", String.class, String.class, Long.class, Integer.class, byte.class,
            long.class, short.class, int.class);
    hasParamMethod.setAccessible(true);

    Method emptyParamMethod = target.getClass().getMethod("add");
    emptyParamMethod.setAccessible(true);

    //有参
    hasParamMethodReflectInvoker = ReflectMethodInvokerUtils.createReflectMethodInvoker(
        hasParamMethod);
    hasParamDirectInvoker = ReflectMethodInvokerUtils.createDirectMethodInvoker(hasParamMethod);
    hasParamStaticLambdaInvoker = ReflectMethodInvokerUtils.createLambdaMethodInvoker(
        hasParamMethod, true);
    hasParamLambdaInvoker = ReflectMethodInvokerUtils.createLambdaMethodInvoker(hasParamMethod,
        false);

    //无参
    emptyParamMethodReflectInvoker = ReflectMethodInvokerUtils.createReflectMethodInvoker(
        emptyParamMethod);
    emptyParamDirectInvoker = ReflectMethodInvokerUtils.createDirectMethodInvoker(emptyParamMethod);
    emptyParamStaticLambdaInvoker = ReflectMethodInvokerUtils.createLambdaMethodInvoker(
        emptyParamMethod, true);
    emptyParamLambdaInvoker = ReflectMethodInvokerUtils.createLambdaMethodInvoker(emptyParamMethod,
        false);

    //属性值设置
    Field field = target.getClass().getDeclaredField("cc");
    field.setAccessible(true);

    //unsafe
    unsafeInvoker = ReflectFieldInvokerUtils.createUnsafeInvoker(field);
    varHandleInvoker = ReflectFieldInvokerUtils.createVarHandleInvoker(field);
    directInvoker = ReflectFieldInvokerUtils.createDirectInvoker(field);
    fieldInvoker = ReflectFieldInvokerUtils.createFieldInvoker(field);
  }

  /**
   * 反射方法调用
   *
   * @param blackhole
   * @throws InvocationTargetException
   * @throws IllegalAccessException
   */
  @org.openjdk.jmh.annotations.Benchmark
  public void emptyParamRefMethodInvokeCall(Blackhole blackhole) {
    emptyParamMethodReflectInvoker.invoke(target);
    blackhole.consume(emptyParamMethodReflectInvoker);
  }


  /**
   * lambda表达式调用
   *
   * @param blackhole
   * @throws Exception
   */
  @org.openjdk.jmh.annotations.Benchmark
  public void emptyParamLambdaMethodInvokeCall(Blackhole blackhole) throws Throwable {
    emptyParamLambdaInvoker.invoke(target);
    blackhole.consume(emptyParamLambdaInvoker);
  }

  @org.openjdk.jmh.annotations.Benchmark
  public void emptyStaticLambdaMethodInvokerCall(Blackhole blackhole) throws Throwable {
    emptyParamStaticLambdaInvoker.invoke(target);
    blackhole.consume(emptyParamStaticLambdaInvoker);
  }

  @org.openjdk.jmh.annotations.Benchmark
  public void emptyDirectInvokerMethodInvokerCall(Blackhole blackhole) throws Throwable {
    emptyParamDirectInvoker.invoke(target);
    blackhole.consume(emptyParamDirectInvoker);
  }

  /**
   * 直接调用方法
   *
   * @param blackhole
   */
  @org.openjdk.jmh.annotations.Benchmark
  public void emptyParamDirectMethodCall(Blackhole blackhole) {
    target.add();
    blackhole.consume(target);
  }

//  ==================================================有参=====================================================================


  /**
   * 反射方法调用
   *
   * @param blackhole
   * @throws InvocationTargetException
   * @throws IllegalAccessException
   */
  @org.openjdk.jmh.annotations.Benchmark
  public void hasParamRefMethodInvokerCall(Blackhole blackhole)
      throws InvocationTargetException, IllegalAccessException {
    hasParamMethodReflectInvoker.invoke(target, "username", "password", 1L, 1, (byte) 1, 1L,
        (short) 1, 1);
    blackhole.consume(hasParamMethodReflectInvoker);
  }


  /**
   * lambda表达式调用
   *
   * @param blackhole
   * @throws Exception
   */
  @org.openjdk.jmh.annotations.Benchmark
  public void hasParamLambdaMethodInvokerCall(Blackhole blackhole) throws Throwable {
    hasParamLambdaInvoker.invoke(target, "username", "password", 1L, 1, (byte) 1, 1L, (short) 1, 1);
    blackhole.consume(hasParamLambdaInvoker);
  }

  @org.openjdk.jmh.annotations.Benchmark
  public void hasStaticLambdaMethodInvokerCall(Blackhole blackhole) throws Throwable {
    hasParamStaticLambdaInvoker.invoke(target, "username", "password", 1L, 1, (byte) 1, 1L,
        (short) 1, 1);
    blackhole.consume(hasParamStaticLambdaInvoker);
  }

  @org.openjdk.jmh.annotations.Benchmark
  public void hasDirectInvokerMethodInvokerCall(Blackhole blackhole) throws Throwable {
    hasParamDirectInvoker.invoke(target, "username", "password", 1L, 1, (byte) 1, 1L, (short) 1, 1);
    blackhole.consume(hasParamDirectInvoker);
  }

  /**
   * 直接调用方法
   *
   * @param blackhole
   */
  @org.openjdk.jmh.annotations.Benchmark
  public void hasParamDirectMethodCall(Blackhole blackhole) {
    target.add("username", "password", 1L, 1, (byte) 1, 1L, (short) 1, 1);
    blackhole.consume(target);
  }


  @State(Scope.Thread)
  public static class ThreadState {

    @Param({"1", "10", "20"})
    public int iterations;
  }
}
