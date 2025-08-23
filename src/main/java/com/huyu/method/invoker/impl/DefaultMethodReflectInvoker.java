package com.huyu.method.invoker.impl;

import com.huyu.method.invoker.MethodReflectInvoker;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class DefaultMethodReflectInvoker implements MethodReflectInvoker<Object, Object> {

  private final Method method;

  public DefaultMethodReflectInvoker(Method method) {
    this.method = method;
    method.setAccessible(true);
  }

  @Override
  public Object invoke(Object target, Object... args) {
    try {
      return method.invoke(target, args);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    } catch (InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }
}
