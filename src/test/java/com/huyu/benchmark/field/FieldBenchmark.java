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
package com.huyu.benchmark.field;

import com.huyu.field.ReflectFieldInvokerUtils;
import com.huyu.field.invoker.FieldReflectInvoker;
import com.huyu.service.impl.XxServiceImpl;
import java.lang.reflect.Field;
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
public class FieldBenchmark {

  private XxServiceImpl target;

  FieldReflectInvoker unsafeInvoker;
  FieldReflectInvoker varHandleInvoker;
  FieldReflectInvoker directInvoker;
  FieldReflectInvoker fieldInvoker;

  @Setup
  public void setup() throws Throwable {
    target = new XxServiceImpl();

    //属性值设置
    Field field = target.getClass().getDeclaredField("cc");
    field.setAccessible(true);

    //unsafe
    unsafeInvoker = ReflectFieldInvokerUtils.createUnsafeInvoker(field);
    varHandleInvoker = ReflectFieldInvokerUtils.createVarHandleInvoker(field);
    directInvoker = ReflectFieldInvokerUtils.createDirectInvoker(field);
    fieldInvoker = ReflectFieldInvokerUtils.createDefaultInvoker(field);
  }


  @org.openjdk.jmh.annotations.Benchmark
  public void fieldInvokerCall(Blackhole blackhole) throws Throwable {
    fieldInvoker.set(target, 100L);
    fieldInvoker.get(target);
    blackhole.consume(fieldInvoker);
  }

  @org.openjdk.jmh.annotations.Benchmark
  public void unsafeFieldInvokerCall(Blackhole blackhole) throws Throwable {
    unsafeInvoker.set(target, 100L);
    unsafeInvoker.get(target);
    blackhole.consume(unsafeInvoker);
  }

  @org.openjdk.jmh.annotations.Benchmark
  public void varHandleFieldInvokerCall(Blackhole blackhole) throws Throwable {
    varHandleInvoker.set(target, 100L);
    varHandleInvoker.get(target);
    blackhole.consume(varHandleInvoker);
  }

  @org.openjdk.jmh.annotations.Benchmark
  public void directFieldInvokerCall(Blackhole blackhole) throws Throwable {
    directInvoker.set(target, 100L);
    directInvoker.get(target);
    blackhole.consume(directInvoker);
  }

  @org.openjdk.jmh.annotations.Benchmark
  public void directFieldCall(Blackhole blackhole) throws Throwable {
    target.cc = 100L;
    blackhole.consume(target);
  }


  @State(Scope.Thread)
  public static class ThreadState {

    @Param({"1", "10", "20"})
    public int iterations;
  }
}
