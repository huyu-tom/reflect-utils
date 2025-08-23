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
package com.huyu.benchmark.proxy;

import com.huyu.proxy.OptimizedInvocationHandler;
import com.huyu.service.XxService;
import com.huyu.service.impl.XxServiceImpl;
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
public class ProxyBenchmark {

  private XxServiceImpl target;
  private XxService FAST_PROXY;
  private XxService METHOD_PROXY;


  @Setup
  public void setup() throws Throwable {
    target = new XxServiceImpl();

    // lambda表达式
    FAST_PROXY = (XxService) java.lang.reflect.Proxy.newProxyInstance(
        XxService.class.getClassLoader(), new Class[]{XxService.class},
        new OptimizedInvocationHandler(target, true));

    // 原生反射的代理
    METHOD_PROXY = (XxService) java.lang.reflect.Proxy.newProxyInstance(
        XxService.class.getClassLoader(), new Class[]{XxService.class},
        new OptimizedInvocationHandler(target, false));
  }


  @org.openjdk.jmh.annotations.Benchmark
  public void invokerProxyCall(Blackhole blackhole) throws Throwable {
    FAST_PROXY.add("user134", "password");
    blackhole.consume(FAST_PROXY);
  }

  @org.openjdk.jmh.annotations.Benchmark
  public void proxyCall(Blackhole blackhole) throws Throwable {
    METHOD_PROXY.add("user134", "password");
    blackhole.consume(METHOD_PROXY);
  }


  @State(Scope.Thread)
  public static class ThreadState {

    @Param({"1", "10", "20"})
    public int iterations;
  }
}
