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
package com.huyu.proxy;

import com.huyu.method.ReflectMethodInvokerUtils;
import com.huyu.method.invoker.MethodReflectInvoker;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 提供一种JDK动态代理优化写法
 *
 * @author huyu
 */
public class OptimizedInvocationHandler implements InvocationHandler {

  private static final VarHandle LAMBDA_CACHE_VARHANDLE;


  static {
    try {
      LAMBDA_CACHE_VARHANDLE = MethodHandles.lookup()
          .findVarHandle(OptimizedInvocationHandler.class, "invokerMap", Map.class);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * 代理对象
   */
  private final Object target;


  /**
   * 基于 LambdaMetafactory 生成的缓存
   */
  private Map<Method, MethodReflectInvoker> invokerMap = Collections.emptyMap();


  /**
   * /** 快速模式
   */
  private final boolean fast;

  /**
   * 0 methodHandler 1 lambda 2 ref
   *
   * @param target 代理的目标对象
   * @param fast
   */
  public OptimizedInvocationHandler(Object target, boolean fast) {
    this.target = target;
    this.fast = fast;
  }


  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    if (fast) {
      return fastInvoke(proxy, method, args);
    }
    return method.invoke(target, args);
  }


  private Object fastInvoke(Object proxy, Method method, Object[] args) throws Throwable {
    MethodReflectInvoker invoker = invokerMap.get(method);
    if (invoker != null) {
      return invoker.invoke(target, args);
    }
    invoker = ReflectMethodInvokerUtils.createMethodInvoker(method);
    final Map<Method, Object> newCache = new HashMap<>(invokerMap);
    newCache.put(method, invoker);
    LAMBDA_CACHE_VARHANDLE.setVolatile(this, newCache);
    return invoker.invoke(target, args);
  }
}
